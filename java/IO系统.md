**TODO：查看当前新IO的操作模式**

创建一个好的输入/输出（I/O）系统是一项艰难的任务。

不仅存在各种I/O源端和想要与之通信的接收端（文件、控制台、网络链接等），而且还需要以多种  
不同的方式与它进行通信（顺序、随机存取、缓冲、二进制、按字符、按行、按字节等）

自从Java 1.0版本以来，Java的I/O库发生了明显改变，在原来面向字节的类中添加了面向字符  
和基于Unicode的类。  
在JDK 1.4中，添加了nio类（新I/O）添加进来是为了改进性能及功能。

# File类

File类既能代表一个特定文件的名称，又能代表一个目录下的一组文件的名称。
也可以用File对象来创建新的目录或尚不存在的整个目录路径。
还可以查看文件的特性（如：大小、最后修改日期、读、写），删除文件，检查File对象代表  
的是一个文件还是一个目录。

# 输入与输出

编程语言的I/O类库中常使用流这个抽象概念，它代表任何有能力产出数据的数据源对象或者是  
有能力接收数据的接收端对象。  
”流“屏蔽了实际的I/O设备中处理数据的细节。

Java类库中的I/O分成输入和输出两部分。
通过继承，

任何自InputStream或Reader派生而来的类都含有名为read()的基本方法，用于读取  
单个字节或者字节数组。

任何自OutputStream或Writer派生而来的类都含有名为write()的基本方法，用于写单个  
字节或字节数组。

通常不会用到这些方法，它们之所以存在，是因为别的类可以使用它们，以便提供更有用的接口。
Java 1.0 中，类库的设计得首先限定与输入有关的所有类都应该从InputStream继承，与输出有关  
的所有类都应该从OutputStream继承
## InputStream类型

InputStream的作用是用来表示那些从不同数据源产生输入的类。  
这些数据包括：
1. 字节数据
2. String对象
3. 文件
4. 管道
5. 一个由其他种类的流组成的序列
6. 其他数据源，比如Internet连接等。
AudioInputStream, ByteArrayInputStream, FileInputStream, FilterInputStream, InputStream, ObjectInputStream, PipedInputStream, SequenceInputStream, StringBufferInputStream


## OutputStream类型
表示输入所要去往的目标：字节数组、文件、管道
ByteArrayOutputStream, FileOutputStream, FilterOutputStream, ObjectOutputStream, OutputStream, PipedOutputStream

#Reader和Writer

提供兼容unicode与面向字符的I/O功能。

#自我独立的类：RandomAccessFile
适用于由大小已知的记录组成的文件。