

## 环境变量

bash shell用一个叫作环境变量(enviroment variable)的特性来存储有关shell会话和工作环境  
的信息（这也是它们被称作环境变量的原因），这项特性允许你在内存中存储数据，这也是存储持久  
数据的一种简便方法。

在bash shell中，环境变量分为两类：

* 全局变量

全局环境变量对于shell会话和所有生成的子shell都是可见的。退出此shell，此变量消失。
Linux系统在你开始bash会话时就设置了一些全局环境变量（也叫系统环境变量）。
系统环境变量基本上都是使用全大写字母，以区别普通用户的环境变量。

* 局部变量（也叫自定义变量）

局部变量则只对创建它的shell可见。



### 全局环境变量

**全局环境变量对于shell会话和所有生成的子shell都是可见的。** 

如要使此变量变为：待加
*  查看全局变量	 

相关命令：env、printenv  

* 查看指定环境变量

````
printenv variable
echo $variable
```` 
比如
````
printenv HOME
echo $HOME
````





### 局部环境变量(用户自定义变量)

局部变量则只对创建它的shell可见。退出此shell，此变量被移除。

相关命令：set 
* 查看所有变量

````
set
````
set会同时显示全局及局部变量。

* 定义局部变量

````
变量名=值      如果值里包含空格，需要使用双引号。注：变量号、等号、值之间没有空格。
````

* 导出到全局环境中
  
相关命令：export

````
export 变量名
````

* 删除环境变量

相关命令：unset

````
unset 变量名
````

如果删除的是全局环境变量，且在子shell中删除，回到父shell当中，此环境变量依然存在。

**注：
变量中$的使用，在涉及环境变量名时，
如果要用到变量，使用$；
如果要操作变量，不使用$。
这条规则的一个例外就是使用printenv显示某个变量的值，不需要加$。**



### 默认的Shell环境变量

待加


### PATH环境变量

当你在shell命令行界面中输入一个外部命令，shell必须搜索系统来找到对应的程序。  
PATH环境变量定义了用于进行命令和程序查找的目录。
PATH中的目录使用冒号分隔。

如果命令或程序的位置没有包括在PATH变量中，那么不指定路径（相对或者绝对），shell是没法  
找到的。


* 添加新目录

````
PATH=$PATH:需要添加的路径
````

注：对PATH变量的修改只能持续到退出或重启系统。



### 定位系统环境变量

这里主要介绍如何让环境变量的作用持久化。


在你登入Linux系统启动一个bash shell时，默认情况下bash会在几个文件中查找命令。  
这些文件叫作启动文件或环境文件。  
bash检查的启动文件取决于你启动bash shell的方式。
启动bash shell有3种方式：

* 登录时作为默认登录shell
* 作为非登录shell的交互式shell
* 作为运行脚本的非交互shell

#### 登录shell
以base shell为例。 登录bash shell会从5个不同的启动文件里读取命令：

* /etc/profile(这个文件，一般会执行/etc/bash.bashrc及/etc/profile.d/*.sh,具体看文件内容而定)
* $HOME/.bash_profile
* $HOME/.bashrc
* $HOME/.bash_login
* $HOME/.profile

1. /etc/profile  
``/etc/profile``文件是系统上默认的bash shell的主启动文件。系统上的每个用户登录bash shell时，都会执行这个启动文件。
2. $HOME目录下的启动文件
 * $HOME/.bash_profile
 * $HOME/.bashrc
 * $HOME/.bash_login
 * $HOME/.profile

针对不同的用户，根据个人需求定制。提供一个用户专属的启动文件来定义该用户所用到的环境变量。
它们位于用户的HOEM目录下，所以每个用户都可以编辑这些文件并添加自己的环境变量，  
这些环境变量会在每次启动bash shell会话时生效。

bash shell会按照以下顺序，运行第一个被找到的文件 ，余下的则被忽略。

``$HOME/.bash_profile``
``$HOME/.bash_login``
``$HOME/.profile``

注：$HOME/.bashrc不在此列表中，因为该文件通常通过其他文件运行。

#### 交互式shell进程
  
如果你的bash shel不是登录系统时启动的（比如在命令行提示符下敲入bash时启动），那么你  
启动的shell叫作交互式shell。

如果bash是作为交互式shell启动的，它就不会访问/etc/profile文件，只会检查用户HOME目录中的.bashrc文件。

#### 非交互式shell

非交互式shell，系统执行脚本时用的就是这种shell。不同的地方在于它没有命令行提示符。

支持在系统上运行脚本时，运行一些特定启动的命令，bash shell提供了BASH_ENV环境变量  
来设置需要执行的启动文件。


#### 环境变量持久化


1. 全局变量
通过修改/etc/profile文件，来添加新全局变量和修改。

在/etc/profile.d目录创建一个.sh结尾的文件，把所有新的或修改过的全局环境变量设置  
在这个文件中

2. 用户个性配置

最好的解决方案就是$HOME/.bashrc文件。

3. 非交互式的

放在BASH_ENV指定的文件中。
 


### 数组变量

````
mytest=(one two three four five) 用空格分开多个值

echo mytest  部分shell中可用

echo ${mytest[*]}  显示所有值

echo ${mytest[1]} 指定索引上的值（从0开始）

unset mytest[2] 删除指定位置上的值

unset mytest 删除整个数量变量
````