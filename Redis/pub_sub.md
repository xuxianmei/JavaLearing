
# 发布订阅（Pub/Sub）

Redis提供了基于“发布/订阅”模式的消息机制，此种模式下，消息发布
者和订阅者不进行直接通信，发布者客户端向指定的频道（channel）发布消
息，订阅该频道的每个客户端都可以收到该消息，如图3-16所示。Redis提
供了若干命令支持该功能，在实际应用开发时，能够为此类问题提供实现方
法。

![](./images/3-16.png)

# 命令

* publish
	* 功能
发布消息
	* 格式
```
publish channel message
```
	* 示例
```
publish channel:sports "Hello World!"
```

* subscribe
	* 功能
订阅消息
	* 格式
```
subscribe channel [channel ...]
```
	* 示例
```
subscribe channel:sports
```
注：客户端在执行订阅命令之后进入了订阅状态，
只能接收subscribe、psubscribe、unsubscribe、punsubscribe的四个命令。
新开启的订阅客户端，无法收到该频道之前的消息，因为Redis不会对
发布的消息进行持久化。

* unsubscribe
	* 功能
取消订阅
	* 格式
```
unsubscribe [channel [channel ...]]
```

* psubscribe和punsubscribe
	* 功能
按照模式订阅和取消订阅
	* 格式
```
psubscribe pattern [pattern...]
punsubscribe [pattern [pattern ...]]
```

* pubsub
	* 格式
```
pubsub channels [pattern] 查看活跃的频道
pubsub numsub [channel ...] 查看频道订阅数
pubsub numpat 查看模式订阅数
```
所谓活跃的频道是指当前频道至少有一个订阅者.