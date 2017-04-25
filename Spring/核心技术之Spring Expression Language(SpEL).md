# 1.介绍

Spring Expression Language(SpEL)是一个支持在运行时查询和操作对象导航图的强有力的表达式语言。  

SpEL不是直接绑定到Spring上，可以单独使用它。

一个经典的用法就是，在XML或者annotated based 的 bean definitions中使用SpEL。

这里将会介绍表达式语言的特点、API和它的语言语法。


# 2.功能概述

表达式语言支持以下功能：
* 字面表达式
* 布尔和关系操作符
* 正则表达式
* 类表达式
* 访问属性、数组、list、map
* 方法调用
* 关系操作符
* Assignment
* 调用构造函数
* Bean引用
* 构造数组
* 内嵌lists
* 内嵌map
* 三元操作符
* 变量
* 用户定义的函数
* 集合投射
* 集合筛选
* 模板表达式

# 3. 使用Spring Expression接口进行表达式计算

本节介绍SpEL的接口和表达式语言的简单使用。

下面这个代码介绍使用SpEL API来计算字面量表达式``'Hello World'``

```
ExpressionParser parser = new SpelExpressionParser();
Expression exp = parser.parseExpression("'Hello World'");
String message = (String) exp.getValue();
```

message的值是``'Hello World'``

SpEL的主要类和接口都在org.springframework.expression包及它的子包spel.support包中。

ExpressionParser接口负责解析表达式字符串。
在这个例子中，表达式字符串，是一个用单引号引起来的字符串字面常量。

当调用parser.parseExpression和exp.getValue时，有可能分别抛出，ParseException和EvaluationException。

SpEL支持一系列的功能：比如调用方法，访问属性，调用构造函数
* 方法调用
```
ExpressionParser parser = new SpelExpressionParser();
Expression exp = parser.parseExpression("'Hello World'.concat('!')");
String message = (String) exp.getValue();
```
message的值为 'Hello World!'

* 属性访问


```
ExpressionParser parser = new SpelExpressionParser();

// invokes 'getBytes()'
Expression exp = parser.parseExpression("'Hello World'.bytes");
byte[] bytes = (byte[]) exp.getValue();
```

SpEL支持内嵌的属性，通过使用标准的'``.``'语法，如：prop1.prop2.prop3及属性值的设置。

公共字段也能访问

```
ExpressionParser parser = new SpelExpressionParser();

// invokes 'getBytes().length'
Expression exp = parser.parseExpression("'Hello World'.bytes.length");
int length = (Integer) exp.getValue();
```

* 调用构造函数

```
ExpressionParser parser = new SpelExpressionParser();
Expression exp = parser.parseExpression("new String('hello world').toUpperCase()");
String message = exp.getValue(String.class);
```

使用``public <T> T getValue(Class<T> desiredResultType)``方法，可以免除将表达式的值  
转化为需要的结果类型。

如果转换失败，就会抛出EvaluationException异常。

SpEL比较常用的用法是，针对一个特定的对象实例（称为根对象）提供表达式字符串。

这里有两种选择，具体选择哪一个，要看这个根对象在每次对计算表达式的调用时，是否会改变。

下面这个例子，是获取Inventor类上的name属性。
* 根对象不改变

```
// Create and set a calendar
GregorianCalendar c = new GregorianCalendar();
c.set(1856, 7, 9);

// The constructor arguments are name, birthday, and nationality.
Inventor tesla = new Inventor("Nikola Tesla", c.getTime(), "Serbian");

ExpressionParser parser = new SpelExpressionParser();
Expression exp = parser.parseExpression("name");

EvaluationContext context = new StandardEvaluationContext(tesla);
String name = (String) exp.getValue(context);
```
在最后一行，name变量的值会被设置为"Nikola Tesla"。
StandardEvaluationContext类是用来指定表达式计算时，使用的对象（也就是在哪个对象上来执行这个表达式）
如果根对象不会改变时，就可以使用上述的这种途径，它仅仅计算（求值）上下文环境中被设置一次。  

* 根对象反复变化

如果根对象反复变化时，在每次调用getValue时，都要提供一个根对象。

```
/ Create and set a calendar
GregorianCalendar c = new GregorianCalendar();

c.set(1856, 7, 9);
// The constructor arguments are name, birthday, and nationality.
Inventor tesla = new Inventor("Nikola Tesla", c.getTime(), "Serbian");

ExpressionParser parser = new SpelExpressionParser();
Expression exp = parser.parseExpression("name");
String name = (String) exp.getValue(tesla);
```

在这个例子中tesla对象，被直接应用到了getValue上，求值表达式的基础在内部创建并管理一个默认的求值上下文， 
它不要求提供一个根对象。


StandardEvaluationContext构造相对昂贵（使用的计算机资源和时间），当重复使用时，它创建了
缓存状态，这个缓存使得后续的表达式计算会更快。
出于这个原因，缓存它并尽可能的重复利用，而不是为每一个表达式计算构造一个新的。



注：
>当独立使用SpEL时，需要创建 parser和parse expressions，同时可能需要提供解析的context和root context object。

但，通常的使用情况是将SpEL作为配置文件的一部分，比如Spring bean或者Spring Web Flow deinnitions。
在这种情况下， parser, evaluation context, root object 和所有预定义的变量都隐式地设置好了。
用户需要做的仅仅是表达式，别无其它。

* 布尔操作符
```
Expression exp = parser.parseExpression("name == 'Nikola Tesla'");
boolean result = exp.getValue(context, Boolean.class); // evaluates to true
```

## 3.1 EvaluationContext接口

当计算表达式去解析属性、方法、字段及执行类型转换时，需要用到EvaluationContext接口。

StandardEvaluationContext是EvaluationContext的一个实现，支持开箱即用。

StandardEvaluationContext使用反射操作对象，缓存了java.lang.reflect.Method,  java.lang.reflect.Field, 
java.lang.reflect.Constructor的实例，以此来提升性能。

使用setRootObject()指定表达式的根对象或传递根对象到构造函数，这都在StandardEvaluationContext完成。

同时，也可以使用setVariable()来指定变量，使用
