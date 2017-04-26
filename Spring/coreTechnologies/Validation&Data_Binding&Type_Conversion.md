# 概述
将数据校验当作为业务逻辑褒贬不一，Spring提供的校验和数据绑定对这种褒贬都不排斥。
数据验证不应该被绑定在web层，应该很容易本地化并且可以方便地添加新的验证逻辑。
考虑到这个原因，Spring提供了Validator接口，这个基础接口适用于应用程序中的任何一层。

数据绑定用来将用户的输入动态的绑定到应用程序的域模型上。
Spring提供了叫做DataBinder来做完成此事。  

validation包由Validator和DataBinder组成，这个包主要用于MVC framework中，但不只限于
MVC framework中使用。

BeanWrapper是Spring Framework中一个基本的根本概念，在很多地方都有使用。  
但是，通常来说，很少直接使用到。  

Spring的DataBinder和BeanWrapper都使用PropertyEditors来转换和格式化属性值。  

PropertyEditor概念是JavaBeans规范中的一部分。

# Validation using Spring's Validator interface

Spring提供Validator接口来效验对象，这个接口在校验对象时，使用了一个Errors对象，  
它会将校验的失败信息汇集到Errors对象当中。

```
package org.springframework.validation;

public interface Validator {
	boolean supports(Class<?> clazz);	
	void More validate(Object target, Errors errors);
}
```

Validator有两个方法：
* boolean supports(Class<?> clazz)
	主要用来验证当前校验是否支持相应的类
* void More validate(Object target, Errors errors)
	进行校验操作

比如：
```
public class Person {
	private String name;
	private int age;
	// the usual getters and setters...
}
```

实现这个接口中，主要根据是使用ValidationUtils这个帮助类来完成相应的校验。
针对Person这个类来编写一个类实现``org.springframework.validation.Validator``接口：

```
public class PersonValidator implements Validator {
	/**
	* This Validator validates *just* Person instances
	*/
	public boolean supports(Class clazz) {
		return Person.class.equals(clazz);
	}

	public void validate(Object obj, Errors e) {
		ValidationUtils.rejectIfEmpty(e, "name", "name.empty");
		Person p = (Person) obj;
		if (p.getAge() < 0) {
			e.rejectValue("age", "negativevalue");
		} else if (p.getAge() > 110) {
			e.rejectValue("age", "too.darn.old");
		}
	}
}
```
*注：ValidationUtils类是一个抽象类全是静态方法，不可被继承*



# 3.Resolving codes to error messages（从错误代码到错误信息）

上面已经讲述了数据绑定和校验。下面要讲述的与校验错误相关的错误信息的输出。

在上面的例子中，我们展示了校验name和age字段，并发现了错误。  
现在，我们将要把这些错误信息进行输出，这个操作通过使用MessageSource来完成。

当调用ValidationUtils中的rejectValue相关的方法或者任何实现Errors接口中的类的reject方法时，这个实现不仅注册你传入的错误代码，还会注册一些额外的错误代码（具体的错误代码由使用的  
**``MessageCodesResolver``**来决定），默认这些额外的错误代码使用defaultMessageCodesResolver来生成，它不仅注册代提供的错误代码信息，同时还会记录包含你提供的字段名称的错误信息（这些你提供的代码和字段名称，都是通过reject方法传入的）。

所以在这个例子中，你通过``rejectValue("age", "too.darn.old")`` ``age``为字段名称，``too.darn.old``为错误代码，它不仅注册了``too.darn.old``代码，还会注册``too.darn.old.age``（包含字段名称）和``too.darn.old.age.int``（包含字段类型）。 

这种实现是为了方便开发者定位诸如此类的错误信息。

更多信息[MessageCodesResolver](http://docs.spring.io/spring-framework/docs/4.3.7.RELEASE/javadoc-api/org/springframework/validation/MessageCodesResolver.html)和默认的策略[DefaultMessageCodesResolver](http://docs.spring.io/spring-framework/docs/4.3.7.RELEASE/javadoc-api/org/springframework/validation/DefaultMessageCodesResolver.html)。


# 4.Bean manipulation and BeanWrapper

org.springframework.beans包遵循Oracle的[JavaBeans标准规范](http://docs.oracle.com/javase/tutorial/javabeans/index.html)。


在这个package中，有个重要的BeanWrapper接口和它的实现类BeanWrapperImpl。
从javadocs中可以看出，BeanWrapper提供的功能包含：
设置和获取属性值（单个或者批量的），获取属性描述，以及查询属性判断它们是可读还是可写的。

BeanWrapper也提供了嵌套的属性的操作（无限嵌套深度）。

BeanWrapper不需要在目标类中添加支持的代码，就可以支持添加标准的JavaBeans的PropertyChangeListeners和VetoableChangeListeners。

BeanWrapper通常不直接在程序代码中使用，而是由DataBinder和BeanFactory来使用。

BeanWrapper就像它的名字一样，它就是用来包装bean来执行bean上的操作，比如设置和获取属性值。

## 4.1 Setting and gettting basic and nested properties

Setting and gettting basic and nested properties通过使用setPropertyValue(s)和
getPropertyValue(s)方法来完成，它们具有多个重载版本。

针对对象属性，有一些约定的规则。比如：

|表达式|说明
|---|---|
|name|	指向属性name，与getName() 或 isName() 和 setName()相对应。|
|account.name|指向属性account的嵌套属性name，与之对应的是getAccount().setName()和getAccount().getName()|
|account[2]|指向索引属性account的第三个元素，索引属性可能是一个数组（array），列表（list）或其它天然有序的容器。|
|account[COMPANYNAME]|指向一个Map实体account中以COMPANYNAME作为键值（key）所对应的值|

下面这个例子，将会展示BeanWrapper如何来设置和获取属性：
```
public class Company {
	private String name;
	private Employee managingDirector;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Employee getManagingDirector() {
		return this.managingDirector;
	}

	public void setManagingDirector(Employee managingDirector) {
		this.managingDirector = managingDirector;
	}
}
```
```
public class Employee {
	private String name;
	private float salary;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getSalary() {
		return salary;
	}

	public void setSalary(float salary) {
		this.salary = salary;
	}
}
```


```
BeanWrapper company = new BeanWrapperImpl(new Company());

// setting the company name..
company.setPropertyValue("name", "Some Company Inc.");

// ... can also be done like this:
PropertyValue value = new PropertyValue("name", "Some Company Inc.");
company.setPropertyValue(value);

// ok, let's create the director and tie it to the company:
BeanWrapper jim = new BeanWrapperImpl(new Employee());
jim.setPropertyValue("name", "Jim Stravinsky");
company.setPropertyValue("managingDirector", jim.getWrappedInstance());

// retrieving the salary of the managingDirector through the company
Float salary = (Float) company.getPropertyValue("managingDirector.salary");

```

## 4.2 Built-in PropertyEditor 实现

Spring使用PropertyEditors的概念来完成对象和字符串之间的转换。

有时候，使用一个对象本身以外的方式来表示属性，将会显示很有用。   
比如，Date类型，使用人类可读的方式（如：字符串'2017-04-22'），实际使用时，还是会将  
其转换为原有的Date对象类型。

要完成这项工作，通过注册自定义的editors（类型为java.beans.PropertyEditor）来实现，  
可以在BeanWrapper中注册，或者是一个IoC容器中注册。

属性编辑器在Spring中应用比较多的两个地方：

* 使用PropertyEditors设置beans上的属性，当在XML文件中配置bean的属性值时，使用java.lang.String作为值（如果这个属性对应有一个类类型的参数），Spring会使用ClassEditor来将这个字符串值转换为这个对象的一个实例。
* 在Spring'MVC framework中，使用各种PropertyEditors来解析HTTP请求参数，这个操作也  
	可以使用CommandController的子类来手动实现绑定。

Spring有一系列的已经内建的PropertyEditors（位于``org.spring.framework.beans.propertyeditors``包中），下面中列表列出所有的内建PropertyEditors，它们中的大多数已经默认在BeanWrapperImpl中注册好了。
property editor中有部分可配置的，你可以注册自己的实现来覆盖默认的那一个。

**内建的PropertyEditors**

|类|说明|
|---|---
|ByteArrayPropertyEditor|byte数组编辑器，字符串会被转换为对应的byte。默认已经在BeanWrapperImpl中注册|
|ClassEditor|将字符串转换为对应的实际类，当类没有被找到，会throw IllegalArgumentException，默认已经在BeanWrapperImpl中注册|
|CustomBooleanEditor|Boolean的自定义属性编辑器，默认已经在BeanWrapperImpl中注册，可配置项，可以通过注册一个自定义实例来替代它|
|CustomCollectionEditor|集合的属性编辑器，转换任何源collection到指定的类型的Collection|
|CustomDateEditor|java.util.Date的自定义属性编辑器，默认没有注册|
|CustomNumberEditor|Number(Integer,Long,Float,Double)的自定义编辑器，默认已经在BeanWrapperImpl中注册,，可配置项|
|FileEditor|将字符串转换为java.io.File对象，默认已经在BeanWrapperImpl中注册|
|InputStreamEditor|单向的属性编辑器，通过ResourceEditor和Resource将字符串转换为InputStream，注：默认的使用不会自动关闭这个InputStream，默认已经在BeanWrapperImpl中注册|
|LocaleEditor|在String对象和Locale 对象之间互相转化。（String的形式为[语言]_[国家]_[变量]，这与Local对象的toString()方法得到的结果相同）在BeanWrapperImpl中已经默认注册好了。|
|PatternEditor|将字符串转换为java.util.regex.Pattern对象|
|PropertiesEditor|能将String转化为Properties对象（由JavaDoc规定的java.lang.Properties类型的格式）。在BeanWrapperImpl中已经默认注册好了。|
|StringTrimmerEditor|一个用于修剪(trim)String类型的属性编辑器，具有将一个空字符串转化为null值的选项。默认没有注册，必须由用户在需要的时候自行注册。|
|URLEditor|能将String表示的URL转化为一个具体的URL对象。在BeanWrapperImpl中已经默认注册好了。|

## 4.2.1 Registering addition custom PropertyEditors


# 5. Spring Type Conversion

Spring 3引入了一个core.convert包，这个包提供了一个通用类型转换系统。
这个系统定义了一个SPI来实现类型转换逻辑，还提供了在运行时执行类型转换的API。
在Spring container当中，这个系统可以用来充当PropertyEditors的角色来将String  
类型的属性值转换成属性实际类型的值。  

这个公开的API在你的程序只要有类型转换出现，就会使用到。

## 5.1 Converter SPI

实现类型转换逻辑的SPI是简单和强类型的：
```
package org.springframework.core.convert.converter;

public interfac Converter<S,T>{
	T convert(S source)
}
```

要创建自己的转换器，只会简单的实现上面的Converter<S,T>接口，参数化S是源类型，参数化T是  
目标类型。
这样的转换器也可以用于集合或者数组之间，一个默认的数组/集合 转换已经被注册了（默认的是  
DefaultConversionService）。

每次调用convert(S),都必须保证参数不为NULL。请确保你的转换器是线程安全的。

一些转换器的实现已经在core.convert.support包中提供了。
其中就包含了Strings到Numbers和其它一般类型之间的转换，
比如实现一个：StringToInteger

```
package org.springframework.core.convert.support;

final class StringToInteger implements Converter<String, Integer> {

	public Integer convert(String source) {
		return Integer.valueOf(source);
	}

}```

## 5.2 ConvertFactory

当你需要集中化整个类层次上的类型转换时（比如String to java.lang.Enum对象），实现ConverterFactory：

```
package org.springframework.core.convert.converter;

public interface ConverterFactory<S, R> {

	<T extends R> Converter<S, T> getConverter(Class<T> targetType);

}
```

参数化S是源类型，R是转换到目标的基类型。然后实现getConverter(Class<T>)，其中T是R的子类类型。

比如StringToEnum ConverterFactory这个例子：
```
package org.springframework.core.convert.support;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.util.NumberUtils;

final class StringToNumberConverterFactory implements ConverterFactory<String, Number> {

	@Override
	public <T extends Number> Converter<String, T> getConverter(Class<T> targetType) {
		return new StringToNumber<>(targetType);
	}


	private static final class StringToNumber<T extends Number> implements Converter<String, T> {

		private final Class<T> targetType;

		public StringToNumber(Class<T> targetType) {
			this.targetType = targetType;
		}

		@Override
		public T convert(String source) {
			if (source.isEmpty()) {
				return null;
			}
			return NumberUtils.parseNumber(source, this.targetType);
		}
	}

}
```

## 5.3 GenericConverter

当你需要一个更加复杂的转换器的实现是，考虑使用GenericConverter接口。
这个接口具有更加的灵活性，但不是强类型的，一个GenericConverter支持多个源类型和目标类型  
间的转换。
除此之外，GenericConverter还提供了源和目标字段的上下文环境供你在实现自己的类型转换逻辑时使用。

```
package org.springframework.core.convert.converter;

import java.util.Set;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.Assert;

public interface GenericConverter {

	Set<ConvertiblePair> getConvertibleTypes();


	Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType);

	final class ConvertiblePair {

		private final Class<?> sourceType;

		private final Class<?> targetType;

		/**
		 * Create a new source-to-target pair.
		 * @param sourceType the source type
		 * @param targetType the target type
		 */
		public ConvertiblePair(Class<?> sourceType, Class<?> targetType) {
			Assert.notNull(sourceType, "Source type must not be null");
			Assert.notNull(targetType, "Target type must not be null");
			this.sourceType = sourceType;
			this.targetType = targetType;
		}

		public Class<?> getSourceType() {
			return this.sourceType;
		}

		public Class<?> getTargetType() {
			return this.targetType;
		}

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			if (other == null || other.getClass() != ConvertiblePair.class) {
				return false;
			}
			ConvertiblePair otherPair = (ConvertiblePair) other;
			return (this.sourceType == otherPair.sourceType && this.targetType == otherPair.targetType);
		}

		@Override
		public int hashCode() {
			return (this.sourceType.hashCode() * 31 + this.targetType.hashCode());
		}

		@Override
		public String toString() {
			return (this.sourceType.getName() + " -> " + this.targetType.getName());
		}
	}

}

```

为了实现GenericConverter，首先让getConvertibleTypes()返回支持的源#目标类型的键值对集合。
然后实现 convert(Object, TypeDescriptor, TypeDescriptor)来完成转换逻辑。

source的TypeDescriptor提供了源字段存储的待转换的值的访问。
target的TypeDescriptor提供了转换后的值存放的目标字段的访问。

比如：ArrayToCollectionConverter

注：
>因为GenericConverter是一个更加复杂的SPI接口，只有需要的时候才会使用。

## 5.4 ConditionalGenericConverter
有时候，你只想要转换器在满足某些特殊条件时才执行。
比如，只有当一个指定的注解在目标字段上，才执行这个转换，或者只有在某一个方法在目标类型上  
定义了，才去执行。

ConditionalGenericConverter是GenericConverter和ConditionalConverter接口的结合，
这个接口允许你定义这样的一个自定义的匹配条件。

```
public interface ConditionalGenericConverter	extends GenericConverter, ConditionalConverter {

	boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType);

}
```


## 5.5 ConversionService API

ConversionService为运行时执行类型转换定义了一个标准的API。
转换器往往都是在这个接口背后被执行。
```
package org.springframework.core.convert;

public interface ConversionService {

	boolean canConvert(Class<?> sourceType, Class<?> targetType);

	<T> T convert(Object source, Class<T> targetType);

	boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType);

	Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType);
}
```

大部分ConversionService的实现都实现了ConverterRegistry(为注册转换器提供了一个SPI)。
在内部，ConversionService的实现委托它注册的转换器来执行类型转换逻辑。

在core.convert.support包中，提供了一个强壮的ConversionService实现。

GenericConversionService是适合于大多数情况下的一般目的的实现。

ConversionServiceFactory提供了一个方便创建一般的ConversionService结构的工厂。

## 5.5 Configuring a ConversionService


一个ConversionService是一个无状态的对象，在程序启动时被实例化，然后在多个线程中共享。  

在一个Spring 应用 程序中，通常每个Spring容器（或者每个ApplicationContext）配置一个ConversionService实例。

Spring会在任何发生类型转换的地方来调用这个对象，你也可以将这个ConversionService注入到你的beans当中，然后直接调用它。

注：
>如果没有ConversionService在Spring中注册，原始的PropertyEditor-based系统将会被使用。

为Spring注册一个默认ConversionSerivce，通过加入如下的id为conversionService的bean definition：
```
<bean id="conversionService" class="org.springframework.context.support.ConversionServiceFactoryBean"/>
```

默认的ConversionService能够在string number,enum,collection,map及其它一般类型之间  
进行转换。

可以通过自定义的converter来扩展或者覆盖这个默认的转换器，设置converters属性，可以做到这点。这个属性值，可以是Converter,ConverterFactory,GenericConverter接口中任何一个的实现。
比如：
```
<bean id="conversionService" class="org.springframework.context.support.ConversionServiceFactoryBean">
	<property name="converters">
		<set>
			<bean class="example.MyCustomConverter"/>
		</set>
	</property>
</bean>
```

## 5.6 使用编程方式来使用一个ConversionService

为了使用编程方式来与一个ConversionService协同工作，仅仅需要注入一个引用，就像以前的任何的一个bean一样：
```
@Service
public class MyService {

	@Autowired
	public MyService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public void doIt() {
		this.conversionService.convert(...)
	}
}
```

# 6. Spring Field Formatting


就如上节所述，core.convert是一个通用的类型转换系统。
它为实现类型转换逻辑提供了一个统一的ConversionService API，以及强类型的Converter SPI。  

Spring容器使用这个系统来完成bean属性值的绑定，除此之外，Spring Expression Language(SpEL)和DataBinder使用这个系统来绑定字段值。

现在考虑，根据不同的客户运行环境来执行类型转换，比如Web和桌面应用。

你可能经常需要本地化字符串值。

Spring 3针对客户环境引入了一个与PropertyEditors替代的Formatter SPI来完成直接解决这些问题。


## 6.1 Formatter SPI

实现字段格式逻辑的Formatter SPI是简单和强类型的：

```
package org.springframework.format;
public interface Formatter<T> extends Printer<T>, Parser<T> {

}
```

Formatter从Printer和Parser中扩展
```
public interface Printer<T> {
	String print(T fieldValue, Locale locale);
}
```
```
import java.text.ParseException;

public interface Parser<T> {

	T parse(String clientValue, Locale locale) throws ParseException;
}
```

为了创建你自己的Formatter，只需要简单的实现Formatter接口。  
参数化T是你将要格式化的类型，比如，java.util.Date。  
实现print()操作来输出T的一个实例，在客户端本地化显示出来。
实现parse()操作，来将客户端本地化的表现解析成T的一个实例。

有很多Formatter实现已经在org.springframework.format子包中提供便于使用。


# 6.2 Annotaion-driven Formatting


# 6.3 FormatterRegistry SPI


# 6.4 FormatterRegistrar SPI

# 6.5 Configuring Formatting in Spring MVC

# 7.Configuring a global date & time format

# 8.Spring Validation

Spring 3在它的校验支持上提供了很多的增加。

* JSR-303 Bean Validation API现在已经完全支持了，
* 当以编程方式使用时，Spring的DataBinder在绑定对象时，同时支持它们的校验。
* Spring MVC现在已经支持声明式校验@Controller的输入

## 8.1 JSR-303 Bean Validation API 概述


它标准化了校验约束声明和元数据。

注：
>JSR（Java规范请求，Java Specification Requests）

使用这个API，你可以使用校验约束来注解注解域模型属性，并在运行时强制它们有效。

已经有很多的内建的约束可以使用，也可以添加自定义的约束。

比如：

```
public class PersonForm {
	private String name;
	private int age;
}
```

```
public class PersonForm {

	@NotNull
	@Size(max=64)
	private String name;

	@Min(0)
	private int age;
}

```


## 8.2 Configuring a Bean Validation Provider

Spring为Bean Validation API提供了完全的支持。

这包括对引导一个JSR-303/JSR-349 Bean Validation provider成为一个Spring bean的支持。

这允许在程序内任何需要校验的地方，注入一个``javax.validation.ValidatorFactory``或者 ``javax.validation.Validator``。


使用LocalValidatorFactoryBean来配置一个默认的校验器作为一个Spring bean。
```
<bean id="validator"
class="org.springframework.validation.beanvalidation.LocalValidatorFactoryBean"/>
```

这个基本配置将会触发Bean Validation使用默认的引导机制进行实例化。

JSR-303/JSR-349的提供者，比如Hibernate Validator，要存在于classpath中，并且会  
被自动检测到。

### 8.2.1 注入一个Validator

LocalValidatorFactoryBean实现了javax.validation.ValidatorFactory和
javax.validation.Validator两个接口，也实现了org.springframework.validation.Validator。

你可以对需要调用校验逻辑的beans中，对这些接口中的任意一个，注入一个引用到beans当中去。

如果你选择与[Bean Validation](http://beanvalidation.org/)直接协同工作，注解一个引用到``javax.validation.Validator``中


```
import javax.validation.Validator;

@Service
public class MyService {

	@Autowired
	private Validator validator;
}
```

如果你选择使用Spring Validation API,注解一个引用到``org.springframework.validation.Validator``

```
import org.springframework.validation.Validator;

@Service
public class MyService {

	@Autowired
	private Validator validator;
}
```


### 8.2.2 Configuring Custom Constraints 

每个Bean Validation 约束由两部分组成。
* @Constraint注解
	声明约束和它可配置的属性
* javax.validation.ConstraintValidator接口的实现
	实现约束的具体行为





### 8.3.3 Spring-driven Method Validation

### 8.3.4 Additional Configuration Options

## 8.4 Configuring a DataBinder


## 8.5 Spring MVC Validation