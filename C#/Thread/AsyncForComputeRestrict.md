## 计算限制的异步操作

异步的计算限制操作要用其他线程执行。
比如：编译代码、拼写检查、语法检查、电子表格计算、音频和视频的转码等。

### CLR线程池基础

前面说过，创建和销毁线程是一个比较昂贵的操作，会耗费大量的时间，太多的线程也会浪费内存资源。
由于操作系统必须调度可运行的线程并执行上下文切换，所以太多的线程还影响性能。
为了改善这个情况，CLR使用了代码来管理它自己的线程池。
可将线程池想像成可由你的应用程序使用的一个线程集合。

每个CLR都有一个线程池，它在各个应用程序域(AppDomain)是共享的。
如果一个进程加载多个CLR，那么每个CLR都有它自己的线程池。

CLR初始化时，线程池中是没有线程的。
在内部，线程池维护了一个**操作请求队列**。
* 使用线程池中的线程
当程序需要执行一个异步操作时，就会调用某个方法来将一个记录项(entry)追加到该队列中。
线程池从这个操作请求队列中提取记录项，将这个记录项派发（dispatch）给线程池中的一个线程。
如果线程池中没有线程，就会创建一个新线程。

* 线程的复用
创建线程会造成一定的性能损失，然而，当线程池中的线程完成任务后，会返回到线程池中，不会被销毁。
在池中进入空闲状态，等待线程池分配下一个操作请求队列中的下一项。

如果程序向程序池发出请求的速度低于线程处理的速度，那么只要复用一个线程就可以服务所有请求。
如果超过了线程处理的速度，就会创建额外的线程，以使处理速度高于请求速度。
最终，这些异步操作请求，都由少量线程处理（复用），所以线程池不必创建大量线程

如果线程池中有大量的空闲线程，线程会自己醒来终止自己以释放占用的资源。

**注：线程池本身是启发式的，结合程序负载，他会自己根据当前线程池内线程的状态销毁/新增线程。**


### 执行简单的计算限制操作

将一个异步的、计算限制的操作放到一个线程池的队列中，通常可以调用ThreadPool类定义的以下方法之一：
````
//将方法排入队列以便执行。此方法在有线程池线程变得可用时执行。
static Boolean QueueUserWorkItem(WaitCallback callBack);
//将方法排入队列以便执行，并指定包含该方法所用数据的对象。此方法在有线程池线程变得可用时执行。
static Boolean QueueUserWorkItem(WaitCallback callBack,Object state);
````
这些方法向线程池的队列中添加一个"工作项"(work item)以及可选的状态数据， 如果此方法成功排队，则为 true；如果无法将该工作项排队，则引发 OutOfMemoryException。工作项其实就是由callBack参数标识的一个方法，该方法将由线程池线程调用。可通过state实参(状态数据)向方法传递一个参数。无state参数的那个版本的QueueUserWorkItem则向回调方法传递null。最终，池中的某个线程会处理工作项，造成你指定的方法被调用。你写的回调方法必须匹配System.Threading.WaitCallBack委托类型，它的定义如下：
```
delegate void WaitCallback(Object state);
```
以下演示了如何让一个线程池线程以异步方式调用一个方法：
```
class Program
{
    static void Main(string[] args)
    {

        Console.WriteLine("Main Thread:IsThreadPoolThread:{0}", Thread.CurrentThread.IsThreadPoolThread);
        Console.WriteLine("Main Thread:IsBackground:{0}", Thread.CurrentThread.IsBackground);
        Console.WriteLine("Main Thread:向队列添加操作项");
        ThreadPool.QueueUserWorkItem(ComputeBoundOp, "5");
        Console.WriteLine("Main Thread:继续做其他事件");            
        Thread.Sleep(1000);
        Console.ReadLine();
        

    }
    private static void ComputeBoundOp(Object obj)
    {
        Thread.CurrentThread.Name = "ComputeBoundOp Thread";
        Console.WriteLine("In ComputeBoundOp:state={0}",obj);
        Console.WriteLine("{0}:IsThreadPoolThread:{1}", Thread.CurrentThread.Name, Thread.CurrentThread.IsThreadPoolThread);
        Console.WriteLine("{0}:IsBackground:{1}", Thread.CurrentThread.Name, Thread.CurrentThread.IsBackground);
        Thread.Sleep(1000);
    }
}
```
output:
````
Main Thread:IsThreadPoolThread:False
Main Thread:IsBackground:False
Main Thread:向队列添加操作项
Main Thread:继续做其他事件
In ComputeBoundOp:state=5
ComputeBoundOp Thread:IsThreadPoolThread:True
ComputeBoundOp Thread:IsBackground:True
````
注：输出结果，不一定，因为是异步执行。

### 执行上下文

每个线程都关联了一个执行上下文数据结构。
执行上下文(execution context)包括的东西有：
* 安全设置
压缩栈、Thread的Principal属性和Windows身份
* 宿主设置
* 逻辑调用上下文数据

线程执行代码时，一些操作会受到执行上下文设置的影响。

默认情况下，当一个线程（初始线程）使用另一个线程（辅助）执行任务时，前者的执行上下文应该复制到辅助线程。

但是，因为执行上下文包含大量的信息，而收集这些信息，再把它们复制到辅助线程，要耗费不少时间。

在辅助线程不需要这些信息时，就阻止这种信息的复制。

通过使用``System.Threading.ExecutionContext``来完成这个操作。
常用方法
```
[SecurityCritical]
//取消执行上下文在异步线程之间的流动
public static AsyncFlowControl SuppressFlow();
//恢复执行上下文在异步线程之间的流动
public static void RestoreFlow();
//指示当前是否取消了执行上下文的流动。
public static bool IsFlowSuppressed();
```
比如

````
// 将一些数据放到Main线程的逻辑调用上下文中
CallContext.LogicalSetData("Name", "Jeffrey");

// 线程池能访问到逻辑调用上下文数据，加入到程序池队列中
ThreadPool.QueueUserWorkItem(
   state => Console.WriteLine("Name={0}", CallContext.LogicalGetData("Name")));


// 现在阻止Main线程的执行上下文流动
ExecutionContext.SuppressFlow();

//再次访问逻辑调用上下文的数据
ThreadPool.QueueUserWorkItem(
   state => Console.WriteLine("Name={0}", CallContext.LogicalGetData("Name")));

//恢复Main线程的执行上下文流动
ExecutionContext.RestoreFlow();

//再次访问逻辑调用上下文的数据
ThreadPool.QueueUserWorkItem(
   state => Console.WriteLine("Name={0}", CallContext.LogicalGetData("Name")));
````
output:
```
Name=Jeffrey
Name=
Name=Jeffrey
```


### 协作式取消和超时

.NET Framework提供了标准的**取消操作**模式。
这个是模式是**协作式**的，意味着要取消的操作必须显式支持**取消**。

换言之，无论执行操作的代码，还是试图取消操作的代码，都必须使用本节提到的类型。

下面是两个作为标准协作式取消模式的一部分的两个FCL类型。
* **System.Threading.CancellationTokenSource**
 
这个对象包含了与管理取消有关的所有状态。通过Token属性可以获得一个或多个CancellationToken(值类型)对象实例

* **System.Threading.CancellationToken** 


首先我们通过 System.Threading.CancellationTokenSource 对象管理或者取消对象状态，使用时直接 new 一个即可，而该对象拥有一个 CancellationToken 对象。

这个 Token 对象用于传递给执行计算限制操作的方法，通过该 Token 的 IsCancellationRequested 属性你可以在方法内部确认任务是否被取消，如果被取消你就可以进行返回操作。

```
static void Main(string[] args)
{
    var tokenSource = new CancellationTokenSource();

    ThreadPool.QueueUserWorkItem(z => Calculate(CancellationToken.None, 10000));

    Console.ReadKey();
    tokenSource.Cancel();

    Console.ReadLine();
}

private static void Calculate(CancellationToken token, int count)
{
    for (int i = 0; i < count; i++)
    {
        if (token.IsCancellationRequested)
        {
            Console.WriteLine("用户提前终止操作，退出线程..");
            break;
        }
        
        Console.WriteLine(count);
        Thread.Sleep(200);
    }
    
    Console.WriteLine("计数完成.");
}
```
注：如果你要执行一个不允许被取消的操作，可以为方法传递一个 CancellationToken.None 对象，因为该对象没有 Source 源，则不可能会被调用 Cancel() 进行取消。
#### **常用操作：**
* 注册取消事件
``CancellationToken`` 允许我们通过 ``Register()`` 方法注册多个委托，这些被注册了的委托会在 ``TokenSource`` 调用 ``Cancel`` 取消的时候优先调用，其调用的先后顺序为注册时的顺序。
* TokenSource 链接
可以通过 ``CancellationTokenSource.CreateLinkedTokenSource()`` 链接两个或多个对象，链接成功后会返回一个单独的 ``TokenSource`` 对象。

一旦这个新对象链接的任何一个 ``TokenSource`` 对象被取消的时候，该对象也会被取消掉。
* Cancel 的异常处理
在调用 ``TokenSource`` 的 ``Cancel() ``方法时(默认为 false)，该方法还有另外一个重载传入 bool 值，如果为 true 的时候，有多个注册的回调委托，一旦某个出现异常直接会被抛出该异常，不会等待其他回调执行完毕。

如果为 false，则会等到所有回调方法执行完成时，抛出一个 ``AggregateException`` 异常，内部的 ``InnerExceptions`` 包含有所有在执行过程中产生的异常信息集合。
* 超时取消
除了直接调用 ``Cancel()`` 立即取消操作之外，还有一个延迟取消的方法 ``CancelAfter()``，通过传递具体的延迟时间，我们可以在指定的之间之后取消某个任务。

### 任务

通过ThreadPool可以很方便地发起一次异步的计算限制的操作，最大的问题在于没有内建的机制让你知道这个操作何时完成，也没有机制在操作完成之后获得返回值。
为了克服这些机制，就需要使用任务Task。

使用任务执行一个计算限制操作有两种方式，两者也一样的可以传递 CancellationToken 进行取消操作：
```
new Task(Sum,20).Start();

Task.Run(()=>Sum(20));
```
构建一个Task对象可以传递的参数
* 委托
异步操作的执行代码
* CancellationToken
取消Task
* TaskCreationOptions
指定Task的执行方式（执行的时刻）

#### 等待任务完成并获取结果

任务除了标准的无返回值的 Task 类型之外，还有一个包含有泛型参数的 Task<TResult> 类型，其中 TResult 参数就是任务的返回值类型。

在创建好 Task<TResult> 对象之后，可以通过 Task.Wait() 等待任务执行完成，Task 的 Wait() 方法会阻塞调用者线程直到任务执行完成。执行完成之后，可以通过 Task.Reuslt 获取任务执行之后的返回值。
比如：
```
var  t=Task<String>.Run(() => {
            Thread.Sleep(1000);
            return "Hello";
        });
t.Wait();
var result = t.Result;
```
注：单独获取 Result 属性值时，其内部也会调用 Wait() 方法。


### 取消任务

可以通过一个 CancellationTokenSource 来取消 Task，一样的需要传入的计算限制方法添加 CancellationToken 参数。

只不过，在Task任务内部我们不通过 IsCancellationRequested 来判断任务是否取消，而是通过调用Token 的 ThrowIfCancellationRequested() 方法来抛出异常。

该方法会判断当前任务是否被取消，如果被取消了，则抛出异常。这是因为与直接通过线程池添加任务不同，线程池无法知道任务何时完成，而任务则可以表示是否完成，并且还能返回值。

重新看取消任务，并写代码