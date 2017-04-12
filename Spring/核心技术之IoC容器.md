** IoC 容器 **

# 1. IoC容器和beans

* IoC
	通过分析一个对象对其它的对象依赖，然后通过构造函数参数、工厂方法参数、属性进行创建这些依赖对象的实例的行为。

* 容器
	当创建一个bean时，容器会将这些依赖对象实例注入到这个bean需要的地方去。


控制反转（Inversion of Control）：bean本身通过使用类的构造函数，或其它方法控制它依赖的对象的实例化。


## 1.1 Spring Framework's IoC container的基础

org.springframework.beans与org.springframework.context
这两个包，组成Spring Framewokr's IoC 容器的基础。

* BeanFactory
	BeanFactory接口提供了高级配置机制来管理任何类型的对象。

* ApplicationContext
	BeanFactory子接口，通过这个接口，使得能够与其它Spring'AOP 特性更方便容易的集成：
message resource handing,event publication 以及应用层特性的上下文环境（  
比如：在web应用程序中使用的WebApplicationContext）。

简而言之，BeanFactory提供了可配置框架和基本的功能，ApplicationContext添加了更多的
企业特定的功能。

ApplicationContext是BeanFactory的超集（功能包含BeanFactory的所有功能）。

在Spring中，构成应用程序的骨架的对象（这些对象由Spring IoC容器管理）称为beans。

一个bean就是一个应用程序当众多对象中的一个。
Beans及它们之间的依赖，体现在配置元数据当中。


# 2. 容器概述

 * org.springframework.context.ApplicationContext接口
	此接口代表了Spring IoC Container，它负责  
	对前面提到的beans进行实例化、配置、装配。

容器通过读取配置元数据来决定对象何时创建、配置、装配。

配置元数据(the configuration metadata)可以使用XML,Java annotations或者Java代码来实现。


你的应用程序中的类，通过使用configuration metadata组合在一起， 
所以当ApplicationContext被创建和实例化以后，你就有了一个完整配置且可执行的应用程序。

下面这张图，展示了Spring工作时的高级视角。

**The Spring IoC Container**
>![The Spring IoC Container](images/Spring_IoC_Container.png)





## 2.1 Configuration metadata

如上图所示，Spring IoC Container通过使用一系列的configuration metadata，

configuration metadata代表了，作为一个应用程序开发人员，告诉Spring container如何  
实例化、配置、装配应用程序当中的对象。


### 2.1.1 configuration metadata组织形式

* XML-based configuration
* Annotation-based configuration
* Java-based configuration

** XML-based configuration 示例**
下面这个例子，展示了XML-based configuration的基本结构
	
	<?xml version="1.0" encoding="UTF-8"?>
	<beans xmlns="http://www.springframework.org/schema/beans"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://www.springframework.org/schema/beans
			http://www.springframework.org/schema/beans/spring-beans.xsd">
		<bean id="..." class="...">
			<!-- collaborators and configuration for this bean go here -->
		</bean>
		<bean id="..." class="...">
			<!-- collaborators and configuration for this bean go here -->
		</bean>
		<!-- more bean definitions go here -->
	</beans>
	

1. id
	用来惟一标识bean定义的字符串
2. class
	定义bean的类型（需要使用全路径标识符）


## 2.2 实例化一个容器

	ApplicationContext context =
		new ClassPathXmlApplicationContext(new String[] {"services.xml", "daos.xml"});

通过指定资源文件的名称（包含路径），来告知ApplicationContext从何处读取configuration metadata。



### 2.2.1  组合多个配置信息

当有多个XML的配置metadata时，可以通过在配置文件中，使用<import/>来组合。
比如：

	<beans>
		<import resource="services.xml"/>
		<import resource="resources/messageSource.xml"/>
		<import resource="/resources/themeSource.xml"/>
		<bean id="bean1" class="..."/>
		<bean id="bean2" class="..."/>
	</beans>

所有的路径都是根据本文件为基础的相对路径。

### 2.2.2  Groovy Bean Definition DSL


## 2.3 容器的使用

ApplicationContext是一个高级工厂的接口，这个工厂的功能是维护众多beans的注册表，及它们的依赖。

通过使用下面这个方法，就可以获取指定beans的实例。
	
	T getBean(String name,Class<T> requiredType)


通过ApplicationContext就可以读取这些bean的定义以及访问，如下：

	// create and configure beans
	ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "daos.xml");

	// retrieve configured instance
	PetStoreService service = context.getBean("petStore", PetStoreService.class);

	// use configured instance
	List<String> userList = service.getUsernameList();

最灵活的变体是GenericApplicationContext与读取代理的结合使用，比如：
* 与读取XML文件的XmlBeanDefinitionReader结合
	
		GenericApplicationContext context = new GenericApplicationContext();
		new XmlBeanDefinitionReader(ctx).loadBeanDefinitions("services.xml","daos.xml");
		context.refresh();
* 与读取Groovy文件的GroovyBeanDefinitionReader结合

		GenericApplicationContext context = new GenericApplicationContext();
		new GroovyBeanDefinitionReader(ctx).loadBeanDefinitions("services.groovy", "daos.groovy");
		context.refresh();

上述的读取代理，同样适用于ApplicationContext。且可混用。

理论上来说，不需要手动直接调用getBean()方法，这样也就不会对
Spring APIS产生依赖。
比如Spring的集成Web框架，就为很多Web框架提供了依赖注入的组件。比如controllers和JSF-managed beans，这些组件允许在特殊的bean声明依赖（比如 autowiring注解。）

# 3. Bean概述

一个Spring IoC Container管理一个或者更多的beans。
这些beans都是通过你提供给容器的configuration metdata来创建的。

对于容器本身来说，这些beans的定义，都被表示成了包含了如下metadata的BeanDefinition对象：

* 使用包限定的类名称  

	指定当前bean实际的实现类
* Bean行为配置元素  

	bean在容器中的状态行为，比如（scope,lifecycle,callbacks等）
* 对其它对象的引用  

	当前bean正常工作时，需要对其它对象的依赖
* 其它一些在创建新对象时相关的配置设置  
	比如，bean的连接数

这些组成bean定义的metadata通过下列这些属性列表来表示。

**_The bean definition_**

|属性名|
|---|
|class|
|name|
|scope|
|constructor arguments |
|properties|
|autowiring mode|
|class|
|lazy-initialization mode |
|initialization method |
|destruction method |

除了上述定义包含如何创建一个bean，ApplicationContext的实现，同样允许在容器外部创建的对象注册为bean。

通过使用getBeanFactory()返回ApplicationContext的BeanFactory接口的实现:DefaultListableBeanFactory。
DefaultListableBeanFactory支持通过registerSingleton(..) and registerBeanDefinition(..)方法来注册新的bean。
## 3.1 beans命名

每个bean都有至少一个标识符，这些标识符必须在容器当中保持惟一性。
一般来说，一个bean只要有一个标识符就可以了，但是如果有特殊需要多个标识符，可以通过
别名来完成。

通过为一个bean提供id和name来进行标识，如果想要有其它别名，可以在name属性值中，通过使用（逗号，分号，或者空格来隔开多个name值）。

如果不提供id和name，容器会自动产生一个惟一的name给bean。

通过ref的值(关联name的值)来引用关联一个bean，或者是Service Locator。

如果没有提供ref，一般使用inner beans和autowiring 指定的对象。

**注：名称命名规划采用标准的Java实例字段，第一个单词首字母小写，后续单词首字母大写**

### 3.1.1 在bean定义外部 设置别名

	<alias name="fromName" alias="toName"/>

## 3.2 创建实例化beans

一个bean的定义实际上就是创建对象的说明书，容器通过name查找被请求的bean，然后使用
bean definition中封装的configuration metadata来创建或者获取一个实际上的对象。

如果使用的是XML-based配置信息，实例化的就是<bean/>中class属性指定的值的实例。

class属性，是一个BeanDefinition实例的Class属性。

可以通过下面两个方法之一来使用这个Class属性：

* 常规，查找bean class，容器本身通过反射调用类的构造函数的直接创建这具类的实例，
	相当于，Java 代码中的new操作符

* 查找包含static工厂方法实际类，这个方法会被调用来创建这个对象实例。

*内部类名称*
>如果使用静态内部类，需要使用内部类的binary name，比如com.example.Foo类中的，静态  
>内部类Bar,设置时值为：com.example.Foo$Bar


### 3.2.1 使用构造函数创建

当使用构造函数方法来创建bean时，所有普通的类在Spring都是可用的。
这意味着，这个类不用实现任何特殊的接口或者任何特殊风格的代码。
只需要声明相关的bean class。
但是，取决于你使用的IoC类型，可以需要一个无参构造函数。

	<bean id="exampleBean" class="examples.ExampleBean"/>
	<bean name="anotherExample" class="examples.ExampleBeanTwo"/>

更多的有参构造后续介绍。


### 3.2.2 使用静态工厂方法创建
当指定使用一个静态方法返回bean的一个实例时，通过使用class来指定包含静态工厂方法的类，
通过facory-method来指定静态方法名称。
比如：
配置：

	<bean id="clientService"
		class="examples.ClientService"
		factory-method="createInstance"/>



源码：

	public class ClientService {
		private static ClientService clientService = new ClientService();
		private ClientService() {}
		public static ClientService createInstance() {
			return clientService;
		}
	}

注：上述配置，定义了返回的静态方法的所在，但是并没有指定实际返回的对象类型。

更多配置后续讲到。


### 3.2.3 使用实例工厂方法

与静态工厂方法类似，指定一个实例方法（非静态）创建返回bean。
* 首先，不对class设置值。
* 然后设置factory-bean属于，指定其值为当前容器中一个bean的name。
	指定的bean包含创建并返回并返回此bean的实例方法。
* 最后通过设置factory-method属性设置实例方法的名称。

比如：

配置

	<!-- the factory bean, which contains a method called createInstance() -->
	<bean id="serviceLocator" class="examples.DefaultServiceLocator">
		<!-- inject any dependencies required by this locator bean -->
	</bean>

	<!-- the bean to be created via the factory bean -->
	<bean id="clientService"
		factory-bean="serviceLocator"
		factory-method="createClientServiceInstance"/>

源码：	
	
	public class DefaultServiceLocator {
		private static ClientService clientService = new ClientServiceImpl();
		private DefaultServiceLocator() {}
		public ClientService createClientServiceInstance() {
			return clientService;
		}
	}

## 4. 依赖性

任何一个应用程序，都是由众多对象构成的。  

### 4.1 依赖注入(Dependency Injection)

依赖注入，是一个过程，这个过程通过定义对其它对象的依赖，然后在创建此对象的实例时，
通过构造函数参数，或者其它方法（比如属性、工厂方法参数等），创建这些依赖对象的实例。

通过DI原则，可以从它们依赖的对象当中解耦出来，代码更加简洁和具有可维护性。

一般来说，都通过依赖接口和抽象类，而不是实际类，这样也更便于单元测试。

DI主要存在两中方式：构造函数注入和属性Setter注入

### 4.1.1 构造函数依赖注入

构造函数DI，通过调用包含一系列参数（代表对其它对象的依赖）的构造函数来完成DI。
构造函数实质上也是静态方法。
如：

	public class SimpleMovieLister {
		// the SimpleMovieLister has a dependency on a MovieFinder
		private MovieFinder movieFinder;
		// a constructor so that the Spring container can inject a MovieFinder
		public SimpleMovieLister(MovieFinder movieFinder) {
			this.movieFinder = movieFinder;
		}
		// business logic that actually uses the injected MovieFinder is omitted...
	}
此类，只是一个POJO (plain old java obejct)，没有任何的容器相关的接口依赖，也没有任何基类和注解。充分证明了Spring DI的非侵入式。

* 构造函数参数解决方案

比如
源代码 ：

	package x.y;
	public class Foo {
		public Foo(Bar bar, Baz baz) {
		// ...
		}
	}

上述类Foo，对Bar和Baz有依赖。默认参数构造函数参数顺序与配置顺序一致。

XML配置：


	<beans>
		<bean id="foo" class="x.y.Foo">
			<constructor-arg ref="bar"/>
			<constructor-arg ref="baz"/>
		</bean>
		<bean id="bar" class="x.y.Bar"/>
		<bean id="baz" class="x.y.Baz"/>
	</beans>

通过使用< constructor-arg />标签来指定参数。
当一个bean被引用时，类型是可知的，匹配会正确的执行。

基本类型的构造参数
源代码：

	package examples;
	public class ExampleBean {
		// Number of years to calculate the Ultimate Answer
		private int years;
		// The Answer to Life, the Universe, and Everything
		private String ultimateAnswer;
		public ExampleBean(int years, String ultimateAnswer) {
			this.years = years;
			this.ultimateAnswer = ultimateAnswer;
		}
	}
配置：

	<bean id="exampleBean" class="examples.ExampleBean">
		<constructor-arg type="int" value="7500000"/>
		<constructor-arg type="java.lang.String" value="42"/>
	</bean>
通过指定type和value，同理也可以使用索引指定匹配的参数

	<bean id="exampleBean" class="examples.ExampleBean">
		<constructor-arg index="0" value="7500000"/>
		<constructor-arg index="1" value="42"/>
	</bean>
也可以通过name来指定

	<bean id="exampleBean" class="examples.ExampleBean">
		<constructor-arg name="years" value="7500000"/>
		<constructor-arg name="ultimateAnswer" value="42"/>
	</bean>

type指定参数类型，index指定参数列表中的参数所在的位置，value指定参数值。
name直接指定绑定的参数名称（必须以debug模式编译）。
也可以使用@ConstructorProperties JDK注解来显示命名参数。


### 4.1.2 Setter-based 依赖注入


Setter-based DI 当使用无参构造函数或者无参静态工厂方法实例化bean时，容器通过调用setter方法来完成DI动作。

比如：

	public class SimpleMovieLister {
		// the SimpleMovieLister has a dependency on the MovieFinder
		private MovieFinder movieFinder;
		// a setter method so that the Spring container can inject a MovieFinder
		public void setMovieFinder(MovieFinder movieFinder) {
			this.movieFinder = movieFinder;
		}
		// business logic that actually uses the injected MovieFinder is omitted...
	}

上述类，同样没有只是一个普通的POJO，没有任何对于容器接口的依赖，基类和注解等。

ApplicationContext支持构造函数参数注入和setter方法注入，也支持混合使用。

**注：上述两种方式的选择**

构造函数注入的依赖，主要是为了保证类中使用的依赖，都一定不为null，为安全使用起见。

但，如果一个构造函数有太多的参数，也比较不好。

setter可以根据需要进行注入，但是任何使用这个注入的对象时，都需要进行检查是否为null。

### 4.1.3 依赖性解析过程(Dependency resolution process)
容器根据如下步骤来执行依赖性解析过程：

1. ApplicationContext通过用于描述所有beans的configuration metadata创建并实例化。

2. 对于所有的bean，它的依赖信息，都通过一系列的属性、构造函数参数、或者静态工厂方法的参	数来表明。当bean被创建时，这些依赖的对象，都会被创建并实例化提供给此bean。

3. 每一个属性或者构造函数参数都是一个待设置的值，或者是容器当中另外一个bean的引用。

4. 每一个属性或者构造函数参数，都可以通过一个特殊的格式来转型为实际类型。
	默认的Spring可以通过将string格式的值，转化为所有的内置的的类型，比如  
	int,ong,String,boolean等。  

## 4.2 依赖及配置详细说明

Spring的XML-based configuration metadata支持在< property/>和< constructor-arg/>中的子元素类型。

### 4.2.1 直接值（基本类型及String）

``<property/>``的value属性指定属性的值。
比如：
	
	<bean id="myDataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
		<!-- results in a setDriverClassName(String) call -->
		<property name="driverClassName" value="com.mysql.jdbc.Driver"/>
		<property name="url" value="jdbc:mysql://localhost:3306/mydb"/>
		<property name="username" value="root"/>
		<property name="password" value="masterkaoli"/>
	</bean>

Spring的 conversion service被用来进行数据类型转换。下面是更简洁的写法：

	<beans xmlns="http://www.springframework.org/schema/beans"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xmlns:p="http://www.springframework.org/schema/p"
		xsi:schemaLocation="http://www.springframework.org/schema/beans
		http://www.springframework.org/schema/beans/spring-beans.xsd">

		<bean id="myDataSource" class="org.apache.commons.dbcp.BasicDataSource"
			destroy-method="close"
			p:driverClassName="com.mysql.jdbc.Driver"
			p:url="jdbc:mysql://localhost:3306/mydb"
			p:username="root"
			p:password="masterkaoli"/>
	</beans>

**idref元素**
用来进行错误校验

	<bean id="theTargetBean" class="..."/>
	<bean id="theClientBean" class="...">
		<property name="targetName">
			<idref bean="theTargetBean" />
		</property>
	</bean>

### 4.2.2 对其它bean的依赖


### 4.2.3 Inner beans

### 4.2.4 集合

使用ref元素，

# 5. Bean范围(Bean scopes)
Spring Framework支持七种scopes，如果使用关于web的ApplicationContext，其中5种可用。

| scope | Description|
|---|---|
|singleton|默认scope，在每个Spring IoC container中，一个bean definition对应单例|
|prototype|多个实例对象对应一个bean definition|
|request| 每一个HTTP请求，都会有一个单例实例对象对应，只有使用web相关的ApplicationContext时可用。|
|session|每一个HTTP session,都有一个单例对象对应，只有使用web相关的ApplicationContext时可用。|
|globalSession|每一个 global HTTP session，都有一个单例对象对应，只有使用web相关的ApplicationContext时可用。|
|application|一个ServletContext的生命周期内，有一个单例对象对应，只有使用web相关的ApplicationContext时可用。|
|websocket|一个WebSocket的生命周期内，有一个单例对象对应，只有使用web相关的ApplicationContext时可用。|


## 5.1 singleton scope
Spring中的默认scope。
在同一个容器内，所有关于此bean的请求引用，都返回同一个实例对象。

	<bean id="accountService" class="com.foo.DefaultAccountService"/>
	<!-- the following is equivalent, though redundant (singleton scope is the default) -->
	<bean id="accountService" class="com.foo.DefaultAccountService" scope="singleton"/>
![singletonscope](images/singletonScope.png)

## 5.2 prototype scope
同一个容器中，每一次关于此bean的请求引用，都返回一个新的实例对象。

	<bean id="accountService" class="com.foo.DefaultAccountService" scope="prototype"/>
![prototypeScope](images/prototypeScope.png)

与其它scope不同的是，Spring不会对此类型的bean进行回收和清理。


## 5.3 Request,session,global session,application,and WebSocket scopes
这些scope只在使用关于web的Spring ApplicationContext的实现中可用，比如
XmlWebApplicationContext。

### 5.3.1 初始化Web configuration
为了支持上述5种scope(web-scoped beans)，必须做一些初始化配置（initial configuration）。

如果通过使用Spring Web Mvc，不需要做相应的配置，DispatcherServlet或DispatcherPortlet  
已经完成了这部分的工作

如果使用的是Servlet 2.5 Web容器，请求信息不通过DispatcherServlet（比如，JSF或Struts）时，你需要注册  
org.springframework.web.context.request.RequestContextListener
ServletRequestListener。

如果使用的是Servlet 3.0+版本，可以通过WebApplicationInitializer接口来使用编程方式完成。
如果是较老版本的容器，需要在web应用程序中的web.xml文件中添加如下声明：

	<web-app>
		...
		<listener>
			<listener-class>
				org.springframework.web.context.request.RequestContextListener
			</listener-class>
		</listener>
		...
	</web-app>

另外一种选择是，可以考虑使用Spring的RequestContextFilter，下面这些配置信息需根据实际情况调整：
	
	<web-app>
		...
		<filter>
			<filter-name>requestContextFilter</filter-name>
			<filter-class>org.springframework.web.filter.RequestContextFilter</filter-class>
		</filter>
		<filter-mapping>
			<filter-name>requestContextFilter</filter-name>
			<url-pattern>/*</url-pattern>
		</filter-mapping>
		...
	</web-app>

DispatcherServlet，RequestContextListener，RequestContextFilter做的工作是一样的，
它们的工作就是绑定HTTP请求对象(HTTP request object)到服务于这个请求的进程(Thead)当中。

### 5.3.2 Request scope
Spring容器通过使用bean definition，为每一个HTTP请求，创建一个LoginAction实例对象，当HTTP请求完成时，此对象也会被舍弃。
**XML-based：**

	<bean id="loginAction" class="com.foo.LoginAction" scope="request"/>  



**注解(annotaion-based)：**

	@RequestScope
	@Component
	public class LoginAction {
		// ...
	}



### 5.3.3 Session scope

Spring容器通过使用bean definition,为每一个HTTP Session创建一个UserPreferences实例，当会话完成时，此对象会被舍弃。

**XML-based：**

	<bean id="userPreferences" class="com.foo.UserPreferences" scope="session"/>

**注解(annotaion-based)：**

	@SessionScope
	@Component
	public class UserPreferences {
		// ...
	}


### 5.3.4 Global session scope
与Session scope类似。
此scope只应用在portlet-based的web应用程序，此scope在所有的portlets之间共享，
如果使用此scope在Servlet-based的web应用程序中，其实使用的就是上面的 Session scope，不会  
报任何错误。
**XML-based：**

	<bean id="userPreferences" class="com.foo.UserPreferences" scope="globalSession"/>


### 5.3.4 Application scope

Spring 容器通过使用bean definition,只为整个web 应用程序创建一次实例化对象，即只一个此对象实例存在。
此scope位于ServletContext level，存储为ServletContext的一个常规属性。
这与Spring singleton bean有点类似，但它们有两个重要的不同点：
1. Application scope是每个ServletContext上的一个单例，不是每个Spring ApplicationContext上的单例
2. 可以通过访问ServletContext的属性获取到

**XML-based：**

	<bean id="appPreferences" class="com.foo.AppPreferences" scope="application"/>

**注解(annotaion-based)：**

	@ApplicationScope
	@Component
	public class AppPreferences {
		// ...
	}

### 5.3.5 使用Scoped beans作为依赖

Spring IoC Container不仅仅管理着beans的实例化，也管理着依赖对象的装配工作。
如果你需要注入一个HTTP request scoped的bean到一个scope更加长久的bean当中时，
可能会选择在注入一个AOP 代理(proxy)，用来替代这个scoped bean。
这意味着，你需要注入一个与scoped bean具有同样的public接口的proxy对象来作为scoped bean，
而且也会从相关的scope（比如一个 HTTP request）当中接收一个真实的目标对象，并委托在该真实的对象  
上调用方法。

*注：*
>scoped proxies不是实现访问scopes更加短的beans惟一方法。


	
	<?xml version="1.0" encoding="UTF-8"?>
	<beans xmlns="http://www.springframework.org/schema/beans"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xmlns:aop="http://www.springframework.org/schema/aop"
		xsi:schemaLocation="http://www.springframework.org/schema/beans
			http://www.springframework.org/schema/beans/spring-beans.xsd
			http://www.springframework.org/schema/aop
			http://www.springframework.org/schema/aop/spring-aop.xsd">
	
		<!-- an HTTP Session-scoped bean exposed as a proxy -->
		<bean id="userPreferences" class="com.foo.UserPreferences" scope="session">
			<!-- instructs the container to proxy the surrounding bean -->
			<aop:scoped-proxy/>
		</bean>
	
		<!-- a singleton-scoped bean injected with a proxy to the above bean -->
		<bean id="userService" class="com.foo.SimpleUserService">
			<!-- a reference to the proxied userPreferences bean -->
			<property name="userPreferences" ref="userPreferences"/>
		</bean>
	</beans>

为了创建这样的一个代理(proxy)，你需要在一个scoped bean definition中，
插入一个子元素`<aop:scoped-proxy/>`。
为什么scope为request、session、globalSession和custom-scope等级的beans需要  
`<aop:scoped-proxy/>`元素？

比如：

	<bean id="userPreferences" class="com.foo.UserPreferences" scope="session"/>
	<bean id="userManager" class="com.foo.UserManager">
		<property name="userPreferences" ref="userPreferences"/>
	</bean>

userManager就一个singletion scoped bean，在本例中，userManager在容器中只会被实例化一次， 
并且，如果不使用`<aop:scoped-proxy/>`,它的依赖 userPreferences，也只会被实例化一次到此bean当中。

然后这与userPreferences本身是想在违背的。

事实你需要的应该是，UserManager在容器中只会存在一个，它的其它引用，应该根据bean本身的scope情景出现，在本例中UserPreferences应该是每一个HTTP Session都有一个新的实例化对象。

当使用`<aop:scoped-proxy/>`时，容器会为此bean创建一个具有同样公共接口的对象，

在这个例子中，当一个UserManager实例调用UserPreferences对象上的一个方法时，实际是调用  
proxy对象上的方法，这个proxy对象获取真实的UserPreferences对象（本例从HTTP Session中获得），然后委托调用返回的UserPreferences对象上的方法。

	<bean id="userPreferences" class="com.foo.UserPreferences" scope="session">
		<aop:scoped-proxy/>
	</bean>
	<bean id="userManager" class="com.foo.UserManager">
		<property name="userPreferences" ref="userPreferences"/>
	</bean>

request-scoped,session-scoped,globalSession-scoped都一样。

**选择创建何种类型的proxy**

默认，当Spring Container为一个使用了`<aop:scoped-proxy/>`子元素bean创建一个proxy时，就会创建一个CGLIB-based class proxy。

CGLIB-based proxy只会拦截public方法的调用。

后续还将介绍 class-based和 interface-based proxying。


## 5.5 自定义scopes


#6. 定制bean性质

## 6.1 生命周期的回调
为了影响容器中bean生命周期的管理，你可以实现Spring InitializingBean和DisposableBean接口。
容器在之前调用afterPropertiesSet()，在之后调用destory()，来使bean在初始化和销毁时，完成确定  
的动作。
*注：*
>  JSR-250 @PostConstruct 和 @PreDestroy 注解在现行的Spring应用程序中，是最好的考虑方案来接收  
>  生命周期的回调，使用这些注解时，意味着可以将beans从Spring独有的接口中解耦出来。
>  如果不使用此注解，也可以使用init-method和destroy-method对象定义metadata

在Sping内部，Spring Framework使用BeanPostProcessor实现来完成查找回调接口并执行对应的方法。  

如果你需要自定义一些特性或在Sping当中没有提供的开箱即用的生命周期行为，你可以自己来实现一个
BeanPostProcessor。这将在容器扩展点里介绍。                                                                                                                                                                                                                                                                                                                                                                                                      

Spring管理的对象，除了实例化和销毁时的回调，还可以通过实现Lifecycle接口，来驱动这些对象在容器  
本身的启动和关闭来完成某些动作。

### 6.1.1 实例化回调
org.springframework.beans.factory.InitializingBean接口允许当容器为一个bean设置了所有  
的必需属于以后，来完成实例化的工作（回调）。
此接口只有一个方法:

	void afterPropertiesSet() throws Exception;
但是，并不推荐使用这个接口，因为对代码具有侵入性。
相对的，可以通过使用@PostConstruct注解来指定一个POJO的初始化回调方法。

**XML-based：**
配置：

	<bean id="exampleInitBean" class="examples.ExampleBean" init-method="init"/>
源代码：

	public class ExampleBean {
		public void init() {
			// do some initialization work
		}
	}


**使用InitializingBean接口**
配置：

	<bean id="exampleInitBean" class="examples.AnotherExampleBean"/>
源代码：

	public class AnotherExampleBean implements InitializingBean {
		public void afterPropertiesSet() {
			// do some initialization work
		}
	}
对于XML-based的配置，使用init-method属性来指定方法名称
对于Java config，使用@Bean的initMethod属性指定

### 6.1.2 销毁方法回调
org.springframework.beans.factory.DisposableBean接口，允许当Bean从容器中销毁时，执行  
销毁相关的方法回调。
此接口同样只有一个方法：

	void destroy() throws Exception;
同样，不推荐使用，因为没有从源码中解耦出来。推荐使用@PreDestroy注解。

**XML-based：**
配置：  

	<bean id="exampleInitBean" class="examples.ExampleBean" destroy-method="cleanup"/>
源代码：

	public class ExampleBean {
		public void cleanup() {
			// do some destruction work (like releasing pooled connections)
		}
	}

**使用DisposableBean接口**
配置： 

	<bean id="exampleInitBean" class="examples.AnotherExampleBean"/>
源代码：

	public class AnotherExampleBean implements DisposableBean {
		public void destroy() {
			// do some destruction work (like releasing pooled connections)
		}
	}

对于XML-based的配置，使用destroy-method属性来指定方法名称
对于Java config，使用@Bean的destroyMethod属性指定

### 6.1.3 设置默认的初始化回调和销毁回调

通过在beans中设置默认的方法名称，这样就无须在所有的bean定义中指定

	<beans default-init-method="init">
		<bean id="blogService" class="com.foo.DefaultBlogService">
			<property name="blogDao" ref="blogDao" />
		</bean>
	</beans>
default-init-method 是顶层`<beans/>`元素上的一个属性，如果一个bean存在一个这样的方法，
那么当bean被创建时，就会在相应的时间调用这个bean的init方法。
当然，可以通过前面的两个属性在bean上重新定义这两个回调方法的名称。

### 6.1.4 组合生命周期机制

Spring有三种方式来控制bean的生命周期中的行为。
1. InitializingBean和DisposableBean回调接口
2. 自定义 init()和destroy()方法
3. @PostConstruct和@PreDestroy注解

当这三种方式同时对初始化回调进行设置时，如果这三种方式指定的是同一个方法，那么这个方法只会执行一次。

如果配置了不同的初始化回调方法，会按照如下顺序执行：
* @PostConstruct注解的方法
* 实现InitializngBean接口的afterPropertiesSet()方法
* 自定义的init()方法

如果配置了不同的销毁回调方法，会按照如下顺序执行：

* @PreDestroy注解的方法
* 实现DisposableBean接口的destroy()方法
* 自定义的destroy()方法


### 6.1.6 Startup and shutdowncallbacks

## 6.2 ApplicationContextAware and BeanNameAware

## 6.3 Other Aware interfaces


#7. Bean definition 继承

通过使用继承，可以重用一些Bean definition,并且可以将某些Bean definition当作模板使用，
这样减少了很多的输入。

如果你使用编程方式来与ApplicationContext接口，进行交互，ChildBeanDefinition代表子bean definitions。  

大部分用户通常使用configuring bean definitions声明，这个connfiguring bean definitons通过  
ClassPathXmlApplicationContext内部使用。

**XML-based：**
使用parent属性来设置parent bean。

	<bean id="inheritedTestBean" abstract="true"
			class="org.springframework.beans.TestBean">
		<property name="name" value="parent"/>
		<property name="age" value="1"/>
	</bean>
	<bean id="inheritsWithDifferentClass"
			class="org.springframework.beans.DerivedTestBean"
			parent="inheritedTestBean" init-method="initialize">
		<property name="name" value="override"/>
		<!-- the age property value of 1 will be inherited from parent -->
	</bean>
一个子bean definition可以继承父bean definition中继承 scope、constructor arugment values,  
property values、method overrides，同时也可以设置新的值。
任何scope、initialization method、destroy method、static factory method 设置，都可以子bean中，进行  
覆盖重写。

余下的设置，总是从子definition中获取：depends on，autowire mode，dependency check，singleton,lazy init。

如果要将一个bean设置为bean definitions模块，只需要将abstract设置为true即可，这个bean并不指定任何一个类，只是用来当模块使用。
比如：

	<bean id="inheritedTestBeanWithoutClass" abstract="true">
		<property name="name" value="parent"/>
		<property name="age" value="1"/>
	</bean>
	<bean id="inheritsWithClass" class="org.springframework.beans.DerivedTestBean"
			parent="inheritedTestBeanWithoutClass" init-method="initialize">
		<property name="name" value="override"/>
		<!-- age will inherit the value of 1 from the parent bean definition-->
	</bean>



#8. 容器扩展点
## 8.1使用一个BeanPostProcessor来自定义 beans
通过BeanPostProcessor接口定义的回调方法，你可以自己实现实例化逻辑和依赖方案逻辑。
## 8.2 使用一个BeanFactoryPostProcessor来自定义configuration metadata

## 8.3 使用一个FactoryBean自定义实例初始化逻辑

#9.（基于注解的容器配置） Annotation-based container configuration

通过使用注解将配置放到component class上的相关类、方法、字段声明。
这些注解与BeanPostProcessor结合使用，来扩展了Spring IoC Container。
比如：@Required、@Autowired、@PostConstruct、@PreDestroy @Inject、@Named。

通过在XML-based Spring configuration中，来通过使用<context:annotation-config/>来显示  
注册这些功能。

	<?xml version="1.0" encoding="UTF-8"?>
	<beans xmlns="http://www.springframework.org/schema/beans"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xmlns:context="http://www.springframework.org/schema/context"
		xsi:schemaLocation="http://www.springframework.org/schema/beans
			http://www.springframework.org/schema/beans/spring-beans.xsd
			http://www.springframework.org/schema/context
			http://www.springframework.org/schema/context/spring-context.xsd">

		<context:annotation-config/>
	</beans>

>隐含的注册post-processors包括：AutowiredAnnotationBeanPostProcessor,
>CommonAnnotationBeanPostProcessor, PersistenceAnnotationBeanPostProcessor,
>RequiredAnnotationBeanPostProcessor.

*注：*
><context:annotation-config/>仅查找位于同一个appliction context中beans上的注解。
>如果将 <context:annotation-config/>放到一个DispatcherServetl中的WebApplicationContext里。
>它只会查找在controllers中使用@Autowired修饰的beans。

##9.1 @Required

@Required注解应用于属性的setter方法。如：

	public class SimpleMovieLister {
		private MovieFinder movieFinder;
		@Required
		public void setMovieFinder(MovieFinder movieFinder) {
			this.movieFinder = movieFinder;
		}
		// ...
	}
这个注解简单的表示，被影响的属性必须在配置阶段存在，方式可以是在一个bean definition  
中有显式的属性值，或者通过autowiring。
如果在配置阶段不存在，容器就会抛出异常。 
这主要通过这个异常，来避免产生NullPointerException的错误。

##9.2 @Autowired
自动装配
**@Atuowired 应用于构造函数（constructors）**

	public class MovieRecommender {

		private final CustomerPreferenceDao customerPreferenceDao;

		@Autowired
		public MovieRecommender(CustomerPreferenceDao customerPreferenceDao) {
			this.customerPreferenceDao = customerPreferenceDao;
		}
		// ...
	}

**@Atuowired 应用于传统的setter方法**

	public class SimpleMovieLister {

		private MovieFinder movieFinder;

		@Autowired
		public void setMovieFinder(MovieFinder movieFinder) {
			this.movieFinder = movieFinder;
		}
		// ...
	}



**@Atuowired 应用于任何名称方法或者(和)具有多个参数的方法**

	public class MovieRecommender {

		private MovieCatalog movieCatalog;

		private CustomerPreferenceDao customerPreferenceDao;

		@Autowired
		public void prepare(MovieCatalog movieCatalog,
		CustomerPreferenceDao customerPreferenceDao) {
			this.movieCatalog = movieCatalog;
			this.customerPreferenceDao = customerPreferenceDao;
		}
		// ...
	}

**@Autowired 应用于字段，同时也可以应用构造函数**

	public class MovieRecommender {

		private final CustomerPreferenceDao customerPreferenceDao;

		@Autowired
		private MovieCatalog movieCatalog;

		@Autowired
		public MovieRecommender(CustomerPreferenceDao customerPreferenceDao) {
			this.customerPreferenceDao = customerPreferenceDao;
		}
		// ...
	}

**@Autowired 应用于数组类型的字段，来提供所有从ApplicationContext中来的beans一个实际类型**

	public class MovieRecommender {

		@Autowired
		private MovieCatalog[] movieCatalogs;

		// ...
	}
	
*注：*
>每个类中只能有一个构造函数使用@Required

## 9.3 @Primary

因为自动装配(autowiring)可能会导致存在多个候选，这时对于这个选择过程就有必要控制。
其中一种解决方法就是使用Spring的@Primary注解。
此注解表示，当有多个候选beans可应用于一个单值的依赖，被此注解修饰的bean具有优先权。

比如下面这个例子，
配置：

	@Configuration
	public class MovieConfiguration {
	
		@Bean
		@Primary
		public MovieCatalog firstMovieCatalog() { ... }
	
		@Bean
		public MovieCatalog secondMovieCatalog() { ... }
		// ...
	}

代码：

	public class MovieRecommender {
		@Autowired
		private MovieCatalog movieCatalog;
		// ...
	}

相应的bean defintions：

	<?xml version="1.0" encoding="UTF-8"?>
	<beans xmlns="http://www.springframework.org/schema/beans"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xmlns:context="http://www.springframework.org/schema/context"
		xsi:schemaLocation="http://www.springframework.org/schema/beans
			http://www.springframework.org/schema/beans/spring-beans.xsd
			http://www.springframework.org/schema/context
			http://www.springframework.org/schema/context/spring-context.xsd">

		<context:annotation-config/>

		<bean class="example.SimpleMovieCatalog" primary="true">
		<!-- inject any dependencies required by this bean -->
		</bean>

		<bean class="example.SimpleMovieCatalog">
		<!-- inject any dependencies required by this bean -->
		</bean>

		<bean id="movieRecommender" class="example.MovieRecommender"/>
	</beans>


##9.4 @Qualifer
@Qualifer也作用于上述的选择过程当中，控制更加细致

##9.5 其它：@Resource @PostConstruct @PreDestroy

#10.类路径 扫描和管理的componetns

本节中的大部分例子，都是使用XML来指定configuration metadata来为Spring Container提供每一个  
BeanDefinition。
基于注解的container configuration，演示了通过源码层次注解提供一些configuration metadata。
然而，基本的bean definitons仍然是显式的定义在XML文件中，这些注解只是驱动了依赖注入这块(dependency injection)。

这里主要讲述，通过scanning the classpth，来隐式的查找候选components。
候选components是符合一些筛选条件，且在容器中有对应的bean definition注册的类。

这使得不再需要使用XML来完成bean的注册，而改用注解、AspectJ type expressions或者你自己定义  
的筛选条件来选择将哪些类注册为容器中的bean defintions。

*注：*
>从Spring 3.0起，core Spring Framwork构造部分中的JavaConfig，提供了很多特性。
>这些特性允许你使用Java而不是传统的XML文件来定义beans。
>比如:@Configuration,@Bean,@Import,@DependsOn

## 10.1 @Componnet和futher stereotype annotations

Spring提供further stereotype annotations:@Component，@Service，@Controller，@Repository.

@Component是一个任何一个Spring管理的component的普通的stereotype（类似模板）。

@Repository，@Service，@Controller是@Component的特殊类型，适用于更特殊的使用场景，  
比如，分别在持久化层、服务层和表现层。

所以，你可以使用@Component来注解component class，但是如果使用@Repository，@Service，@Controller  
来注解他们，你的类将会更加适用于工具的处理，或者是切面的结合。

比如，这些sterotype annotations为切点创建理想的目标。

所以，如果你正在选择使用@Component还是@Service来用于服务层（service layer)，
@Service明显是更好的选择。
同样的，@Repository已经在持久化层提供了自动化异常事务。

## 10.2 元注解(Meta-annotations)

Spring提供了很多可以当作元注解（meta-annotations）的注解。
元注解是一个可以用来修饰其它注解的注解。

比如@Service就是一个使用了@Component修饰过的注解。

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@Component // Spring will see this and treat @Service in the same way as @Component
	public @interface Service {
		// ....
	}


元注解也可以组合在一起，来创建组合注解，比如SpringMvc中的@RestController注解就是
@Controller和@ResponseBody的组合体。

除此之外，组合注解可以选择性的重新声明元注解中的属性，来完成用户的自定义化。  
这对于想对外公布元注解的一个子集时，特别有用。

比如，Spring的@SessionScope注解硬编码了scope名称，但是仍然允许proxyMode的自定义。


	@SessionScope可以设置proxyMode来使用:
	@Service
	@SessionScope
	public class SessionScopedService {
		// ...
	}

或者重写proxyMode的值来使用:

	@Service
	@SessionScope(proxyMode = ScopedProxyMode.INTERFACES)
	public class SessionScopedUserService implements UserService {
		// ...
	}

## 10.3 自动发现类和自动注册bean defintions

Spring能够自动检测发现stereotyped类，并在ApplicationContext中注册相应BeanDefintion。

比如，下面这两个类，都符合自动发现：



	@Service
	public class SimpleMovieLister {

		private MovieFinder movieFinder;

		@Autowired
		public SimpleMovieLister(MovieFinder movieFinder) {
			this.movieFinder = movieFinder;
		}
	}

和
 
	@Repository
	public class JpaMovieFinder implements MovieFinder {
		// implementation elided for clarity
	}

为了自动发现这些类及注册相应的beans，你需要在@Configuration类中添加@ComponentScan注解，  
ComponentScan的basePackages属性是上面这两个类的基本的parent package。（也可以在basePackages  
中设置多个package，通过逗号、空格、分号隔开多个package项。）

	@Configuration
	@ComponentScan(basePackages = "org.example")
	public class AppConfig {
		...
	}

等同的XML:

	<?xml version="1.0" encoding="UTF-8"?>
	<beans xmlns="http://www.springframework.org/schema/beans"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xmlns:context="http://www.springframework.org/schema/context"
		xsi:schemaLocation="http://www.springframework.org/schema/beans
				http://www.springframework.org/schema/beans/spring-beans.xsd
				http://www.springframework.org/schema/context
				http://www.springframework.org/schema/context/spring-context.xsd">

		<context:component-scan base-package="org.example"/>

	</beans>
`<context:component-scan/>`隐式的开启了`<context:annotation-config>`功能，同时也隐式的引入 
了AutowiredAnnotationBeanPostProcessor和CommonAnnotationBeanPostProcessor注解。


##10.4 使用筛选条件来自定义扫描

默认时，使用@Component、@Repository、@Service、@Controller注解的类，或者使用了@Component  
注解的自定义注解修饰的类，这些类，会是待发现的components。

可以通过自定义filters修改或者扩展这些行为。

##10.5 在components中定义bean metadata

Spring components也会提供bean definition metadata给容器。
使用@Bean注解来完成这项工作（@Bean也同样用于在@Configuration中来提供bean metadata）。

如：

	@Component
	public class FactoryMethodComponent {

		@Bean
		@Qualifier("public")
		public TestBean publicInstance() {
			return new TestBean("publicInstance");
		}

		public void doWork() {
			// Component method implementation omitted
		}
	}

上面这个类是一个Spring component
1. 这个类包含doWork()方法，这个方法是完成应用程序内功能的代码（与bean definition无关）。
2. 提供了一个bean defintion，这个定义包括一个工厂方法（publicInstance）.

@Bean注解定义了工厂方法和bean的其它定义属性，比如使用@Qualifier来设置qualifer属性。
其它在方法level上的注解，也可以应用上来，比如@Scope、@Lazy和其它自定义的qualifier注解。

也可支持自动装配的字段和方法，比如：

	@Component
	public class FactoryMethodComponent {

		private static int i;

		@Bean
		@Qualifier("public")
		public TestBean publicInstance() {
			return new TestBean("publicInstance");
		}

		// use of a custom qualifier and autowiring of method parameters
		@Bean
		protected TestBean protectedInstance(
			@Qualifier("public") TestBean spouse,
			@Value("#{privateInstance.age}") String country) {
			TestBean tb = new TestBean("protectedInstance", 1);
			tb.setSpouse(spouse);
			tb.setCountry(country);
			return tb;
		}

		@Bean
		private TestBean privateInstance() {
			return new TestBean("privateInstance", i++);
		}

		@Bean
		@RequestScope
		public TestBean requestScopedInstance() {
			return new TestBean("requestScopedInstance", 3);
		}
	}

## 10.6 自动发现的components(autodetected components)的命名











#12. Java-based container configuration

# 13. 环境抽象

#14. BeanFactory

# 15.ApplicationContext 额外的能力