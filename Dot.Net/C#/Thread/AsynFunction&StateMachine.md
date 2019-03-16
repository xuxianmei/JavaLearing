# 异步函数和状态机

* 同步函数
当一个函数是同步执行时，那么当该函数被调用时不会立即返回，直到该函数所要做的事情全都做完
了才返回。
当一个线程调用一个同步函数时（例如：该函数用于完成写文件任务），如果该函数没有立即完成规
定的操作，则该操作会导致该调用线程的挂起（将CPU的使用权交给系统，让系统分配给其他线程使
用），直到该同步函数规定的操作完成才返回，最终才能导致该调用线程被重新调度。


* 异步函数
如果一个异步函数被调用时，该函数会立即返回尽管该函数规定的操作任务还没有完成

当一个线程调用的是一个异步函数（例如：该函数用于完成写文件任务），该函数会立即返回尽管其
规定的任务还没有完成，这样调用线程就会执行异步函数的下一条语句，而不会被挂起。那么该异步函数所规定的工作是如何被完成的呢？当然是通过另外一个线程完成的了啊；那么新的线程是哪里来的？
可能是在异步函数中新创建的一个线程也可能是系统中已经准备好的线程。

## 异步函数转换成状态机

```
static async Task<String> GetStrAsync()
{
    await Task.Run(() =>
    {
        return "abc";
    });
   return "something";
}
```

使用编译器，编译上述代码，再反编译成源代码，就可以看到状态机的代码。

```
internal class Program
{
	//AsyncStateMachine特性指出这是一个异步方法，typeof(StateMachine)指出实现状态机的类型
    [AsyncStateMachine(typeof(StateMachine))]
    private static Task<string> GetStrAsync()
    {
        StateMachine m_state_machine=new StateMachine();//创建状态机实例
        m_state_machine.builder = AsyncTaskMethodBuilder<string>.Create();//创建builder,从这个存根方法返回Task<String>，同时，状态机通过builder来设置Task完成或异常。
        m_state_machine.m_state = -1;//初始化状态机位置
        m_state_machine.builder.Start<StateMachine>(ref m_state_machine);//开始执行状态机
        return m_state_machine.builder.Task;//返回状态机的Task
    }
  
	//Run中的使用的委托:begin
    [Serializable, CompilerGenerated]
    private sealed class Test 
    {
        public static readonly Test test = new Test();
        public static Func<string> test_fun;

        internal string Metthod1()
        {
            return "abc";
        }
    }
	//Run中的使用的委托:end

		
	//状态机本身:begin
    [CompilerGenerated]
    private struct StateMachine : IAsyncStateMachine
    {
        public int m_state;//当前状态机的位置
        public AsyncTaskMethodBuilder<string> builder;//状态机的builder
        private TaskAwaiter<string> m_awaiter;//等待一个Task的waiter，可以使用这个waiter来查询Task完成状态，及为Task.OnCompleted设置回调方法。

        private void MoveNext()
        {
            string str;
            int num = this.m_state;//获取状态机状态
            try
            {
                TaskAwaiter<string> awaiter;//声明一个TaskAwaiter
                if (num != 0)//代表状态机第一次使用,简单来说，就是第一次执行MoveNext()方法。第一次的调用者是调用线程（调用异步函数的线程）
                {
					//创建一个Task并运行，线程池使用一个新线程（区别于当前调用线程）执行Test.Metthod1()方法，并返回这个Task的TaskAwaiter。
                    awaiter = Task.Run<string>(Test.test_fun ?? (Test.test_fun = new Func<string>(Test.test.Metthod1))).GetAwaiter();
                    if (!awaiter.IsCompleted)//这里做判断是避免多次执行
                    {
                        this.m_state = num = 0;//设置状态机状态，表示下一次进入MoveNext()，不是第一次运行状态机的MoveNext()
                        this.m_awaiter = awaiter;//设置最新状态机的TaskAwaiter
                        this.builder.AwaitUnsafeOnCompleted<TaskAwaiter<string>, StateMachine>(ref awaiter, ref this);//通过此方法，设置状态机对象的MoveNext为awaiter的OnCompleted回调方法，也是Task的OnCompleted回调方法
                        return;//立即返回当前调用线程，不阻塞当前调用线程，调用线程可以继续执行，调用线程执行完后面的代码，可以返回到线程池中。
                    }
                }
                else//状态机在本例中，在这里只有0和-1两种状态，这里代表this.m_awaiter指定的Task完成时，调用了状态机的MoveNext()（此处调用者，非异步函数的调用者线程）
                {
                    awaiter = this.m_awaiter;//获取状态机的TaskAwaiter成员
					
					//清空状态机，在本例中，只有两次执行MoveNext()
                    this.m_awaiter = new TaskAwaiter<string>();
                    this.m_state = num = -1;
                }
				
                awaiter.GetResult();//获取Task的返回结果，在本例是，"abc"，
				//线程返回到await后面的代码继续执行（注：不是原来的异步函数调用线程）
                str = "something";//await后面的代码
            }
            catch (Exception exception)
            {
                this.m_state = -2;
                this.builder.SetException(exception);
                return;
            }
            this.m_state = -2;
            this.builder.SetResult(str);//设置异步函数的返回结果
        }
       
    }
	//状态机本身:end


}
```

## 异步函数扩展性


##FCL中的异步函数


