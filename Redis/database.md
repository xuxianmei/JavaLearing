# 数据库管理

Redis提供了几个面向Redis数据库的操作，它们分别是dbsize、select、flushdb、flushall命令。



## select

* 功能
切换数据库

* 格式
```select dbindex```
Redis只是用数字作为多个数据库的实现。Redis默认配置中是有16个数据库（以0开始）。

注：假设databases=16，select0操作将切换到第一个数据库，select15选择最
后一个数据库，但是0号数据库和15号数据库之间的数据没有任何关联，甚
至可以存在相同的键

![](./images/2-30.png)

Redis3.0中已经逐渐弱化这个功能。
多数据库的使用方式，会让调试和运维不同业务的数据库变的困难，假如有一个慢查询存在，
依然会影响其他数据库，这样会使得别的业务方定位问题非常的困难。

如果要使用多个数据库功能，完全可以在一台机器上部署多个Redis实例，
彼此用端口来做区分，因为现代计算机或者服务器通常是有多个CPU的。
这样既保证了业务之间不会受到影响，又合理地使用了CPU资源。

## flushdb/flushall

flushdb/flushall命令用于清除数据库，两者的区别的是flushdb只清除当
前数据库，flushall会清除所有数据库。
