# AOP

AOP（Aspect Oriented Programming），即面向切面编程，可以说是OOP（Object Oriented Programming，面向对象编程）的补充和完善。

OOP引入封装、继承、多态等概念来建立一种对象层次结构，用于模拟公共行为的一个集合。不过OOP允许开发者定义纵向的关系，但并不适合定义横向的关系，例如日志功能。

日志代码往往横向地散布在所有对象层次中，而与它对应的对象的核心功能毫无关系对于其他类型的代码，如安全性、异常处理和透明的持续性也都是如此，这种散布在各处的无关的代码被称为横切（cross cutting），在OOP设计中，它导致了大量代码的重复，而不利于各个模块的重用。

AOP技术恰恰相反，它利用一种称为"横切"的技术，剖解开封装的对象内部，并将那些影响了多个类的公共行为封装到一个可重用模块，并将其命名为"Aspect"，即切面。

## AOP解决的问题
把这些横切关注点与业务逻辑相分离。


横切关注点（也可称为“次要关注点”）可以被模块化为特殊的类，这些类被称为切面(aspect)。
切面：简单说就是那些与业务无关，却为业务模块所共同调用的逻辑或责任封装起来，便于减少系统的重复代码，降低模块之间的耦合度，并有利于未来的可操作性和可维护性。


使用"横切"技术，AOP把软件系统分为两个部分：“核心关注点”和横切关注点（也可称为次要关注点）。
业务处理的主要流程是核心关注点，与之关系不大的部分是横切关注点。
横切关注点的一个特点是，他们经常发生在核心关注点的多处，而各处基本相似，比如权限认证、日志、事物。

## AOP的作用
在于分离系统中的各种关注点，将核心关注点和横切关注点分离开来。

## AOP核心概念及术语

* 横切关注点
跨多个应用对象的逻辑（非业务逻辑），比如安全，事务，日志，权限等
* 切面 
类是对物体特征的抽象，切面就是对横切关注点的抽象，切面实现了横切关注点的模块化。
也是通知和切点的结合。通知和切点定义了切面的全部内容--何时何处执行什么操作。
* 通知(Advice)
通知定义了切面是什么以及何时使用（何时，执行哪些代码）。
* 连接点(Join point)
连接点是在应用执行过程中能够插入切面的一个点。这个点可以是调用方法时，抛出异常时，修改字段时（在程序中也叫着被拦截的点）。切面代码可以利用这些点插入到应用的正常流程之中，并添加新的行为。
* 切点(Poincut)
如果说通知定义了切面的“什么”和“何时”的话，那么切点就定义了“何处”。
切点的定义会匹配通知所要织入的一个或多个连接点。  
我们通常使用明确的类和方法名称或是利用正则表达式定义所匹配的类和方法名称来指定这些切点。
* 织入(Weaving)
把切面应用到目标对象并创建新的代码对象的过程。切面在指定的连接点被织入到目标对象中。  
在目标对象的生命周期 时有多个点可以进行织入：
	* 编译期 
	* 类加载期
	* 运行期

* 引入(Introduction)
引入允许我们不修改原有类的情况下，向现有的类添加新方法和属性。



# 1.Spring 中的AOP
下面是Spring如何来处理横切关注点的。
# 2.Pointcut API in Spring（Spring中的切点API）

## 2.1 概念

Spring的切点模型使得切点可以独立于通知类型进行重用，这就使得针对不同的advice使用相同的pointcut成为可能。

org.springframework.aop.Pointcut是最核心的接口，这个接口用来将通知应用到特定的类和方法上。
```
public interface Pointcut {

	ClassFilter getClassFilter();

	MethodMatcher getMethodMatcher();
}
```

将Pointcut分成两部分，方法和类的匹配的重用和组合操作（比如与其它方法匹配器的并集）。

ClassFilter接口用来约束切点只能应用于一系列的目标类。
如果matches()接口返回true，所以目标类都会被匹配到。

```
public interface ClassFilter{

	boolean matches(Class clazz);
}
```

MethodMatcher接口更加重要。

```
public interface MethodMatcher{

	boolean matches(Method m, Class targetClass);

	boolean isRuntime();

	boolean matches(Method m, Class targetClass, Object[] args);
}
```

 matches(Method, Class)方法用于检测这个切点是否匹配目标类上的某个方法。
当一个AOP proxy被创建后，可以避免在每一个方法调用上来执行这个检测。
如果在指定的方法上上，这个具有2个参数的matches()返回了true，并isRuntime()方法也返回了true，那么具有3个参数的matches()将会在每个方法调用时被调用。

这可以让一个切点在目标上的通知(advice)被执行前，检测传递给这个方法调用的参数。

大部分MethodMatchers都是静态，这也意味着它们的isRuntime()方法返回false.
在这种情况下，具有3个参数的matches()永远都不会被调用。

## 2.2 Operations on pointcuts(切点上的操作)

Spring支持在切点上执行这两种操作：union和intersection

* union（条件或）
	任意一个切点上匹配的方法（这个方法只要满足其中一个匹配原则就可）
* intersection(条件与)
	在所有切点上都匹配的方法（这个方法需要满足所有的匹配原则）


切点可以通过使用org.springframework.aop.support.Pointcuts类或org.springframework.aop.support.ComposablePointcut类中的静态方法来完成组合操作。

但是，使用AspectJ pointcut expressions更加简单方便。

## 2.3 AspectJ expression pointcuts

从Spring 2.0开始，Spring使用的最重要的切点类型就是：org.springframework.aop.aspectj.AspectJExpressionPointcut。  

这是切点，使用AspectJ提供的类库，来解析AspectJ pointcut expression字符串。


## 2.4 Convenience pointcut implementations（简便的pointcut实现）

Spring提供很多简便的pointcut实现，一些开箱即用，一些在特定应用中当作基类使用。

### 2.4.1 Static pointcuts

静态切点全都是基于方法和目标类，不考虑方法的参数。

静态切点大多数都是高效、最好的选择。
在Spring中，静态切点的检测只有当一个方法被一次调用时会执行，后面其它所有方法的调用，不再需要检测这个切点。

#### 2.4.1.1 Regular expression pointcuts

一个常用的指定静态切点的方式就是使用正则表达式。
包括Spring框架以外的AOP 框架都能做到这些。

org.springframework.aop.support.JdkRegexpMethodPointcut是一个普通的正则表达式切点，使用JDK1.4+支持的正则表达式。

使用JdkRegexpMethodPointcut类，你可以提供匹配字符串的一个列表，如果这当中有任何一个  
匹配，这个切点就会返回true。（相当于union）
```
<bean id="settersAndAbsquatulatePointcut"
		class="org.springframework.aop.support.JdkRegexpMethodPointcut">
	<property name="patterns">
		<list>
			<value>.*set.*</value>
			<value>.*absquatulate</value>
		</list>
	</property>
</bean>
```

Spring提供了一个RegexpMethodPointcutAdvisor类，使用这个类可以引用一个Advice。
注： an Advice can be an interceptor, before advice,
throws advice etc.

在这个技术的背后，Spring使用的是一个JdkRegexpMethodPointcut。

使用RegexpMethodPointcutAdvisor简化装配，比如下面这个bean，同时封装pointcut和advice。
```
<bean id="settersAndAbsquatulateAdvisor"
		class="org.springframework.aop.support.RegexpMethodPointcutAdvisor">

	<property name="advice">
			<ref bean="beanNameOfAopAllianceInterceptor"/>
	</property>

	<property name="patterns">
		<list>
			<value>.*set.*</value>
			<value>.*absquatulate</value>
		</list>
	</property>
</bean>
```

RegexpMethodPointcutAdvisor可以应用于任何一种Advice type。



#### 2.4.1.2 Attribute-driven pointcuts
一个重要的静态切点的类型是metadat-driven pointcut。
它使用元数据属性的值，一般是源码级别上的元数据。

### 2.4.2  Dynamic pointcuts

动态切点相比静态切点，在执行时要消耗更多的资源。它们同时考虑方法参数和静态信息。
这意味着它们在每个方法调用时都被执行。导致的结果就是，不能被缓存，因为参数是变化的。
最主要的例子就是control flow切点

#### 2.4.2.1 Control flow pointcuts

Spring control flow切点在概念上与AspectJ cflow切点类似，尽管不如后者强大。

一个控制流切点，匹配当前调用栈。  
比如，如果连接点被com.mycompany.web包中的一个方法或者SomeCaller类调用，就会触发这个切点。

使用org.springframework.aop.support.ControlFlowPointcut来指定控制流切点。


## 2.5 Pointcut superclasses

Spring提供了很多有用的切点基类来帮助实现你自己的切点。  

因为静态切点是最有用的，你可以会像如下方式来继承StaticMethodMatcherPointcut。
这只需要实现一个抽象方法（虽然也可以覆盖其它方法来自定义其它行为）
```
class TestStaticPointcut extends StaticMethodMatcherPointcut {

	public boolean matches(Method m, Class targetClass) {

		// return true if custom criteria match

	}
}
```
同样的也是动态切点基类。

## 2.6 自定义切点

因为在Spring AOP中的切点都是Java类，而不是语言特性（比如AspectJ中的切点）。
所以无论是静态还是动态，都可以定义自定义的切点。推荐使用AspectJ pointcut expression language。



# 3. Advice API in Spring


## 3.1 Advice lifecycles
