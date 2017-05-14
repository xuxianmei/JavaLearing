
# 1.介绍
Aspect-Oriented Programming(AOP)通过提供思考编程结构的另一种方式,  
补足和完善了面向对象编程(Object-Oriented Programming)。

类是OOP中模块关键的单元，切面是AOP中模块中关键的单元。


切面实现了模块化的关注，比如：事务管理。

AOP framework是Spring中一个关键的组件。
但Spring IoC窗口不依赖于AOP，这意味着，如果不想用AOP，就可以不使用。
AOP完善Spring来提供一个更有能力的中间件解决方案。

注：
>Spring 2.0 AOP
>Spring 2.0 引入了一个更加简单和有力的方式来编写自定义切面，使用schema-based方式或者  
>@AspectJ annotation方式。Spring 2.0 AOP保持向后兼容Spring 1.2 AOP。
>后续会介绍1.2 APIs提供的低级AOP。

AOP在Spring Framework中被用来
* 提供声明式事务服务，最重要的服务是声明式事务管理。
* 允许用户实现自定义切面，使用AOP来完善OOP。

# 1.1 AOP概念

这里主要介绍一些AOP的概念和术语。

* 切面(Aspect)：横切多个类的关注模块（横切关注点）。事务管理是Java企业应用程序中一个横切关注的好例子。在Spring AOP中，切面通过使用正常的类（schema-based approach）或者使用@Aspect注解  
修饰的类（@Aspect style）

* 连接点(Join point)：程序执行过程中的一个点，比如方法的执行或异常的处理。
在Spring AOP中，连接点始终代表一个方法的执行。

* 通知（Advice）：切面在一个实际的连接点执行的操作。包含三种不同类型的通知：  
“around","before","after"。包括Spring在内的许多AOP框架，将通知当作拦截器，维护着  
连接点上的拦截器链。

* 切点(Pointcut）：用来匹配连接点的谓语。通知与切点表达式结合，在根据切点匹配的连接点上运行（比如，一个指定名称的方法的执行）。基于切点表达式匹配的连接点的概念是AOP的中心，Spring默认使用AspectJ切点表达式。
* 引入（Introduction）：声明附加的方法和字段来增强一个类型。Spring AOP允许你引入新的  
接口（及相应的实现）到任何被通知的对象上。比如，你可以使用引入来创建一个bean实现IsModified接口，简化缓存。

* 目标对象(Target object)：对象可以被一个或多个切面通知。也可以称为advised object，因为  
Spring AOP是使用运行时proxies实现的，所以这个对象永远是一个proxied object

* AOP proxy：一个为了实现切点联系，而被AOP框架创建的对象。在Spring Framework，AOP proxy 可以是一个JDK dynamic proxy或CGLIB proxy。

* 织入（Weaving）：连接萧山与其它应用程序类型或对象来创建一个 advised object。
这可以在编译时（比如使用AspectJ compiler）、加载时、运行时完成。
Spring AOP和其它纯净的Java AOP 框架一样，在运行时执行织入。

**通知类型**

* Before advice
在连接点之前执行，除非抛出异常。
* After returning advice
在连接点正常结束时执行，比如一个方法没有抛出异常的正常返回。
* After throwing advice
在一个方法抛出异常时执行。
* After(finally) advice
无论连接点返回是正常返回，还是异常抛出结束，都会执行。
* Around advice
这个advice就像是一个方法调用器。这是最有力的advie类型。
Around advice能够做到自定义在方法调用前和调用后的行为。
它也负责选择是否执行正常结束，还是抛出异常来提前结束。

推荐在使用advice时，使用满足需求的最小advice(恰好能做这件事，而不是什么样的需求都提供功能最强的Around advice)。

在Spring 2.0中，所有的通知参数都是静态类型，所以使用的参数都是相应适合的参数（比如方法调用的返回值类型），而不是Object arrays。

AOP区分其它只提供拦截器的老技术的关键点就是，使用切点匹配连接点概念。

切点使得advice能够独立于面向对象机制。

比如，一个提供声明式事务管理的aound advice，能够应用到跨越多个对象的一系列方法上。
（比如服务层上的所有业务操作。）

## 1.2 Spring AOP的功能和目标

Spring AOP是使用纯粹的Java实现的。不需要额外的编译过程。
Spring AOP不需要控制类加载机制，所以也适用于Servlet container或application server中。

Spring AOP目前只支持方法执行连接点（通知Spring beans上的方法执行）。
字段的拦截没有实现，如果要使用这类似的功能，考虑AspectJ语言。

Spring AOP与大多数AOP框架的区别在于，它的目录不是提供最完整的AOP实现，
而是在AOP实现和Spring IoC中提供一个紧密的集成，以此来帮助解决企业应用程序中的觉问题。

所以， Spring Framework’s AOP的功能通常都是和Spring IoC container一起联合使用。

切面通过使用普通的bean definition语言来配置（尽管也提供了autoproxying功能）：  
这是一个区别其它AOP实现的重要不同点。

Spring AOP主要是为企业Java应用程序中的大多数问题提供AOP负责的优秀的解决方案。

Spring AOP永远不会发展成像AspectJ那样的复杂的AOP解决方案。

Spring AOP和AspectJ,这两个proxy-based构架，都是有价值的，也是互补的。

Spring可以无缝地让Spring AOP和IoC与AspectJ集成。

 

## 1.3 AOP Proxies

Spring AOP默认为AOP proxyes使用标准的JDK dynamic proxies，这让任何接口都可以被代理。
Spring AOP也可以使用CGLIB proxies,这种情况下代理类是必须的，而不是接口。


## 2. @AspectJ support
@AspectJ指声明切面成为被注解修饰的普通类的风格。
@Aspect风格在AspectJ project的 AspectJ5 release版本中引入。

Spring使用AspectJ为解析和匹配提供的类库，以Aspect 5一样来解释同样的注解。

## 2.1 启用@AspectJ支持

为了在Spring configuration中使用@AspectJ切面，你需要启用Spring来支持基于@AspectJ切面的Spring AOP 配置，以及基于bean是否被这些切面通知(advised)的自动代理beans。

自动代理，如果Spring检测一个bean被一个或多个切面通知（advised），它就会为那个bean自动生成一个代表来拦截方法的调用和确保这些通知按需执行。

可以通过XML风格和Java风格的配置来开启@AspectJ 支持。
无论使用哪种风格，都需要使用到AspectJ的aspectjweaver.jar类库。

### 2.1.1使用Java配置启用@AspectJ支持

为了使用Java @Configuration来启用@AspectJ支持，只需要添加@EnableAspectJAutoProxy注解：
```
@Configuration
@EnableAspectJAutoProxy
public class AppConfig {

}
```
### 2.1.2 使用XML配置启用@AspectJ支持
在XML-based configuration中启用@AspectJ支持：使用aop:aspectj-autoproxy元素

```
<aop:aspectj-autoproxy/>
```

## 2.2 声明切面
启用@AspectJ支持，应用程序环境中定义的任何一个对应@AspectJ 切面类（使用了@Aspect注解的类）的bean，都会被Spring自动检测到，并用来配置Spring AOP。
下面这个例子，展示一个不太有用的切面的最小定义要求:

使用org.aspectj.lang.annotation.Aspect注解的类定义
```
package org.xyz;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class NotVeryUsefulAspect {

}
```
应用程序环境中一个常规的bean definition，连接一个使用了@Aspect注解的类：
```
<bean id="myAspect" class="org.xyz.NotVeryUsefulAspect">
	<!-- configure properties of aspect here as normal -->
</bean>
```

切面(使用@Aspect注解的类)也会像其它类一样有方法和字段。它们也可以包含切点
通知和引入声明。


## 2.3 定义切点

切点决定感兴趣的连接点，并允许我们使用它来控制通知的执行。Spring AOP只支持Spring beans上的方法调用切点，所以，你可以把切点当作匹配Spring beans上的方法执行、

一个切点声明有两个部分：
一个由一个名称和任意多个参数组成的签名。
一个决定具体感兴趣于哪个方法执行的切点表达式。

在AOP的@AspectJ 注解风格中：
切点签名由常规的方法定义提供
切点表达式使用@Pointcut注解来声明（担任切点签名的方法，返回值类型必须为void）

下面这个例子，可以帮助区分切点签名和切点表达式:
定义了一个名为anyOldTransfer的切点，这个切点匹配任何名为transfer的方法的执行。
```
@Pointcut("execution(* transfer(..))")// the pointcut expression
private void anyOldTransfer() {}// the pointcut signature
```

组成@Pointcut注解的值的切点表达式是一个常规的AspectJ 5的切点表达式。
更多关于[@AspectJ 语言](http://www.eclipse.org/aspectj/doc/released/progguide/index.html)的知识。


### 2.3.1 支持的切点designators（指示器，指示符）

Spring AOP支持如下的AspectJ pointcut designators(PCD)在切点表达式中使用。

* execution
用于匹配连接点的方法执行。在使用Spring AOP时，这是最主要使用的切点
* within
限制匹配在指定的类型中的连接点（当使用Spring AOP时，在匹配的类型中声明的方法的执行）
* this
限制匹配为bean的reference(Spring AOP proxy)是指定类型的一个实例的连接点。
* target
限制匹配连接点（使用Spring AOP时方法的执行），这个连接点的目标类型对象（被代理的应用程序对象）是指定类型的一个实例。
* args
限制匹配连接点，参数是指定类型的实例。
* @target
限制匹配连接点，执行对象的类上有一个指定类型的注解
* @args
限制匹配连接点，传递的实参的运行时类型有指定类型的注解。
* @within
限定匹配连接点，连接点位于使用了指定注解的类型当中。
* @annotation
限定匹配连接点，连接点的实际项（在Spring AOP中被执行方法）有指定类型的注解。

>Other pointcut types
>
>The full AspectJ pointcut language supports additional pointcut designators >that are not supported
>in Spring. These are: call, get, set, preinitialization, >staticinitialization,
>initialization, handler, adviceexecution, withincode, cflow, cflowbelow,
>if, @this, and @withincode. Use of these pointcut designators in pointcut >expressions
>interpreted by Spring AOP will result in an IllegalArgumentException being >thrown.
>
>The set of pointcut designators supported by Spring AOP may be extended in >future releases to
>support more of the AspectJ pointcut designators.

因为Spring AOP限制匹配只在方法执行连接点，所以这里讨论的，只是AspectJ中的很小的部分。

 AspectJ itself has type-based semantics and at an execution join point both this and target
refer to the same object - the object executing the method. 


Spring AOP is a proxy-based system and
differentiates between the proxy object itself (bound to this) and the target object behind the proxy
(bound to target).

注：
>因为Spring AOP framework的proxy-based性质，所以任何切点只会针对public方法来匹配。
>如果要使用protected/private的方法，甚至构造函数，可以考虑使用Spring-driven native >AspectJ weaving 来替代Spring’s proxy-based AOP framework。


Spring AOP还支持一个额外的名为bean的PCD。这个PCD允许你限定连接点到一个特定名称的Spring bean，或者特定名称的Spring beans的集合（当使用通配符时）。
```
bean(idOrNameOfBean)
```
idOrNameOfBean可以是任何Spring bean的名称：支持 *，所以如果你为Spring beans建立了一些命名规范，你可以轻松的编写一个bean PCD来选出它们。
与其它pointcut designators一样，支持`&&`, `||`, `!`等.

注：
> bean PCD仅在Spring AOP中支持，在原生的AspectJ weaving中不支持。
> 所以在@Aspect中不可用。
> The bean PCD operates at the instance level (building on the Spring bean >name concept) rather
>than at the type level only (which is what weaving-based AOP is limited to). >Instance-based
>pointcut designators are a special capability of Spring’s proxy-based AOP >framework and its close
>integration with the Spring bean factory, where it is natural and >straightforward to identify specific
>beans by name.


### 2.3.2 合并切点表达式

可以使用`&&`，`||`，`!` 来结合切点表达式，也可以通过名称来引用切点表达式。

下面这个例子，展示了三个切点表达式：

```
@Pointcut("execution(public * *(..))")
private void anyPublicOperation() {}

@Pointcut("within(com.xyz.someapp.trading..*)")
private void inTrading() {}

@Pointcut("anyPublicOperation() && inTrading()")
private void tradingOperation() {}
```
anyPublicOperation：匹配连接点上的任何公共方法的执行
inTrading：匹配连接点上的com.xyz.someapp.trading包中的方法的执行
tradingOperation：匹配com.xyz.someapp.trading包中的所有公共方法的执行

这是一个比较好的方式，使用较小的命名的组件来组成更加复杂的切点表达式。
当通过名称来引用切点时，Java的可访问性规则会起作用。

### 2.3.3 共享常见的切点定义

当使用企业应用程序时，你可能经常会想从一些几个切面中来参考应用程序的模块和特定集合的操作。
我们推荐定义一个捕获常见切点表达式的`SystemArchitecture`的切面来达到这个目的。

一个典型的这样的切面，可能看起来像这样：
```
package com.xyz.someapp;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class SystemArchitecture {

	/**
	* A join point is in the web layer if the method is defined
	* in a type in the com.xyz.someapp.web package or any sub-package
	* under that.
	*/
	@Pointcut("within(com.xyz.someapp.web..*)")
	public void inWebLayer() {}


	/**
	* A join point is in the service layer if the method is defined
	* in a type in the com.xyz.someapp.service package or any sub-package
	* under that.
	*/
	@Pointcut("within(com.xyz.someapp.service..*)")
	public void inServiceLayer() {}


	/**
	* A join point is in the data access layer if the method is defined
	* in a type in the com.xyz.someapp.dao package or any sub-package
	* under that.
	*/
	@Pointcut("within(com.xyz.someapp.dao..*)")
	public void inDataAccessLayer() {}


	/**
	* A business service is the execution of any method defined on a service
	* interface. This definition assumes that interfaces are placed in the
	* "service" package, and that implementation types are in sub-packages.
	*
	* If you group service interfaces by functional area (for example,
	* in packages com.xyz.someapp.abc.service and com.xyz.someapp.def.service) then
	* the pointcut expression "execution(* com.xyz.someapp..service.*.*(..))"
	* could be used instead.
	*
	* Alternatively, you can write the expression using the 'bean'
	* PCD, like so "bean(*Service)". (This assumes that you have
	* named your Spring service beans in a consistent fashion.)
	*/
	@Pointcut("execution(* com.xyz.someapp..service.*.*(..))")
	public void businessService() {}


	/**
	* A data access operation is the execution of any method defined on a
	* dao interface. This definition assumes that interfaces are placed in the
	* "dao" package, and that implementation types are in sub-packages.
	*/
	@Pointcut("execution(* com.xyz.someapp.dao.*.*(..))")
	public void dataAccessOperation() {}
}
```

在这样的切面中定义的这些切点，可以涉及任何你需要一个切点表达式的地方(  
The pointcuts defined in such an aspect can be referred to anywhere that you need a pointcut
expression)。

比如，事务化服务层，可以这样写：
```
<aop:config>
	<aop:advisor
		pointcut="com.xyz.someapp.SystemArchitecture.businessService()"
		advice-ref="tx-advice"/>
</aop:config>

<tx:advice id="tx-advice">
	<tx:attributes>
		<tx:method name="*" propagation="REQUIRED"/>
	</tx:attributes>
</tx:advice>
```

### 2.3.4 示例

Spring AOP 使用者，使用的最频繁的切点指定符（pointcut designator）就是execution。

一个execution的表达式格式是：
```
execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?name-pattern(param-pattern)
throws-pattern?)
```
除了返回值类型模式(ret-type-pattern)、名称模式、参数模式是必须的，其它都是可选的。

* returning type pattern
决定被匹配的连接点，这个连接点的方法返回类型必须是指定的类型，最常使用的就是 * ，指明可以是任何返回值类型。

* name pattern
匹配的方法名称，可以使用通配符`*`作为name pattern的一部分，或者全部。

* parameters pattern
稍微有些复杂，
(..)匹配任何数量的参数（0-n个）
(*) 一个任何类型的参数
(*,String)匹配包含两个参数的方法，第一个参数类型不限，第二个参数类型为String。

[更多参考aspectj](http://www.eclipse.org/aspectj/doc/released/progguide/)



**常见切点表达式**

* 公共方法的执行
```
execution(public * *(..))
```

* 方法名以set开始的方法的执行
```
execution(* set*(..))
```

* 任何AccountService接口中定义的方法的执行
```
execution(* com.xyz.service.AccountService.*(..))
```

* 任何com.xyz.service包中定义的方法的执行
```
execution(* com.xyz.service.*.*(..))
```

* 任何com.xyz.service包及其子包中定义的方法的执行
```
execution(* com.xyz.service..*.*(..))
```

* com.xyz.service包中任何连接点的（在Spring AOP中只有方法的执行）
```
within(com.xyz.service.*)
```
* com.xyz.service包及其子包中任何连接点（在Spring AOP中只有方法的执行）
```
within(com.xyz.service..*)
```
* 任何代理实现了AccountService接口的连接点（在Spring AOP中只有方法的执行）
```
this(com.xyz.service.AccountService)
```
* 任何目标对象实现了AccountService的连接点（在Spring AOP中只有方法的执行）
```
target(com.xyz.service.AccountService)
```

* 只有一个参数，且传递的参数在运行时是Serializable类型的连接点
```
args(java.io.Serializable)
```
注：execution(* *(java.io.Serializable))代表的是方法签名，声明的参数是Serializable。

* 目标对象有@Transactional注解的连接点
```
@target(org.springframework.transaction.annotation.Transactional)
```












## 3. Schema-based AOP support




