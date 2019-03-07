## Lambda表达式

在C#没有引入匿名函数功能之前，如果要将方法作为实参传递给委托类
型的参数(得益于类型推断，不然需要先将方法作为参数先传递给委托，
创建一个委托的实例，再将委托实例作为实参)，就需要为使用到的所有
方法都定义一个方法原型和方法体。

在有了匿名函数这个功能以后，可以不用直接定义方法，而是使用匿名函数这个功能，让C#编译器，自动生成一个作为私有成员的匿名方法。

Lambda表达式其实就是一种编写匿名函数的语法糖。

 * Lambda表达式的目
避免在需要很简单的方法生成委托时，声明全新的方法。

Lambda表达式可以不指定方法名，可访问性和返回类型，有时甚至可以不指定参数类型和return等

* 语句Lambda和表达式Lambda
	* 语句Lambda
````
	  Func<int,int,bool>  f1= (int  x, int y) => {
                return x > y;
                };
````
	* 表达式Lambda
````
	Func<int,int,bool>  f1= (int x, int y) => x > y;
	Func<int,int,bool>  f1= ( x,  y) => x > y;
````

## Lambda与外部变量

在Lambda表达式外部声明的局部变量，称为Lambda表达式的外部变量。
当Lambda主体使用一个外部变量的时候，就说变量被这个Lambda表达式捕捉。


局部变量的生存期一般和它的作用域绑定，离开作用域后，变量的存储位置就不再有效。
但是，如果Lambda表达式捕捉了外部变量，根据该表达式创建的委托可能具有比外部变量在一般情况 下更长（或更短）的生存期。
因为委托每次被调用时，都必须能安全地访问外部变量，在这种情况下，被捕捉的变量的生存期被延长了。
这个生存期至少与存活时间最长的委托对象一样长。

注：Lambda不是立马执行的（因为Lambda表达式本身就是一个方法），只有等到委托调用此方法时，才会执行，所以委托要为Lambda表达式存储这个外部变量。

委托本身就是一个类，在编译的时候，会为委托创建一个类，如果Lambda捕捉了外部变量，那么编译
器会将每个外部变量作为这个类的实例字段实现。所有使用外部局部变量的地方都被重写为使用
这个实例字段。

这就会产生所谓的闭包。

比如：

````
var actions = new List<Action>();            
for (var i=0; i <5; i++)
{
    actions.Add(()=> {
        Console.WriteLine(i);
    });
}
foreach (var action in actions)
{
    action();
}
````
output:
````
3
3
3
````

更改一下：
````
var actions = new List<Action>();
var names = new String[] { "A", "B", "C" };
int i = 0;
for ( ; i <3; i++)
{
    actions.Add(()=> {
        Console.WriteLine(i);
    });
}
i = 9;
foreach (var action in actions)
{
    action();
}
````
output:
````
9
9
9
````

foreach中循环外部变量
````
var actions = new List<Action>();
var names = new String[] { "A", "B", "C" };
int i = 0;
foreach (var name in names)
{
    actions.Add(()=> {
        Console.WriteLine(name);
    });
}
foreach (var action in actions)
{
    action();
}
````
output:
````
A
B
C
````

C# 5.0以后认为foreach中的每一次迭代，都是一个新的变量。所以不会共享同一个变量。



## Lambda表达式树

表达式树主要是为LINQ而引入.NET的。

将Lambda表达式转换成 System.Linq.Expressions.Expression<TDelegate>将创建
一个表达式树，而不是委托。

表达式树主要用LINQ。

Lambda表达式提供了一种简洁的语法来定义代码中"内联"方法，使其能转换成委托类型。
表达式Lambda（不是语句Lambda和匿名方法）也能转换成表达式树。


委托是一个对象，允许像传递其他任何对象那样传递一个方法，并在任何时候调用该方法。

表达式树也是一个对象，允许传递编译器对Lambda主体的分析。


这个分析有什么有用呢？显然编译器的分析在生成CIL时对编译器有用。
但为什么说在程序执行时，开发人员拥有代表这种分析的一个对象有用呢。

1. Lambda表达式作为数据使用。

````
persons.Where(
	person=>psrson.Name.Contains("TOM"));
````

现在假设persons不是Person[]类型，而是代表数据库表的对象，表中包含数百万人的数据。

表中的每一行的信息都可以从服务器传输到客户端，客户端创建一个Person对象来代表那一行。

现在有两种技术可以做到:
一个技术是将所有数据行全部传输到客户端，为每一行创建一个Person对象，根据Lambda创建一个
委托，再对每一个Person对象执行这个委托，筛选符合条件的对象。

第二个技术是，将Lambda的含义(过滤掉姓名不包含TOM的每一行）发送给服务器。
数据库服务器本来就很擅长快速执行这种筛选。然后，数据库服务器只将符合条件的少数几行传输
到客户端，再将这些行转换成指定对象。


转换成表达式树的Lambda表达式代表的是对Lambda表达式进行描述的数据，而不是编译好的、用于
实现匿名函数的代码。

由于表达式树代表数据而非编译好的代码，所以能在执行时分析这个数据，用来构造数据库中的查询。




Expression<Tdelegate>是从LambdaExpression派生的
LambdaExpression是从Expression派生的类型之一。
Tdelegate：代表泛型委托


当然不光只有Lambda表达式树这一种，还有很多其他种类的Expression。

## Expression的一些演示

* 演示1

````
Expression firstArg = Expression.Constant(2);//常量表达式2
Expression secondArg = Expression.Constant(3);//常量表达式3

Expression add=Expression.Add(firstArg, secondArg);//算术加法运算的BinaryExpression

Func<int> compiled = Expression.Lambda<Func<int>>(add).Compile();//将传入add构造一个LambdaExpresion，再将LambdaExpression转换成Lambda，最后隐式转换成委托。
Console.WriteLine(compiled());

Expression<Func<int, int, bool>> expression = (x, y) =>   x > y ;//Lambda表达式转换成Expression<Tdelegate>


Func<int, int, bool> func = expression.Compile();//Expression<Tdelegate>转换成委托

Console.WriteLine(func(1, 3));
````

output

````
5
False
````


* 演示2

````
Expression<Func<int, int, bool>> expression = (x, y) =>   x > y ;

Console.WriteLine("Type:"+expression.Type);
Console.WriteLine("NodeType:" + expression.NodeType);
Console.WriteLine("Parameters:" );
foreach(var param in expression.Parameters)
{
    Console.WriteLine(param);
}
Console.WriteLine("ReturnType:" + expression.ReturnType);
Console.WriteLine("Body:" + expression.Body);
````

output:
````
Type:System.Func`3[System.Int32,System.Int32,System.Boolean]
NodeType:Lambda
Parameters:
x
y
ReturnType:System.Boolean
Body:(x > y)

````


## 表达式树解析实例

下面这个例子用来解析LambdaExpression中的Body，BinaryExpression

````

static void PrintNode(Expression expression)
{
    if (expression is BinaryExpression)
    {
        PrintBinaryExpressionNode(expression as BinaryExpression);
    }
    else
      Console.WriteLine(NodeToString(expression));
}

static void PrintBinaryExpressionNode(BinaryExpression expression)
{
    PrintNode(expression.Left);
    Console.WriteLine(NodeToString(expression));
    PrintNode(expression.Right);
}


static String NodeToString(Expression expression)
{
    switch (expression.NodeType)
    {
       
        case ExpressionType.Add:
            return "+";
        case ExpressionType.Subtract:
            return "-";
        case ExpressionType.Multiply:
            return "*";
        case ExpressionType.Divide:
            return "/";
        case ExpressionType.GreaterThan:
            return ">";
        case ExpressionType.LessThan:
            return "<";
        default:
            return expression.ToString()+"("+expression.NodeType+")";

    }
}

````
使用上述方法来解析
````
Expression<Func<int, int, bool>> expression = (int x, int y) => x > y;
PrintNode(expression);
PrintNode(expression.Body );
````

output

````
(x, y) => (x > y)(Lambda)
x(Parameter)
>
y(Parameter)

````

## LINQ

将Lambda表达式，表达式树和扩展方法合并到一起，其实就是LINQ在C#语言这一层面的全部体现。

可以提供不同的LINQ Provider，比如（LINQ TO XML、LINQ TO Object、LINQ TO SQL )。
这些Provider的中心思想在于，我们可以从一个熟悉的源语言（C#）生成一个表达式树，将
结果作为一个中间格式，再将其转换成目标平台的本地语言，比如SQL。

![](../images/ExpressionAndLambda.jpg)