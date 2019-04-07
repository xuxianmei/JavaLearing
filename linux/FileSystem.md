使用Linux系统时，需要作出的决策之一就是为存储设备选用什么文件系统。
Linux的文件系统为我们在硬盘中存储的0和1，和应用中使用的文件与目录之间搭建起了一座桥梁。  


## VFS
为了支持各种实际文件系统，VFS定义了文件系统都支持的基本、概念上的接口和数据。  
一个实际的文件系统想要被Linux支持，就必须提供一个符合VFS标准上的接口。

### VFS 数据结构

#### super block

存储一个已安装的文件系统的控制信息，代表一个已安装的文件系统。

#### 索引节点
存储了文件的相关信息，代表存储设备上的一个实际的物理文件。

#### 目录项
引入目录项的概念主要是出于方便查找文件的目的。
一个路径的各个组成部分，不管是目录还是普通的文件，都是一个目录项对象。
如，在路径/home/source/test.c中，目录 /, home, source和文件 test.c都对应一个目录项对象。VFS在遍历路径名的过程中现场将它们逐个地解析成目录项对象。


## ext 文件系统
Linux操作系统中引入的最早的文件系统叫作扩展文件系统(extended filesystem，简称为ext)。
它为Linux提供了一个基本的类Unix文件系统：使用虚拟目录来操作硬件设备，在物理设备上  
按定长的块来存储数据。

ext文件系统采用名为**索引节点**的系统来存放虚拟目录中所存储文件的信息。  
索引节点系统在每个物理设备中创建一个单独的表（称为索引节点表）来存储这些文件的信息。  
存储在虚拟目录中的每一个文件在索引节点表中都有一个条目。
ext文件系统名称中的extended部分来自其跟踪的每个文件的额外数据，包括：
* 文件名
* 文件大小
* 文件的属主
* 文件的属组
* 文件的访问权限
* 指向存有文件数据的每个硬盘块的指针
* 其它
后续升级有ex2、ex3、ex4，增加了日志，成为日志文件系统，为Linux系统增加了一层安全性。


## ex2文件系统的主要信息

inode的内容在记录文件的权限与相关属性，至于 block 区块则是在记录文件的实际内容。 而且文件系统一开始就将 inode 与 block 规划好了，除非重新格式化（或者利用 resize2fs 等指令变更文件系统大小），否则 inode 与 block 固定后就不再变动。但是如果仔细考虑一下，如果我的文件系统高达数百GB时， 那么将所有的 inode 与 block 通通放置在一起将是很不智的决定，因为 inode 与 block 的数量太庞大，不容易管理。
为此之故，因此 Ext2 文件系统在格式化的时候基本上是区分为多个区块群组 （block group） 的，每个区块群组都有独立的 inode/block/superblock 系统。感觉上就好像我们在当兵时，一个营里面有分成数个连，每个连有自己的联络系统， 但最终都向营部回报连上最正确的信息一般！这样分成一群群的比较好管理啦！整个来说，Ext2 格式化后有点像下面这样：
![](images/ext2_filesystem.jpg)


### 启动扇区(Boot Sector)
文件系统最前面有一个开机扇区（boot sector），这个开机扇区可以安装开机管理程序， 这是个非常重要的设计，因为如此一来我们就能够将不同的开机管理程序安装到个别的文件系统最前端，而不用覆盖整颗磁盘唯一的MBR， 这样也才能够制作出多重开机的环境啊


每一个Block Group（数据区块组）包含：
#### Super Block
Superblock 是记录整个 filesystem 相关信息的地方， 没有 Superblock ，就没有这个 filesystem 了。
#### Inode Table
Inode表，每个Inode记录了文件的权限和属性。

#### Data Block
实际存储文件内容的区块

#### File System Description

这个区段可以描述每个 block group 的开始与结束的 block 号码，以及说明每个区段 （superblock, bitmap, inodemap, data block） 分别介于哪一个 block 号码之间。这部份也能够用 dumpe2fs 来观察的。


#### Block Bitmap(区块对照表)
记录block的使用状态（已用，可用）。

#### Inode Bitmap(Inode对照表)
记录已使用及未使用的inode号。


## inode/block及Fat文件系统数据存取示意图
* inode/block 数据存取示意图
![](images/filesystem-1.jpg)
* FAT文件系统数据存取示意图
![](images/filesystem-2.jpg)


## 写时复制机制 就是编程界“懒惰行为”
https://www.cnblogs.com/lengender-12/p/7054896.html
参考：

[鸟哥](https://wizardforcel.gitbooks.io/vbird-linux-basic-4e/content/59.html)


