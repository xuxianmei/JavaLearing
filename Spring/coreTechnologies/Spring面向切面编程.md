
# 介绍
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