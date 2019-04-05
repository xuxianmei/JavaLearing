# Pipeline概念

Redis客户端执行一条命令分为如下四个过程：
1. 发送命令
2. 命令排队
3. 命令执行
4. 返回结果

1-4 称为Round Trip Time(RTT，往返时间)。

Redis提供了批量操作命令（例如mget、mset等），有效地节约RTT。
但大部分命令是不支持批量操作的，例如要执行n次hgetall命令，并没有
mhgetall命令存在，需要消耗n次RTT。

Pipeline（流水线）机制能改善上面这类问题，它能将一组Redis命令进
行组装，通过一次RTT传输给Redis，再将这组Redis命令的执行结果按顺序
返回给客户端，图3-5为没有使用Pipeline执行了n条命令，整个过程需要n次
RTT。

![](./images/3-5.png)

使用Pipeline执行n条命令模型：

![](./images/3-6.png)


客户端和服务端的网络延时越大，Pipeline的效果越明显。
注： Pipeline只能操作一个Redis实例

# 原生批量处理命令与Pipeline对比

可以使用Pipeline模拟出批量操作的效果。
但是它与原生批量命令还有如下区别：
* 原生批量命令是原子的,Pipeline是非原子的。
* 原生批量命令是一个命令对应多个key，Pipeline支持多个命令。
* 原生批量命令是Redis服务端支持实现的，而Pipeline需要服务端和客户
端的共同实现。