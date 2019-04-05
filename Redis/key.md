# 键管理

# 单个键管理

* rename和renamenx
	* 功能
键重命名
	* 格式
```
rename key newkey
renamenx key newkey
```
如果在rename之前，newkey已经存在，那么它的值也将被覆盖。
为了防止被强行rename，Redis提供了renamenx命令，确保只有newKey
不存在时候才被覆盖。
由于重命名键期间会执行del命令删除旧的键，如果键对应的值比较大，
会存在阻塞Redis的可能性，这点不要忽视

* randomkey
	* 功能
随机返回一个数据库中的键
	* 格式
```
randomkey
```

# 键过期相关命令
expire、ttl、expireat、pexpire、pexpireat、pttl、persist。
* expire
``expire key seconds``，设置键过期时间（秒），如果seconds为-1，会马上执行，相当于del。
注：


* pexpire
同上，毫秒级

* expireat
``expireat key timestamp``，设置键过期时间（秒级时间戳）

* pexpireat
同上，毫秒级

* ttl
``ttl key``，返回键过期剩余时间（秒），-1，键没有设置过期时间，-2键不存在。

* pttl
``pttl key``，返回键过期剩余时间（毫秒）

* persist
将键的过期时间清除

注：但无论是使用过期时间还是时间戳，秒级还是毫秒级，在Redis内部最终使用的都是pexpireat。	
expire key的键不存在，返回结果为0。
如果过期时间为负值，键会立即被删除。
对于字符串类型键，执行set命令会去掉过期时间，这个问题很容易在开发中被忽视。
Redis不支持二级数据结构（例如哈希、列表）内部元素的过期功
能，例如不能对列表类型的一个元素做过期时间设置。
setex命令作为set+expire的组合，不但是原子执行，同时减少了一次网络通讯的时间。


# 迁移键

迁移键功能非常重要，因为有时候我们只想把部分数据由一个Redis迁
移到另一个Redis（例如从生产环境迁移到测试环境），Redis发展历程中提
供了move、dump+restore、migrate三组迁移键的方法，它们的实现方式以及
使用的场景不太相同。

* move
	* 功能
move命令用于在Redis内部进行数据迁移，Redis内部可以有多个数据库。
	* 格式
```
move key db
```
![](./images/2-26.png)

* dump+restore
```
dump key
restore key ttl value
```
dump+restore可以实现在不同的Redis实例之间进行数据迁移的功能，整
个迁移的过程分为两步：
1）在源Redis上，dump命令会将键值序列化，格式采用的是RDB格式。
2）在目标Redis上，restore命令将上面序列化的值进行复原，其中ttl参
数代表过期时间，如果ttl=0代表没有过期时间。

![](./images/2-27.png)

有关dump+restore有两点需要注意：

第一，整个迁移过程并非原子性的，而是通过客户端分步完成的。

第二，迁移过程是开启了两个客户端连接，所以dump的结果不是在源Redis和目标Redis之间进行传输。


* migrate
```
migrate host port key|"" destination-db timeout [copy] [replace] [keys key [key ..
```
migrate命令也是用于在Redis实例间进行数据迁移的。
实际上migrate命令就是将dump、restore、del三个命令进行组合，从而简化了操作流程。
migrate命令具有原子性，而且从Redis3.0.6版本以后已经支持迁移多个键的功能，有效地提高了迁移效率。


整个过程如图2-28所示，实现过程和dump+restore基本类似，但是有3点不太相同：

第一，整个过程是原子执行的，不需要在多个Redis实例上开启客户端的，只需要在源Redis
上执行migrate命令即可。

第二，migrate命令的数据传输直接在源Redis和目标Redis上完成的。

第三，目标Redis完成restore后会发送OK给源Redis，源Redis接收后会根据migrate对应的
选项来决定是否在源Redis上删除对应的键。

参数说明：
host：目标Redis的IP地址。

port：目标Redis的端口。

key|""：在Redis3.0.6版本之前，migrate只支持迁移一个键，所以此处是
要迁移的键，但Redis3.0.6版本之后支持迁移多个键，如果当前需要迁移多
个键，此处为空字符串""

destination-db：目标Redis的数据库索引，例如要迁移到0号数据库，这里就写0。

timeout：迁移的超时时间（单位为毫秒）。

[copy]：如果添加此选项，迁移后并不删除源键。

[replace]：如果添加此选项，migrate不管目标Redis是否存在该键都会
正常迁移进行数据覆盖。

[keys key[key...]]：迁移多个键，例如要迁移key1、key2、key3，此处填
写“keys key1 key2 key3”。

# 遍历键

* keys

```
keys pattern
```

keys命令支持pattern匹配。
``*``：代表匹配任意字符。
·代表匹配一个字符。
[]代表匹配部分字符
\x用来做转义，例如要匹配星号、问号需要进行转义。

如果考虑到Redis的单线程架构就不那么美妙了，如果Redis包含了
大量的键，执行keys命令很可能会造成Redis阻塞，所以一般建议不要在生
产环境下使用keys命令

* scan
使用下面要介绍的scan命令渐进式的遍历所有键，可以有效防止阻塞。

和keys命令执行时会遍历所有键不同，scan采用渐进式遍历
的方式来解决keys命令可能带来的阻塞问题，每次scan命令的时间复杂度是
O（1），但是要真正实现keys的功能，需要执行多次scan。Redis存储键值对
实际使用的是hashtable的数据结构，其简化模型如图2-29所示

![](./images/2-29.png)

那么每次执行scan，可以想象成只扫描一个字典中的一部分键，直到将字典中的所有键遍历完毕。
match pattern是可选参数，它的作用的是做模式的匹配，这点和keys的
模式匹配很像

```
scan cursor [match pattern] [count number]
```
cursor是必需参数，实际上cursor是一个游标，第一次遍历从0开始，每
次scan遍历完都会返回当前游标的值，直到游标值为0，表示遍历结束。

count number是可选参数，它的作用是表明每次要遍历的键个数，默认
值是10，此参数可以适当增大。

现有一个Redis有26个键（英文26个字母），现在要遍历所有的键，使
用scan命令效果的操作如下。第一次执行scan0，返回结果分为两个部分：第
一个部分6就是下次scan需要的cursor，第二个部分是10个键
```
127.0.0.1:6379> scan 0
1) "6"
2) 1) "w"
2) "i"
3) "e"
4) "x"
5) "j"
6) "q"
7) "y"
8) "u"
9) "b"
10) "o"
```
使用新的cursor="6"，执行scan6：
```
127.0.0.1:6379> scan 6
1) "11"
2) 1) "h"
2) "n"
3) "m"
4) "t"
5) "c"
6) "d"
7) "g"
8) "p"
9) "z"
10) "a"
```
这次得到的cursor="11"，继续执行scan11得到结果cursor变为0，说明所
有的键已经被遍历过了：
```
127.0.0.1:6379> scan 11
1) "0"
2) 1) "s"
2) "f"
3) "r"
4) "v"
5) "k"
6) "l"
```

除了scan以外，Redis提供了面向哈希类型、集合类型、有序集合的扫
描遍历命令，解决诸如hgetall、smembers、zrange可能产生的阻塞问题，对
应的命令分别是hscan、sscan、zscan。

渐进式遍历可以有效的解决keys命令可能产生的阻塞问题，但是scan并
非完美无瑕，如果在scan的过程中如果有键的变化（增加、删除、修改），
那么遍历效果可能会碰到如下问题：新增的键可能没有遍历到，遍历出了重
复的键等情况，也就是说scan并不能保证完整的遍历出来所有的键，这些是
我们在开发时需要考虑的。