# Redis Shell
Redis提供了redis-cli、redis-server、redis-benchmark等Shell工具。

# redis-cli
要了解redis-cli的全部参数，可以执行redis-cli-help命令来进行查看。

重要参数：

* -r
-r（repeat）选项代表将命令执行多次，如：
``redis-cli -r 3 ping``

* -i
-i（interval）选项代表每隔几秒执行一次命令，-i必须和-r一起使用，才能实现间隔
多次执行命令。如：
``redis-cli -r 5 -i 1 ping``

* -x 

-x选项代表从标准输入（stdin）读取数据作为redis-cli的最后一个参
数。如：
``echo "world" | redis-cli -x set hello``

* -c
-c（cluster）选项是连接Redis Cluster节点时需要使用的，-c选项可以防
止moved和ask异常。

* -a 
如果Redis配置了密码，可以用-a（auth）选项，有了这个选项就不需要
手动输入auth命令

* --scan和--pattern
--scan选项和--pattern选项用于扫描指定模式的键，相当于使用scan命令。

* --slave
--slave选项是把当前客户端模拟成当前Redis节点的从节点，可以用来
获取当前Redis节点的更新操作，合理的利用这个选项可以记录当前连接Redis节点的一些更新操作，这
些更新操作很可能是实际开发业务时需要的数据。

* --rdb
--rdb选项会请求Redis实例生成并发送RDB持久化文件，保存在本地。
可使用它做持久化文件的定期备份。

* --pipe
--pipe选项用于将命令封装成Redis通信协议定义的数据格式，批量发送
给Redis执行。
如：同时执行了set hello world和incr counter两条命令
echo -en '*3\r\n$3\r\nSET\r\n$5\r\nhello\r\n$5\r\nworld\r\n*2\r\n$4\r\nincr\r\
n$7\r\ncounter\r\n' | redis-cli --pipe

* --bigkeys
--bigkeys选项使用scan命令对Redis的键进行采样，从中找到内存占用比
较大的键值，这些键可能是系统的瓶颈。

* --eval
--eval选项用于执行指定Lua脚本

* --latency
latency有三个选项，分别是--latency、--latency-history、--latency-dist。
它们都可以检测网络延迟。
--latency：该选项可以测试客户端到目标Redis的网络延迟
```redis-cli -h {machineB} --latency```
--latency-history：--latency的执行结果只有一条，如果想以分时段的形式了解延迟信息，
可以使用--latency-history选项
----latency-dist：该选项会使用统计图表的形式从控制台输出延迟统计信息。

* --stat
--stat选项可以实时获取Redis的重要统计信息

* --raw和--no-raw
--no-raw选项是要求命令的返回结果必须是原始的格式，
--raw恰恰相反，返回格式化后的结果。

## redis-server
 
redis-server除了启动Redis外，还有一个--test-memory选项。redis-server-
-test-memory可以用来检测当前操作系统能否稳定地分配指定容量的内存给
Redis，通过这种检测可以有效避免因为内存问题造成Redis崩溃，例如下面
操作检测当前操作系统能否提供1G的内存给Redis：
```redis-server --test-memory 1024```
通常无需每次开启Redis实例时都执行--test-memory选项，该功能更偏向
于调试和测试，例如，想快速占满机器内存做一些极端条件的测试，这个功
能是一个不错的选择。

## redis-benchmark

redis-benchmark可以为Redis做基准性能测试，它提供了很多选项帮助开
发和运维人员测试Redis的相关性能

* -c
-c（clients）选项代表了客户端的并发数量（默认是50）

* -n<requests>
-n（num）代表客户端请求问题（默认100000）。

比如：edis-benchmark-c100-n20000代表100各个客户端同时请求Redis，一
共执行20000次。redis-benchmark会对各类数据结构的命令进行测试，并给
出性能指标。


* -q 
-q选项仅仅显示redis-benchmark的requests per second信息

* -r

一个空的Redis上执行了redis-benchmark会发现只有3个键。
如果想向Redis插入更多的键，可以执行使用-r（random）选项，可以向
Redis插入更多随机的键。
```
redis-benchmark -c 100 -n 20000 -r 10000
```
-r选项会在key、counter键上加一个12位的后缀，-r10000代表只对后四
位做随机处理（-r不是随机数的个数）。

* -P
-P选项代表每个请求pipeline的数据量（默认为1）。

*  -k<boolean>
-k选项代表客户端是否使用keepalive，1为使用，0为不使用，默认值为
1。

* -t
-t选项可以对指定命令进行基准测试。
```
redis-benchmark -t get,set -q
SET: 98619.32 requests per second
GET: 97560.98 requests per second
```
* --csv
--csv选项会将结果按照csv格式输出，便于后续处理，如导出到Excel
等