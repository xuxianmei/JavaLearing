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

同时，也可以使用setVariable()来指定变量，使用registerFunction()来指定方法。

StandardEvaluationContext同时也是注册自定义的ConstructorResolvers,MethodResolvers 
和PropertyAccessors来扩展SpPL如何计算表达式的点。

### 3.1.1 Type Conversion

默认SpEL使用Spring core(org.springframework.core.convert.ConversionService)中的类型转换服务.

这个转换服务，自带很多普通类型的转换器，同时也支持扩展，通过扩展可以添加类型之间的自定义转换。
此外，还能感知泛型。这意味着，当在表达式中与泛型类型工作时，SpEL将会把它遇到的对象转换维持为正确的类型。


比如，当使用setValue()来设置一个List属性。属性的实际类型是List<Boolean>，SpEL知道
将其填充到列表前，需要将其转换为Boolean类型。

比如 ：

````
class Simple {
	public List<Boolean> booleanList = new ArrayList<Boolean>();
}

Simple simple = new Simple();

simple.booleanList.add(true);

StandardEvaluationContext simpleContext = new StandardEvaluationContext(simple);


// false is passed in here as a string. SpEL and the conversion service will
// correctly recognize that it needs to be a Boolean and convert it
parser.parseExpression("booleanList[0]").setValue(simpleContext, "false");

// b will be false
Boolean b = simple.booleanList.get(0);

````

## 3.2 Parser configuration

可以使用一个解析配置对象(org.springframework.expression.spel.SpelParserConfiguration)来配置SpEL表达式解析器。

这个配置对象控制一些表达式组件的行为。
比如，如果索引到一个数组或一个colleciton，在这个索引上的元素为null时，自动创建这个元素。

当使用一个由属性引用链时，这会特别有用。如果索引到一个array或list，并且这个索引超越了  
size大小，可以自动增长这个array或list来适应这个索引。
如：
````
class Demo {
	public List<String> list;
}

// Turn on:
// - auto null reference initialization
// - auto collection growing
SpelParserConfiguration config = new SpelParserConfiguration(true,true);

ExpressionParser parser = new SpelExpressionParser(config);

Expression expression = parser.parseExpression("list[3]");

Demo demo = new Demo();

Object o = expression.getValue(demo);

// demo.list will now be a real collection of 4 entries
// Each entry is a new empty String

````
## 3.3 SpEL compilation

Spring Framework 4.1提供了一个基本表达式编译器。
表达通常都是解释执行的，这样提供了很多的动态灵活性，但没有提供最好的性能。
对于偶尔的表达式使用是很好的，但是如果被其它组件比如Spring Intergration使用时，性能会  
相当重要，动态性没有多大的需要。

这个新的SpEL表达式就是来解决性能问题的。
这个编译器会将注入代表表达式行为的运算生成一个真正的Java类，并使用它来实现更快的表达式计算。

在执行编译时，因为缺少表达式相关的类型信息，所以编译器使用表达式解释执行计算时收集到的信息。

当首次解释执行计算时，这会找出这个类型是什么。
当然，如果表达式中的众多元素的类型随着时间而改变，基于这些信息的编译，会引起一些麻烦。
出于这个原因，所以最适合编译的表达式，是那些类型信息不会随着重复计算时类型信息发生改变的。

比如：

### 3.3.1 Compiler configuration

默认时，编译器是关闭状态的，有两种方式可以将其打开。
使用上面讲到的解析配置器，或者当SpEL被嵌入到其它组件中使用时，通过系统属性设置

编译器可以在一些模式下运行，枚举（org.springframework.expression.spel.SpelCompilerMode）。

* OFF 关闭模式，也是默认模式
* IMMEDIATE 立即模式，表达式会尽可能快的编译，如果编译表达式失败，会抛出异常
* MIXED 混合模式

当选择了一个模式后，就可以使用SpelParserConfiguration来配置解析器

````
SpelParserConfiguration config = new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE,this.getClass().getClassLoader());

SpelExpressionParser parser = new SpelExpressionParser(config);

Expression expr = parser.parseExpression("payload");

MyMessage message = new MyMessage();

Object payload = expr.getValue(message);
````



### 3.3.2 Compiler limitations

目前，仍然一些表达式不能被编译


* expressions involving assignment
* expressions relying on the conversion service
* expressions using custom resolvers or accessors
* expressions using selection or projection

More and more types of expression will be compilable in the future.

# 4.Expression support for defining bean definitios

SpEL表达式可以用于定义BeanDefinitions的XML-based和annotation-based配置元数据。

在这两种情况，定义表达式的语法都是 
``#{ <expression
string> }``

## 4.1 XML-based configuration

* 设置构造参数函数

```
<bean id="numberGuess" class="org.spring.samples.NumberGuess">
	<property name="randomNumber" value="#{ T(java.lang.Math).random() * 100.0 }"/>
	<!-- other properties -->
</bean>
```

* 使用预定义的变量
systemProperties是已经定义好的变量。

```
<bean id="taxCalculator" class="org.spring.samples.TaxCalculator">
	<property name="defaultLocale" value="#{ systemProperties['user.region'] }"/>
	<!-- other properties -->
</bean>
```

* 通过名称使用其它bean的属性


```
<bean id="numberGuess" class="org.spring.samples.NumberGuess">
	<property name="randomNumber" value="#{ T(java.lang.Math).random() * 100.0 }"/>
	<!-- other properties -->
</bean>

<bean id="shapeGuess" class="org.spring.samples.ShapeGuess">
	<property name="initialShapeSeed" value="#{ numberGuess.randomNumber }"/>
	<!-- other properties -->
</bean>
```

## 4.2 Annotation-based configuration

@Value注解可以放置在字段、方法和方法参数/构造器参数上来指定一个默认值。

* 设置字段变量的值


```
public static class FieldValueTestBean{

	@Value("#{ systemProperties['user.region'] }")
	private String defaultLocale;

	public void setDefaultLocale(String defaultLocale) {

		this.defaultLocale = defaultLocale;
	}

	public String getDefaultLocale() {

		return this.defaultLocale;
	}
}
```

* 设置属性setter方法参数默认值

```
public static class PropertyValueTestBean {

	private String defaultLocale;
	@Value("#{ systemProperties['user.region'] }")

	public void setDefaultLocale(String defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	public String getDefaultLocale() {
		return this.defaultLocale;
	}
}

```

* 自动装配的方法和构造器也可以使用@Value

```
public class SimpleMovieLister {

	private MovieFinder movieFinder;
	private String defaultLocale;

	@Autowired
	public void configure(MovieFinder movieFinder,@Value("#{ systemProperties['user.region'] }") String defaultLocale){
		this.movieFinder = movieFinder;
		this.defaultLocale = defaultLocale;
	}
	// ...
}
```

```
public class MovieRecommender {

	private String defaultLocale;
	private CustomerPreferenceDao customerPreferenceDao;

	@Autowired
	public MovieRecommender(CustomerPreferenceDao customerPreferenceDao,@Value("#{systemProperties['user.country']}") String defaultLocale) {
		this.customerPreferenceDao = customerPreferenceDao;
		this.defaultLocale = defaultLocale;
	}
	// ...
}

```

# 5.语言参考

* 字面量表达式

表达式支持的字面量表达式：字符串，数字、布尔和null。

字符串使用单引号限定，如果要把一个单引号也当作一个字符串中的内容，使用两个单引号字符。
```
ExpressionParser parser = new SpelExpressionParser();

// evals to "Hello World"
String helloWorld = (String) parser.parseExpression("'Hello World'").getValue();

double avogadrosNumber = (Double) parser.parseExpression("6.0221415E+23").getValue();

// evals to 2147483647
int maxValue = (Integer) parser.parseExpression("0x7FFFFFFF").getValue();

boolean trueValue = (Boolean) parser.parseExpression("true").getValue();

Object nullValue = parser.parseExpression("null").getValue();
```

* 属性，Arrays,Lists,Maps,Indexers
```
/ evals to 1856
int year = (Integer) parser.parseExpression("Birthdate.Year + 1900").getValue(context);
String city = (String) parser.parseExpression("placeOfBirth.City").getValue(context);
```
```
ExpressionParser parser = new SpelExpressionParser();
// Inventions Array
StandardEvaluationContext teslaContext = new StandardEvaluationContext(tesla);
// evaluates to "Induction motor"
String invention = parser.parseExpression("inventions[3]").getValue(
teslaContext, String.class);
// Members List
StandardEvaluationContext societyContext = new StandardEvaluationContext(ieee);
// evaluates to "Nikola Tesla"
String name = parser.parseExpression("Members[0].Name").getValue(
societyContext, String.class);
// List and Array navigation
// evaluates to "Wireless communication"
String invention = parser.parseExpression("Members[0].Inventions[6]").getValue(
societyContext, String.class);
```
```
// Officer's Dictionary
Inventor pupin = parser.parseExpression("Officers['president']").getValue(
societyContext, Inventor.class);
// evaluates to "Idvor"
String city = parser.parseExpression("Officers['president'].PlaceOfBirth.City").getValue(
societyContext, String.class);
// setting values
parser.parseExpression("Officers['advisors'][0].PlaceOfBirth.Country").setValue(
societyContext, "Croatia");
```

* Inline lists
```
// evaluates to a Java list containing the four numbers
List numbers = (List) parser.parseExpression("{1,2,3,4}").getValue(context);
List listOfLists = (List) parser.parseExpression("{{'a','b'},{'x','y'}}").getValue(context);
```
{}代表一个空list
* Inline Maps
使用{key:value}。
```
// evaluates to a Java map containing the two entries
Map inventorInfo = (Map) parser.parseExpression("{name:'Nikola',dob:'10-July-1856'}").getValue(context);
Map mapOfMaps = (Map) parser.parseExpression("{name:{first:'Nikola',last:'Tesla'},dob:
{day:10,month:'July',year:1856}}").getValue(context);
```
{:}代表一个空map

* 数组构造
```
int[] numbers1 = (int[]) parser.parseExpression("new int[4]").getValue(context);
// Array with initializer
int[] numbers2 = (int[]) parser.parseExpression("new int[]{1,2,3}").getValue(context);
// Multi dimensional array
int[][] numbers3 = (int[][]) parser.parseExpression("new int[4][5]").getValue(context);
```

* 方法调用
```
// string literal, evaluates to "bc"
String c = parser.parseExpression("'abc'.substring(2, 3)").getValue(String.class);
// evaluates to true
boolean isMember = parser.parseExpression("isMember('Mihajlo Pupin')").getValue(
societyContext, Boolean.class);
```

* 操作符
	* 关系操作符
```
// evaluates to true
boolean trueValue = parser.parseExpression("2 == 2").getValue(Boolean.class);
// evaluates to false
boolean falseValue = parser.parseExpression("2 < -5.0").getValue(Boolean.class);
// evaluates to true
boolean trueValue = parser.parseExpression("'black' < 'block'").getValue(Boolean.class);
```
除了标准的关系操作符，SpEL支持instanceof和正则表达式matches操作符
```
// evaluates to false
boolean falseValue = parser.parseExpression(
"'xyz' instanceof T(Integer)").getValue(Boolean.class);
// evaluates to true
boolean trueValue = parser.parseExpression(
"'5.00' matches '\^-?\\d+(\\.\\d{2})?$'").getValue(Boolean.class);
//evaluates to false
boolean falseValue = parser.parseExpression(
"'5.0067' matches '\^-?\\d+(\\.\\d{2})?$'").getValue(Boolean.class);
```
	* 逻辑运算符
anr,or,not
```
// -- AND --
// evaluates to false
boolean falseValue = parser.parseExpression("true and false").getValue(Boolean.class);
// evaluates to true
String expression = "isMember('Nikola Tesla') and isMember('Mihajlo Pupin')";
boolean trueValue = parser.parseExpression(expression).getValue(societyContext, Boolean.class);
// -- OR --
// evaluates to true
boolean trueValue = parser.parseExpression("true or false").getValue(Boolean.class);
// evaluates to true
String expression = "isMember('Nikola Tesla') or isMember('Albert Einstein')";
boolean trueValue = parser.parseExpression(expression).getValue(societyContext, Boolean.class);
// -- NOT --
// evaluates to false
boolean falseValue = parser.parseExpression("!true").getValue(Boolean.class);
// -- AND and NOT --
String expression = "isMember('Nikola Tesla') and !isMember('Mihajlo Pupin')";
boolean falseValue = parser.parseExpression(expression).getValue(societyContext, Boolean.class);
```
	* 数值操作符
`+ - * / % ^`,+可以应用于字符串和数字，其它的只能用于数字。
```
 // Addition
int two = parser.parseExpression("1 + 1").getValue(Integer.class); // 2
String testString = parser.parseExpression(
"'test' + ' ' + 'string'").getValue(String.class); // 'test string'
// Subtraction
int four = parser.parseExpression("1 - -3").getValue(Integer.class); // 4
double d = parser.parseExpression("1000.00 - 1e4").getValue(Double.class); // -9000
// Multiplication
int six = parser.parseExpression("-2 * -3").getValue(Integer.class); // 6
double twentyFour = parser.parseExpression("2.0 * 3e0 * 4").getValue(Double.class); // 24.0
// Division
int minusTwo = parser.parseExpression("6 / -3").getValue(Integer.class); // -2
double one = parser.parseExpression("8.0 / 4e0 / 2").getValue(Double.class); // 1.0
// Modulus
int three = parser.parseExpression("7 % 4").getValue(Integer.class); // 3
int one = parser.parseExpression("8 / 5 % 2").getValue(Integer.class); // 1
// Operator precedence
int minusTwentyOne = parser.parseExpression("1+2-3*8").getValue(Integer.class); // -21
```
* 赋值
可以使用setValue来实现属性赋值，也可以使用表达式来完成。
```
Inventor inventor = new Inventor();
StandardEvaluationContext inventorContext = new StandardEvaluationContext(inventor);
parser.parseExpression("Name").setValue(inventorContext, "Alexander Seovic2");
// alternatively
String aleks = parser.parseExpression(
"Name = 'Alexandar Seovic'").getValue(inventorContext, String.class);
```
* 类型
T操作符，可以用来指定java.lang.Class(java.lang中的任何一个类)的一个实例。
所有的静态方法都是使用这个操作符来调用的。
StandardEvaluationContext使用TypeLocator来查找类型，StandardTypeLocator（可替换）编译到
一个可以解析java.lang的包中。
这意味着，除了java.lang中的类不需要指定类的完全限定名称，其它都需要指定。
```
Class dateClass = parser.parseExpression("T(java.util.Date)").getValue(Class.class);
Class stringClass = parser.parseExpression("T(String)").getValue(Class.class);
boolean trueValue = parser.parseExpression(
"T(java.math.RoundingMode).CEILING < T(java.math.RoundingMode).FLOOR")
.getValue(Boolean.class);
```
* 构造器
使用new来调用构造器，除了primitive类型（比如int float）和字符串类型，其它的类都需要指定类的完全限定名称
```
Inventor einstein = p.parseExpression(
"new org.spring.samples.spel.inventor.Inventor('Albert Einstein', 'German')")
.getValue(Inventor.class);
//create new inventor instance within add method of List
p.parseExpression(
"Members.add(new org.spring.samples.spel.inventor.Inventor(
'Albert Einstein', 'German'))").getValue(societyContext);
```

* 变量
使用`#变量名称`来引用变量，这些变量都是通过使用StandardEvaluationContext上的setVariable  
来设置的。
```
Inventor tesla = new Inventor("Nikola Tesla", "Serbian");
StandardEvaluationContext context = new StandardEvaluationContext(tesla);
context.setVariable("newName", "Mike Tesla");
parser.parseExpression("Name = #newName").getValue(context);
System.out.println(tesla.getName()) // "Mike Tesla"
```
	* `#this`和`#root`变量
	`#this`一直存在，代表当前计算的对象。
	`#root`一直存在，代表root context对象  
	
	```
	// create an array of integers
	List<Integer> primes = new ArrayList<Integer>();
	primes.addAll(Arrays.asList(2,3,5,7,11,13,17));
	
	// create parser and set variable 'primes' as the array of integers
	ExpressionParser parser = new SpelExpressionParser();
	StandardEvaluationContext context = new StandardEvaluationContext();
	context.setVariable("primes",primes);
	
	// all prime numbers > 10 from the list (using selection ?{...})
	// evaluates to [11, 13, 17]
	List<Integer> primesGreaterThanTen = (List<Integer>) parser.parseExpression(
	"#primes.?[#this>10]").getValue(context);
	```

* 函数
可以通过注册用户自定义的函数来扩展SpEL，这些函数在注册后，可以在表达式中使用。
使用StandardEvaluationContext的`public void registerFunction(String name, Method m)`  
方法来注册自定义函数。
```
public abstract class StringUtils {
	public static String reverseString(String input) {
		StringBuilder backwards = new StringBuilder();
		for (int i = 0; i < input.length(); i++)
		backwards.append(input.charAt(input.length() - 1 - i));
		}
		return backwards.toString();
	}
}
```
````
ExpressionParser parser = new SpelExpressionParser();
StandardEvaluationContext context = new StandardEvaluationContext();
context.registerFunction("reverseString",
StringUtils.class.getDeclaredMethod("reverseString", new Class[] { String.class }));
String helloWorldReversed = parser.parseExpression(
"#reverseString('hello')").getValue(context, String.class);
````
* Bean references

如果待计算的上下方已经被配置为一个bean resolver。
可以通过在表达式中使用`@`标志来寻找对应的bean
```
ExpressionParser parser = new SpelExpressionParser();
StandardEvaluationContext context = new StandardEvaluationContext();
context.setBeanResolver(new MyBeanResolver());
// This will end up calling resolve(context,"foo") on MyBeanResolver during evaluation
Object bean = parser.parseExpression("@foo").getValue(context);
```

如果访问一个工厂bean本身，需要在bean name上加上前缀`&`  
```
ExpressionParser parser = new SpelExpressionParser();
StandardEvaluationContext context = new StandardEvaluationContext();
context.setBeanResolver(new MyBeanResolver());
// This will end up calling resolve(context,"&foo") on MyBeanResolver during evaluation
Object bean = parser.parseExpression("&foo").getValue(context);
```
* 三元操作符
```
tring falseString = parser.parseExpression(
		"false ? 'trueExp' : 'falseExp'").getValue(String.class);
```
* Elvis操作符
这是三元操作符的一个简化。
```
ExpressionParser parser = new SpelExpressionParser();
String name = parser.parseExpression("name?:'Unknown'").getValue(String.class);
System.out.println(name); // 'Unknown'
```
相当于`name != null ? name : "Unknown";`
* 安全导航操作符
避免访问一个对象的属性和方法时，这个对象为null时，抛出异常,使用了这个操作符，不会抛出异常，  
而是直接返回null。
```
ExpressionParser parser = new SpelExpressionParser();
Inventor tesla = new Inventor("Nikola Tesla", "Serbian");
tesla.setPlaceOfBirth(new PlaceOfBirth("Smiljan"));
StandardEvaluationContext context = new StandardEvaluationContext(tesla);
String city = parser.parseExpression("PlaceOfBirth?.City").getValue(context, String.class);
System.out.println(city); // Smiljan
tesla.setPlaceOfBirth(null);
city = parser.parseExpression("PlaceOfBirth?.City").getValue(context, String.class);
System.out.println(city); // null - does not throw NullPointerException!!!
```

* 集合选择
使用SpEL来选择一些满足指定条件的元素。
`.?[selectionExpression]`，将会筛选集合，并返回一个原有集合的子集的新集合。
```
List<Inventor> list = (List<Inventor>) parser.parseExpression(
"Members.?[Nationality == 'Serbian']").getValue(societyContext);
```

* 集合投射(Collection Projection)
`![projectionExpression]`。
```
// returns ['Smiljan', 'Idvor' ]
List placesOfBirth = (List)parser.parseExpression("Members.![placeOfBirth.city]");
```
* 表达式模板
可以在表达式中，混合包含一个或者多个表达式，使用#{}来作为界定符
```
String randomPhrase = parser.parseExpression(
	"random number is #{T(java.lang.Math).random()}",
	new TemplateParserContext()).getValue(String.class);
// evaluates to "random number is 0.7038186818312008"
```


# 例子中使用的类

Inventor类
```
package org.spring.samples.spel.inventor;

import java.util.Date;
import java.util.GregorianCalendar;

public class Inventor {
	private String name;
	private String nationality;
	private String[] inventions;
	private Date birthdate;
	private PlaceOfBirth placeOfBirth;

	public Inventor(String name, String nationality) {
		GregorianCalendar c= new GregorianCalendar();
		this.name = name;
		this.nationality = nationality;
		this.birthdate = c.getTime();
	}

	public Inventor(String name, Date birthdate, String nationality) {
		this.name = name;
		this.nationality = nationality;
		this.birthdate = birthdate;
	}

	public Inventor() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public PlaceOfBirth getPlaceOfBirth() {
		return placeOfBirth;
	}

	public void setPlaceOfBirth(PlaceOfBirth placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}

	public void setInventions(String[] inventions) {
		this.inventions = inventions;
	}

	public String[] getInventions() {
		return inventions;
	}
}
```
PlaceOfBirth类：
```
package org.spring.samples.spel.inventor;
public class PlaceOfBirth {
	private String city;
	private String country;

	public PlaceOfBirth(String city) {
		this.city=city;
	}

	public PlaceOfBirth(String city, String country) {
		this(city);
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String s) {
		this.city = s;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}
```
Society类：
```
package org.spring.samples.spel.inventor;

import java.util.*;
public class Society {
	private String name;

	public static String Advisors = "advisors";
	public static String President = "president";

	private List<Inventor> members = new ArrayList<Inventor>();
	private Map officers = new HashMap();

	public List getMembers() {
		return members;
	}

	public Map getOfficers() {
		return officers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isMember(String name) {
		for (Inventor inventor : members) {
			if (inventor.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
}
```