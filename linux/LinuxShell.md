 ## Shell
shell 就是一个程序，它接受从键盘输入的命令， 然后把命令传递给操作系统去执行。  

从用户的角度来看，Shell是用户与Linux操作系统沟通的桥梁。用户既可以输入命令执行，又可以利用 Shell脚本编程，完成更加复杂的操作。

## Shell类型

Linux的Shell种类众多。
常见的有：Bourne Shell（/usr/bin/sh或/bin/sh）、Bourne Again Shell（/bin/bash）、C Shell（/usr/bin/csh）、
K Shell（/usr/bin/ksh）、Shell for Root（/sbin/sh），等等。
不同的Shell语言的语法有所不同，所以不能交换使用。每种Shell都有其特色之处，基本上，掌握其中任何一种就足够了。

大多数Linux发行版的默认Shell都是GNU bash Shell(Bourne Again Shell)

## 用户使用的Shell

系统启动什么样的Shell程序，取决你个人的用户ID配置。  
在/etc/passwd文件中，在用户ID记录的第7个记录中列出了默认的Shell程序。  
只要用户登录到某个虚拟控制台终端或GUI中启动终端仿真器，默认的Shell就会开始运行。
这是用户的默认交互Shell。

还有另外一种默认shell是/bin/sh，它作为默认的系统shell，用于那些需要在启动时使用的  
系统Shell脚本。

在有些发行版上，默认的系统shell和默认的交互shell并不相同。

## Shell终端

### 控制台终端
进入CLI（comman line interface）的一种方法是让Linux系统退出图形化桌面模式，进入文本模式。
Linux系统启动后，它会自动创建一些虚拟控制台。虚拟控制台是运行在Linux系统内存中的终端会话。
``Ctrl+Alt+(F1-F7)``组合键进入

### 图形化终端
除了虚拟化终端控制台，还可以使用Linux图片桌面环境中的终端终端仿真包（器）。  
终端仿真包会在一个桌面图形化窗口中模块控制台终端的使用。