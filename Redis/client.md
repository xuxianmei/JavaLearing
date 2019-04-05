# 客户端

## 通信协议（Redis序列化协议）

* 客户端与服务端之间的通信协议是在TCP协议之上构建的
* Redis制定了RESP（Redis Serialization Protocol，Redis序列化协议）实现客户端与服务端的正常交互，
这种协议简单高效，既能够被机器解析，又容易被人类识别。

例如客户端发送一条set hello world命令给服务端，按照RESP
的标准，客户端需要将其封装为如下格式（每行用\r\n分隔）：
```
*3
$3
SET
$5
hello
$5
world
```
这样Redis服务端能够按照RESP将其解析为set hello world命令，执行后
回复的格式如下：
```
+OK
```

![](./images/4-1.png)

### 通过协议格式说明

* 发送命令格式
RESP的规定一条命令的格式如下，CRLF代表"\r\n"。
```
*<参数数量> CRLF
$<参数 1 的字节数量> CRLF
<参数 1> CRLF
...
$<参数 N 的字节数量> CRLF
<参数 N> CRLF
```
依然以set hell world这条命令进行说明。
参数数量为3个，因此第一行为:
```
*3
```
参数字节数分别是355，因此后面几行为：
```
$3
SET
$5
hello
$5
world
```
有一点要注意的是，上面只是格式化显示的结果，实际传输格式为如下
代码
```
*3\r\n$3\r\nSET\r\n$5\r\nhello\r\n$5\r\nworld\r\n
```


* 返回结果格式

Redis的返回结果类型分为以下五种

状态回复：在RESP中第一个字节为"+"。
错误回复：在RESP中第一个字节为"-"。
整数回复：在RESP中第一个字节为"："。
字符串回复：在RESP中第一个字节为"$"。
多条字符串回复：在RESP中第一个字节为"*"。

![](./images/4-2.png)

为了看到Redis服务端返回的“真正”结果，可以使用nc命令、telnet命
令、甚至写一个socket程序进行模拟。

有了RESP提供的发送命令和返回结果的协议格式，各种编程语言就可
以利用其来实现相应的Redis客户端。


## 客户端管理

Redis提供了客户端相关API对其状态进行监控和管理。

* client list
此命令能列出与Redis服务端相连的所有客户端信息。
```
127.0.0.1:6379> client list
id=29722 addr=127.0.0.1:56976 fd=9 name= age=1943 idle=1547 flags=N db=0 sub=0 psub=0 multi=-1 qbuf=0 qbuf-free=0 obl=0 oll=0 omem=0 events=r cmd=unsubscribe
id=29724 addr=127.0.0.1:56980 fd=10 name= age=1534 idle=0 flags=N db=0 sub=0 psub=0 multi=-1 qbuf=26 qbuf-free=32742 obl=0 oll=0 omem=0 events=r cmd=client
```
输出结果的每一行代表一个客户端的信息，可以看到每行包含了十几个
属性，它们是每个客户端的一些执行状态
	* 标识：id、addr、fd、name
这四个属性属于客户端的标识
id：客户端连接唯一标识，这个id是随着Redis的连接自增的，重启Redis后会重置为0。
addr：客户端连接的IP和端口
fd：socket的文件描述符，与lsof命令结果中的fd是同一个，如果fd=-1
代表当前客户端不是外部客户端，而是Redis内部的伪装客户端。
name：客户端的名字，后面的client setName和client getName两个命令
会对其进行说明。
	* 输入缓冲区：qbuf、qbuf-free（总容量和剩余容量）
Redis为每个客户端分配了输入缓冲区，它的作用是将客户端发送的命
令临时保存，同时Redis从会输入缓冲区拉取命令并执行，输入缓冲区为客
户端发送命令到Redis执行命令提供了缓冲功能。
Redis没有提供相应的配置来规定每个缓冲区的大小，输入缓冲区会根据输
入内容大小的不同动态调整，只是要求每个客户端缓冲区的大小不能超过1G，
超过后客户端将被关闭。
	* 输出缓冲区：obl、oll、omem
Redis为每个客户端分配了输出缓冲区，它的作用是保存命令执行的结
果返回给客户端，为Redis和客户端交互返回结果提供缓冲。
输入缓冲区不同的是，输出缓冲区的容量可以通过参数client-output-
buffer-limit来进行设置，并且输出缓冲区做得更加细致，
按照客户端的不同分为三种：普通客户端、发布订阅客户端、slave客户端。
	* 客户端的存活状态：（age、idle）
client list中的age和idle分别代表当前客户端已经连接的时间和最近一次
的空闲时间。
	* 客户端类型（flags)
![](./images/4-4.png)

* client setName和 client getName
用来获取和设置客户端标识名称。

* client kill 
```client kill ip:port```，用于kill指定ip地址和商品的客户端。

* client pause 
```client pause timeout（毫秒）```，用于阻塞客户端timeout毫秒数。

* monitor
monitor命令用于监控Redis正在执行的命令。
注：每个客户端都有自己的
输出缓冲区，既然monitor能监听到所有的命令，一旦Redis的并发量过大，
monitor客户端的输出缓冲会暴涨，可能瞬间会占用大量内存



### 客户端的限制
 
Redis提供了maxclients参数来限制最大客户端连接数，一旦连接数超过
maxclients，新的连接将被拒绝。maxclients默认值是10000，可以通过info clients来查询当前Redis的连接数


可以通过config set maxclients对最大客户端连接数进行动态设置。
```
config get maxclients
config set maxclients num
```

Redis提供了timeout（单位为秒）参数来限制连接的最大空闲时间，防止
连接长期空闲不关闭连接。
比如：
```
config set timeout 30
```

### 客户端相关配置

* timeout：检测客户端空闲连接的超时时间，一旦idle时间达到了
timeout，客户端将会被关闭，如果设置为0就不进行检测。

* maxclients：客户端最大连接数

* tcp-keepalive：检测TCP连接活性的周期，默认值为0，也就是不进行
检测，如果需要设置，建议为60，那么Redis会每隔60秒对它创建的TCP连
接进行活性检测，防止大量死连接占用系统资源。

* tcp-backlog：TCP三次握手后，会将接受的连接放入队列中，tcp-
backlog就是队列的大小，它在Redis中的默认值是511。

### 客户端统计片段
```
127.0.0.1:6379> info clients
# Clients
connected_clients:1414
client_longest_output_list:0
client_biggest_input_buf:2097152
blocked_clients:0
```

* connected_clients：代表当前Redis节点的客户端连接数，需要重点监
控，一旦超过maxclients，新的客户端连接将被拒绝。

* client_longest_output_list：当前所有输出缓冲区中队列对象个数的最大值。

* client_biggest_input_buf：当前所有输入缓冲区中占用的最大容量。

* blocked_clients：正在执行阻塞命令（例如blpop、brpop、
brpoplpush）的客户端个数。

除此之外info stats中还包含了两个客户端相关的统计指标，如下：
```
127.0.0.1:6379> info stats
# Stats
total_connections_received:80
...
rejected_connections:0
```
* total_connections_received：Redis自启动以来处理的客户端连接数总数。
* rejected_connections：Redis自启动以来拒绝的客户端连接数，需要重点监控。
