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

当在一个指定的application context当中调用getResource()时，如果路径名称，没有使用任何特殊的前缀，那么会根据这个application context的实际类型，来返回一个对应的Resource类型。 
比如：下面这个代码在ClassPathXmlApplicationContext环境中执行

	Resource template = ctx.getResource("some/resource/path/myTemplate.txt");

这将会返回得到一个ClassPathResource,同样的，如果这个方法在FileSystemXmlApplicationContext当中执行，会返回一个FileSystemResource，如果在
WebApplicationContext中执行，会返回一个ServletContextResource。

如果想要返回指定类型的Resource，可以通过为路径添加指定的前缀来完成这项工作。

	Resource template = ctx.getResource("classpath:some/resource/path/myTemplate.txt");

	Resource template = ctx.getResource("file:///some/resource/path/myTemplate.txt");

	Resource template = ctx.getResource("http://myhost.com/resource/path/myTemplate.txt");

表格：下面的表格总结了使用前缀来创建对应的Resource类型的对应表。

|前缀|例子|说明
|---|---|---|
|classpath:|`asspath:com/myapp/config.xml`| 从classpath中加载|
|file:|`file:///data/config.xml`|从文件系统中加载为一个URL
|http:|`http://myserver/logo.png`|加载一个URL|
|无|`/data/config.xml`|具体依赖于ApplicationContext|


# 5. ResourceLoaderAware接口

这个接口是一个特殊的标记接口，标识拥有一个ResourceLoader引用的对象。
````
public interface ResourceLoaderAware {
	void setResourceLoader(ResourceLoader resourceLoader);
}
````

当一个类实现了这个接口，并且部署到application conxtext当（比如受Spring管理的bean）中时,它就会被这个application context当作为ResourceLoaderAware。

application context将会调用setResourceLoader方法，并将它自己作为参数传入这个方法（记住，所有的application context都实现了ResourceLoader接口）。

所以，因为ApplicationContext就是ResourceLoader类型，bean就可以实现ApplicationContextAware接口，然后直接使用提供的application context来加载资源。  
但是，通常使用能满足所有需求的特定ResourceLoader会更好。这样一来，代码就只需要依赖于  
作为辅助功能的资源加载接口，而不是整个Spring ApplicationContext接口。

# 6. Resources作为依赖

如果bean想要通过一系列动态方法来决定和提供资源路径，那么使用ResourceLoader接口来加载资源就显得非常有意义了。

所有的application context注册并使用了一个特殊的JavaBeans：`PropertyEditor`来将String路径转换为Resource对象，这使得注入Resource属性值很简单方便。
比如：

````
<bean id="myBean" class="...">
	<property name="template" value="some/resource/path/myTemplate.txt"/>
</bean>
````

value值不加前缀时，根据application context本身来返回指定的类型，也可以加前缀指定。


# 7. Application contexts和Resource paths

## 7.1 构造application contexts

一个Application context构造器，通常使用字符串或者字符串数组来作为资源（比如：组成context定义的XML文件）的定位路径。

Resource类型，根据使用的构造器来决定：比如


````
//从classpath中加载bean定义文件，并且使用了ClassPathResource
ApplicationContext ctx = new ClassPathXmlApplicationContext("conf/appContext.xml");

//从文件系统中加载bean定义文件（在本例中，是相对工作目录的路径），
ApplicationContext ctx = new FileSystemXmlApplicationContext("conf/appContext.xml");


//从从classpath中加载bean定义文件,并且使用了ClassPathResource，使用了前缀就会覆盖Resource的默认类型
ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath:conf/appContext.xml");
````

## 7.2 构造ClassPathXmlApplicationContext实现 快捷方式

ClassPathXmlApplicationContext 具有一系列的构造函数来简化便利实例初始化工作。

最基本的一个方法是，两个参数，第1个参数提供一个String数组，这个数组的每一项，都是配置的  
文件名称（但不包含路径信息），第2个参数提供一个Class。
ClassPathXmlApplicationContext从这个class中来获取路径信息。
比如下面这个目录：

```
com/
	foo/
	services.xml
	daos.xml
	MessengerService.class
```
使用如下方式来构造：
```
ApplicationContext ctx = new ClassPathXmlApplicationContext(
new String[] {"services.xml", "daos.xml"}, MessengerService.class);
```

## 7.3 appliction context构造器资源路径中的通配符

application context构造器中的资源路径，除了一对一的目标资源。  
还可以是特殊的 ``classpath*:`` 前缀和内部的Ant-style正则表达式，这两个都是有效的通配符。

### 7.3.1 Ant-style Patterns

### 7.3.2 The Classpath*: portability classpath*: prefix

### 7.3.3 其它

## 7.4 FileSystemResource说明

下面这两个都是相对路径
````
ApplicationContext ctx =
new FileSystemXmlApplicationContext("conf/context.xml");

ApplicationContext ctx =
new FileSystemXmlApplicationContext("/conf/context.xml");
````

下面这两个一个是相对，一个是绝对
````
FileSystemXmlApplicationContext ctx = ...;
ctx.getResource("some/resource/path/myTemplate.txt");
FileSystemXmlApplicationContext ctx = ...;
ctx.getResource("/some/resource/path/myTemplate.txt");
````

如果要使用绝对路径，最好使用``file:``前缀
```
// actual context type doesn't matter, the Resource will always be UrlResource
ctx.getResource("file:///some/resource/path/myTemplate.txt");

// force this FileSystemXmlApplicationContext to load its definition via a UrlResource
ApplicationContext ctx =
new FileSystemXmlApplicationContext("file:///conf/context.xml");
```