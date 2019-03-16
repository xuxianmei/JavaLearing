## 委托
委托也是一个类型。
定义一个委托类型语法：
````
delegate + 方法原型
````
比如：
````
public delegate void Action();
````
### 委托的用处

委托是一个类型，可以创建一个委托的类型实例对象，允许像传递其他任何对象那样传递一个方法，并调用该方法。
所以委托类似方法指针。

### 委托的本质

比如
````
public delegate void FeedBack(Int32 value);
````
编译器会将这些代码编译实际会像下面定义一个完整的类：

````
public class FeedBack:System.MulticastDelegate{
	//构造器
	public Feedback(Object obj,IntPtr method);

	//这个方法的原型与源代码指定的一样：void MethodName(Int32);
	public virtual void Invoke(Int32 value);

	//以下方法实现对回调方法的异步回调
	public virtual IAsyncResult BeginInvoke(Int32 value,AsyncCallback callback,Object obj);

	public virtual void EndInvoke(IasyncResult result);
}

````

简单来说，委托也是类，所以能够定义类的地方，都能定义委托，也可以创建创建委托的实例。


MulticastDelegate三个重要的非公共字段

字段|类型|说明
---|---|---
_target| System.Object| 这个字段当方法参数是实例方法时，记录的是对象this的值，当委托对象包装一个静态方法时，这个字段为null
_methodPtr|System.IntPtr| 方法指针，一个内部的整数值，CLR用它标识要回调的方法。
_invocationList|System.Object|该字段通常为null。构造委托链时，它引用一个委托数组。用回调回调多个方法时使用。

所有委托都有一个构造器，它有两个参数，一个对象引用，另一个是引用回调方法的整数（从MethodDef或MemberRef元数据token获得。

所以每个委托对象实际都是一个包装器，其中包装了一个方法和调用该方法时要操作的对象。



### 委托链

委托链是委托对象的集合，通过委托链可以实现用一个委托来回调多个方法。

````
//使用到的类
public class Test
{
   public static void DataToFile(Int32 value)
    {
        Console.WriteLine("DataToFile:"+value);

    }

   public void DataToConsole(Int32 value)
    {
        Console.WriteLine("DataToConsole:" +value);
    }
}
````
使用：
````
Test t = new Test();
FeedBack f1 = (FeedBack)System.Delegate.Combine(new FeedBack(Test.DataToFile), new FeedBack(t.DataToConsole));

GetDataTo(1, f1);
````
注意：如果是有返回值的委托组成的委托链，只返回最后一个委托回调方法的返回值。

C#编译器自动为委托类型的实例重载了+=和-=操作符，这些操作符，分别调用了Delegate.Combine和Delegate.Remove。

还可以通过委托的实例方法GetInvocationList来获取委托数组，来显示调用链中的任意一个委托。


````
FeedBack f1 = new FeedBack(Test.DataToFile);
f1 += new FeedBack(t.DataToConsole);
GetDataTo(1, (FeedBack)f1.GetInvocationList()[1]);
````




### FCL中的委托


最常使用的就是Action和Func，这些委托包括泛型和非泛型的。


如果需要使用ref或out关键字来传递参数，就需要定义自己的委托了。

### 委托的简化语法 

* 不需要构造委托对象
因为C#编译器，能根据传入的方法进行类型推断，所以不需要声明一个委托实例，而可以直接使用方法作为接受委托类型的实参。
比如
````
GetDataTo(1, Test.DataToFile);
````

* 不需要定义方法
通过lambda表达式（C# 3.0引进的一种编写匿名函数的语法糖），
在本应该传入委托类型作为实参的地方，直接使用lambda表达式。
````
GetDataTo(1, (value)=> {
            Console.WriteLine("DataToFile:" + value);
        });
````
编译器看到这个lambda表达式之后，会在本类中自动定义一个新的私有方法。这个新方法称为匿名函数，因为方法名称由编译器自动创建。
注：C#2.0中还有创建匿名函数的方法
````
GetDataTo(1, delegate (int value){
            Console.WriteLine("DataToFile:" + value);
        });
````

