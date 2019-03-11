## 事件

事件：定义了事件成员的类型，允许此类型（或类型的实例）通知其他对象发生了特定的事情。



在使用委托来实现发布————订阅模式时。
有可能会碰到两个问题
* 异常处理
为了避免委托链中，某个委托触发异常时，导致后面的委托得不到执行的情况，
需要获取委托链列表，迭代显式执行每一个委托，并将显式调用放在一段try中，单独处理每一个委托
触发的异常，并返回一个AggregateException异常（一个异常集合）。
* 返回值。
如果委托不返回void，或者有ref或out参数，也有必要遍历委托链列表。


事件这可以解决这两个问题。


### 事件的作用

CLR事件模型以委托为基础。

定义了事件成员的类型能提供以下功能
* 方法能登记它对事件的关注
* 方法能注销它对事件的关系
* 事件发生时，登记了的方法将收到通知。


要设计一个公开事件的类型，需要三步操作

1. 定义类型来容纳所有需要发送给事件通知接收者的附加信息

2. 在需要使用事件的类型中，定义事件成员

3. 定义负责触发事件的方法，来通知事件的登记对象

4. 定义方法将输入转化为期望事件


### 事件声明语法

```
event 委托类型的名称 事件变量的名称
```

比如：
````
//委托类型定义
public delegate void FeedBack(Int32 value);
class Program{
//事件作为实例字段使用
public event FeedBack feedBack;
}
````

### 设计要公开事件的类型


#### 1. 定义用来包装发送给事件通知接收者的附加信息的类型

````
internal class NewMailEventArgs : System.EventArgs
{
    public NewMailEventArgs(String from,String to,String subject)
    {
        From = from;
        To = to;
        Subject = subject;

    }
    public String From { get;private set; }
    public String To { get; private set; }
    public String Subject { get; private set; }
}
````
FCL中``System.EventArgs``的定义：
````
public class EventArgs
{
    public static readonly EventArgs Empty;
    public EventArgs();
}
````

#### 2. 定义事件成员
````
internal class MailManager
{
    public event EventHandler<NewMailEventArgs> NewMail;
}
````
FCL中``System.EventHandler<T>``的定义
```
public delegate void EventHandler<TEventArgs>(object sender, TEventArgs e);
```
所以委托声明的方法原型为：
```
void MethodName(Object sender,NewMailEventArgs e);
```
NewMail是事件名称。事件成员类型为``EventHandler<NewMailEventArgs>``，
这意味着事件通知的接收者必须提供一个与``EventHandler<NewMailEventArgs>``委托类型匹配的方法。sender代表事件调用的对象，e为事件通知时传递的附加信息。
#### 3. 定义触发事件通知的方法
按照约定，类要定义一个受保护的虚方法。
引发事件时，类及其派生类中的代码会调用该方法。
方法只获取一个参数，即一个``NewMailEventArgs``对象，其中包含了传给接收通知的对象的信息。

```
internal class MailManager
{
	...
	
    protected virtual void OnNewMail(NewMailEventArgs e)
    {
		//线程安全考虑，将委托字段的引用复制到一个临时变量中
        EventHandler<NewMailEventArgs> temp = Volatile.Read(ref NewMail);
        if (temp != null) temp(this, e);
    }
}
```

#### 4. 定义方法将输入转化为期望事件（在内部调用触发事件通知的方法）

````
internal class MailManager
{
	public void SimulateNewMail(string from,string to ,string subject)
    {
        NewMailEventArgs e = new NewMailEventArgs(from, to, subject) ;
        OnNewMail(e);
    }
}
````
#### 5. 定义监听事件的类型
```
//设计监听事件的类型
internal sealed class Fax
{
    private void FaxMsg(Object sender,NewMailEventArgs e)
    {
        //sender表示MailManger对象，便于将信息传回给它

        //e表示MailManger对象想要传给我们的附加信息
        Console.WriteLine("Faxing mail message:");
        Console.WriteLine("From={0},Tp={1},Subject={2}",e.From,e.To,e.Subject);
    }
    public Fax(MailManager mm)
    {
        mm.NewMail += this.FaxMsg;
    }
    public void Unregister(MailManager mm)
    {
        mm.NewMail -= this.FaxMsg;
    }
    
}

internal sealed class Tax
{
    private void TaxMsg(Object sender, NewMailEventArgs e)
    {
        //sender表示MailManger对象，便于将信息传回给它

        //e表示MailManger对象想要传给我们的附加信息
        Console.WriteLine("Taxing mail message:");
        Console.WriteLine("From={0},Tp={1},Subject={2}", e.From, e.To, e.Subject);
    }
    public Tax(MailManager mm)
    {
		//登记方法为事件通知的接收者（监听）
        mm.NewMail += this.TaxMsg;
    }
    public void Unregister(MailManager mm)
    {
		//注销监听
        mm.NewMail -= this.TaxMsg;
    }

}
```

完整代码：
````
    //发送给事件接收者的附加信息
    internal class NewMailEventArgs : System.EventArgs
    {
        public NewMailEventArgs(String from,String to,String subject)
        {
            From = from;
            To = to;
            Subject = subject;

        }
        public String From { get;private set; }
        public String To { get; private set; }
        public String Subject { get; private set; }
    }
    
    internal class MailManager
    {
        //定义事件类型成员
        public event EventHandler<NewMailEventArgs> NewMail;

        //定义负责引发事件的方法来通知事件的登记对象
        protected virtual void OnNewMail(NewMailEventArgs e)
        {
            EventHandler<NewMailEventArgs> temp = Volatile.Read(ref NewMail);
            if (temp != null) temp(this, e);
        }

        //定义方法将输入转化为期望事件

        public void SimulateNewMail(string from,string to ,string subject)
        {
            NewMailEventArgs e = new NewMailEventArgs(from, to, subject) ;
            OnNewMail(e);
        }

    }

    //设计监听事件的类型
    internal sealed class Fax
    {
        private void FaxMsg(Object sender,NewMailEventArgs e)
        {
            //sender表示MailManger对象，便于将信息传回给它

            //e表示MailManger对象想要传给我们的附加信息
            Console.WriteLine("Faxing mail message:");
            Console.WriteLine("From={0},Tp={1},Subject={2}",e.From,e.To,e.Subject);
        }
        public Fax(MailManager mm)
        {
            mm.NewMail += this.FaxMsg;
        }
        public void Unregister(MailManager mm)
        {
            mm.NewMail -= this.FaxMsg;
        }
        
    }

    internal sealed class Tax
    {
        private void TaxMsg(Object sender, NewMailEventArgs e)
        {
            //sender表示MailManger对象，便于将信息传回给它

            //e表示MailManger对象想要传给我们的附加信息
            Console.WriteLine("Taxing mail message:");
            Console.WriteLine("From={0},Tp={1},Subject={2}", e.From, e.To, e.Subject);
        }
        public Tax(MailManager mm)
        {
            mm.NewMail += this.TaxMsg;
        }
        public void Unregister(MailManager mm)
        {
            mm.NewMail -= this.TaxMsg;
        }

    }

````
事件演示
````
MailManager mm = new MailManager();
Fax fax = new Fax(mm);
Tax tax = new Tax(mm);
mm.SimulateNewMail("abc@google.com", "xyz@yahoo.cn", "Hello!");
Console.ReadLine();
````
output:
```
Faxing mail message:
From=abc@google.com,Tp=xyz@yahoo.cn,Subject=Hello!
Taxing mail message:
From=abc@google.com,Tp=xyz@yahoo.cn,Subject=Hello!

```

