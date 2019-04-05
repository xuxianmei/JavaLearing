# HyperLogLog

HyperLogLog并不是一种新的数据结构（实际类型为字符串类型），而是一种基数算法。
通过HyperLogLog可以利用极小的内存空间完成独立总数的统计，数据集可以是IP、Email、ID等。


hyperLogLog提供了3个命令
---
pfadd、pfcount、pfmerge。
例如2016-03-06的访问用户是uuid-1、uuid-2、uuid-3、uuid-4，
2016-03-05的访问用户是uuid-4、uuid-5、uuid-6、uuid-7，

如图3-15所示。

![](./images/3-15.png)

HyperLogLog内存占用量小得惊人，但是用如此小空间来估
算如此巨大的数据，必然不是100%的正确，其中一定存在误差率。Redis官
方给出的数字是0.81%的失误率。

结构选型时只需要确认如下两条即可：

* 只为了计算独立总数，不需要获取单条数据。

* 可以容忍一定误差率，毕竟HyperLogLog在内存的占用量上有很大的优
势。