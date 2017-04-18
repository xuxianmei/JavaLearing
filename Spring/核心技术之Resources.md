# 1.介绍

Java的标准java.net.URL类及一些标准的处理器（支持一些URL前缀），无法胜任所有访问低级资源的工作。
比如，没有标准的URL实现可以用于实现访问classpath当中的资源，或者涉及ServletContext的资源。  

但是，可以为特殊的URL前缀注册新的处理器（与存在的相关前缀处理器类似，比如http:），
这项工作比较复杂，而且URL接口缺少一些功能，比如没有方法来检查资源是否存在。

# 2. Resource interface
Spring提供的Resource接口，是一个更加有能力胜任的接口，Spring的这个接口，抽象了低级资源的访问。

在Spring中，通过实现这个接口的方法，来实现一个特殊URL前缀资源的访问。

	
	public interface InputStreamSource {
		InputStream getInputStream() throws IOException;
	}	

	public interface Resource extends InputStreamSource{
	
		boolean exists();

		boolean isOpen();

		URL getURL() throws IOException;

		File getFile() throws IOException;

		Resource createRelative(String relativePath) throws IOException;

		String getFilename();

		String getDescription();
	}

Resource接口中几个重要的方法

* getInputStream()：定位和打开指定的资源，返回从这个资源中读取的InputStream
* exists()：检测是否实际存在
* isOpen()
* getDescription()

其它一些方法允许你获取一个实际的URL或者File对象代表这个资源。

Resource抽象在Spring本身内部也广泛应用，在一个方法签名中当需要使用一个资源参数时，Resource就会被当作这个参数类型。

Spring APIS中其它一些方法，（比如ApplicationContext的实现类的一些构造函数）通过使用一个普通的String类型来创建一个Resource，或者通过特殊的前缀在String path上，来允许调用者指定，一个特殊的Resource 实现必须被创建和使用。

虽然Resource在Spring当中使用很多，但也可以当作一个单独的utility class(功能类)来使用，用它来访问资源，就算你的代码不关心Spring的任何其它部分。

意思也就是，可以完全将Resource当作一个外部类库来使用，来作为URL的有力的替代。

**It is important to note that the Resource abstraction does not replace functionality: it wraps it where
possible. For example, a UrlResource wraps a URL, and uses the wrapped URL to do its work.**



# 3. Built-in Resource implementation

在Spring中提供了一系列开箱即用的 Resource实现

## 3.1 UrlResource
UrlResource包装了一个java.net.URL，可以被用于访问任何通常使用URL访问的对象，比如：
files an HTTP target,an FTP target等。  
所有的URLs都有标准化的String表示化，比如
file: 
http:
ftp:

一个UrlResource通过使用Java 代码显示的使用UrlResource构造函数来创建，但是也经常在调用包含代表路径参数的方法时被创建，


## 3.2 ClassPathResource
ClassPathResource代表从classpath中获取一个资源。
这使用 thread context class loader, a given class loader或者a given class来加载资源。
这个Resource实现，支持加载为java.io.File


## 3.3 FileSystemResource

这个Resource实现，支持加载为java.io.File和URL

## 3.4 ServletContextResource

## 3.5 InputStreamResource

## 3.6 ByteArrayResource


# 4. ResourceLoader

ResourceLoader接口被可以返回Resource实例的对象使用。

	public interface ResourceLoader {
		Resource getResource(String location);
	}
所有的application context都实现了ResourceLoader接口，所以所有的application context都可以被用于获得Resource实例。



#