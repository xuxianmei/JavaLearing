# sed
shell脚本最常见的一个用途就是处理文本文件。
使用sed和gawk能够轻松实现自动格式化、插入、修改、替换、删除文本元素。

sed编辑器被称为流编辑器（stream editor），和普通的交互式文本编辑器愉好相反。
在文本编辑器中，可以用键盘命令来交互式地插入、删除和替换数据中的文本。
流编辑器则会在编辑器处理数据之前基于预先提供的一组规则来编辑数据流。

sed编辑器可以根据命令来处理数据流中的数据，这些命令要么从命令行中输入，要么存储在
一个命令文本文件中。

sed编辑器执行下列操作：
1. 一次从输入中读取一行数据
2. 根据所提供的编辑器命令匹配数据
3. 按照命令修改流中的数据
4. 将新的数据输出到STDOUT
5. 重复1步骤，直接所有数据被读取无

一句话概括：sed编辑器逐行处理输入，然后把结果发送到屏幕。

>文件内容并没有 改变，除非你使用重定向存储输出。

sed命令的格式如下：
```
sed [options] '[address]command' file(s)
sed [options] -f scriptfile file(s)
```

command格式：

options允许你修改sed命令的行为
---
选项|描述
---|---
-e script|在处理输入时，将script中指定的命令添加到已有的命令中
-f file|在处理输入时，将file中指定的命令添加到已有的命令中
-n|不产生命令输出，使用print命令来完成输出

sed命令
---
命令|描述
---|---
a\|在当前行后添加一行或多行
c\|用新文本替换当前行中的文本
d|删除行
i\|在当前行之前插入文本
h|把模式空间的内容复制到暂存缓冲区
H|把模式空间的内容添加到缓冲区
g|取出暂存缓冲区的内容，将其复制到模式缓冲区
G|取出暂存缓冲区的内容，将其追加到模式缓冲区
l|列出非打印字符
p|打印行
n|读入下一行输入，并从下一条而不是第一条命令对其处理
q|结束或退出sed
r|从文件中读取输入行
!|对所选行以外的行应用所有命令
s|用一个字符串替换另外一个字符串

## 基础范例

* 在命令行定义编辑命令
```
echo "This is a test" | sed 's/test/bit test/'
输出:This is a bit test
```

* 在命令行使用多个编辑器命令 -e
使用-e选项，加上分号，分隔编辑器命令。
```
echo "This is a test" | sed -e 's/test/big test/;s/This/this/'
输出：this is a big test
```
使用多个-e选项，分隔编辑器命令。
```
echo "This is a test" | sed -e 's/test/big test/' -e 's/This/this/'
输出：this is a big test
```
将多个编辑器命令，多行放置
```
echo "This is a test" | sed -e 's/test/big test/
> s/This/this/
> '
输出：this is a big test
```

* 从文件中读取编辑器命令 -f
```
$cat a1.sed
输出：
s/test/big test/
s/This/this/
```
使用
```
echo "This is a test" |  sed -f a1.sed 
this is a big test
```

# sed编辑器基础
成功使用sed编辑器的关键字在于掌握其各式各样的命令和格式，它们能够帮助你定制文本编辑行为。
## 替换选项
比如文本替换命令：s
格式：
```
s/pattern/replacement/flags
```
pattern支持正则表达式。

替换标志flags

替换标志|描述
---|---
g|	在行内进行全局替换
p|	打印行
w|	将行写入文件
x|	交换暂存缓冲区和模式空间的内容
y|	将字符转换成另外一个字符

## 自定义分隔字符
有时，会在文本字符串中遇到一些不太方便在替换模式中使用的字符，比如Linux中一个
常见的例子就是正斜线(``/``)
替换文件中的路径名会比较麻烦，需要使用转义符\来转义，可读性会变得很差，可以使用替换字符
作为替换命令中的字符串分隔符：
比如 使用/bin/csh替换 /bin/bash：

```
sed 's!/bin/bash!/bin/csh! '  /etc/passwd
```

## 使用地址：行寻址
默认情况下，在sed编辑器中使用的命令会作用于文本数据的所有行。
如果只想将命令作用于特定行或某些行，则必须使用**行寻址(line addressing)**。

在sed编辑器中有两种形式的行寻址：
* 以**数字形式**表示**行区间**
* 用**文本模式**来**过滤出某一行**

两种形式都使用相同的格式来指定地址：
```
[address]command
```
也可以将特定地址的多个命令分组（多个命令针对同一个地址进行操作）：


### 数字方式的行寻址
当使用数字方式的行寻址时，可以用行在文本流中的行位置来引用。
sed编辑器会将文本流中的第一行编号为1。

在命令中指定的地址可以是单个行号，或是用起始行号、逗号以及结尾行号指定的一定
区间范围内的行。

比如
```
sed '2s/dog/cat/' data1.txt #第2行
sed '2,3s/dog/cat/' data1.txt #第2-3行
sed '2,$s/dog/cat/' data1.txt  #第2-最后一行
```
>指定行的范围，使用逗号

注：如果要在指定的行上，执行多个命令，需要使用
```
addresss{
	command1
	command2
	command3
}
```
### 使用文本模式过滤器

sed编辑器允许指定文本模式（支持正则）来过滤出命令要作用的行。
格式如下：
```
/pattern/command 
/pattern1/，/pattern2/command 区间指定，文本模式的区间指定，要格式注意。
```
>指定行的范围，使用逗号

patten支持正则表达式。（指包含（pattern模式匹配的）字符串的行）
sed编辑器在文本模式中采用了正则表达式特性。

比如：只修改用户xxm的默认shell
```
sed '/xxm/s/bash/csh/' /etc/passwd
```

# sed命令

## 打印行：p

打印sed编辑器输出中的一行。

范例：

命令|描述
---|---
``sed  -n '/abc/p'  file|	使用了-n，所以只打印匹配的行，这里是只打印，包含abc的行。
``sed  -n '2,3p'  file|	只打印第2和3行。
如果需要在修改之前查看行，也可以使用打印命令，比如与替换或修改命令一起使用。
```
$sed -n '/3/{
>p
>s/line/test/p
}' file
```

## 打印行号：=
```
sed '=' file
```

## 列出行：l
打印数据流中的文本和不可打印的ASCII字符。比如制表符等。
```
sed -n 'l' file
```


## 替换命令：s

针对满足条件的行，将pattern匹配的字符串替换为replacement
```
s/pattern/replacement/flags
```
实例：

命令|描述
---|---
``sed   's/abc/def/g' file``|	把行内的所有abc替换成def，如果没有g,则只替换行内的第一个abc
``sed   -n 's/abc/def/p' file``|	只打印发生替换的那些行
``sed   's/abc/&def/' file``	|在所有的abc后面添加def（&表示匹配的内容）
``sed   -n 's/abc/def/gp' file``	|把所有的abc替换成def，并打印发生替换的那些行
``sed   's#abc#def#g' file``|把所有的abc替换成def，跟在替换s后面的字符就是查找串和替换串之间的分割字符，本例中使用``#``

## 删除行：d

删除匹配指定寻址模式的所有行。
>如果没有加入寻找模式，流中的所有文本行都会被删除。

常见范例：

命令|描述
---|---
``sed '3d' file``|删除第三行的内容
``sed '1,3d' file``|删除1至3行的内容
``sed '$d' file|删除最后一行内容
``sed '/abc/d'|删除包含abc的行的内容

注：sed在数据流中匹配到了开始模式，删除功能就会打开，直接找到停止模式，或者最后一行。

所以如果使用两个文本模式来删除某个区间的行时，要格外小心。

比如有如内容：
```
This is 1
This is 2
This is 3
This is 4
This is another 1
This is another 2
This is text you want to keep
This is the last line in the file
```
如果使用
```
sed '/1/,/3/d' file # 起始模式是包含1的行，终止模式是包含3的行
```
最后只有``This is 4``这一行会保留下来。

## 插入行：i
相当于insert,在指定行前增加一个新行。
格式
```
[address]i\new line contet
```
范例

命令|描述
---|---
``sed 'i\This is a new line' file``|在首行插入一条新行，内容为：This is a new line
``sed '3i\This is a new line' file``|在第三前插入一条新行，内容为：This is a new line
``sed '2,4i\This is a new line' file``|分别在第2、3、4行前插入一条新行，内容为：This is a new line
``sed '\abc\i\This is a new line' file``|在包含abc的行前插入一条新行，内容为：This is a new line

## 附加行：a
相当于append，在指定行后增加一个新行。
```
[address]a\new line contet
```
同插入行命令i，只不过是在后面。

## 行替换：c
```
[address]c\new line contet
```
与插入行相似，进行是的内容替换。
注：当指定行区间时，会将整个行区间当作一个整体，替换成新的一行，而不是每行都替换。

## 转换命令：y
唯一一个可以处理单个字符的sed编辑器命令。
格式：
```
[address]y/inchars/outchars/
```
转换命令会对inchars和outchars值进行一对一的映射，进行替换。

范例：
命令|描述
---|---
``sed '3y/123/456/' file``|针对第3行进行中所有匹配字符(1、2、3)替换，1换成4，2换成5，3换成6

## 写入文件：w

用来向文件写入行。
格式：
```
[address]w filename
```
filename可以使用相对路径或绝对路径（运行sed的用户必须有文件的写权限）。
address可以是任意类型的寻址方式：单个行号、文本模式、行区间、文本模式

范例

命令|描述
---|---
``sed '1，3w test.txt' file``|将1至3行的内容，写入文件test.tx中。



## 从文件中读取数据：r
将一个独立文件中的数据插入到数据流中。
格式：
```
[address]r filename
```
sed编辑器会将文件中的文本插入到指定地址后。

范例

命令|描述
---|---
``sed '3r test.txt' file``|将test.txt文件中的内容插入到第3行后面

读取命令的另一个很酷的用法是和删除命令配合使用：利用另一个文件中的数据来替换文件中的占位文本。
比如，一个文件中使用List字符串用来占位，现在使用一个文件内的数据来替换这个List，可以这样写：
```
$sed '/List/{
>r data.txt
>d
}' file
```
