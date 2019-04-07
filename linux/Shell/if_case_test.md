

# if-then

* 基本用法

````
if command
then 
	commands
fi
````
或
````
if  command; then
	commands
fi
````

bash shell的if语句会运行if后面的那个命令。如果该命令的退出状态码是0，  
位于then部分的命令就会被执行。  
如果该命令的退出状态码为其他值，then部分的命令就不会被执行。
fi语句用来表示if-then语句至此结束。

* if-then-else 


````
if command ; then 
	commands
else
	commands
fi
````

* if-then-elif-then-fi

````
if command ; then
	commands
elif command ;then
	commands
fi
````



# test命令

[参考文档](http://tldp.org/LDP/abs/html/comparison-ops.html)
if-then语句不能测试命令退出码之外的条件。  
但是，通过test命令，提供了在if-else语句中测试不同条件的途径（test可用于任何  
条件语句）。

如果在test命令中列出的条件成立，test命令就会退出并返回退出状态码0。  
如果条件不成立，test命令就会退出并返回非零的退出状态码，这使得if-then语句不会被执行。
* 条件测试类型
  * 数值比较
  * 字符串比较
  * 文件比较


* 使用格式

````
test condition
````
或者
```
[ condition ] #中括号两边有空格
```

condition是test命令要测试的一系列参数和值。  
当用在if-then语句中时，test命令看起来是这样的。

````
if test condition
then 	
	commands
fi
````


如果不写test命令的condition部分，它会以非零的退出状态码退出，并执行else语句块。


## 整数比较

使用test命令最常见的情形是对两个数值进行比较。

|比较|描述|
|---|---|
|``n1 -eq n2``| 检查n1==n2|
|``n1 -ge n2``| 检查n1>=n2|
|``n1 -gt n2``| 检查n1>n2|
|``n1 -le n2``| 检查n1<=n2|
|``n1 -lt n2``| 检查n1<n2|
|``n1 -ne n2``| 检查n1!=n2|

数值条件测试可以用在数字和变量上。

如果涉及浮点值时，数值条件测试会有一个限制，bash shell只能处理整数。
echo中可以输出，但test命令中，不可使用浮点值。

**注：比较符两边需要使用空格隔开。**


## 字符串比较

|比 较|描 述|
|---|---|
|``str1 = str2``|检查str1是否等于str2|
|``str1 != str2``|检查str1是否与str2不同|
|``str1 < str2``|检查str1是否小于str2|
|``str1 > str2``|检查str1是否大于str2|
|``-n str1``|检查str1的长度是否非0|
|``-z str1``|检查str1的长度是否为0|


**注：比较符两边需要使用空格隔开。**


当要开始使用测试条件的大于或小于功能时，就会出现两个经常困扰shell程序员的问题：

* 大于号和小于号必须转义，否则shell会把它们当作重定向符号，把字符串值当作文件名。

**转义大于号和小于号**
因为``>``和``<``这两个符号是特殊的重定向符。所以要当作比较符使用，需要将其转义。

``\>``及``\<``

* 大于和小于顺序和sort命令采用的不同

在比较测试中，大写字母被认为是小于小写字母的（ASCII顺序）。
sort命令使用的是系统的本地化语言设置中定义的排序顺序。


## 文件状态及比较

用来测试Linux文件系统上文件和目录的状态。

|比 较|描 述|
|---|---|
|``-d file``|检查file是否存在并是一个目录|
|``-e file``|检查file是否存在|
|``-f file``|检查file是否存在并是一个文件|
|``-r file``|检查file是否存在并可读|
|``-s file``|检查file是否存在并非空|
|``-w file``|检查file是否存在并可写|
|``-x file``|检查file是否存在并可执行|
|``-O file``|检查file是否存在并必当前用户所有|
|``-G file``|检查file是否存在并且默认组与当前用户相同|
|``file1 -nt file2``|检查file1是否比file2新|
|``file1 -ot file2``|检查file1是否比file2旧|



## 复合条件

### 逻辑与

``-a``及&&都可使用。
* -a
````
[ conditaion1 -a  condition2 ]
````
比如：
````
if [ "$expr1" -a "$expr2" ]
then
  echo "Both expr1 and expr2 are true."
else
  echo "Either expr1 or expr2 is false."
fi
````

* &&

````
[ condition1 ] && [ condition2 ]
````
或者
````
[[ condition1 && condition2 ]]
````

### 逻辑或

``-o``及``||``都可使用

* -o



* ||

````
[ condition1 ] || [ condition2 ]
````
或者
````
[[ condition1 || condition2 ]]
````


# if-then的高级特性

bash shell提供了两项可在if-then语句中使用的高级特性：

* 用于数学表达式的双括号 ``(( ))``
* 用于高级字符串处理功能的双方括号``[[ ]]``，支持正常表达式

## 使用双括号  (( ))

双括号命令允许你在比较过程中使用高级数学表达式。
test命令只能在比较中使用简单的算术操作。  
双括号命令提供了更多的数学符号。  

双括号命令的格式如下：
````
(( expression ))  //括号两边有空格
````

expression可以是任意的数学赋值或比较表达式。
除了test命令使用的标准数学运算符，expression还支持其他运算符：

|符 号|描 述|
|---|---|
|``val++``|后增|
|``val--``|后减|
|``++val``|先增|
|``--val``|先减|
|``**``|幂运算|
|``&&``|逻辑与|
| &#124;&#124;| 逻辑或 |
|``!``|逻辑求反|
|``**``|幂运算|
|``<<``|左移|
|``>>``|右移|
|``~``|按位取反|
|``&``|按位与|
|&#124;|按位或|

**注：不需要将双括号中表达式里的大于号和小于号黑底。这是双括号命令提供的另一个高级特性。**

## 使用双方括号 [[ ]]

双括号提供了针对字符串比较的高级特性。
格式如下：

````
[[ expression ]]
````

双方括号里的expression使用了test命令中采用的标准字符串比较。  
但它也提供了test命令未提供的另一个特性-----模式匹配。

在模式匹配中，可以定义一个正则表达式来匹配字符串值。

比如
````
if [[ $USER==x* ]]
````



# case

主要用于尝试计算一个变量的值。

格式：
````
case variable in 
pattern1 | pattern2 ) commands1;;
pattern3) commands2;;
*) default commands;;
esac
````