
参考：
* [linux-package-managers](https://www.tecmint.com/linux-package-managers/)
* [difference-between-apt-and-aptitude](https://www.tecmint.com/difference-between-apt-and-aptitude/)
   


## 包管理程序

包管理程序，是一系列软件工具的集合，通过这些工具，可以自动化下载、安装、升级、配置、移除软件程序。

### 包管理程序通常包含以下特性

* 包下载

提供一个仓储来供存储软件，下载直接从这个仓储当中下载。

* 依赖解决

解决一个程序依赖的包，自动下载。

* 标准的二进制包格式

统一了软件包的格式，解决兼容性问题

* 通用的安装和配置位置


* 额外的系统相关的配置和功能

* 质量控制

## DPKG——Debian Package Management System

DPKG是一个应用于Debian-based(Debian、Ubuntu等)的Linux系列中的工具，主要适用于**.deb**包。

### dpkg  

dpkg不像其它包管理系统，它不能自动下载和安装包、或者安装它们的依赖。
只能用来管理本地的包。

* 用法

````
dpkg [option... action
````
#### 常见用法

* 列出已安装包

````
dpkg -l
````

* 安装包

````
dpkg -i zsh.deb
````

* 移除包

````
dpkg -r zsh
````


* 查看包内容

````
dpkg -c zsh.deb
````
* 检查包安装与否

````
dpkg -s zsh
````

* 检查包安装位置

````
dpkg -L zsh
````

* 安装一个目录中所有包

````
dpkg -R --install debpackages/
````
* 解包

````
dpkg --unpack flashplugin-nonfree_3.2_i386.deb
````

* 重新配置一个解开的包


````
dpkg --configure flashplugin-nonfree
````




### apt(Advanced Package Tool)

APT是一个主要在Ubuntu-based Linux中使用。

#### 常用用法

* 安装

````
apt install glances
````

* 查找包安装位置

````
apt content glances
````

* 检查依赖

````
apt depends glances
````

* 查找包

````
apt search apache
````

* 查找包信息

````
apt show firefox
````

* 显示已安装包版本

````
apt version firefox
````

* 更新包列表

````
apt update
````

* 升级包

````
apt upgrade
````

### apt-get（基于 apt）与apt-cache

apt-get是一个与apt共同工作的命令行程序。

apt-cache命令行工具，用于查找apt软件包缓存。

#### apt-cache 常用用法

* 包查找

````
apt-cache search nginx
````
* 检查包信息

````
apt-cache show netcat
````

* 缓存统计信息

````
apt-cache stats
````

#### apt-get 常用用法

* 安装

````
apt-get install nginx
````
安装指定版本：
````
 apt-get install vsftpd=2.3.5-3ubuntu1
````

* 卸载包

````
apt-get remove nginx
````

* 更新包列表

````
apt-get update
````

* 升级软件包

````
apt-get upgrade
````

* 清理已下载的包

````
apt-get clean
````


### aptitude（更高级的管理工具，多一个gui端管理工具）


## Red Hat Linux

* PRM
* YUM
* DNF

## Arch Linux

* pacman

## openSUSE Linux

* Zypper
* 