# gawk程序
虽然sed编辑器是非常方便自动修改文本文件的工具，但其也有自身的限制。
gawk提供一个类编程环境和重新组织文件中的数据。

gawk是Unix中的原始awk程序的GNU版本。
它提供了一种编程语言而不只是编辑器命令。
在gawk中，可以做下面的事情：
1. 定义变量来保存数据
2. 使用算术和字符串操作符来处理数据
3. 使用结构化编程概念（比如if和for）来为数据处理增加处理逻辑
4. 通过提取数据文件中的数据元素，将其重新排列或格式人，生成格式化报告。

gawk程序的报告生成能力通常用来从大文本文件中提取数据元素，并将它们格式化
成可读的报告。（最佳例子就是格式化日志文件）

## gawk基础

## gawk命令格式

```
gawk options program datafile
```
或
```
gawk options  -f scriptFile datafile
```
program与-f programFile，可以组合使用。
>类似shell和sed，可以将gawk的程序脚本直接写在命令行上，或者放入脚本文件中。

gawk常用options
---
Option | Description
---|---
-F fs|指定行中划分数据字段的字段分隔符
-f scriptFile|从指定的文件中读取程序，可使用多个 -f
-v var-value| 指定要处理的数据文件中的最大字段数
-mf N| 指定要处理的数据文件中的最大字段数
-mr N|指定数据文件中的最大数据行数
-w keyword| 指定gawk的兼容模式或警告等级

gawk程序脚本（program）
---
gawk程序脚本用一对花括号来定义。

格式：在命令行中使用，需要将下面的这些放入单引号中。
```
{
	program1;
	program2;
}
{
	program 3;	
	program 4;
}
```
从命令行读取程序脚本中，由于gawk命令行假定脚本是单个文本字符串，所以还必须将脚本放在单引号中。

范例：
```
gawk '{print "Hello"}'
#没有指定datafile，gawk会从标准输入中读取数据，这里只有一个功能，输出"Hello",无论输入是什么。
#使用CTRL+D 给出EOF字符。
1
Hello
2
Hello
3
Hello
4
Hello
5
Hello

```


## 数据记录变量与数据字段变量

gawk的主要特性之一就是其处理文本文件中数据的能力。
它会自动给一行中的每个数据元素分配一个变量。
>这里的行实际为一条数据记录，记录之间使用记录分隔符，由RS指定，默认为换行符
>数据字段，由数据分隔符分隔，由FS指定，默认为任意空白字符（空格或制表）

默认情况下，gawk会将如下变量分配给它在文本行中发现的数据字段。
* $0 代表整个文本行
* $1 代表第一个数据字段
* $2 代表第二个数据字段
...
* $n 代表第n个数据字段

范例：
```
xuxianmei@XXMPC ~> cat testdata.txt 
One line in the text
Tow Lines in the text
Three Lines in te text
xuxianmei@XXMPC ~> gawk '{print $1}' testdata.txt
输出： 
One
Tow
Three
```

## 在程序中使用多个命令
使用分号隔开

```
xuxianmei@XXMPC ~> gawk '{$2="data";print $0}' testdata.txt 
One data in the text
Tow data in the text
Three data in te text
xuxianmei@XXMPC ~>
```

## 从文件中读取程序
使用``-f``指定，脚本文件中的命令，仍然需要使用花括号``{}``。
```
xuxianmei@XXMPC ~> cat script1.gawk 
{
  print $1 "'s home direcotory is" $6
}
xuxianmei@XXMPC ~> gawk -F: -f script1.gawk   /etc/passwd | sed  -n '1,3p'
输出：
root's home direcotory is/root
daemon's home direcotory is/usr/sbin
bin's home direcotory is/bin
```
>上面使用``-F:``重新指定了输入数据字段分隔符。

## 在处理数据前运行脚本：BEGIN
默认情况下，gawk会从输入中读取一行文本，然后针对该行的数据执行脚本。
有时可能需要在处理数据前运行脚本操作一此行为，比如为报告创建标题。
BEGIN就是用来做这个的。
格式：
```
BEGIN{program}
```
范例：
```
xuxianmei@XXMPC ~> cat script1.gawk 
BEGIN{print "Hello World!" }
{
  print $1 "'s home direcotory is" $6
}

xuxianmei@XXMPC ~> gawk -F: -f script1.gawk   /etc/passwd | sed  -n '1,3p'
输出：
Hello World!
root's home direcotory is/root
daemon's home direcotory is/usr/sbin
```

## 在处理数据后运行脚本：END
与BEGIN原理一致，只是在最后。
范例：
```
xuxianmei@XXMPC ~> cat script1.gawk
BEGIN{print "Hello World!" }
{
  print $1 "'s home direcotory is" $6
}
END{ print "BYE BYE!!"}
xuxianmei@XXMPC ~> sed -n '1,5p' /etc/passwd | gawk -F: -f script1.gawk
输出：   
Hello World!
root's home direcotory is/root
daemon's home direcotory is/usr/sbin
bin's home direcotory is/bin
sys's home direcotory is/dev
sync's home direcotory is/bin
BYE BYE!!
```

# 使用变量

gawk编程语言支持两种不同类型的变量
* 内建变量(builtin variable)，比如$0
* 自定义变量(custom variable)

## 内建变量(builtin variable)
gawk有一些内建变量。
这些变量用来处理数据中的数据字段和记录的信息，以及引用程序数据里的一些特殊功能。

### 字段分隔符和记录分隔符 变量

变量|描述
FS|输入字段分隔符，默认为空白字符（空格或制表）
RS|输入记录分隔符，默认为换行符
OFS|输出字段分隔符，默认为空白符
ORS|输出记录分隔符，默认为换行符
FILEDWIDTHS|定义数据字段的确切宽度（每几个字符为一个数据字段）
>FILEDWIDTHS一旦定义，不可再变，且会使FS失效。




