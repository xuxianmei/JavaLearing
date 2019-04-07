
# 参数

向shell脚本传递数据的最基本方法是使用命令行参数。  
命令行参数允许在运行脚本时向命令行添加数据。  
比如：

````
./addem 10 30 //10和30就是传递的两个命令行参数
````
## 读取参数

bash shell会将一些称为位置参数的特殊变量分配给输入到命令行中的所有参数。  
这也包括shell所执行的脚本名称。  
位置参数变量是标准的数字：$0是程序名,$1是第一个参数，$2是第二个参数，一直递增。

注：当大于9以后，需要使用${10}、${11}这种表示。
在使用前，最好测试对应的参数是否存在。



## 特殊参数变量

在bash shell中有些特殊变量，它们会记录命令行参数。

### 参数统计

* $#

脚本运行时携带的命令行参数的个数。
可以在脚本中任何地方使用这个特殊变量，就像普通的变量一样。


* ${!#}

代表最后一个命令行参数变量

###　所有参数 

下面这两个变量都能够在单个变量中存储所有的命令行参数。

* $*

此变量会将命令行上提供的所有参数当作一个单词保存，$*会将这些参数视为一个整体。

* $@

此变量将命令行上提供的所有参数当作同一字符串中的多个独立的单词。

## 移动变量

bash shell工具箱的另一件工具是shift命令。  
bash shell的shift命令能够用来操作命令行参数。  
shift命令会根据它们的相对位置来移动命令行参数。  

在使用shift命令时，默认情况下它会将每个参数变量向左移动一个位置。  
所以$3的值会移动到$2中，而变量$1的值则会被删除（$0的值不会变）。

比如：

````
#!/bin/bash

count=1
while [ -n "$1" ]
do
	echo "Parameter #${count} = $1"
	count=$[ ${count}+1 ]
	shift
done
````



# 选项

选项是跟在单破折线后面的单个字母，它能改变命令的行为。

## 查找选项

* 处理简单选项

使用shift和普通遍历命令行参数一样处理命令行选项。

比如

````
#!/bin/bash

while [ -n "$1" ]
do
	case "$1" in
		-a) echo "Found the -a option";;
		-b) echo "Found the  -b option";;
		*) echo "$1 is not an option";;
	esac
	shift
done
````

* 分离参数和选项

当在shell脚本中同时使用选项和参数时，Linux使用一个特殊字符来将二者分开，  
该字符会告诉脚本何时选项结束以及普通参数何时开始。  

对Linux来说，这个特殊字符是双破折线（--）线。  

shell 会用双破折线来表明选项列表结束。 
在双破折线之后，脚本就可以放心地将剩下的命令行参数当作参数，而不是选项来处理了。


如：

````
#!/bin/bash

while [ -n "$1" ]
do
	case "$1" in
		-a) echo "Found the -a option";;
		-b) echo "Found the  -b option";;
		--) shift
			break;;
		*) echo "$1 is not an option";;
	esac
	shift
done

# 参数处理

count=1
for param in $@
do
	echo "Parameter #${count}:${param}"
	count=$[ $count+1 ]
done
````

* 处理带值的选项

有些选项会带一个额外的参数值。  
在这种情况下，命令行看起来像下面这样。

````
./testing.sh -a test1 -b -c -d test2
````

比如：

````
#!/bin/bash

while [ -n "$1" ]
do
	case "$1" in
		-a) echo "Found the -a option";;
		-b) param=$2
			echo "Found the -b option, with parameter value ${param}"
			shift;;
		--) shift
			break;;
		*) echo "$1 is not a option";;
	esac
	shift
done

count=1

for param in "$@"
do
	echo "Parameter #${count}:${param}"
	count=$[ $count+1 ]
done
````

## 使用getopt命令

getopt命令是一个在处理命令行选项和参数时非常方便的工具 。  
它能够识别命令行参数，从而在脚本中解析它们时更方便。

### 命令的格式
getopt命令可以接受一系列任意形式的命令行选项和参数，并自动将它们转换成适当的格式。
```
getopt optstring parameters
```
optstring是这个过程的关键所在。它定义了命令行有效的选项字母。
然后，在每个需要参数值的选项字母后加一个冒号。
getopt命令会基于你定义的optstring解析提供的参数。

比如：
```
getopt ab:cd -a -b test1 -cd test2 test3
```

optstring定义了四个有效选项字母：a、b、c、d。
冒号被放在了字母b后面，因为b选项需要一个参数值。

当getopt命令运行时，它会检查提供的参数列表(-a -b test1 -cd test2 test3)，
并基于提供的optstring进行解析。
注意：它会自动将-cd选项分成两个单独的选项，并插入双破折线来分隔行中的额外参数。

如果指定了一个不存在于optstring中的选项，默认情况下，getopt命令会产生一条错误消息。

可以使用 getopt -q optstring来忽略这条错误消息。

### 在脚本中使用getopt
可以在脚本中使用getopt来格式脚本所携带的任何命令行选项或参数。

方法是，用optget命令生成的格式化后的版本来替换已有命令行选项和参数。
set命令能做到

set命令的选项之一是双破折线(--)，它会将命令行参数替换成set命令的命令行值。
然后，该方法会将原始脚本中的命令行参数传给getopt命令，之后再将getopt命令的输出传给
set命令。
```
set -- $(getopt -q ab:cd "$@")
```

现在原始的命令行参数变量的值会被getopt命令的输出替换，而getopt已经为我们
格式好了命令行参数。




## 使用更高级的getopts



## 将选项标准化

常用的Linux命令选项。


# 获取用户输入


## 基本的读取

read命令从标准输入（键盘）或另一个文件描述符中接受输入。
在收到输入后，read命令将数据放进一个变量。
```
read [OPTIONS] [VARIABLES...]
```

变量与输入值个数一一对应，如果不够，就将最后多出来的值全部给最后一个变量。

也可以不指定变量，read命令会将它收到的任何数据都放进特殊环境变量REPLY中。

常用选项：
```
-p:输入提示符
-s：隐藏方式读取，类似password
-t:设置超时时间
```
## 读取
read可以用来读取Linux系统文件里保存的数据。
每次调用read命令，它都会从文件中读取一行文本。
当文件中再没有内容时，read命令会退出并返回非零退出状态码。

最常见的方法是对文件使用cat命令，将结果通过管道直接传给含有read命令的while命令。





