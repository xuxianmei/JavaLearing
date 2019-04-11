# sed
shell脚本最常见的一个用途就是处理文本文件。
使用sed和gawk能够轻松实现自动格式化、插入、修改、替换、删除文本元素。

sed编辑器被称为流编辑器（stream editor）。
与普通的交互式文本编辑器愉好相反。
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

一句话概括：sed编辑器逐行处理输入（读取到模式空间内进行sed命令操作）然后把结果发送到屏幕。

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
-n|仅显示script处理后的结果

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

## pattern space（模式空间）
pattern space是一块活跃的缓冲区，在sed编辑器执行命令时，它会保存待检查的文本。

模式空间用于 sed 执行的正常流程中。
该空间是sed内置的一个缓冲区，用来存放、修改从输入文件读取的内容。每次循环读取数据过程中，模式空间的内容都会被清空。
可以针对这个空间，使用sed命令，比如文本替换、输出打印等。

可以简单理解，每次读取一个数据流，将满足pattern条件的行放入模式空间中，可以对这个空间内的数据使用sed命令进行输出打印、修改，删除等操作。

## hold space（保存空间）
保持空间是另外一个缓冲区，用来存放临时数据。Sed 可以在保持空间
和模式空间交换数据，但是不能在保持空间上执行普通的 sed 命令。我们已经讨论

过，，然而保持空间的内容

则保持不变，不会在循环中被删除。

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
最后一行为$

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
	[address]command1
	[address]command2
	[address]command3
}
```
或者
```
addresss{ [address]command1;[address]command2;[address]command3 }
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
p命令最常用的就是打印匹配模式的行。

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

## 重新读取下一行到模式空间：n
TODO:没有理解这句话。
``
n命令会告诉sed编译器移到到数据流中的下一文本行，而不用重新回到命令的最开始再执行一遍。
``

使用address匹配到行以后，如果使用n，会读取这行的下一行到模式空间中（相当于使用了新内容替代模式空间中的原有内容）。
范例：
例一：
```
$ echo "This is 1
This is 2
This is 3
This is 1
This is 4" | sed -n "/1/{n;p}"
```
输出：
```
This is 2
This is 4
```
例二:
```
echo "This is 1
This is 2
This is 3
This is 1
This is 4" | sed -n "{n;p}" #n;p;位置前后顺序，可实现输出奇数行，偶数行。
```
输出：
```
This is 2
This is 1
```

## 继续读取下一行到模式空间：N
n相当于重新设置模式空间内容，N相当于附加内容到模式空间。
类似``>``、``>>``。

当一个单词，或多个单词，分布在两行或多行之内，可以使用N拼接多行，进行操作。
范例：
```
echo "This is 1
This is 2
This is 3
This is 4
This is 5" | sed -n "{N;s/\n/ /g;p}" #相当于一次读取两行放到模式空间（注：实际形式上还是两行数据放在模式空间，并不是放在同一行上。），这里将换行换成空格，并打印模式空间内容。
```
输出：
```
This is 1 This is 2
This is 3 This is 4
```
注：当后面没有足够的行读取到模式空间时，相当于不满足条件，sed会跳过。
注：如果中间有空白行（啥内容都没有），也会读取附加到模式空间。

## 删除行命令：D（配合N）
当使用了N读取了多行到模式空间中，如果使用d，会删除模式空间中所有行。
可以使用D，这个命令，只删除模式空间中的第一行。（该命令会删除到换行符为止的所有字符。）


```
xuxianmei@XXMPC ~/script $ echo "This is Hello
World" | sed '{N;/Hello\nWorld/d;p}'
	# 两行都被删除了
xuxianmei@XXMPC ~/script $ echo "This is Hello
World" | sed '{N;/Hello\nWorld/D;p}'
World # 只删除了第一行
```


## 打印命令：P（配合N和D）
当使用了N读取了多行到模式空间中，如果使用p，会打印模式空间中所有行。
可以使用P，这个命令，只打印模式空间中的第一行。

## D、N、P
D命令的独特之处在于强制sed编辑器返回到脚本的起始处，对同一模式空间的内容重新
扫行这些命令（它不会从数据流中读取新的文本行）。

在命令脚本中加入N命令，你就能单步扫过整个模式空间，将多行一起匹配。 
接下来，使用P命令打印出第一行，然后用D命令删除第一行，并绕回到脚本的起始处。
一旦返回，N命令会读取下一行文本并重新开始这个过程。
这个循环会一直继续下去，直到数据流结束。 
范例：

```
echo "Something will hapen


This is Hello
World
 
What Happen

" |sed -n "{N;/Hello\nWorld/p;D}" ##尝试去掉D，不会得到结果，因为This is Hello这句和上一句空行进行了组合。
```
输出：
```
This is Hello
World
```

## 保持空间与模式空间的互操作命令：h、H、g、G、x
在处理模式空间中的某些行时，可以用保持空间来临时保存一些行。
有5个命令可用来操作保持空间。

命令|功能
---|---
h| 将模式空间内容复制到保持空间
H| 将模式空间内容附加到保持空间
g| 将保持空间内容复制到模式空间
G| 将保持空间内容附加到模式空间
x| 并换两个空间的内容

通常，使用h或H将字符串移动到保持空间后，最终还是要用g、G、x命令将保存的字符串
移回到模式空间（否则，一开始就没有必要保存它们了）。
范例：
```
echo "This is the header line.
This is the first data line
This is the second data line
This is the third data line
This is the footer line." | sed -n "/first/{h;p;n;p;g;p}"
输出：
This is the first data line
This is the second data line
This is the first data line
```
步骤解释：
1. 使用正则表达式文本模式``/first/``，过滤包含fisrt的行
2. 当满足模式的条件行出现时，将模式空间的内容复制到保存空间
3. p命令打印模式空间中的内容
4. n读取下一行数据流更新当前模式空间
5. p命令打印模式空间中的内容
6. g将保存空间内容复制到模式空间中的内容
7. 打印模式空间内容

## 排除命令 ：!
```
[address]!command
```
排除命令``!``（感叹号），让原本会起作用的命令不起作用。
可以这么理解：表示后面的命令对所有没有被address选定的行发生作用。（或者可以理解为反转了命令作用效果）
>如果没有address限定部分行，个人觉得``!``命令没有任何必要。因为相当于不作用于任何行。

范例：
```
echo "This is the header line.
This is the first data line
This is the second data line
This is the third data line
This is the footer line." | sed -n "/first/!p"
输出：包含first的行p命令不起作用，其它行p起作用。
This is the header line.
This is the second data line
This is the third data line
This is the footer line.
```

文本反转
```
echo "This is the header line.
                   This is the first data line
                   This is the second data line
                   This is the third data line
                   This is the footer line." | sed  -n '1!G;h;$p'
输出：
This is the footer line.
This is the third data line
This is the second data line
This is the first data line
This is the header line.
```
>可以理解为使用两个Quene，来实现一个Stack。
>其中每次第一个行为操作：读取一个新数据放置到模式空间队列中。

改变流
---
通常，sed编辑器会从脚本的顶部开始，一直执行到脚本的结尾
>D命令是个例外，它会强制sed编译器返回到脚本的顶部，而不读取新的行。
>这里的脚本的顶部，不是指文本文档的顶部，而是sed命令脚本。
>也就是说，不重新读取新的行到模式空间，针对模式空间重新执行一遍sed命令。

sed编辑器提供了一个方法来改变命令脚本的执行流程，其结果与结构化编程类似。

## 分支命令：b

分支命令可以基于地址、地址模式、地址区间排除一整块命令。
这允许你只对数据流中的特定行执行一组命令。
>可以理解为``goto label``;

格式：
[address]b [label]

>如果没有address限定部分行，``b``命令没有任何必要。因为相当于不作用于任何行。

address参数决定了哪些行会的数据会触发分支命令。
lable参数定义了要跳转到的位置。
>如果没有label，会跳转到脚本的结尾。类似：continue;

范例:
>其中，限定了``/first/``匹配的行，路过``s/This/That/``,所有行都执行两个替换命令This->That，the->The。

```
 echo "This is the header line.
This is the first data line
This is the second data line
This is the third data line
This is the footer line." | sed  -n '{/first/b label1;s/This/That/;:label1 s/the/The/;p}'
输出：
That is The header line.
This is The first data line
That is The second data line
That is The third data line
That is The footer line.
```
## 测试命令：t
测试命令``t``也可以用来改变sed编辑器脚本的执行流程。
测试命令``t``根据替换命令的结构跳转到某个标签。
>如果没有指定标签，同样相当于continue。

格式：
[address]t [label]

范例：当发生一个替换，就不再执行另一个替换

```
echo "This is the header line.
This is the first data line
This is the second data line
This is the third data line
This is the footer line." | sed  '{/first/s/first/XXXX/;t;s/This/That/;}'
输出：
That is the header line.
This is the XXXX data line
That is the second data line
That is the third data line
That is the footer line.
```
范例：当第三行发生一个替换时，跳到This->THIS
```
echo "This is the header line.
This is the first data line
This is the second data line
This is the third data line
This is the footer line." | sed  '{/data/s/data/XXXX/;3t label1 ;s/This/That/;:label1 s/This/THIS/}'
That is the header line.
That is the first XXXX line
THIS is the second XXXX line
That is the third XXXX line
That is the footer line.
```
>如果没有跳转命令，只有:label时，并不影响正常流程。

注意：排除命令``!``同样适用于分支命令``b``和测试命令``t``

## 模式替代命令:&
在脚本中，模式替代命令``&``，类似于高级语言中的this。
范例：为cat加上双引号
```
 echo "The cat sleeps" | sed '{s/cat/"&"/}'
The "cat" sleeps
```
>&相当于匹配模式匹配的整个字符串。

## 子模式替代命令：\数字（\1,\2,\3...\n）
子模式替代命令，主要用于匹配正则表达式文本模式时的分组。
```
 echo "The cat is doing something" | sed "{s#\(cat\).is.\(doing\) some.*#\1 is moveing#}"
The cat is moveing
```

## 在脚本中使用sed（TODO，简单，不重要）

## 创建sed实用工具（TODO范例）






                                 