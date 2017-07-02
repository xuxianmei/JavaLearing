# for 

bash shell提供了for命令，允许你创建一个遍历一系列值的循环，每次迭代都使用其中一个值来  
执行已定义好的一组命令。

````
for var in list
do
	commands
done

或

for var in list ; do
	commands
done
````

在do和done语句之间输入的命令可以是一条或多条标准的bash shell命令。

$var包含本次迭代对应的当前列表项中的值。

在遍历以后，$var变量会一直存在于shell当中，直到脚本结束。


* 读取列表的值

````
#!/bin/bash
# for 命令读取普通列表值

for name in TOM MIKE JACK 
do
	echo $name
done

echo "now name is $name"
````

$name会一直存在。

如果列表中的值当中包含空格，或者其它一些特殊符号（比如单引号），就需要使用双引号或者转义符``\``来转义。

* 从变量读取列表

````
#!/bin/bash

list="Alabama Alaska Arizona Arkansas Colorado"
list=$list" Connecticut"
for state in $list
do
	echo now state is $state
done
````

* 从命令读取值

生成列表中所需的值的另外一个途径就是使用命令的输出。  
可以用命令替换来执行任何能产生输出的命令，然后在for命令中使用该命令的输出。

````
#!/bin/bash

for item in $(ls -l)
do
	echo $item
done
````



* 更改字段分隔符

IFS环境变量定义了bash shell作用字段分隔符的一系列字符。

默认情况下，bash shell会将下列字符当作字段分隔符：空格、制表符、换行符。

如果bash shell在数据中看到了这些字符中的任意一个，它就会假定这表明了列表中  
一个新数据字段的开始。

可以在shell脚本中临时更改IFS环境变量的值来限制被bash shell当作字段分隔符的字符。

可以在脚本中设置IFS变量的值，比如换行符
````
IFS=$'\n'
````

如果要设置多个IFS字符，只要将它们在赋值行串起来就行：

````
IFS=$'\n':;"  //这个赋值会将换行符、冒号、分号和双引号作为字段分隔符。
````

在处理代码量较大的脚本时，可能在一个地方需要修改IFS的值，然后忽略这次修改，在脚本的其他  
地方继续沿用IFS的默认值。

````
IFS.OLD=$IFS
IFS=$'\n'
...

IFS=$IFS.OLD
````

比如

````
#!/bin/bash
IFS=$'\n'
for item in $(ls -l)
do
	echo $item
done
````

* 用通配符读取目录

可以用for命令来自动遍历目录中的文件，进行此操作时，必须在文件名或路径名中使用通配符。  
它会强制shell使用文件扩展匹配。  
文件扩展匹配是生成匹配指定通配符的文件名或路径名的过程。

比如：

````
#!/bin/bash

for file in ~/*s*
do
	# 如果此处$file使用双引号包含，那么如果文件路径中包含空格，就会发生错误 。
	if [ -d "$file" ]  
	then
		echo $file is a directory
	elif [ -f "$file" ]
	then
		echo $file is a file
	fi
done
````

也可以在for命令中列出多个目录通配符，将目录查找和列表合并进同一个for语句。

````
#!/bin/bash

for file in ~/*s* /usr/bin
do
	
	if [ -d "$file" ]  
	then
		echo $file is a directory
	elif [ -f "$file" ]
	then
		echo $file is a file
	fi
done
````


## C语言风格的for

待加

# while 命令


* 基本格式

````
while test command
do
	other command
done
````


比如

````
#!/bin/bash

i=10
while  [ $i -gt 0 ]
do
	echo $i
	i=$[ $i-1 ]
done
````


* 使用多个测试命令



# until 命令

do something until something happen then done。


* 基本格式 

````
until test command
do 
	other commands
done
````

比如：

````
#!/bin/bash

i=10
count=1

until [ $i -eq 1 ]
do
	echo "i=${i}"
	count=$[ ${count} * $i ]
	i=$[ $i-1 ]
done

echo "count=${count}"	
````


* 使用多个测试命令


# 控制循环

相关命令：break、continue


## break 命令

终止循环

* 格式

````
break [n] ：默认n为1，跳转当前循环，如果n大于2，表示跳转对应层次的外循环。
````

比如


````
#!/bin/bash

i=10
count=1

until [ $i -eq 1 ]
do
	if [ $i -eq 5 ]
	then
		break
	fi

	echo "i=${i}"
	count=$[ ${count} * $i ]
	i=$[ $i-1 ]
done

echo "count=${count}"
````

## continue 命令

终止本次循环，继续下一个循环项。

* 格式

````
continue [n] ：同理n，如果大于2，表示继续哪一层次的循环。
````

# 处理循环的输出

在shell脚本中，可以对循环的输出使用管理或进行重定向。

````
for file in /home/rich/*
do
	if [ -d "$file" ]
	then
		echo "$file is a directory"
	else
		echo "$file is a file"
	fi
done > output.txt
````

````
for file in /home/rich/*
do
	if [ -d "$file" ]
	then
		echo "$file is a directory"
	else
		echo "$file is a file"
	fi
done | sort
````
