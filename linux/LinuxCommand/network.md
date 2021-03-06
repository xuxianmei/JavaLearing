### netstat

显示/proc/net当中的文件内容，通过网络子系统。
它会告诉你端口的状态是什么：开启、关闭、等待、masquerade connections，还会有一些其它信息（连接信息、路由表信息）。
* 用法

````
netstat [OPTION]
````

* 常用参数

````
-t：TCP连接
-u：UDP连接
-x：UNIX连接
-l：监听中连接
-s：统计信息
-p：PID及程序名称
-r：路由表
-i：数据包
-e：内核接口表
-g：多播组成员信息
-c：持续刷新
--verbos：不支持地址组
````
[netstat常见用法](commonUse/netstatExample.md)

### tcpdump
网络嗅探工具，能够获取网络接口上的所有数据包。
能够解析所有的internet协议。
* 用法

````
tcpdump [OPTION]
````
* 常用参数

````
-i interface: 监听指定网络接口
````



### ping

ping(Packet Internet Groper)命令是测试两个节点之间连接的最好的方式。
Ping使用ICMP(Internet Control Message Protocol)来与其它设备进行通信。

* 常见用法

````
ping 域名/目标IP
ping -c count 域名/目标IP
````

### hostname
设置和查看系统主机名称



###   traceroute
这是一个网络排查工具，跟踪数据包到达网络主机所经过的路由工具。
traceroute 的原理是试图以最小的TTL发出探测包来跟踪数据包到达目标主机所经过的网关，然后监听一个来自网关ICMP的应答。发送数据包的大小
默认为 38个字节。
* 用法

````
traceroute [OPTION]  目标主机/域名/IP地址
````
### tracepath

用来追踪并显示报文到达目的主机所经过的路由信息。

### nmap
端口扫描工具。

主机发现（Host Discovery）
端口扫描（Port Scanning）
版本侦测（Version Detection）
操作系统侦测（Operating System Detection）

## Network Configuration

### ifconfig


ifconfig（interface configurator)，主要用初始化接口，分配接口的IP地址，以及根据需要启用、禁用接口。
通过这个命令，可以查看分配给接口的IP地址及MAC地址，以及MTU（最大传输单元）。
### ifup

启用网络接口

### ifdown

禁用网络接口

### route

查询和设置ip路由表。
路由添加: route add
路由删除：route del

## Internet特定命令

### host
用来查询目标主机\域名的DNS记录及IP地址.
* 用法

````
host [OPTION] 域名/主机
```` 

* 常用参数


### dig
dig (domain information groper) query DNS related information like A Record, CNAME, MX Record etc.
* 用法


````
dig 域名
````

* 常用参数

````
-t CNAME/NS/MX/SOA ：查看路由资源记录
````
  

### whois
whois目录服务的服务端，用来查询域名的whois信息。
* 用法

````
whois [OPTION]  OBJECT
````


### wget

(GNU Web get) 非交互式（可以在后台运行）网站下载工具。
GNU Wget主要用来从Web上下载资源，支持HTTP、HTTPS、FTP协议。
* 用法

````
wget [OPTION]... [URL]...
````



### curl
curl是一个可以向远程服务发送和接收数据的工具。支持的协议，参考 man curl。

* 用法

````
curl [options] [URL...]
````

## Remote Administration Related

### ssh
一种加密的网络通讯协议。

### scp

scp是secure copy的简写，用于在Linux下进行远程拷贝文件的命令，和它类似的命令有cp，不过cp只是在本机进行拷贝不能跨服务器，而且scp传输是加密的。可能会稍微影响一下速度。当你服务器硬盘变为只读 read only system时，用scp可以帮你把文件移出来。
scp使用ssh协议来传输数据
* 用法
scp [Option] [Source] [destination]

* 常用参数
````
-1  强制scp命令使用协议ssh1  
-2  强制scp命令使用协议ssh2  
-4  强制scp命令只使用IPv4寻址  
-6  强制scp命令只使用IPv6寻址  
-B  使用批处理模式（传输过程中不询问传输口令或短语）  
-C  允许压缩。（将-C标志传递给ssh，从而打开压缩功能）  
-p 保留原文件的修改时间，访问时间和访问权限。  
-q  不显示传输进度条。  
-r  递归复制整个目录。  
-v 详细方式显示输出。scp和ssh(1)会显示出整个过程的调试信息。这些信息用于调试连接，验证和配置问题。   
-c cipher  以cipher将数据传输进行加密，这个选项将直接传递给ssh。   
-F ssh_config  指定一个替代的ssh配置文件，此参数直接传递给ssh。  
-i identity_file  从指定文件中读取传输时使用的密钥文件，此参数直接传递给ssh。    
-l limit  限定用户所能使用的带宽，以Kbit/s为单位。     
-o ssh_option  如果习惯于使用ssh_config(5)中的参数传递方式，   
-P port  注意是大写的P, port是指定数据传输用到的端口号   
-S program  指定加密传输时所使用的程序。此程序必须能够理解ssh(1)的选项。
````

### sftp

sftp与scp一样，都是基于ssh协议，sftp与scp最大的区别就在于，sftp支持断点续传。

## iproute2工具包

主要用来替代net=tools(如ifconifg route arp netstat命令 )。
iproute2主要基于ip命令。

[https://linux.cn/article-4326-1.html](https://linux.cn/article-4326-1.html)
[http://www.linuxdiyf.com/linux/23935.html](http://www.linuxdiyf.com/linux/23935.html)
[http://www.iamle.com/archives/1750.html](http://www.iamle.com/archives/1750.html)