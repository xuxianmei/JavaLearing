PrimitiveServlet类可以用于测试servlet容器。

通过两个小应用程序说明如何开发servlet窗口。

这两个servlet容器都能处理简单的servlet和静态资源。

# javax.servlet.Servlet

Servlet编程需要使用到javax.servlet和javax.servlet.http两个包下的接口和类。 
在所有的类和接口中，javax.servlet.Servlet接口最为重要。  
所有的servlet程序都必须实现javax.servlet.Servlet或继承自实现了javax.servlet.Servlet的类。

javax.servlet.Servlet接口中声明了5个方法：

	void init(ServletConfig config)
	   throws ServletException

	ServletConfig getServletConfig()

	void service(ServletRequest req,
	             ServletResponse res)
	      throws ServletException,
	             IOException

	String getServletInfo()

	void destroy()

在Servlet接口中声明的5个方法里，init()、service()、destory()方法是与servlet的生命周期相关的方法。  
当实例化某个servlet类后，servlet容器会调用其init()方法进行初始化。  
servlet只会调用该方法一次，调用后则可以执行服务方法了。  
在servlet接收任何请求之前，必须是经过正确初始化的。  
servlet程序员可以覆盖此方法，在其中编写仅需要执行一次的初始化代码。  

当servlet的一个客户端请求达到后，servlet容器就调用相应的servlet的service()方法，并将  
javax.servlet.servletRequest对象和javax.servlet.servletResponse对象作为参数传入。  
ServletRequest对象包含客户端的HTTP请求的信息，  
ServletResponse对象则封装servlet的响应信息。  
在servlet对象的整个生命周期内，service()方法会被多次调用。 

在将实例从服务中移除前，servlet容器会调用servlet实例的destory()方法。  
一般当servlet容器关闭或servlet容器要释放内存时，才会调用destory()方法。  
当destory()方法被调用后，就不会再调用该servlet实例的service()方法了。
# 编写Servlet

示例： 

	import javax.servlet.*;
	import java.io.IOException;
	import java.io.PrintWriter;
	
	public class PrimitiveServlet implements Servlet {
	
	  public void init(ServletConfig config) throws ServletException {
	    System.out.println("init");
	  }
	
	  public void service(ServletRequest request, ServletResponse response)
	    throws ServletException, IOException {
	    System.out.println("from service");
	    PrintWriter out = response.getWriter();
	    out.println("Hello. Roses are red.");
	    out.print("Violets are blue.");
	  }
	
	  public void destroy() {
	    System.out.println("destroy");
	  }
	
	  public String getServletInfo() {
	    return null;
	  }
	  public ServletConfig getServletConfig() {
	    return null;
	  }
	
	}

# 应用程序1

## 从servlet容器角度审视servlet程序的开发

对一个Servlet的每个HTTP请求，一个功能齐全的servlet的容器有以下几件事情要做：

* 当第一次调用某个servlet时，要载入该servlet类，并调用init()方法（仅此一次）；
* 针对每个request请求，创建一个javax.servlet.ServletRequest对象实例和一个javax.servlet.Servlet.Response实例；
* 调用该servlet的service()方法，将servletRequest对象和servletResponse对象作为参数传入；
* 当关闭该servlet类时，调用其destroy()方法，并卸载该servlet类。

本次示例的应用程序，只是建立了一个很小的servlet容器功能，没有实现全部功能。  
因此，它只能运行非常简单的servlet，而且也会不调用servlet的init()和destroy()。  

它会做以下几件事

* 等待HTTP请求
* 创建一个servletRequest对象和一个servletResponse对象
* 若请求一个静态资源，则调用StaticResourceProcessor对象的process()方法，传入servletRequest对象和servletResponse对象；
* 若请求的是servlet，则载入相应的servlet类，并调用其service()方法，传入servletRequest对象和servletResponse对象；

# [源代码](src/ex02)

* [HttpServer1.java](src/ex02/src/com/tlxxm/learing/HttpServer1.java)
* [Request.java](src/ex02/src/com/tlxxm/learing/Request.java)
* [Response.java](src/ex02/src/com/tlxxm/learing/Response.java)
* [StaticResourceProcessor.java](src/ex02/src/com/tlxxm/learing/StaticResourceProcessor.java)
* [ServletProcessor1.java](src/ex02/src/com/tlxxm/learing/ServletProcessor1.java)
* [Constants.java](src/ex02/src/com/tlxxm/learing/Constants.java)

## HttpServer1

与上一节介绍的HttpServer基本相同，只是在await()方法中添加了：

	// check if this is a request for a servlet or a static resource
    // a request for a servlet begins with "/servlet/"
    if (request.getUri().startsWith("/servlet/")) {
        ServletProcessor1 processor = new ServletProcessor1();
        processor.process(request, response);
    }
    else {
        StaticResourceProcessor processor = new StaticResourceProcessor();
        processor.process(request, response);
    }

## Request

对于每个HTTP请求来说，servlet容器必须创建一个ServletRequest对象和一个ServletResponse对象，并将它们作为参数传给它服务的  
servlet的service()方法。

此Request表示传递给servlet的service()方法的一个request对象。
它必须实现javax.Servlet.servletRequest接口中声明的所有方法。

此类还包括了前面介绍过的parse()，用来解析原始HTTP请求信息。

## Response 

Response类实现javax.servlet.servletResponse接口。
该类提供了servletResponse接口中声明的所有方法的实现。


##StaticResourceProcessor类

用于处理对静态资源的请求，只有一个方法，即process()方法。

## servletProcessor1类

用于处理对servlet资源的HTTP请求。只有一个方法：process()方法。  
该方法接收两个参数，一个javaxservlet.ServletRequest对象实例和一个javax.servlet.ServletResponse对象实例。  

该方法通过调用getRequestUri()方法从ServletRequest对象中获取 URI：
在本程序中对servlet的访问URI是：

 	/servlet/servletName

其中，servletName是请求的servlet资源的类名。


获取了要访问的servlet类名以后，为了载入类，需要一个类载入器：

	URLClassLoader

并且指定哪里查找要载入的类。  
对于本节的servlet窗口，类载入器会到Constants.WEB_ROOT目录中查找要载入的类。  

为了载入一个servlet类，可以使用java.net.URLClassLoader类来完成，该类是java.lang.ClassLoader的一个直接子类。 
一旦创建了URLClassLoader类的实例后，就可以使用它的loadClass()方法来载入servlet类。

loadClass()返回一个Class对象，通过此Class对象，就可以创建servlet类的实例。

通过将其向下为javax.servlet.Servlet后，调用其service()方法，并传入对应的request和response对象。

#应用程序2

主要是讨论Request和Response的安全问题，后续待完成


# 测试及数据