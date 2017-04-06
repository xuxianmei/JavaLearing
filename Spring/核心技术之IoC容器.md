# IoC 容器

## IoC容器和beans

* IoC
	通过分析一个对象对于其它的对象依赖，然后通过构造函数参数、工厂方法参数、属性进行创建  
	这些依赖对象的实例的行为。

* 容器
	当创建一个bean时，容器会将这些依赖对象实例注入到这个bean需要的地方去。


控制反转（Inversion of Control）：bean本身通过使用类的构造函数，或其它方法控制它依赖的对象的实例化。


## Spring Framework's IoC container的基础

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


## 容器概述

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





## Configuration metadata

如上图所示，Spring IoC Container通过使用一系列的configuration metadata，

configuration metadata代表了，作为一个应用程序开发人员，告诉Spring container如何  
实例化、配置、装配应用程序当中的对象。


## configuration metadata组织形式

* XML-based configuration
* Annotation-based configuration
* Java-based configuration

## XML-based configuration 示例
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
	

* id
	用来惟一标识bean定义的字符串
* class
	定义bean的类型（需要使用全路径标识符）


## 实例化一个容器

	ApplicationContext context =
		new ClassPathXmlApplicationContext(new String[] {"services.xml", "daos.xml"});

通过指定资源文件的名称（包含路径），来告知ApplicationContext从何处读取configuration metadata。

