
参考：
[Secure Shell](https://en.wikipedia.org/wiki/Secure_Shell)
[https://www.ssh.com/ssh/protocol/](https://www.ssh.com/ssh/protocol/)

[http://blog.robertelder.org/what-is-ssh/](http://blog.robertelder.org/what-is-ssh/)

[SSH原理与运用（一）：远程登录](http://www.ruanyifeng.com/blog/2011/12/ssh_remote_login.html)
[数字签名](http://www.ruanyifeng.com/blog/2011/08/what_is_a_digital_signature.html)

## 什么是SSH
Secure Shell（SSH），是一个用于保证计算机之间的信息交流安全的加密网络协议。

最常使用的地方就是用户远程登录其它计算机操作系统。

SSH在一个不保证安全的网络的"客户端-服务端"架构中，提供了一个安全通道，通过SSH server与
SSH client的连接来完成。

一般的程序，比如：命令行登录、远程命令执行，都可以使用SSH保证安全。  
任何网络服务都可以使用SSH来保证安全性。

SSH主要有两个发布版本：SSH-1、SSH-2。

SSH是设计被用来替代 Telnet、不安全的远程shell协议（如：rlogin,rsh,rexec）、不安全的文件传输协议（如:FTP），这些  
协议将信息通过明文发送，致使它们可以被拦截以及被数据包分析暴露信息。



## SSH用途

SSH经典用途是登录一个远程机器及执行命令，但是也支持tunneling、转发TCP端口、X11连接。
SSH还支持使用关联的SSH file transfer（SFTP）或者secure copy（SCP）协议来传输文件。
SSH使用的是 client-server 模型。
默认使用的是标准的TCP端口号22。

## SSH工作原理

SSH工作在 client-server模型中，这意味着，需要使用SSH client来创建连接到SSH server上。  
SSH client驱动连接设置进程，使用 public key 加密算法来验证SSH server的身份。
在设置阶段以后，SSH协议使用对称加密及哈希算法，来确保客户机和服务器之间传输的数据的隐私性和完整性。



## SSH实现

SSH只是一种协议，存在多种实现，既有商业实现，也有开源实现。最常见的[OpenSSH](https://www.openssh.com/)，开源免费。



### SSH使用

参考：[http://blog.robertelder.org/what-is-ssh/](http://blog.robertelder.org/what-is-ssh/)