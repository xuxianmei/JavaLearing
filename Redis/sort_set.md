# 有序集合（sorted set)

它保留了集合内不能有重复成员的特性，但不同的是，有序集合中的成员可以排序。

但是，它不同列表使用索引下标作为排序依据。
sorted set使用一个score来作为排序的依据。
注：有序集合中的成员不能重复，但是score可以重复

![](./images/2-24.jpg)

# 特点
* 不能有重复成员
* 成员有序
使用score作为排序依据

# 命令

## 集合内操作

* zadd
	* 功能
添加成员
	* 格式 
```
zadd key [NX|XX] [CH] [INCR] score member [score member ...]
```
有关zadd命令有两点需要注意：
有nx、xx、ch、incr四个选项：
nx：member必须不存在，才可以设置成功，用于添加。
xx：member必须存在，才可以设置成功，用于更新。
ch：返回此次操作后，有序集合元素和分数发生变化的个数
incr：对score做增加，相当于后面介绍的zincrby。

有序集合相比集合提供了排序字段，但是也产生了代价，zadd的时间
复杂度为O（log（n）），sadd的时间复杂度为O（1）。
	* 示例
```
zadd rank nx 100 a
```

* zcard
	* 功能
计算成员个数
	* 格式
```
zcard key
```

* zscore
	* 功能
计算某个成员的score
	* 格式
``` 
zscore key member
```

* zrank与zrevrank
	* 功能
计算成员的排名（zrank从低————>高，zrevrank从高————>低），排名从0开始
	* 格式
```
zrank key member
zrevrank key member
```

* zrem
	* 功能
删除成员
	* 格式
```
zrem key member [member ...]
```

* zincrby 
	* 功能
增加成员的score
	* 格式
```
zincrby key increment member
```

* zrange和zrevrange
	* 功能
返回指定排名范围的成员(zrange，从低到高，zrevrange从高到低)
	* 格式
```
zrange key start stop [WITHSCORES]
zrevrange key start stop [WITHSCORES]
```

* zrangebyscore和zrevrangebyscore
	* 功能
返回指定score范围的成员
	* 格式
```
zrangebyscore key min max [WITHSCORES] [LIMIT offset count]
zrevrangebyscore key min max [WITHSCORES] [LIMIT offset count]
```
其中zrangebyscore按照分数从低到高返回，zrevrangebyscore反之。例如
下面操作从低到高返回200到221分的成员，withscores选项会同时返回每个
成员的分数。[limit offset count]选项可以限制输出的起始位置和个数。
同时min和max还支持开区间（小括号）和闭区间（中括号），-inf和
+inf分别代表无限小和无限大

* zcount
	* 功能
返回指定score范围成员个数
	* 格式
```
zcount key min max
```

* zremrangebyrank
	* 功能
删除指定排名内的成员
	* 格式
```
zremrangebyrank key start stop
```
start stop为低到高


* zremrangebyscore
	* 功能
删除指定score范围的成员
	* 格式
```
zremrangebyscore key min max
```
min和max还支持开区间（小括号）和闭区间（中括号），-inf和
+inf分别代表无限小和无限大


## 集合间操作

* zinterstore
	* 功能

	* 格式
```
zinterstore destination numkeys key [key ...] [weights weight [weight ...]]
```
参数说明：
destination：交集计算结果保存到这个键
numkeys：需要做交集计算键的个数。
key[key...]：需要做交集计算的键。
weights weight[weight...]：每个键的权重，在做交集计算时，每个键中
的每个member会将自己分数乘以这个权重，每个键的权重默认是1。
aggregate sum|min|max：计算成员交集后，score可以按照sum（和）、
min（最小值）、max（最大值）做汇总，默认值是sum

* zunionstore
	* 功能
并集

* zdiffstore
	* 功能
差集

# 内部编码
有序集合类型的内部编码有两种：
* ziplist（压缩列表）
当有序集合的元素个数小于zset-max-ziplist-entries配置（默认128个），
同时每个元素的值都小于zset-max-ziplist-value配置（默认64字节）时，
Redis会用ziplist来作为有序集合的内部实现，ziplist可以有效减少内存的使用。

* skiplist（跳跃表）
当ziplist条件不满足时，有序集合会使用skiplist作为内部实现，
因为此时ziplist的读写效率会下降。

# 使用场景
有序集合比较典型的使用场景就是排行榜系统。例如视频网站需要对用
户上传的视频做排行榜，榜单的维度可能是多个方面的：按照时间、按照播
放数量、按照获得的赞数。

