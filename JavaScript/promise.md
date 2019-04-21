## Promise


想象一下，你自己是一位顶尖歌手，粉丝没日没夜地询问你下个单曲何时到来。

为了从中解放，你承诺会在单曲发布的第一时间通知他们。你让粉丝们填写了他们的个人信息，因此他们会在歌曲发布的第一时间获取到。即使遇到了不测，歌曲可能永远不会被发行，他们也会被通知到。

每个人都很开心：你不会被任何人催促；粉丝也不会错过单曲发行的第一时间。

在编程中，我们经常用现实世界中的事物进行类比：

* “生产者代码” 会做一些事情，也需要事件。比如，它加载一个远程脚本。此时它就像“歌手”。
* “消费者代码” 想要在它准备好时知道结果。许多函数都需要结果。此时它们就像是“粉丝”。
* promise 是将两者连接的一个特殊的 JavaScript对象。就像是“列表”。生产者代码创建它，然后将它交给每个订阅的对象，因此它们都可以订阅到结果。

Promise 对象用于表示一个异步操作的最终状态（完成或失败），以及该异步操作的结果值。

这种类比并不精确，因为 JavaScipt promises比简单的列表更加复杂：它们拥有额外的特性和限制。

但是它们仍然有相似之处。

Promise 对象的构造语法是：

```
let promise = new Promise(function(resolve, reject) {
  // executor (生产者代码，"singer")
});
```

传递给 new Promise的函数称之为 executor。
当 promise 被创建时，它会被自动调用。
它包含生产者代码，这最终会产生一个结果。
与上文类比，executor 就是“歌手”。

promise 对象有内部属性：

* state —— 最初是 “pending”，然后被改为 “fulfilled” 或 “rejected”，
* result —— 一个任意值，最初是 undefined。


当 executor 完成任务时，应调用下列之一：

* resolve(value) —— 说明任务已经完成：
    * 将 state 设置为 "fulfilled"，
    * sets result to value。
* reject(error) —— 表明有错误发生：
    * 将 state 设置为 "rejected"，
    * 将 result 设置为 error。
    
![](https://zh.javascript.info/article/promise-basics/promise-resolve-reject.png)

接下来，我们会看到这些变化如何通知到fans。

这个例子中有一个简单的Promise 构造，以及一个包含生成者代码的简单 executor函数:

```
let promise = new Promise(function(resolve, reject) {//executor函数
  // 当 promise 被构造时，函数会自动执行

  alert(resolve); // function () { [native code] }
  alert(reject);  // function () { [native code] }

  // 在 1 秒后，结果为“完成！”，表明任务被完成
  setTimeout(() => resolve("done!"), 1000);//生产者代码
});
```
通过运行上述代码，我们会看到两件事：

* executor立即会被自动调用 （通过 new Promise）。
* executor 接受两个参数 resolve 和 reject —— 这些函数来自于 JavaScipt 引擎。我们不需要创建它们，相反，executor 会在它们准备好时进行调用。

经过一秒钟后，executor 调用 resolve("done") 来产生结果：

![](https://javascript.info/article/promise-basics/promise-resolve-1.png)

这是“任务成功完成”的示例。

现在的是示例则是 promise 的 reject 出现于错误的发生：

```
let promise = new Promise(function(resolve, reject) {
  // after 1 second signal that the job is finished with an error
  setTimeout(() => reject(new Error("Whoops!")), 1000);
});
```
总之，executor 应该完成一个工作任务（通常某些需要时间的事情），然后调用 resolve 或 reject 来改变 promise 对象的对应状态。

Promise被称为 “settled”时， 要么被 resolved过，要么被 rejected过 ，而不是 “pending” 状态的 promise。

### There can be only a single result or an error
executor 仅调用 一个resolve 或 一个reject。Promise 的最后状态一定会变化。

对 resolve 和 reject 的更远的调用都会被忽略：

```
let promise = new Promise(function(resolve, reject) {
  resolve("done");

  reject(new Error("…")); // 被忽略
  setTimeout(() => resolve("…")); // 被忽略
});
```

其思想是，由执行器执行的工作可能只有一个结果或错误。

同时，resolve/reject最多只能接受一个参数（或者没有参数）。多余的参数会被忽略。


### Reject with Error objects

从技术上来说，我们可以使用任何类型的参数来调用 reject（就像 resolve）。但建议在 reject（或从它们中继承）中使用 Error 对象。 错误原因就会显示出来。

### The state and result are internal
Promise 的 state 和 result 属性是内部的。我们不能从代码中直接访问它们，但是我们可以使用 .then/catch 来访问，下面是对此的描述。

## 消费者：then 和 catch,finally

Promise 对象充当生产者（executor）和消费函数之间的连接 —— 那些希望接收结果/错误的函数。消费者函数可以使用方法 promise.then 、 promise.catch和promise.finally来注册 。

### then
.then 的语法：
```
promise.then(
  function(result) { /* handle a successful result */ },
  function(error) { /* handle an error */ }
);
```
第一个函数参数在 promise 为 resolved 时被解析，然后得到结果并运行。
第二个参数 —— 在状态为 rejected 并得到错误时使用。

例如：
```
let promise = new Promise(function(resolve, reject) {
  setTimeout(() => resolve("done!"), 1000);
});

// resolve 在 .then 中运行第一个函数
promise.then(
  result => alert(result), // 在 1 秒后显示“已经完成！”
  error => alert(error) // 不会运行
);
```

在 rejection 的情况下：
```
let promise = new Promise(function(resolve, reject) {
  setTimeout(() => reject(new Error("Whoops!")), 1000);
});

// reject 在 .then 中运行第二个函数
promise.then(
  result => alert(result), // 无法运行
  error => alert(error) // 在 1 秒后显示 "Error: Whoops!"
);
```

如果我们只对成功完成的情况感兴趣，那么我们只为 .then 提供一个参数：
```
let promise = new Promise(resolve => {
  setTimeout(() => resolve("done!"), 1000);
});

promise.then(alert); // 在 1 秒后显示 "done!"
```

### catch

如果我们只对错误感兴趣，那么我们可以对它使用 .then(null, function) 或 “alias”：.catch(function)

```
let promise = new Promise((resolve, reject) => {
  setTimeout(() => reject(new Error("Whoops!")), 1000);
});

// .catch(f) 等同于 promise.then(null, f)
promise.catch(alert); // 在 1 秒后显示 "Error: Whoops!"
```

调用```.catch(f)```是```.then(null, f)```的模拟，这只是一个简写。

如果 promise 为 pending 状态，.then/catch 处理器必须要等待结果。相反，如果 promise 已经被处理，它们就会立即执行。

### finally

就像异常处理中的finally一样。

调用.finally方法类似与.then(f,f)意思是一样的。

注>两个f是同一个函数。

finally对于清理处理是个很好的解决方案。

比如：
```
new Promise((resolve, reject) => {
  /* do something that takes time, and then call resolve/reject */
})
  // runs when the promise is settled, doesn't matter successfully or not
  .finally(() => stop loading indicator)
  .then(result => show result, err => show error)
```

1. finally的处理函数，没有参数，我们不知道promise的成功还是失败。
因为我们的任务通常是完成一般的结束过程。

2. finally会将promise的结果与错误传递给下一个处理者。

比如：here the result is passed through finally to then
```
new Promise((resolve, reject) => {
  setTimeout(() => resolve("result"), 2000)
})
  .finally(() => alert("Promise ready"))
  .then(result => alert(result)); // <-- .then handles the result
```

And here there’s an error in the promise, passed through finally to catch:
```
 new Promise((resolve, reject) => {
  throw new Error("error");
})
  .finally(() => alert("Promise ready"))
  .catch(err => alert(err));  // <-- .catch handles the error object
```

这是很方便的，因为finally不是用来处理promise的结果的。所以它传递promise的结果给
下一个处理者。

3. 最后，相对.then(f,f)来说finally更方便，因为最少不需要重复写两次函数


##例子：loadScript

现在让我们看一下promise如何帮助我们来完成编写异步代码。

这是上一节回调所演示的：
```
function loadScript(src, callback) {
  let script = document.createElement('script');
  script.src = src;

  script.onload = () => callback(null, script);
  script.onerror = () => callback(new Error(`Script load error ` + src));

  document.head.append(script);
}
```

###我们可以用promise来重写
```
function loadScript(src) {
  return new Promise(function(resolve, reject) {
    let script = document.createElement('script');
    script.src = src;

    script.onload = () => resolve(script);
    script.onerror = () => reject(new Error("Script load error: " + src));

    document.head.append(script);
  });
}
````
使用：
```
let promise = loadScript("https://cdnjs.cloudflare.com/ajax/libs/lodash.js/4.17.11/lodash.js");
promise.then(
  script => alert(`${script.src} is loaded!`),
  error => alert(`Error: ${error.message}`)
);
promise.then(script => alert('One more handler to do something else!'));
```

Promises与回调相比的优势：


Promises|	Callbacks
---|---
Promises允许我们按照自然的顺序处理事情，首先，我们运行loadScript(script)，然后根据这个结果来使用.then决定我们要做什么|	在loadScript调用之彰我们必须知道在要做什么.
我们可以在promise对象上多次调用.then，每次调用，都相当于添加了一个消费者对象|只能有一个回调

所以promise给了我们更好的灵活性和代码流。

