# 泛型

## 如果没有泛型


比如我们要写一个数据结构：栈，为了能够适用于不同数据类型，可以使用Object作为操作对象。

在不用泛型的情况下，创建支持多种数据类型的类，根本的问题在于它们必须支持一个公共基类（或接口），通常是Object。


但是会有两个问题出现
1. 类型安全
对于栈来说，所有的对象都是Object，它并不知道，这个对象真正的类
型，当有可能发生类型转换(Object转换成其它派生类型)，或使用类型的特定方法、属性的时候，就有
可能发生类型转换失败，从而将类型安全检查移到了运行时。

2. 装箱与拆箱
引用类型转换为基类或接口对性能的影响可忽略不计。
但是，如果传入的参数是值类型的（比如int,struct)，要转换成Object类型，也就是引用类型，需要装箱，
同时转换回来的时候，还需要拆箱。
装箱和拆箱，必须要分配内存（堆）、复制值、垃圾回收。

## 实现类型安全、避免值类型与引用类型的转换
在不用泛型的前提下，为了修改Stack类来确保类型安全（编译时就能捕捉），
强迫它存储特定的数据类型，只能创建一个特殊的类，内部操作的对象全是这个特定的数据类型。

但是，这就会出现一个问题，如果要实现，很多种特定数据类型的
Stack，就需要为其中的每一个，写一个对应Stack类。
最终将产生大量重复的代码。


## 泛型

泛型是CLR和编程语言提供的一种特殊机制，它支持另一种形式（面向对象提供了方法、类、继承这种形式的代码重用）
的代码重用，即“算法重用”。开发效率得到提高。

CLR允许创建 泛型引用类型和泛型值类型（但不允许创建泛型枚举类型）。
此外，CLR还允许创建泛型接口和泛型委托。
同时，CLR还允许在引用类型、值类型、接口中定义泛型方法


## FCL中的泛型

在FCL中泛型最明显的应用就是集合类。


## CLR内部如何处理泛型

** 开放类型与封闭类型**

CLR会为各种数据类型创建称为类型对象(type object)的内部数据结构。

具有泛型类型参数的类型仍然是一种数据类型。

CLR同样会为它创建内部的类型对象。

具有泛型类型参数的类型称为**开放类型**，CLR禁止构造开放类型的任何实例。
这类似于CLR禁止构造接口类型的实例。


在引用泛型类型时，可指定一组泛型类型实参。
为**所有类型参数**都传递了实际的数据类型，使其成为**封闭类型**。


CLR允许构封闭类型的实现。

** 泛型中的静态字段和静态构造函数 **

每个封闭类型都有它自己的静态字段集。
````
class Stack<T>
{
    public static readonly Type Ttype=typeof(T);
}

 Console.WriteLine(Stack<int>.Ttype);
 Console.WriteLine(Stack<String>.Ttype);

output
--------
System.Int32
System.String

````
如上所示，Stack<int>和Stack<String>是Stack<T>给定不同的实际类型参数的两种不同的封闭类型，两个类型分别拥有自己的静态字段。



## 泛型类型和继承

泛型类型仍然是类型，所以能从任何类型派生（继承任何类）。


## JIT如何处理泛型

* 同一种泛型类型，类型实参为引用类型，可代码重用

CLR认为所有引用类型实参都完全相同，所以能够代码共享。
以List<T>为例：
比如现在有List<String>和List<Sream>，因为String和Stream都是引用类型，所以这两种类型，会使用相同的代码。
JIT中会为所有的List<引用类型>，只生成一份代码，所有的List<引用类型>共享这份代码。

之所有能这样做，是由于所有引用（指针，内存块的地址号）都具有相同的大小（32bit=4byte，64bit=8byte）。
无论传入怎样的引用类型，栈上一个引用需要的空间始终是相同的。

虽然每个类型还可以有自己的静态字段（堆上存储，堆上空间根据对象实际空间动态申请），但可执行代码本身是可以重用的。

当然不同的泛型类型（不同的算法和逻辑）之间是不能代码重用的。
比如List<String>和Quene<String>


* 同一种泛型类型，类型实参为值类型，不可代码重用

CLR必须专门为那个值类型生成本机代码，这是因为不同值类型的大小
不同，即使两个值类型大小一样，CLR仍然无法共享代码，因为可能需
要用不同的本机CPU指令来操作这些值。


## 泛型中类型实参的协变和逆变

泛型类型实参默认不支持协变和逆变。

* 协变
类型实参 派生类到基类的转换
````
List<String> strList = new List<String>();
List<object> objList = strList
````
* 逆变
类型实参 基类到派生类的转换
````
List<object> objList = new List<object>();
List<String> strList = objList
````

支持协变和逆变是建立在，所有的实际对象的转换，都是从派生类对象到基类对象的转换，而不是基类对象到派生类对象的转换（因为基类对象，很有可能不是派生类对象，比如是从另外一个派生类对象转换这来的，这种转换就有可能发生错误，不符合编译时类型安全策略，而是运行时类型安全策略）

下面演示一下不支持逆变和协变的原因

````
//泛型接口
interface IStack<T> where T: class,new()
    
{
    void Push(T t);
     T Pop();
    

}
//继承泛型接口的泛型类
class MyStack<U> : IStack<U> where U:class,new()
{
    public void Push(U t)
    {

    }
    public U Pop()
    {
        return new U();
    }
}
//基类
public class Person {
    public Int32 Age { get; set; } 
    public String Name { get; set; }
}
//子类
public class Student:Person
{
    public String SchoolName { get; set; }
    
}
````
### 不默认支持逆变的原因

````

Person p = new Person();
Student s = new Student();
//逆变
IStack <Person>  pStack= new MyStack<Person>();            
IStack<Student> sStack = pStack;//不合法的操作
sStack.Push(s);//理论上没有问题，因为传入了一个Student的对象实例，而sStack.Push实际用的是pStack.Push(Person),在本应该接受Person的地方传入了Student。
Student s1=sStack.Pop();//类型不安全操作，sStack.Pop() 实际调用的是Person pStack.Pop()，该方法返回的是一个Person对象，而要接收的是一个Student类型，所以Person对象要转换成Student对象，这不符合类型安全策略
````


根据以上提示，如果能做到，T只做为输入值，而不作为输出值，逆变就没有问题，来对**IStack**做以下调整

* 通过只写接口实现

````
interface IStack<in T> where T: class,new()    
{
    void Push(T t);
     //T Pop();//舍弃
    

}
````


通过in修饰类型参数，同时保证T只出现在输入位置。

### 不默认支持协变的原因


````
Person p = new Person();
Student s = new Student();
 //协变
IStack<Student> sStack = new MyStack<Student>();
IStack<Person> pStack = sStack;
pStack.Push(Person);//类型不安全操作，pStack.Push() 实际调用的是sStackPush(Student)，在本应该接受Student的地方传入了Person，不符合类型安全。

Person p1 = pStack.Pop();//理论上没有问题，pStack.Pop用的是sStack.Pop,将返回的Student对象实例，转换成Person对象，符合类型安全

````

根据以上提示，如果能做到，T都是只做为输出值，而不作为输入值，协变就没有问题，来对**IStack**做以下调整

* 通过只读接口实现

````
interface IStack<out T> where T: class,new()    
{
    //void Push(T t);//舍弃
    T Pop();
}
````

通过out修饰类型参数，同时保证T只出现在输出位置。


### 委托和接口的协变和逆变泛型类型实参

CLR只允许在委托和接口上声明支持协变和逆变的泛型类型实参


FCL中的一个委托：TResult支持协变，T支持逆变
````
 public delegate TResult Func<in T, out TResult>(T arg);

//合法操作( class Student:Person)
Func<Person, Student> f1 = (p) => new Student() { Age = 10, Name = "123" };
Func<Student, Person> f2 = f1;
````

接口就如上所述。


## 泛型方法

定义泛型类、结构、接口时，类型中定义的任何方法都可以引用类型指定的类型参数。
CLR还允许方法指定它自己的类型参数（可作为方法的返回值，参数，局部变量的类型使用）。

泛型方法，在使用的时候，可以不指定类型实参，而由编译器在调用泛型方法时自动判断。


注：在遇到方法调用，有明确的匹配方法和泛型方法，编译器的策略是优先考虑明确的匹配，再考虑泛型匹配。
也可以显示指定调用（通过指定类型实参）。


## 泛型类型变量作为操作数使用

比如**+**、**-**、**``*``**、**>**、**<**
不能将这些操作符应用于泛型类型的变量，因为编译器在编译时确定不了类型。

可通过反射、dynamic和操作符重载来解决。