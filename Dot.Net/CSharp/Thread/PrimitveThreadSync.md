## 基元线程同步构造

一个线程池线程阻塞时，线程池会创建额外的线程，使其饱和，而线程的创建、调度和销毁所需要的时间和内存资源都是相当昂贵的。

**为了构造可伸缩的、响应灵敏的应用程序，关键在于不要阻塞你拥有的线程，使它们能用于和重用于执行任务。**
### 线程安全

指多个线程访问同一数据时，不会发生数据被破坏的情况。（与单线程访问结果一样）

### 线程同步

线程同步是指多个线程在访问同一数据时，保持同步顺序（即线程排队一个一个的访问这些数据）。

假如有一个数据，两个线程都可以访问，但不可能同时接触到数据，两个线程就不需要同步。

线程同步存在的问题：
* ，写法繁琐，容易出错
多线程执行顺序无规则，在代码中，要标识出所有可能由多个线程同时访问的数据，并使用额外的代码将这些代码包围起来，并获取和释放一个线程同步锁

* 性能
获取和释放锁都需要额外的时间，即是是最快的锁，也会造成数倍慢于没有任何锁的版本。

* 一次只允许一个线程访问资源
这是锁的全部意义之所在，但是，阻塞一个线程会造成更多的线程被创建，使CPU保持"饱和"。

#### 不需要线程同步的情况

线程同步是一件不好的事情，应尽可能避免进行线程同步。

* 所有线程只查询数据
* 值类型，总是被复制，每个线程操作的都是它自己的副本。



### FCL的线程安全

在FCL中，所有的静态方法都是线程安全的。
这意味着假如两个线程同时调用一个FCL中的静态方法，不会发生数据被破坏的情况。

比如Console类，它包含了一个静态字段，类的许多方法都要获取和释放这个字段上的锁，
确保一次只有一个线程访问控制台。

在FCL中，不保证实例方法是线程安全的，因为假如全部添加锁定，会造成性能巨大的损失。


注：建议遵循FCL的这个模式，使自己的所有静态方法都线程安全。


### 基元用户模式和内核模式线程同步构造

基元(**primitive**)是指可以在代码中使用的最简单的构造。

有两种基元构造：
* 用户模式(user-mode)
优点：效率高，使用特殊的CPU指令来调节线程，并且在用户模式的基元构造上阻塞的线程池永远不会认为其阻塞，所以线程池不会创建新的线程来替换临时的阻塞，同时，这些CPU指令只阻塞相当短的一段时间。
缺点：只有Windows操作系统内核（内核是操作系统最基本的部分）才能停止一个线程的运行，然而在用户模式中运行的线程可能会被抢占，所以想要取得资源但是取不到资源的线程，就可能会一直“自旋”，这可能会浪费大量的CPU时间。

* 内核模式(kernel-mode)
优点：当线程通过内核模式获取其他线程的资源时，如果资源不可用，内核会阻止这个线程使其阻塞（这样就不会浪费CPU时间），当资源可用时候，Windows内核会恢复这个线程，允许它访问资源。
缺点：因为内核模式构造是由Windows操作系统自己提供的函数来实现的，所以，当应用程序调用Windows自身的函数时，线程会从用户模式切换为内核模式（或相反）会导致巨大的性能损失，这是正是为什么要避免使用内核模式的原因。

* 活锁与死锁
对于在一个构造上等待的线程，如果当前拥有这个构造的线程一直不释放它，前者可能一直阻塞。
如果是用户模式的构造，线程将一直在一个CPU上运行，称为"活锁"，浪费CPU时间，又浪费内存（线程栈等）。
如果是内核模式的构造，线程将一直阻塞，称为死锁，不会浪费CPU时间，只浪费内存。

CLR的线程同步构造实际只是Win32线程同步构造的一些面向对象的类包装器。


### 用户模式线程的同步构造

##### 原子性

一个工作项，要么全部完成，要么全部没有完成。

#### CLR中读写原子性
CLR保证对于以下数据类型的变量的读写是原子性的：
Boolean、Char、(S)Byte,(U)Int16、UInt32、(U)IntPtr,Single以及引用类型的。
这意味着变量中的所有字节都一次性读取或写入。(不可能看到处于写入或读入部分的状态）。

虽然对变量的原子访问可保证读取或定稿操作一次性完成，但是由于编译器和CPU的优化，不保证操作什么时候发生。

#### 控制简单数据类型的原子读取/写入的操作时间 

使用用户模式构造来规划好这些原子性读取/写入的操作时间。
* 易变构造(volatile construct)
在特定的时间，它在包含一个简单数据类型的变量上执行执行原子性的读**或**写操作	

* 互锁构造(interlocked construct)
在特定的时间，它在包含一个简单数据类型的变量上执行执行原子性的读**和**写操作	

注：所有易变和互锁构造都要求传递对包含简单数据类型的一个变量的引用（引用传递：内存地址号）


#### 易变构造 

C#要想让CPU执行指令，需要使用将C#编译成IL，然后JIT将IL转换成本机CPU指令，最后CPU执行这些指令。
这里的每一步都有可能优化你的代码。从而导致一些原子性的写入/读取的操作时间点变的不像代码中编写的那样。

比如以下代码：
```
internal static class StrangeBehaviour {
    private static Boolean s_stopWorker = false;

    public static void Main(String[] args) {
        Console.WriteLine("Main: letting worker run for 5 seconds");
        Thread t = new Thread(Worker);
        t.Start();
        Thread.Sleep(5000);
        s_stopWorker = true;
        Console.WriteLine("Main:waiting for worker to stop");
        t.Join();
        Console.ReadLine();
    }

    private static void Worker(Object o) {
        Int32 x = 0;
        while (!s_stopWorker) x++;
        Console.WriteLine("Worker: stopped when x={0}",x);
    }
}
```
如果以调试方式的运行（或是不加上优化运行），那么程序将会按照期望的那样结束。如果加上优化的话，那么结局将会不同，打开C#编译器的
 platform:x86 和 /optimize+ 开关来编译，然后运行，会发现程序一直运行，并不会停止。
这里的s_stopWorker
接下来解释其过程，当Worker方法开始工作时，s_stopWorker是false，程序进入循环，在5秒钟后，主线程改变s_stopWorker为true,
所以Worker方法中的线程应该立即停止。然而，因而加了优化，所以每次当while(!s_stopWorker)执行时，并不会去检查s_stopWorker的真
实值，只检查一次（也就是在循环开始的时候），之后其会记住s_stopWorker的值，本例中为false，所以程序会一直执行。


输出：
```
Main: letting worker run for 5 seconds
Main:waiting for worker to stop
//这里一直在循环
```

解决办法之一：
``while(!s_stopWorker)``改成``while(!System.Threading.Volatile.Read(ref s_stopWorker))``


##### System.Threading.Volatile中方法工作原理

静态System.Threading.Volatile类提供了很多静态方法（事实上有更多的重载版本,上面说到的类型都可以使用），


这些方法比较特殊。它们事实上会禁止C#编译器、JIT编译器和CPU平常执行的一些优化。
```
public static class Volatile{
 public static void Write(ref Int32 location,Int32 value);
 public static Int32 Read(ref Int32 location);
 public static void Write(ref Boolean location,Boolean value)
 public static Int32 Read(ref Boolean location)
 ....
}
```

* Volatile.Write
该方法强迫location中的值在调用时写入。
此外，按照编译顺序，之前的加载和存储操作必须在调用Volatile.Write之前发生。
* Volatile.Read
该方法强迫location中的值在调用时读取。
此外，按照编译顺序，之后的加载和存储操作必须在调用Volatile.Read之后发生。

**使用提示：当线程通过共享的内存相互通信时，调用Volatile.Wrilte来写入最后一个值，调用Volatile.Read来读取第一个值。**

再来看下面这个例子
```
class Program
{
    private int you = 0;
    private int me = 0;
    public  void Thread1()
    {
        me = 2;
        you = 2;
    }
    public void Thread2()
    {
        if (you == 2)
        {
            Console.WriteLine(me);

        }
    }
    public static void Main(String[] args)
    {
        Program p = new Program();
        (new Thread(p.Thread1)).Start();
        (new Thread(p.Thread2)).Start();
        Console.ReadLine();

    }

}
```

上述代码的问题在于，由于编译器和CPU在解释代码的时候，可能反转Thread1方法中的两行代码，毕竟，反转两行代码不会改变方法的意图。
假如you=2和me=2的顺序反过来，那么当先写了you=2后，me=2这句代码还没执行，此时Thread2已经开始检测到you==2了，那么此时打印的话，会显示我不是2，是0.

或者Thread1中的顺序没有变，而Thread2中的顺序变了，即you读取到数据和me读取到数据的代码也是可以被优化的，编译器在Thread1未运行时，先读了me的值为0，而此时Thread1运行了，虽然给了me为2，但是线程2的寄存器中已经存为0了，所以未读取，那么此时结果依然是你是2，而我不是2；

要解决这个问题就引入了我们的易变构造，可以改成

```
public  void Thread1()
{
    me = 2;//me=2肯定会在you=2之前执行，
    Volatile.Write(ref you, 2);
}
public void Thread2()
{
    if (Volatile.Read(ref you) == 2)
    {
        Console.WriteLine(me);//读取me肯定在读取you之后

    }
}
```
##### 易变字段
为了简化编程，C#编译器提供了volatile关键字。
volatile，不允许进行优化，且告诉编译器不将字段缓存到CPI的寄存器中，确保字段的所有读写操作都在RAM（如果是在循环中，会极大影响性能）中进行。
注：volatile字段，不支持引用传递给方法(ref)
对上述代码进行修改：``private volatile int you = 0;``

要求对字段的所有访问都是易变的，这种情况极为少见。


#### 互锁构造

System.Threading.Interlocked类提供的方法都执行一次原子读取以及写入操作，且这些方法都建立了完整的内存栅栏（memory fence）。
这意味着，调用某个Interlocked方法之前的任何变量写入都在这个Interlocked方法调用之前执行，而这个调用之后的任何变量读取都在
这个方法调用之后读取。

推荐使用Interlocked的方法，它们不仅快，而且也能做不少事情，比简单的加（Add），自增（Increment），自减（Decrement），互换（Exchange）。

最常用的用法就是对Int32的操作。

````
public static class Interlocked
{
	
    //return (++location)
    public static int Increment(ref int location);

	//return (--location)
	 public static int Decrement(ref int location);

	//return (location1+=value)
 	public static int Add(ref int location1, int value);

	//int old=location1;location1=value;return old;
    public static int Exchange(ref int location1, int value);

	//int old=location1;
	//if (location1==comparand) location1=value;
	//return old;
    public static int CompareExchange(ref int location1, int value, int comparand);   
	//还有很多支持其它的类型的方法

}
````


##### 简单的自旋锁
Interlocked的方法虽然好用,可以用于上述的类型及引用类型，但主要用于操作Int类型。

如果想要原子性地操作类对象中的一组字段，需要采用一个办法阻止所有线程，只允许其中一个进入对字段进行操作的代码区域。
可以使用Interlocked的方法构造一个线程同步块,

自旋锁：在存在对锁的竞争的前提下，线程自旋。

使用Interlocked更改前的自旋锁结构（不能做到线程同步）:

```

struct SimpleSpinLock
{
    private int m_InUse;//默认值为0（锁释放状态），1（锁被占用状态）
    public void Enter()
    {
        while (true)//
        {
            if (m_InUse == 0)//处于释放状态
            {
                m_InUse = 1;//设置为1(占用状态)
                break;
            }
           //如果锁没有释放，一直重复循环
        }
        
    }

    public void Exit()
    {
        m_InUse = 0;//设置为释放状态
    }
}

class Program
{

    private Object obj = new object();
    private SimpleSpinLock sp = new SimpleSpinLock();

    public static void Main(String[] args)
    {
        Program p = new Program();
        var ThreadA = new Thread(p.ChangeSomeData);
        var ThreadB = new Thread(p.ChangeSomeData);
        ThreadA.Start();
        ThreadB.Start();
        ...             

    }
    public void ChangeSomeData()
    {
        sp.Enter();
        //代码块T:需要线程同步
        sp.Exit();

    }

}
```
上面的代码有可能出现，A线程进入``if (m_InUse == 0)``以后，但没有完成设置``m_InUse = 1``，这时B线程也进入了``if (m_InUse == 0)``，导致，两个线程都跳出了自旋，就会同时访问需要线程同步方法的代码块T

这时，可以使用Interlocked来设置m_InUse的``读写原子性操作同时完成``

将
```
if (m_InUse == 0)
{
    m_InUse = 1;
    break;
}
改成
if(Interlocked.Exchange(ref m_InUse, 1) == 0)
{
    break;
}
```

同时为了防止``Exit()``方法内的语句被优化，使用:``Volatile.Write(ref m_InUse, 0)``来代替。

自旋锁的最大的问题在于，在存在对锁的竞争的前提下（一个线程已经获得了该锁的占用权），会造成线程“自旋”。
这个自旋会浪费CPU时间（因为它一直在执行代码）。所以这种锁只适用于保护那些执行的非常快的代码区域。

如果能更改当前自旋线程优先级高于占用线程的优先级的问题，这样低优先级的线程可能根本得不到执行，就会产生活锁。




##### Interlocked Anything模式

Interlocked类没有提供Multiple、Divide、Minimum、Maximum、And、Or、Xor等方法，
但一个已知的模式允许使用Interlocked.CompareExchange方法以原子方式在一个Int32上执行任何操作。
CompareExchange方法 有很多的重载版本，所以能操作Int64、Single、Double、Object、泛型引用类型，所以该模式适合所有这些类型。

该模式类似在修改数据库记录时使用的乐观并发模式。

下例使用该模式创建一个原子Maximum方法:

```
public static Int32 Maximum(ref Int32 target,Int32 value)
{
    Int32 currentVal = target;//确保循环中，使用的是target传入方法时的值
    Int32 startVal=0, desiredVal=0;
    //不要在循环中访问目标(target)，除非是想要改变它时，另一个线程也在动它。
    do
    {
        startVal = currentVal;//在循环中使用target的最新值(此处不一定是最新值，因为别的线程，可能更改了target)。

        desiredVal = Math.Max(startVal, value);//返回target和value的最大值

        if (target == startVal) target = desiredVal;//这里有可能被其他线程抢占，别的线程已经更改了target，导致startVal与target值不相等

        //1.获取target最新值，与startVal进行比较，
        //2.1 如果相等（证明target没有被其它线程修改），修改target的值为desiredVal，并。
        //   如果不相等（证明target没有被其它线程修改），此处不修改target的值，因为修改以后，会覆盖其它线程的操作结果，
        //3 返回target的原值
        currentVal = Interlocked.CompareExchange(ref target, desiredVal, startVal);

    }
    while (startVal != currentVal);//starVal不等于currentVal，说明target的值在别处被修改，需要使用新值(target的新值currentVal)重新计算

    return desiredVal;
}
```

### 内核模式线程的同步构造

内核模式的构造比用户模式的构造慢得多

* 要求Windows操作系统自身的配合
* 内核对象上调用的每个方法都造成调用线程比托管代码转换成本机(native)用户模式代码，再转换成本机（native)内核模式代码。

内核模式具备用户模式构造所不具备的优点：

* 检测到一个资源上的竞争时，Windows会阻塞输掉的线程，使它不占用CPU自旋。
* 可实现本机和托管线程相互之间的同步
* 可同步在同一台机器中的不同进程中运行的线程
* 可应用安全性设置，防止未经授权的账户访问它们
* 线程可一直阻塞，直到集合中的所有内核模式的构造都可用，或直到集合中的任何内核模式构造可用
* 可指定线程阻塞超时时间，过了超时时间，线程可以解除阻塞并执行其他任务

两种最基本的基元内核模式线程同步构造

* 事件
* 信号量

其他内核模式构造，比如互斥体，都是在这两个基元构造上构建的。

#### System.Threading.WaitHandle抽象类

System.Threading命名空间提供了一个名为WaitHandle抽象基类，这个类唯一的作用就是包装
了一个Windows内核对象句柄。

FCL提供了几个从WaitHandle派生的类。
所有的类都在System.Threading命名空间中定义。类层次结构如下：

* **WaitHandle**
	* **EventWaitHandle**
	 * **AutoResetEvent**
	 * **ManualResetEvent**
	* **Seamphore**
	* **Mutex**

WaitHandle基类内部有一个SafeWaitHandle字段，它容纳了一个Win32内核对象句柄。
除此之外，WaitHandle类公开了所有派生类继承的方法。
主要方法，有众多
```
public static bool WaitAll(WaitHandle[] waitHandles, TimeSpan timeout);

public static bool WaitAll(WaitHandle[] waitHandles, int millisecondsTimeout, bool exitContext);

public static bool WaitAll(WaitHandle[] waitHandles, TimeSpan timeout, bool exitContext);

public static bool WaitAll(WaitHandle[] waitHandles);
public virtual bool WaitOne(int millisecondsTimeout, bool exitContext);


public virtual bool WaitOne(TimeSpan timeout);


public virtual bool WaitOne(int millisecondsTimeout);


public virtual bool WaitOne(TimeSpan timeout, bool exitContext);
public static int WaitAny(WaitHandle[] waitHandles);
		
public static int WaitAny(WaitHandle[] waitHandles, TimeSpan timeout);

public static int WaitAny(WaitHandle[] waitHandles, TimeSpan timeout, bool exitContext);
还有 SignalAndWait方法
```

在一个内核模式的构造上调用的每个方法都代表一个完整的内存栅栏。

**TODO:解释每个方法的作用。**

#### Event构造（事件构造）

event其实只是由内核维护的Boolean变量。
* true
锁释放状态
* false
锁占用状态
//表示一个线程同步事件。
```
public class EventWaitHandle : WaitHandle
{
    //     初始化 System.Threading.EventWaitHandle 类的新实例，并指定在此调用后创建的等待句柄最初是否处于终止状态，它是自动重置还是手动重置，系统同步事件的名称，一个
    //     Boolean 变量（其值在调用后表示是否创建了已命名的系统事件），以及应用于已命名的事件（如果创建了该事件）的访问控制安全性。
    public EventWaitHandle(bool initialState, EventResetMode mode, string name, out bool createdNew, EventWaitHandleSecurity eventSecurity);

    //     打开指定名称为同步事件（如果已经存在）。
    public static EventWaitHandle OpenExisting(string name);

    //     用安全访问权限打开指定名称为同步事件（如果已经存在）。
    public static EventWaitHandle OpenExisting(string name, EventWaitHandleRights rights);

    //     打开指定名称为同步事件（如果已经存在），并返回指示操作是否成功的值。
    public static bool TryOpenExisting(string name, out EventWaitHandle result);

    //     用安全访问权限打开指定名称为同步事件（如果已经存在），并返回指示操作是否成功的值。
    public static bool TryOpenExisting(string name, EventWaitHandleRights rights, out EventWaitHandle result);

    //     获取 System.Security.AccessControl.EventWaitHandleSecurity 对象，该对象表示由当前 System.Threading.EventWaitHandle
    //     对象表示的已命名系统事件的访问控制安全性。
    public EventWaitHandleSecurity GetAccessControl();

    //     将事件状态设置为非终止状态，导致线程阻止。
    public bool Reset();

    //     将事件状态设置为终止状态，允许一个或多个等待线程继续。
    public bool Set();

    //     设置已命名的系统事件的访问控制安全性。
    public void SetAccessControl(EventWaitHandleSecurity eventSecurity);
}
```

有两种事件，即自动重置事件和手动重置事件。
* 自动重置事件 AutoResetEvent
当一个自动重置事件为true时，它只唤醒一个阻塞的线程，因为在解除第一个线程的阻塞后，内核自动
将事件自动重置回false，造成其余线程继续阻塞。
* 手动重置事件 ManualResetEvent
当一个手动重置事件为true时，它解除正在等待它的所有线程的阻塞，因为内核不将事件自动重置回false，需要手动设置。

都继承自EventHandle

```
class SimpleWaitLock : IDisposable
{
    private readonly AutoResetEvent m_avaiable;
    public SimpleWaitLock()
    {
        m_avaiable = new AutoResetEvent(true);//设置信号初始为true
    }
    public void AutoEventEnter()
    {
        //在内核中阻塞当前线程,直到内核对象收到信号true时，释放一个线程以后，自动reset将信号为false
        m_avaiable.WaitOne();
    }
    public void AutoEventLeave()
    {
        //设置信号量为true，并向内核对象发送信号
        m_avaiable.Set();
    }
    public void Dispose() {
        m_avaiable.Dispose();
    }
}
```

性能比较：
```
class Program
{

  

    public static void Main(String[] args)
    {

        const Int32 iterations = 10000000;
        int x = 0;

        Stopwatch sw = new Stopwatch();
        sw.Restart();
        for(int i = 0; i < iterations; i++)
        {
            x++;
        }
        Console.WriteLine("递增 x :{0:N0}",sw.ElapsedMilliseconds);
        x = 0;

        sw.Restart();
        for (int i = 0; i < iterations; i++)
        {
            x++;
            M();
        }
        Console.WriteLine("递增 x in M :{0:N0}", sw.ElapsedMilliseconds);
        x = 0;
        SpinLock sl = new SpinLock();
        Boolean taken = false;
        sw.Restart();
        for (int i = 0; i < iterations; i++)
        {            
            taken = false;
            sl.Enter(ref taken);
            x++;
            sl.Exit();
        }
        Console.WriteLine("递增 x in SpinLock :{0:N0}", sw.ElapsedMilliseconds);
        x = 0;
        using (AutoResetEvent autoEvent=new AutoResetEvent(true))
        {
            sw.Restart();
            for (int i = 0; i < iterations; i++)
            {

                autoEvent.WaitOne();
                x++;
                autoEvent.Set();
            }
            Console.WriteLine("递增 x in AutoResetEvent :{0:N0}", sw.ElapsedMilliseconds);
        }

        Console.ReadLine();

    }
    [MethodImpl(MethodImplOptions.NoInlining)]
    public static void M()
    {
    }    
    
}
```
output:
```
递增 x :3
递增 x in M :25
递增 x in SpinLock :919
递增 x in AutoResetEvent :27,746
```




#### Semaphore构造（信号量构造）
信号量是由内核维护的Int32变量，
* 大于0
信号量大于0时线程不阻塞，或解除阻塞。在信号量上等待的纯种解除阻塞时，内核自动从信号量
的计数中减1
* 0
在信号量上等等的线程会阻塞

```
public sealed class Semaphore : WaitHandle
{   
	//initialCount:可以同时授予的信号量的初始请求数。maximumCount:可以同时授予的信号量的最大请求数
    public Semaphore(int initialCount, int maximumCount);

    //退出信号量并返回前一个计数。相当于Release(1)
    public int Release();

    //以指定的次数退出信号量并返回前一个计数。
    public int Release(int releaseCount);
}
```

多个线程在一个信号量等待时，释放信号量导致releaseCount个线程被解除阻塞。
注：如果在一个信号量上多次调用Release，会导致它的计数超过最大计数。这是会抛出异常。


使用信号量修改SimpleWaitLock

```
class SimpleWaitLock : IDisposable
{
    private readonly Semaphore m_avaiable;
    public SimpleWaitLock(int maxCount)
    {
        m_avaiable = new Semaphore(maxCount, maxCount);
        
    }
    public void tEnter()
    {              
        m_avaiable.WaitOne();
    }
    public void Leave()
    {               
        m_avaiable.Release(1);
    }
    public void Dispose()
    {
        m_avaiable.Close();
    }
}
```

#### Mutext构造（互斥体构造）

互斥体，代表一个互斥的锁，它的工作方式和AutoResetEvent，或者计数为1的Semaphore相似，三都都是
一次只释放一个正在等待的线程。

```
public sealed class Mutex : WaitHandle
{
	public Mutex();
	public void ReleaseMutex();
}
```
互斥体有一些额外的逻辑，这造成它比其他构造更复杂。

首先，Mutex对象会查询调用线程的Int32ID，记录是哪个线程获得了它。
一个线程在调用ReleaseMutex时，Mutex确保调用线程就是获取Mutex的那个线程。

其次，Mutex对象维护着对一个递归计数(recursion count)，指出拥有该Mutex的线程拥有了它多少次。
同一个线程再次在Mutex上等待，计数就会递增，这个线程允许继续运行。
线程调用ReleaseMutex将导致计数递减。只有计数变成0，另一个线程才能成为该Mutex的所有者。


这些功能都是有代价的。
Mutex对象需要更多的内存来容纳额外的线程ID和计数信息，性能也更差。
尽量避免使用Mutex，可以自己来实现这个功能。

递归锁的实现。
通常，当一个方法获取了一个锁，然后调用也需要锁的另一个方法，就需要一个递归锁，如果不支持这个功能(就会造成死锁)。

```
class SomeClass : IDisposable
    {
        private readonly Mutex m_lock = new Mutex();
        public void Method1()
        {
            m_lock.WaitOne();
            Method2();
            m_lock.ReleaseMutex();
        }
        public void Method2()
        {
            m_lock.WaitOne();
			...
            m_lock.ReleaseMutex();
        }
        public void Dispose()
        {
            m_lock.Dispose();
        }
    }
```

使用AutoResetEvent实现递归锁

```
class RecursiveAtuoResetEvent:IDisposable
{
    private AutoResetEvent m_lock = new AutoResetEvent(true);
    private Int32 threadId { get; set; }
    private Int32 count { get; set; }

    public void Enter()
    {

        var currentThreadId=Thread.CurrentThread.ManagedThreadId;
        if (currentThreadId == threadId)
        {
            //重复拥有锁
            count++;
            return;
          
        }
        //每一次等待锁
        m_lock.WaitOne();
        //第一次拥有锁的状态
        threadId = currentThreadId;
        count = 1;

    }
    public void Leave()
    {
        if (threadId != Thread.CurrentThread.ManagedThreadId)
        {
            throw new InvalidOperationException();//此处一定要抛出错误，不然有可能会发生数据破坏
        }
        if (--count == 0)
        {
            threadId = 0;
            m_lock.Set();
        }
    }
    public void Dispose()
    {
        throw new NotImplementedException();
    }
}
```
性能更好，因为只有第一次获取和最后把它放弃给其他线程时，线程才从托管代码转换为内核代码。


