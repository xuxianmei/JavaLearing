# 函数

## 函数定义及调用

定义：

```
function name(){
	...
}
或
name(){
	...
}
```

要在脚本中使用函数，只需要像其他shell命令一样，指定函数名就行(不需要使用())。
```
#! /bin/bash
function func1(){
	echo "This is an example of a fcuntion" 
}
count=1
while [ $count -le 5 ]
do
	func1
	count=$[$count+1]
done
func1
echo $?
```


## 函数返回值

### 返回状态码
shell把函数当作一个小型脚本，运行结束时会返回一个退出状态码。

* 默认退出状态码 ``$?``
默认情况下，函数的退出状态码是函数中最后一条命令返回的退出状态码。
在函数执行结束后，可以用标准变量$?来确定函数的退出状态码。
>函数一结束就取返回值

* return返回状态码
return 命令允许指定一个整数值来定义函数的退出状态码。
>退出状态码必须是0-255

### 自定义函数返回值，并使用变量获取:使用echo、变量、命令替换。
正如将命令的输出保存到shell变量当中一样，
可以对函数的输出采用相同的处理办法，可以用这种技术来获得任何类型的函数输出，并将其保存到变量中。
```
#! /bin/bash
function func1(){
	echo "This is a test"
}
value=$(func1)#或value=`func1`

```

# 在函数中使用变量

## 向函数传递参数
bash shell会将函数当作小脚本来对待。
这意味着你可以像普通脚本那样向函数传递参数。

函数可以使用标准的参数环境变量来表示命令行上传给函数的参数。
例如：函数名在$0变量中定义，函数命令行上的任何参数都会通过$1、$2等定义。
```
#! /bin/bash
function func1(){
        if [ $# -eq 0 ] || [ $# -gt 2 ] ;then
                echo -1
        elif [ $# -eq 1 ] ; then
                echo $[ $1 + $1]
        else
                echo $[ $1 + $2 ]
        fi
}
func1
func1 1 2 3
func1 1
func1 1 2
```

## 函数中的变量作用域
变量作用域，在shell脚本，在函数外部定义的变量，作用域为整个脚本。
可以在函数中访问使用。

如果需要在函数内部使用定义作用域为本函数内的局部变量，需要使用
```
local 变量名=值
```

# 向函数传递数组变量
如果试图将数组变量作为函数参数，函数只会取数组变量的第一个值。
要解决这个问题，必须将该数组变量的值分解成单个的值，然后将这些值作为函数参数使用。
在函数内部，可以将所有的参数重新组合成一个新的变量。

```
#! /bin/bash

function testarray(){
	local newarray
	newarray=`echo "$@"`
	echo "The new array value is : ${newarray[*]}"
}
myarray=(1 2 3)
testarray ${myarray[*]}

```

从函数中返回数组：
如上：在函数内构造一个数组以后，使用``echo ${newarray[*]}``类似语法。

# 创建函数库及使用函数库

