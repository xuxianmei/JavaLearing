# HTTP

## HTTP
HTTP是应用程的一种协议，主要用于Web浏览器和Web服务器的通信协议规定。
基于“请求-响应”的协议。底层传输协议使用的是TCP。

## HTTP请求


	<method> <request-URL> <version>
	<headers>

	<entity-body>


## HTTP响应

	<version> <status> <reason-phrase>
	<headers>

	<entity-body>


# Socket类

套接字是网络连接的端点。Socket是网络上两个程序双向通讯连接的端点。 
对于一个Socket而言，它至少需要3个参数来指定：

1. 通信的目的地址
2. 使用的传输层协议（如TCP、UDP）
3. 使用的端口号

套接字使用应用程序可以从网络中读取数据，可以向网络中写入数据。
为了从一个应用程序向另一个应用程序发送消息，需要知道另一个应用程序中套接字的IP地址和端口号。  

在Java中，套接字由java.net.Socket抽象表示。

要创建一个套接字，可以使用Socket类众多构造函数中的一个。

比如：

	public Socket(String host, int port)
	        throws UnknownHostException, IOException
	    {
	        this(host != null ? new InetSocketAddress(host, port) :
	             new InetSocketAddress(InetAddress.getByName(null), port),
	             (SocketAddress) null, true);
	    }

host是远程主机的名称或ip地址，port是连接远程应用程序的端口号。

比如：  

	Socket socket = new Socket("127.0.0.1", 80);

一旦成功地创建了Socket类的实例，就可以使用该实例发送或接收字节流。
* 发送字节流   

	需要调用Socket类的getOutputStream()方法获取一个java.io.OutputStream对象。
	要发送文本，通过使用返回的OutputStream对象创建一个java.io.PrintWriter对象。

* 接收字节流  

	想从连接的另一端接收字节流，需要调用Socket类的getInputStream()方法，返回一个  
	java.io.InputStream对象


下面的示例，创建了一个套接字，用户与本地HTTP服务器进行通信：


	package com.tlxxm.learing;
	
	import java.io.BufferedReader;
	import java.io.InputStreamReader;
	import java.io.OutputStream;
	import java.io.PrintWriter;
	import java.net.Socket;
	
	public class Main {
	
	    public static void main(String[] args) {
	        try {
	            Socket socket = new Socket("127.0.0.1", 9002);
	            OutputStream os=socket.getOutputStream();
	            boolean autoflush=true;
	            PrintWriter out=new PrintWriter(socket.getOutputStream(),autoflush);
	            BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
	
	            //发送请求到服务器
	            out.println("GET / HTTP/1.1");
	            out.println("Host:localhost:9002");
	            out.println("Connection:Close");
	            out.println();
	
	            boolean loop=true;
	            StringBuffer sb=new StringBuffer(8096);
	            while (loop){
	                if(in.ready()){
	                    int i=0;
	                    while(i!=-1)
	                    {
	                        i=in.read();
	                        sb.append((char)i);
	                    }
	                    loop=false;
	                }
	                Thread.currentThread().sleep(50);
	            }
	            System.out.println(sb.toString());
	            socket.close();
	        }catch(Exception e){
	            System.out.print(e);
	        }
	    }
	}


为了从Web服务器上获取正确的响应信息，需要发送一个遵循HTTP协议的HTTP请求。

# ServerSocket


ServerSocket类与Socket类并不相同。  
服务器套接字要等待来自客户端的连接请求。  
当服务器套接字收到连接请求后，它会创建一个Socket实例来处理与客户端的通信。

服务器套接字时刻待命，等待客户端的请求。
在Java中，使用java.net.ServerSocket，这是服务套接字的实现。

要创建一个服务套接字，可以使用ServerSocket的构造函数中的任意一个。
需要指明IP地址和服务器套接字侦听的端口号。
典型情况下，IP地址可以是127.0.0.1，即服务器套接字会侦听本地机器接收到的连接请求。
服务器套接字侦听的IP地址称为绑定地址。

服务器套接字的另一个重要属性是backlog，后者表示在服务器拒绝接收传入的请求之前，传入的连接  
请求的最大队列长度。
比如：

	new ServerSocket(80, 1, InetAddress.getByName("127.0.0.1"));

# 应用程序

* 侦听  

	该程序，侦听127.0.0.1:8085。

* 功能  

	这个Web服务器可以处理对指定目录中的静态资源的请求

* 资源目录  

	该目录包括由变量WEB_ROOT指定的目录及其子目录。在程序中，这个变量值为webroot。

* 请求  

	如果要请求静态资源，可以在浏览器中输入如下URL:
		http://macineName:port/staticRescorce

* 关闭服务器  

	程序中，定义了关闭命令。
	若要关闭服务器，输入如下URL：
		http://macineName:port/SHUTDOWN

* [源代码](src/ex01/src/com/tlxxm/learing)

	[HttpServer.java](src/ex01/src/com/tlxxm/learing/HttpServer.java)
	[Request.java](src/ex01/src/com/tlxxm/learing/Request.java)
	[Response.java](src/ex01/src/com/tlxxm/learing/Response.java)

程序运行入口为 HttpServer：main()方法

进入main方法以后，创建了HttpServer的一个实例，然后运行方法await()。

await()方法会创建一个ServerSocket实例，然后进入一个while循环，等待请求到来。

当接收到请求后，ServerSocket类的accept()方法返回，等待结束。

## HttpServer.java
进入await()方法以后，并接收到了一个新的请求。

1. 获取Socket	

		socket = serverSocket.accept();
		input = socket.getInputStream();
		output = socket.getOutputStream();
	当接收到请求后，ServerSocket类的accept()返回Socker类的一个实例，
	并从该实例对象中获取java.io.InputStream和java.io.OutputStream对象实例。	

2. 创建Request对象

		 Request request = new Request(input);
    	 request.parse();
	在await()方法中，创建一个Request对象，并调用其parse()方法来解析HTTP请求的原始数据，
	得到想要的关键项。

3. 创建Response对象

		Response response = new Response(output);
        response.setRequest(request);
        response.sendStaticResource();
	创建一个Response对象，并调用它的两个方法。
4. 关闭Socket

	 socket.close();
	关闭当前accept()返回的Socket对象。

5. 退出循环等待

	如果是shutdown命令，且，退出循环等待，不再监听，程序结束。
	否则，继续侦听。

## Request.java
	
此类用来表示一个请求。通过传递Socket的InputStream来创建Request对象。
	
该类，主要就是根据请求信息来创建一个合格的Request对象，并解析请求的原始数据，填充类内  
数据字段。
主要的两个方法
* parse()

	根据Socket的InputStream来获取请求的原始信息，并对其进行读取解析，设置相应的uri字段。
* getUri()

	返回根据请求原始信息，生成的uri信息。


## Response.java

	此类主要是根据Request对象，查找相应的静态资源文件，并读取出来，将其写入到Socket的OutputStream  
	当中。


# 测试
	待完成

# 运行应用程序 


# 测试及数据