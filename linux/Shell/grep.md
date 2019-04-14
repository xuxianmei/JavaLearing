# grep
grep: Gloabal Search Regular Expression and Print out the line，意为：**全局搜索正则表达式并打印文本行**。所以

* grep是一个强大的文本搜索工具
* grep与正则表达式联系紧密

grep命令的基本语法如下：
```
grep [options] pattern [file...]
```

[options]表示选项，具体的命令选项见下表。
pattern表示要匹配的模式（包括目标字符串、变量或者正则表达式），pattern后面所有的字符串参数都会被理解为文件名。
file表示要查询的文件名，可以是一个或者多个。

pattern可以使用 -f patternfile来指定，patternfile中的，所有正则都会一一匹配（类似或）。
>文件名支持通配符,支持文件夹（对文件夹中的所有文件进行内容匹配）。
>file不存在时，从标准输入读取

options
---
选项|说明
---|---
-A num|表示找到所有匹配行，并显示所有匹配行后的n行
-B num|表示找到所有匹配行，并显示所有匹配行前的n行
-C num|相当于同时使用-A num -B num
-a |将二进制文件当作文本文件来查找。
-c|只打印匹配的文本行的行数，不显示匹配的内容
-i|匹配时忽略字母的大小写
-h|当搜索多个文件时，不显示匹配文件名前缀
-n|列出所有的匹配的文本行，并显示行号
-o|只显示匹配的字符串，不显示匹配的文本行。
-m num| 最多匹配num行
-l|只列出含有匹配的文本行的文件的文件名，而不显示具体的匹配内容
-s|不显示关于不存在或者无法读取文件的错误信息
-v|只显示不匹配的文本行
-w|模式匹配整个单词
-x|模式匹配整个文本行
-r|递归搜索，搜索当前目录和子目录
-q|禁止输出任何匹配结果，而是以退出码的形式表示搜索是否成功，其中0表示找到了匹配的文本行
-b|打印匹配的文本行到文件头的偏移量，以字节为单位
-f patternfile|指定匹配模式文件，这个文件都是模式，一行一个。
-e|-e patter1 -e patter2支持多个正则表达式匹配，可以使用-E 管道符号 替代
-E|支持扩展正则表达式：问号，加号，花括号，管道符号，分组。
-P|支持Perl正则表达式
-F|不支持正则表达式，将模式按照字面意思匹配
--color|是把match的字符用不同颜色标示出来

范例：匹配包含root字符串的文本行。
```
xuxianmei@XXMPC ~> grep -n "root" /etc/passwd
1:root:x:0:0:root:/root:/bin/bash
37:nm-openvpn:x:118:126:NetworkManager OpenVPN,,,:/var/lib/openvpn/chroot:/bin/false
```

## 待加其他范例