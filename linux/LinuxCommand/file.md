## 目录及路径
相关命令：cd、pwd、mkdir、rmdir

1. cd 切换目录  
	`````
	.         代表当前目录
	..        代表上一层目录
	-         代表前一个工作目录
	~         代表【目前用户身份】所在的自家目录
	~account  代表 account 这个用户的自家家目录
	````
2. pwd 当前所在目录  
常用参数：  
````
-P：显示出真实路径（针对链接起效）
````
3. mkdir 创建目录  
常用参数：
````
-m：配置文件目录的权限，不使用umask定义的默认权限  
-p：递归创建子目录
````
4. rmdir（删除空目录）  
常用参数：
```
-p：连同上层空目录一起删除
```


## 文件及目录管理
相关命令：ls、cp、rm、mv、basename、dirname
1. ls 查看文件和目录信息  
用法：
````
ls [OPTION]... [FILE]...
````
常用参数：
````
-a  ：全部的文件，连同隐藏文件( 开头为 . 的目录) 一起列出来～
-A  ：全部的文件，连同隐藏文件，但不包括 . 与 .. 这两个目录，一起列出来～
-d  ：仅列出目录本身，而不是列出目录内的文件数据
-f  ：直接列出结果，而不进行排序 (ls 预设会以文件名排序！)
-F  ：根据文件、目录等信息，给予附加数据结构，例如：
      *：代表可执行文件； /：代表目录； =：代表 socket 档案； |：代表 FIFO 文件；
-h  ：列出文件的打小(例如GB、KB等等)
-i  ：列出 inode 位置，而非列出文件属性；
-l  ：长数据串行出，包含文件的属性等等数据；
-n  ：列出 UID 与 GID 而非使用者与群组的名称 (UID与GID会在账号管理提到！)
-r  ：将排序结果反向输出，例如：原本文件名由小到大，反向则为由大到小；
-R  ：连同子目录内容一起列出来；
-S  ：以文件容量大小排序！
-t  ：依时间排序
--color=never  ：不要依据文件特性给予颜色显示；
--color=always ：显示颜色
--color=auto   ：让系统自行依据设定来判断是否给予颜色
--full-time    ：以完整时间模式 (包含年、月、日、时、分) 输出
--time={atime,ctime} ：输出 access 时间或 改变权限属性时间 (ctime) 
                       而非内容变更时间 (modification time)
````
2. cp 复制  
用法：
````
cp [-adfilprsu] 源文件(source) 目的文件(destination)
cp [options] source1 source2 source3 .... directory
````
常用参数：
````
-a  ：相当于 -pdr 的意思；
-d  ：若来源文件为链接文件的属性(link file)，则复制链接文件属性而非档案本身；
-f  ：为强制 (force) 的意思，若有重复或其他疑问时，不会询问使用者，而强制复制；
-i  ：若目的文件(destination)已经存在时，在覆盖时会先询问是否真的动作！
-l  ：进行硬式连结 (hard link) 的连结文件建立，而非复制文件本身；
-p  ：连同文件的属性一起复制过去，而非使用默认属性；
-r  ：递归持续复制，用于目录的复制行为；
-s  ：复制成为符号链接文件 (symbolic link)，亦即『快捷方式』文件；
-u  ：若 destination 比 source 旧才更新 destination ！
最后需要注意的，如果来源档有两个以上，则最后一个目的文件一定要是『目录』
````
3. rm 删除文件/目录  
用法：
````
rm [OPTION]... [FILE]...
````
常用参数：
````
-f  ：就是 force 的意思，强制移除；
-i  ：互动模式，在删除前会询问使用者是否动作
-r  ：递归删除！最常用在目录的删除了
````
4. mv 移动文件/目录，文件重命名  
用法：
````
mv [OPTION] source destination
mv [OPTION] source1 source2 source3 .... directory
````
常用参数：
````
-f  ：force 强制的意思，强制直接移动而不询问；
-i  ：若目标文件 (destination) 已经存在时，就会询问是否覆盖！
-u  ：若目标文件已经存在，且 source 比较新，才会更新 (update)
````
5. basename 去除NAME中的目录和后缀  
用法：
````
basename NAME
````
常用参数：
````
-a ：支持多个NAME
-s：指定去除后缀字符
````
6. dirname 显示目录名
如：
````
dirname /home/xuxm/lazy_dog.txt  /bin/ln
output: 
/home/xuxm
/bin
````


## 查看文件内容 
相关命令：cat、tac、nl、more、less、head、tail、od
1. cat 连接文件(或标准输入)并输出到标准输出上  
用法：
````
cat [OPTION]...  [FILE]...
````
常用参数：
````
-n：显示行号
````
2. tac  连接文件(或标准输入)并倒序输出到标准输出上  
用法：
````
tac [OPTION]...  [FILE]...
````
3. nl 显示的时候，连同行一起输出  
用法：
````
nl [OPTION]...  [FILE]...
````
4. more 分页显示  
用法
````
more [OPTION]...  [FILE]...
````
常用命令：
````
h：显示所有相关命令帮助
z：下一页
b：上一页
````

5. less 分页显示  
用法
````
less [OPTION]...  [FILE]...
````
常用命令：
````
h：显示所有相关命令帮助
z：下一页
b：上一页
e：上一行
y：下一行
```` 
6. head 取出前几行显示  
用法
````
head [OPTION]...  [FILE]...
````
常用参数：
````
-n：显示几行
````
7. tail 取出后几行显示  
用法
````
tail [OPTION]...  [FILE]...
````
常用参数：
````
-n：显示几行
````
8. od 查看非文本文件  

## 文件创建及修改文件时间
相关时间：
* **modification time (mtime)**： 当该文件的 内容数据 变更时，就会更新这个时间！内容数据指的是文件的内容，而不是文件的属性或权限！

* **status time (ctime)**： 当该文件的 状态 (status) 改变时，就会更新这个时间，例如如果像是权限与属性被更改了，都会更新这个时间啊。

* **access time (atime)**： 当 该文件的内容被修改 时，就会更新这个读取时间 (access)。举例来说，我们使用 cat 去读取 /etc/passwd， 就会升级该文件的 atime。


相关命令：touch

1. touch 修改文件时间及文件创建  
用法：
````
touch [OPTION]... FILE...
````
如果文件不存在，默认会创建一个空文件，除非加上了``-c``或者``-h``。
常用参数：
````
-m：仅修改 mtime
-a：仅修改atime
````


## 文件查找 

相关命令：which、whereis、locate、find

1. which  
在PATH环境变量指定的路径中，查找可执行文件。
用法：
````
which [-a] filename...
````
参数：
````
-a：列出所有匹配的指令，而不是第一个被查找的指令名称
````
2. whereis   
查找一个命令的binary、source、及manual page文件。
用法：
````
whereis [-bmsu]  [-BMS directory... -f]  filename...
````
常用参数：
````
-b    :只找 binary 的文件
-m    :只找在说明文件 manual 路径下的文件
-s    :只找 source 来源文件
-u    :没有说明文件的文件！
````
Linux 系统会将系统内的所有文件都记录在一个数据库档案里面，	而当使用 whereis 或者是 locate 时，都会以此数据库档案的内容为准，	因此，有的时后你还会发现使用这两个执行文件时，会找到已经被删掉的文件， 	而且也找不到最新的刚刚建立的文件！	这就是因为这两个命令是由数据库当中的结果去搜索文件。（相关命令：``updatedb``）

3. locate 寻找特定文件  
用法：
````
locate [OPTION]...  PATTERN...
````
locate的使用比whereis更简单，直接在后面输入 "文件的部分名称" 后，就能得到结果了。例如我们这个例子输入的是 locate passwd，那么，在完整的文件名(包含路径名称)中，只要其中有passwd，就会被搜索显示出来。如果忘记了某个文件的完整文件名时，这是以恶很方便好用的命令。
locate来搜索文件也非常的快，这是因为locate与whereis命令一样都是从数据库中去搜索文件，所以比find命令直接去硬盘里搜索速度要快的多。
4. find   
find命令是用来在给定的目录下查找符合给定条件的文件

## 打包及压缩

相关命令：gzip,bzip2,zip,tar,gunzip

* 命令：gzip  (压缩文件)
　压缩文件后缀.gz
　ps: gzip test.txt 　　结果：生成文件test.txt.gz，原文件test.txt消失。
　注意：只能压缩文件，不能压缩目录，压缩后不保留 原始文件，压缩比大概1/5
　解压命令：gunzip

* tar （打包压缩目录）
严格意义上来说此命令为“打包”命令，将目录打包成一个``*.tar``文件
  语法：tar 选项 压缩后文件名 目录
  压缩文件后缀：``*.tar.gz``
  选项：[-zcvf]　　-c 打包
　　　　　　　　　-v 显示详细信息
　　　　　　　　　-f  指定后文件名
　　　　　　　　　-z 打包同时压缩
``mkdir Japan``                       生成Japan目录
``tar -zcvf Japan.tar.gz Japan`` 生成Japan.tar.gz压缩文件
 解压命令：tar [选项] 压缩文件.tar.gz
  [-zxvf]  -x 解包
　-v 显示详细信息
　-f  指定后文件名
　-z 解压缩
``tar -zxvf Japan.tat.gz``

* zip （压缩文件或目录）
  压缩文件后缀 :``*.zip``
  语法：zip [选项] 压缩后文件名 文件或目录
  -r    压缩目录
``zip -r Japan.zip Japan``   压缩目录Japan,生成Japan.zip文件。
``zip  test.txt.zip test.txt``  压缩文件test.txt,生成test.txt.zip文件，保留原始文件
解压命令：unzip  文件名
注意:压缩后保留原始文件，压缩比不如gzip.

* bzip2        (gzip的升级版，压缩比惊人)
  压缩文件后缀：*.bz2
 语法：bzip2 [选项] 文件
 -k  压缩后保留原始文件
 ```bzip2 -k test.txt```    生成test.txt.bz2文件，原始文件test.txt保留着。
打包压缩 ``tar -cjvf`` 打包压缩后文件名  目录
解包解压 ``tar -xjvf`` 文件


### 列出内容
Syntax|Description|Example(s)
---|---|---
gzip -l {.gz file}|	List files from a GZIP archive	|gzip -l mydata.doc.gz
unzip -l {.zip file}|	List files from a ZIP archive|	unzip -l mydata.zip
tar -ztvf {.tar.gz} tar -jtvf {.tbz2}|	List files from a TAR archive|	tar -ztvf pics.tar.gz  或 tar -jtvf data.tbz2


## 文件类型

文件类型标识|文件类型
---|---
-|普通文件
d|目录
l|	符号链接
s（伪文件）|套接字
b（伪文件）|块设备
c（伪文件）|字符设备
p（伪文件）|管道


占用存储空间的类型：文件、目录、符号链接。
符号链接记录的是路径，路径不长时存在innode里面。
其他四种：套接字、块设备、字符设备、管道是伪文件，不占用磁盘空间。

参考：[打包及压缩](https://www.cyberciti.biz/howto/question/general/compress-file-unix-linux-cheat-sheet.php)