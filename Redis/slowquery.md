# 慢查询分析
许多存储系统（例如MySQL）提供慢查询日志帮助开发和运维人员定
位系统存在的慢操作。

所谓慢查询日志就是系统在命令执行前后计算每条命
令的执行时间，当超过预设阀值，就将这条命令的相关信息（例如：发生时
间，耗时，命令的详细信息）记录下来，Redis也提供了类似的功能。
Redis客户端执行一条命令分为如下4个部分：

![](./images/2-31.png)

1. 发送命令

2. 命令排队

3. 命令执行

4. 返回结果 

注意：慢查询只统计步骤3的时间。

## 慢查询的两个配置参数

* 预设阀值设置

* 慢查询记录存储位置

Redis提供了showlog-log-slower-than和showlog-max-len配置来解决。

* showlog-log-slower-than
设置预设阀值，单位为微秒（1秒=1000毫秒=1000000微秒），默认10000。
如果某项命令的执行时间超过了10000微秒，那么它将被记录在慢查询日志中。
设置为0会记录所有的命令

* slowlog-max-len
Redis使用了一个列表来存储慢查询日
志，slowlog-max-len就是列表的最大长度。

在Redis中有两种修改配置的方法，一种是修改配置文件，另一种是使
用config set命令动态修改。
例如下面使用config set命令将slowlog-log-slower-
than设置为20000微秒，slowlog-max-len设置为1000：

```
config set slowlog-log-slower-than 20000
config set slowlog-max-len 1000
config rewrite
```
![](./images/2-32.png)

虽然慢查询日志是存放在Redis内存列表中的，但是Redis并没有暴露这
个列表的键，而是通过一组命令来实现对慢查询日志的访问和管理

```
slowlog get [n]
slowlog len  
slowlog reset
```

注：慢查询日志是一个先进先出的队列，也就是说如果慢查询比较多的情况下，
可能会丢失部分慢查询命令，为了防止这种情况发生，可以定期
执行slow get命令将慢查询日志持久化到其他存储中（例如MySQL）