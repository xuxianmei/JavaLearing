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

```
public enum TaskCreationOptions
{
    
    None             = 0x0,//默认

    //提议TaskScheduler希望该任务尽快执行
    PreferFairness   = 0x1,

	//提议TaskScheduler应尽快可能地创建线程池线程
    LongRunning      = 0x2,

    //该提议总是被采纳：将一个Task和它的父Task关联
    AttachedToParent = 0x4,

	//该提议总是被采纳：如果一个任务试图与这个父任务连接，这就是一个普通任务，而不是子任务。
    DenyChildAttach  = 0x8,

	//该提议总是被采纳：强迫子任务使用默认调度器，而不是父任务的调度器	
    HideScheduler    = 0x10

}
```

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

注：单独获取 Result 属性值时，其内部也会调用 Wait() 方法。Wait()方法会阻塞当前调用线程。
注：如果一直不调用Wait或Result，或者一直不查询Task的Exception属性，代码就一直注意不到这个异常的发生。
为了帮助检测没有被注意到的异常，可以向TaskScheduler的静态方法
UnobservedTaskException事件登记一个回调方法。

#### 取消任务

可以通过一个 CancellationTokenSource 来取消 Task，一样的需要传入的计算限制方法添加 CancellationToken 参数。

只不过，在Task任务内部我们不通过 IsCancellationRequested 来判断任务是否取消，而是通过调用Token 的 ThrowIfCancellationRequested() 方法来抛出异常。

该方法会判断当前任务是否被取消，如果被取消了，则抛出异常。这是因为与直接通过线程池添加任务不同，线程池无法知道任务何时完成，而任务则可以表示是否完成，并且还能返回值。

```
class Program
    {
        static void Main(string[] args)
        {
            CancellationTokenSource ct = new CancellationTokenSource();
            
            var t = Task<int>.Run(() => Sum(ct.Token,1000000),ct.Token);

            try
            {
                var result = t.Result;
            }
            catch (AggregateException exception)
            {

            }
            
          
            Console.ReadLine();
            

        }

        private static int Sum(CancellationToken ct,int n)
        {
            int sum = 0;
            
            for (; n > 0; n--)
            {
                //如果在外部调用了ct.Cancel，这个方法会抛出异常。
                if(ct.IsCancellationRequested)
                ct.ThrowIfCancellationRequested();

                checked { sum += n; }
            }
            return sum;
        }
     
    }
```




#### 任务完成时自动启动新任务

伸缩性了的软件不应该使线程阻塞。
调用Wait，或在任务尚未完成时查询任务的Result属性，极有可能造成线程池创建新线程（因为
当前线程被阻塞，不能及时回到线程池执行接下的工作项）。

有一个方法，可以知道一个任务在什么时候结束运行。
任务完成时，可启动另一个任务。

如：
```` 
var t = Task<int>.Run(() => Sum(ct.Token,1000000))
    .ContinueWith(task=>Console.WriteLine("The sum is :"+task.Result));
````

这段代码不会阻塞调用线程，它与任务会保持异步执行。
如果调用线程本身就是一个线程池线程，且后面没有阻塞代码和可执行代码，它可以返回池中来
等待其他请求。
这就使线程池中的可用线程又多了一个，下一个请求来时，使用这个线程执行代码，避免创建新的线程的开销。

ContinueWith返回对新Task对象的引用。
Task对象内部包含了Continue任务的一个集合，可以用一个Task对象来多次调用ContinueWith。
任务完成时，所有ContinueWith任务都会进程线程池的操作请求队列中。

可以传递参数指定ContiueWith任务执行条件：TaskCotinuationOptions。

#### 启动子任务

一个任务创建的一个或多个Task对象默认是顶级任务，与创建它们的任务无关。

但可以通过TaskCreationOptions.AttachedToParent标志将一个Task和创建它的Task关联。

父任务执行结束前，所有子任务必须全部执行结束。
调用ContinueWith可以通过TaskCotinuationOptions指定。

比如
````
class Program
{
    static void Main(string[] args)
    {            
        
        var parent =new Task<int[]>(() =>
        {
            var results = new Int32[3];
            String name="parent";
            var task1 = new Task(() => {
                Console.WriteLine("Task1开始执行");
                results[0] = Sum(10000);                    

            }, TaskCreationOptions.AttachedToParent);
            task1.ContinueWith(parent_task1 =>
            {
                Console.WriteLine("Task1执行结束");
            });
            task1.Start();

            var task2 = new Task(() => {
                Console.WriteLine("Task2开始执行");
                results[1] = Sum(20000);

            }, TaskCreationOptions.AttachedToParent);
            task2.Start();
            task2.ContinueWith(parent_task2 => {
                Console.WriteLine("Task2执行结束");
            }, TaskContinuationOptions.AttachedToParent);

            var task3 = new Task(() => {
                Console.WriteLine("Task3开始执行");
                results[2] = Sum(30000);

            }, TaskCreationOptions.AttachedToParent);
            task3.Start();
            task3.ContinueWith(parent_task3 => {
              Console.WriteLine("Task3执行结束");
             },TaskContinuationOptions.AttachedToParent);
            return results;
        });
        parent.Start();
        var cwt = parent.ContinueWith(task =>
        {
             
            foreach (var item in task.Result)
            {
                Console.WriteLine(item);
            }
        });

        
      
        Console.ReadLine();
        

    }

    private static int Sum(int n)
    {
        int sum = 0;            
        for (; n > 0; n--)
        {               
            checked { sum += n; }
        }
        return sum;
    }
 
}
````

output:

````
Task1开始执行
Task3开始执行
Task1执行结束
Task3执行结束
Task2开始执行
Task2执行结束
50005000
200010000
450015000
````

此处的AttachToParent()，可以使用手动调用Wait或者WaitAll来代替。

**注意：如果要使用FCL中的``Task<int[]>.Run``方法调用时，在其方法内部，创建Task时，指定了TaskCreationOptions.DenyChildAttach，只能手动Wait其他任务，来实现父任务与子任务的联系。**


#### 任务内部数据结构

每个Task对象都有一组字段，这些字段构成了任务的状态。
常使用的状态包括：
* Taks的只读Id属性
* Task的执行状态
* 对父任务的引用
* 对Task创建时指定的TaskScheduler的引用
* 对ExecutionContext的引用
* 对ManualResetEventSlim的引用
* CancellationToken对象
* ContinueWithTask对象集合
* 为抛出未处理异常的子任务而准备的一个Task对象集合

所以，虽然任务很有用，但并不是没有代价的，必须为所有这些状态分配内存。
**如果不需要任务的附加功能，那么使用ThreadPool.QueueUserWorkItem能获得更好的资源利用率。**

通过 Task 的只读属性 Task.Status，我们可以知道任务目前处于哪种状态，其最终的状态主要有 3 种，分别是：RanToCompletion(已完成)、Canceled(被取消)、Faulted(出现异常失败)，这三种状态都属于任务完成状态。

另外值得注意的是，通过 ContinueWith()、ContinueWhenAll()、ContinueWhenAny() 等方法创建的任务状态都为 WaitingForActivation，这个状态代表任务会自动开始。

#### 任务工厂

如果你需要在执行多个相同配置的 Task 对象，可以通过 TaskFactory 和 TaskFactory<TResult>，其大概含义与 Task 的含义相同。

在创建工厂时，可以传递一些常用的配置标识位和 CancellationToken 对象，之后我们可以通过 StartNew() 方法来统一执行一堆任务。

```
var cts = new CancellationTokenSource();
        var tf = new TaskFactory<Int32>(cts.Token, TaskCreationOptions.AttachedToParent, TaskContinuationOptions.ExecuteSynchronously, TaskScheduler.Default);

        // 这个任务创建并启动三个子任务
        var childTasks = new[] {
            tf.StartNew(() => Sum(cts.Token, 10000)),
            tf.StartNew(() => Sum(cts.Token, 20000))
            tf.StartNew(() => Sum(cts.Token, Int32.MaxValue))  // 太大，抛出 OverflowException异常
        };

        // 如果子任务抛出异常蛮久取消其余子任务
        for (Int32 task = 0; task < childTasks.Length; task++)
            childTasks[task].ContinueWith(t => cts.Cancel(), TaskContinuationOptions.OnlyOnFaulted);

        // 所有子任务完成后，从未出错/未取消的任务返回的值，
        // 然后将最大值传给另一个任务来显示结果
        tf.ContinueWhenAll(childTasks,
           completedTasks => completedTasks.Where(t => !t.IsFaulted && !t.IsCanceled).Max(t => t.Result),
           CancellationToken.None)
           .ContinueWith(t => Console.WriteLine("The maximum is: " + t.Result),
              TaskContinuationOptions.ExecuteSynchronously).Wait();
    });

    // 子任务完成后，也显示任何未处理的异常
    parent.ContinueWith(p =>
    {
        // 将所有文本放到一个 StringBuilder 中并只调用 Console.WrteLine 一次
        // 因为这个任务可能和上面任务并行执行，而我不希望任务的输出变得不连续
        StringBuilder sb = new StringBuilder("The following exception(s) occurred:" + Environment.NewLine);
        foreach (var e in p.Exception.Flatten().InnerExceptions)
            sb.AppendLine("   " + e.GetType().ToString());
        Console.WriteLine(sb.ToString());
    }, TaskContinuationOptions.OnlyOnFaulted);

    // 启动父任务，便于它启动子任务
    parent.Start();
```

#### 任务调度器
TaskScheduler对象负责执行被调度的任务。

FLC提供了两个派生自TaskScheduler的类型：
* 线程池任务调度器
默认使用此任务调度器
* 同步上下文任务调度器
适合提供了图形用户界面的应用程序

**注：如果有特殊的任务调度需求，可以定义自己的TaskScheduleror派生类。

#### Parallel的静态For、ForEach和Invoke方法

并行执行，使用多个线程池线程辅助完成工作。                                            

使用Parallel的前提条件
* 工作项必须能并行执行
每个工作工作项都由一个多线程异步处理
* 工作项不会修改任何共享数据
因为是多线程异步，所以如果修改了线程共享的数据，会导致不正确的结果。
* 工作量不能太小
Parallel的方法本身也有开销，必须要分配委托，针对每个工作项都要调用一次这些委托。

**Parallel的所有方法**都让**调用线程**参与处理：直接在使用**调用线程**执行Parallel类中的方法，
而不是单独创建一个任务来执行。最后使用**Wait()**阻塞当前线程。
这意味着，当前调用线程一直在工作，直到**Wait()**挂起当前调用线程（前提是，当前**调用线程**完成Parallel方法中的工作比**线程池中的线程**完成工作更晚，如果当前**调用线程**在**Wait()**阻塞前完成这些工作，当前**调用线程**不会挂起，会继续工作），直到线程池中的线程完成Parallel指定的所有工作项，恢复线程状态，继续执行。

这与平普通的for foreach有相同的语义：**线程要在所有工作完成后才继续运行接下来的代码。**
注：Parallel的所有方法，指的是FCL中在Parallel中定义的方法，并不是工作项的委托。

Parallel的For，ForEach和Invoke方法都能接受一个ParallelOptions对象的重载版本。
```
public class ParallelOptions
{
    // 初始化 ParallelOptions 类的新实例
    public ParallelOptions();
    // 获取或设置与此 ParallelOptions 实例关联的 CancellationToken，运行取消操作
    public CancellationToken CancellationToken { get; set; }
    // 获取或设置此 ParallelOptions 实例所允许的最大并行度，默认为-1(可用CPU数)
    public int MaxDegreeOfParallelism { get; set; }
    // 获取或设置与此 ParallelOptions 实例关联的 TaskScheduler。默认为TaskScheduler.Default
    public TaskScheduler TaskScheduler { get; set; }
}
```

除此之外，For和ForEach方法有一些重载版本允许传递3个委托：
* 任务局部初始化委托(localInit)
为参与工作的每一个任务都调用一次委托。这个委托是在任务被要求处理一个工作项之前调用。
* 主体委托(body)
为参与工作的各个线程所处理的每一项都调用一次委托。
* 任务局部终结委托(localFinally)
为参与工作的每一个任务都调用一次委托。这个委托是在任务处理好派遣给它的所有工作之后调用。
即使主体委托引发一个未处理的异常，也会调用它。

计算一个目录中的所有文件的字节长度
```
  var files = Directory.GetFiles(@"D:\Program Files", "*.*", SearchOption.AllDirectories);
            Int64 Total = 0;
            var watch = new Stopwatch();
            watch.Start();
            //异步并行
            Parallel.ForEach(files, localInit: () => {
                return (Int64)0;
            },
            body: (file, loopstate, index, taskLocalTotal) => {
                Int64 count = 0;
                try
                {
                    count = (new FileInfo(file)).Length;
                }
                catch 
                {

                }
                return taskLocalTotal + count;               

            },
            localFinally:(taskLocalTotal) => {
                Interlocked.Add(ref Total, taskLocalTotal);
            });
            watch.Stop();
            Console.WriteLine("并行效率:{0} ms", watch.ElapsedMilliseconds);
            Console.WriteLine("文件总大小:{0} MB",Total / (1024*1024));

            Console.ReadLine();
```

主体委托有一个参数是ParallelLoopState对象：
```
 public class ParallelLoopState
    {
        // 获取循环的任何迭代是否已引发相应迭代未处理的异常
        public bool IsExceptional { get; }
        // 获取循环的任何迭代是否已调用 Stop
        public bool IsStopped { get; }
        // 获取从中调用 Break 的最低循环迭代。
        public long? LowestBreakIteration { get; }
        // 获取循环的当前迭代是否应基于此迭代或其他迭代发出的请求退出。
        public bool ShouldExitCurrentIteration { get; }
        // 告知 Parallel 循环应在系统方便的时候尽早停止执行当前迭代之外的迭代。
        public void Break();
        // 告知 Parallel 循环应在系统方便的时候尽早停止执行。
        public void Stop();
}
```


### PLINQ
 Microsoft的语言集成查询(LINQ)功能提供了一个简捷的语法来查询数据集合。使用LINQ，可轻松对数据线进行筛选、排序、投射等。使用LINQ to Object时，只有一个线程顺序处理数据集合中的所有项；我们称为顺序查询

LINQ为顺序查询，只有一个线程顺序处理数据集合中的所有项。
PLINQ将顺序查询转换成并行查询，在内部使用任务，将集合中的数据项处理工作分散到多个
CPU上，以便并发处理多个数据项。

静态``System.Linq.ParallelEnumerable``类(在System.Core.dll中定义)实现了PLINQ的所有功能。

通过 AsParallel() 扩展方法可以将 IEnumerable<TSource> 转换为ParallelQuery<TSource>。
从并行操作切换回顺序操作，只需要调用 ParallelEnumable 的 AsSequential() 方法即可。

静态``System.Linq.ParallelEnumerable``类公开了所有标准LINQ操作符（Where，Select，SelectMany，GroupBy，Join，Skip，
Take等）的并行版本，所有这些方法都是扩展了System.Linq.ParallelQuery<T>类型的扩展方法。


通常，一个LINQ查询的结果数是让某个线程执行一个foreach语句计算获得的。这意味着只有一个线程遍历查询的所有结果。如果希望以并行的方式处理查询的结果，就应该使用ParallelEnumerable的ForAll方法处理查询：

```
static void ForAll<TSource>(this ParallelQuery<TSource> source,Action<TSource> action)
```

```
//显示结果
query.ForAll(Console.WriteLine);
```
让多个线程同时调用Console.WriteLine反而会损害性能，因为Console类内部会对线程进行同步，确保每次只有一个线程能访问控制台程序窗口，避免来自多个线程的文本最后显示成一团乱麻。希望为每个结果都执行计算时，才使用ForAll方法。

PLINQ 一般会自己分析使用最好的查询方式进行查询，有时候使用顺序查询性能更好。
PLINQ提供了一些额外的ParallelEnumerable方法，可调用它们来控制查询的处理方式。
```
// 设置要与查询关联的 CancellationToken
public static ParallelQuery<TSource> WithCancellation<TSource>(this ParallelQuery<TSource> source, CancellationToken cancellationToken);
// 设置要在查询中使用的并行度。 并行度是将用于处理查询的同时执行的任务的最大数目。
public static ParallelQuery<TSource> WithDegreeOfParallelism<TSource>(this ParallelQuery<TSource> source, int degreeOfParallelism);
// 设置查询的执行模式。
public static ParallelQuery<TSource> WithExecutionMode<TSource>(this ParallelQuery<TSource> source, ParallelExecutionMode executionMode);
//设置此查询的合并选项，它指定查询对输出进行缓冲处理的方式。
public static ParallelQuery<TSource> WithMergeOptions<TSource>(this ParallelQuery<TSource> source, ParallelMergeOptions mergeOptions);
```

### 执行定时计算限制操作
FCL提供了几个计时器：

* System.Threading.Timer
这是将要讨论的计时器。要在一个线程池线程上执行定时的(周期性发生的)后台任务，它是最好的计时器。
* System.Windows.Forms.Timer
 构造这个类的一个实例，相当于告诉Windows将一个计时器和调用线程关联。当这个计时器触发
时，WIndows将一条计时器消息(WM_TIMER)注入线程的消息队列。线程必须执行一个消息泵来提取
这些消息，并把它们派遣给想要的回调方法。注意，所有这些工作都只有一个线程完成——设置计时器
的线程保证就是执行回调方法的线程。这还意味着你的计时器方法不会由多个线程并发执行。
* System.Windows.Threading.DispatcherTimer
这个类是 System.Windows.Forms的Timer类在Siverlight和WPF应用程序中的等价物。
* System.Timers.Timer
这个计时器基本是System.Threading的Timer类的一个包装类。当计时器到触发时，会导致CLR将
事件放到线程池的队列中。尽量不要使用这个类而是使用System.Threading的Timer类。
#### System.Threading.Timer

System.Threading命名空间定义了一个Timer类，可用它让一个线程池线程定时调用一个方法。构造Timer类的一个实例相当于告诉线程池：在将来的某个时间会滴哦啊你的一个方法。Timer类提供了几个构造函数，相互都非常相似：
```
public sealed class Timer : MarshalByRefObject, IDisposable
{
    public Timer(TimerCallback callback, object state, int dueTime, int period);
    public Timer(TimerCallback callback, object state, long dueTime, long period);
    public Timer(TimerCallback callback, object state, TimeSpan dueTime, TimeSpan period);
    public Timer(TimerCallback callback, object state, uint dueTime, uint period);
}
```
参数说明：
* callback
委托 ``delegate void TimerCallback(Object state)``;
传入回调方法。
* state
调用回调方法时，传递状态数据
* duiTime
毫秒数，如果指定为0，立即调用回调方法
* period
再次调用回调方法时，简隔多少毫秒
如果设置为Timeou.Infinite(-1)，回调方法只调用一次。



在内部，线程池为所有Timer对象只使用了一个线程，这个线程知道下一个Timer对象在什么时候到期，下一个Timer对象到期时，线程会唤醒，在内部调用ThreadPool的QueueUserWorkItem,将一
个工作项添加到线程池的队列中，使回调方法得到调用。

这里有可能会出现一种情况，如果回调方法执行的时间比计时器的时间更长，会造成多个线程池线程
同时执行这个回调方法。

解决这种情况，可以在开始设置period为Timeou.Infinite，再在回调方法中，调用Timer的
Change方法来指定一个新的dueTimer，且设置period为Timeou.Infinite。

Timer类还提供了Dispose方法，运行完全取消计时器，并可再当时处于pending状态的所有回调完成之后，向notifyObject参数标识的内核对象发送信号。以下是Dispose方法的各个重载版本：

```
public sealed class Timer : MarshalByRefObject, IDisposable
{
       public void Dispose();
       public bool Dispose(WaitHandle notifyObject);
}
```
注：一个Timer对象被垃圾回收时，它的终结代码告诉线程池取消计时器，使它不再触发。所以，使用一个Timer对象时，要确定有一个变量在保持Timer对象的存货，否则对你的回调方法调用就会停止。
```
class Program
{
    private static Timer s_timer;
    static void Main(string[] args)
    {
        s_timer = new Timer(ComputeBoundOp, null, 0, Timeout.Infinite);
        
        Console.ReadLine();
    }
    private static void ComputeBoundOp(Object state)
    {
        Console.WriteLine(DateTime.Now);
        s_timer.Change(2000, Timeout.Infinite);
    }

}
```

### 线程池如何管理线程


主要是管理工作者线程和I/O线程。

#### 如何管理工作者线程

下面是一张CLR线程池的示意图：

![](./images/CLRThreadPool.png)

ThreadPoolQueueUserWorkItem方法和Timer类总是将工作项（其实就是委托）放到全局队列中。

工作者线程采用一个先入先出（FIFO）算法，将工作项从这个队列中取出。

在对全局队列获取工作项时，所有工作者线程都竞争一个线程同步琐。

下面讨论的是使用默认的TaskScheduler来调度的Task对象。

非工作者线程调度一个Task时，该Task被添加到全局队列。
工作者线程调度一个Task时，该Task被添加到调用线程的本地队列。

每个工作者线程都有自己的本地队列。

工作者线程准备好处理工作项时，先检查本地队列（工作者线程独占自己的本地队列，不需要同步
琐），如果有工作项，采用FILO算法（先进后出）获取工作项，如果队列为空，再从别的工作者线
程中的本地队列取（需要线程同步琐），如果所有的本地队列都为空，那么会采用FIFO算法，从全局队列提取一个工作项（同样要同步琐）。

如果全局队列也为空，工作者线程会进入睡眠状态，等待事情的发生。

如果睡眠太长时间，会自己醒来，销毁自己，允许系统回收线程使用的资源（内核对象、栈、TEB等）

线程池会预先缓存一些工作者线程因为创建线程的代价比较昂贵。
线程池会根据队列中工作项的完成速度。动态创建和销毁工作者线程，以达到较高的资源利用率。

注：线程池从来不保证排除中的工作项的处理顺序。

