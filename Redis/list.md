# 列表（list）

列表类型是用来存储多个有序的字符串，列表中的每个字符串称为
元素（element)，一个列表最多可以存储2的32次方-1个元素。
在Redis中，可以对列表在两端随意插入（push）和弹出（pop）元素,
还可以获取指定范围的元素列表、获取指定索引下标的元素等。

列表是一种比较灵活的数据结构，它可以充当栈和队列的角色。

![](./images/2-18.png)

![](./images/2-19.png)

## 列表的特点

* 列表中的元素是有序的
这就意味着可以通过索引下标（从0开始）获取某个元素或者某个范围内的元素列表。

* 列表中的元素可以是重复的

# 命令

|操作类型|命令|
|---|---|
|添加元素| rpush lpush linsert|
|获取元素|lrange lindex llen|
|删除元素|lpop rpop lrem ltrim |
|修改元素|lset|
|阻塞删除元素|blpop brpop|

* lpush 
	* 功能
从队列左端入元素

	* 格式
```
rpush key value [value ...]
```

	* 示例
```
rpush user:ids 1 2 3 4 5
```

* rpush
	* 功能
与lpush类似，只不过从右端插入元素。

* linsert
	* 功能
向某个元素前或后插入元素,linsert命令会从列表中找到等于pivot的元素，在其前（before）或者后
（after）插入一个新的元素value
	* 格式
```
linsert key BEFORE|AFTER pivot value
```
	* 示例
```
linsert user:ids after 3 3.1
```

* lrange
	* 功能
获取指定范围内的元素列表,索引下标有两个特点：
第一，索引下标从左到右分别是0到N-1，但是从右到左分别是-1到-N。
第二，lrange中的end选项包含了自身
	* 格式 
```
lrange key start stop
```
	* 示例
```
lrange  user:ids  0 -2
```

* lindex
	* 功能
获取列表指定索引下标的元素
	* 格式
```
lindex key index
```
	* 示例
```
lindex user:ids -3
```

* llen
	* 功能
获取列表长度
	* 示例
```
llen key
```

* lpop
	* 功能
从列表左侧弹出元素
	* 格式
```
lpop key
```

* rpop
	* 功能
从列表右侧弹出元素
	* 格式
```
rpop key
```

* lrem
	* 功能
删除指定元素，lrem命令会从列表中找到等于value的元素进行删除，
根据count的不同分为三种情况：
count>0，从左到右，删除最多count个元素。
count<0，从右到左，删除最多count绝对值个元素。
count=0，删除所有。
	* 格式
```
lrem key count value
```

* ltrim
	* 功能
按照索引范围修剪列表
	* 格式
```
ltrim key start end
```

* lset
	* 功能
修改指定索引下标的元素
	* 格式
```
lset key index newValue
```

* blpop和brpop
	* 功能
blpop和brpop是lpop和rpop的阻塞版本，阻塞弹出第一个非空列表的头元素。（如果所有key指定的列表都为空，会阻塞当前连接，直到有列表不为空，或超时）。
	* 格式
```
blpop key [key ...] timeout
brpop key [key ...] timeout
```
timeout为超时时间（秒）。
	* 示例
```
brpop user:ids 3
```

# 内部编码

列表类型的内部编码有两种。

* ziplist（压缩列表）
当列表的元素个数小于list-max-ziplist-entries配置（默认512个），
同时列表中每个元素的值都小于list-max-ziplist-value配置时（默认64字节），
Redis会选用ziplist来作为列表的内部实现来减少内存的使
用
* linkedlist（链表）
当列表类型无法满足ziplist的条件时，Redis会使用
linkedlist作为列表的内部实现
* quicklist

# 使用场景

* 消息队列（理论上来说是生产者-消息者（1-1））

Redis的lpush+brpop命令组合即可实现阻塞队列，生产
者客户端使用lrpush从列表左侧插入元素，多个消费者客户端使用brpop命令阻塞式的“抢”列表尾部的元素，
多个客户端保证了消费的负载均衡和高可用性。

![](./images/2-21.png)

其他常用实现：
lpush+lpop=Stack（栈）
lpush+rpop=Queue（队列）
lpsh+ltrim=Capped Collection（有限集合）
lpush+brpop=Message Queue（消息队列）



