## 异常处理，"try..catch"

通常，一段代码会在出错的时候“死掉”（停止执行）并在控制台将异常打印出来。

但是有一种更为合理的语法结构 try..catch，它会在捕捉到异常的同时不会使得代码停止执行而是可以做一些更为合理的操作。

## try...catch语法

try..catch 结构由两部分组成：try 和 catch：
```
try {

  // 代码...

} catch (err) {

  // 异常处理

}
```

它按照以下步骤执行：

* 首先，执行 try {...} 里面的代码。
* 如果执行过程中没有异常，那么忽略 catch(err) 里面的代码，try 里面的代码执行完之后跳出该代码块。
* 如果执行过程中发生异常，控制流就到了 catch(err) 的开头。变量 err（可以取其他任何的名称）是一个包含了异常信息的对象。

 
![](https://zh.javascript.info/article/try-catch/try-catch-flow.png)

让我们来看更多的例子。

没有异常的例子：显示下面（1）和（2）中 alert 的内容：
```
try {

  alert('Start of try runs');  // (1) <--

  // ...这里没有异常

  alert('End of try runs');   // (2) <--

} catch(err) {

  alert('Catch is ignored, because there are no errors'); // (3)

}

alert("...Then the execution continues");
```
包含异常的例子：显示下面（1）和（3）中 alert 的内容： 
```
try {

  alert('Start of try runs');  // (1) <--

  lalala; // 异常，变量未定义！

  alert('End of try (never reached)');  // (2)

} catch(err) {

  alert(`Error has occured!`); // (3) <--

}

alert("...Then the execution continues");
```
### try..catch works synchronously

如果一个异常是发生在计划中将要执行的代码中，例如在 setTimeout 中，那么 try..catch 不能捕捉到：
```
try {
  setTimeout(function() {
    noSuchVariable; // 代码在这里停止执行
  }, 1000);
} catch (e) {
  alert( "won't work" );
}
因为 try..catch 包裹了计划要执行的 setTimeout 函数。但是函数本身要稍后才能执行，这时引擎已经离开了 try..catch 结构。

要捕捉到计划中将要执行的函数中的异常，那么 try..catch 必须在这个函数之中：

 setTimeout(function() {
  try {
    noSuchVariable; // try..catch 处理异常！
  } catch (e) {
    alert( "error is caught here!" );
  }
}, 1000);
```

## Error对象

当一个异常发生之后，JavaScript 生成一个包含异常细节的对象。这个对象会作为一个参数传递给 catch：

```
try {
  // ...
} catch(err) { // <-- “异常对象”，可以用其他参数名代替 err
  // ...
}
```

对于所有内置的异常，catch 代码块捕捉到的相应的异常的对象都有两个主要属性：

* name 异常名称，对于一个未定义的变量，名称是 "ReferenceError"
* message 异常详情的文字描述。
还有很多非标准的属性在绝大多数环境中可用。其中使用最广泛并且被广泛支持的是：

* stack 当前的调用栈：用于调试的,一个包含引发异常的嵌套调用序列的字符串。
例如：

```

try {
  lalala; // 异常，变量未定义！
} catch(err) {
  alert(err.name); // ReferenceError
  alert(err.message); // lalala 未定义
  alert(err.stack); // ReferenceError: lalala 在... 中未定义

  // 可以完整的显示一个异常
  // 可以转化成 "name: message" 形式的字符串
  alert(err); // ReferenceError: lalala 未定义
}
```

## 使用try...cath

正如我们所知，JavaScript 支持 JSON.parse(str) 方法来解析 JSON 编码的值。

通常，它被用来解析从网络，从服务器或是从其他来源收到的数据。

我们收到数据后，像下面这样调用 JSON.parse：

 


let json = '{"name":"John", "age": 30}'; // 来自服务器的数据

let user = JSON.parse(json); // 将文本表示转化成 JS 对象

// 现在 user 是一个解析自 json 字符串的有自己属性的对象
alert( user.name ); // John
alert( user.age );  // 30
你可以在 JSON 方法，toJSON 这章找到更多的关于 JSON 的详细信息。

如果 json 格式错误，JSON.parse 就会报错，代码就会停止执行。

得到报错之后我们就应该满意了吗？当然不！

如果这样做，当拿到的数据出错，用户就不会知道（除非他们打开开发者控制台）。代码执行失败却没有提示信息会导致糟糕的用户体验。

让我们来用 try..catch 来处理这个错误：

```
let json = "{ bad json }";

try {

  let user = JSON.parse(json); // <-- 当这里抛出异常...
  alert( user.name ); // 不工作

} catch (e) {
  // ...跳到这里继续执行
  alert( "Our apologies, the data has errors, we'll try to request it one more time." );
  alert( e.name );
  alert( e.message );
}
```
我们用 catch 代码块来展示信息，但是我们可以做的更多：发送一个新的网络请求，给用户提供另外的选择，把异常信息发送给记录日志的工具，… 。所有这些都比让代码直接停止执行好的多。

## 自定义的异常
如果这个 json 数据语法正确，但是少了我们需要的 name 属性呢？

像这样：

```
let json = '{ "age": 30 }'; // 不完整的数据

try {

  let user = JSON.parse(json); // <-- 不抛出异常
  alert( user.name ); // 没有 name!

} catch (e) {
  alert( "doesn't execute" );
}
```

这里 JSON.parse 正常执行，但是缺少 name 属性对我们来说确实是个异常。

为了统一的异常处理，我们会使用 throw 运算符。

“Throw” 运算符
throw 运算符生成异常对象。

语法如下：

```
throw <error object>
```

技术上讲，我们可以使用任何东西来作为一个异常对象。甚至可以是基础类型，比如数字或者字符串。但是更好的方式是用对象，尤其是有 name 和 message 属性的对象（某种程度上和内置的异常有可比性）。

JavaScript 有很多标准异常的内置的构造器：Error、 SyntaxError、ReferenceError、TypeError 和其他的。我们也可以用他们来创建异常对象。

他们的语法是：
```
let error = new Error(message);
// 或者
let error = new SyntaxError(message);
let error = new ReferenceError(message);
// ...
```
对于内置的异常对象（不是对于其他的对象，而是对于异常对象），name 属性刚好是构造器的名字。message 则来自于参数。

例如：
```
 let error = new Error("Things happen o_O");

alert(error.name); // Error
alert(error.message); // Things happen o_O
```
让我们来看看 JSON.parse 会生成什么样的错误：
```

try {
  JSON.parse("{ bad json o_O }");
} catch(e) {
  alert(e.name); // SyntaxError
  alert(e.message); // Unexpected token o in JSON at position 0
}
```
如我们所见， 那是一个 SyntaxError。

假定用户必须有一个 name 属性，在我们看来，该属性的缺失也可以看作语法问题。

所以，让我们抛出这个异常。
```

let json = '{ "age": 30 }'; // 不完整的数据

try {

  let user = JSON.parse(json); // <-- 没有异常

  if (!user.name) {
    throw new SyntaxError("Incomplete data: no name"); // (*)
  }

  alert( user.name );

} catch(e) {
  alert( "JSON Error: " + e.message ); // JSON Error: Incomplete data: no name
}
```

在 ``(*) ``标记的这一行，throw 操作符生成了包含着我们所给的 message 的 SyntaxError，就如同 JavaScript 自己生成的一样。try 里面的代码执行停止，控制权转交到 catch 代码块。

现在 catch 代码块成为了处理包括 JSON.parse 在内和其他所有异常的地方。

try…catch…finally
然而，这并不是全部。

try..catch 还有另外的语法：finally。

如果它有被使用，那么，所有条件下都会执行：

try 之后，如果没有异常。
catch 之后，如果没有异常。
该扩展语法如下所示：

```
try {
   ... 尝试执行的代码 ...
} catch(e) {
   ... 异常处理 ...
} finally {
   ... 最终会执行的代码 ...
}
```
试试运行这段代码：
```
 try {
  alert( 'try' );
  if (confirm('Make an error?')) BAD_CODE();
} catch (e) {
  alert( 'catch' );
} finally {
  alert( 'finally' );
}
这
```

段代码有两种执行方式：
如果对于 “Make an error?” 你的回答是 “Yes”，那么执行 try -> catch -> finally。
如果你的回答是 “No”，那么执行 try -> finally。
finally 的语法通常用在：我们在 try..catch 之前开始一个操作，不管在该代码块中执行的结果怎样，我们都想结束的时候执行某个操作。


## 全局 catch

设想一下，try..catch 之外出现了一个严重的异常，代码停止执行，可能是因为编程异常或者其他更严重的异常。

那么，有没办法来应对这种情况呢？我们希望记录这个异常，给用户一些提示信息（通常，用户是看不到提示信息的），或者做一些其他操作。

虽然没有这方面的规范，但是代码的执行环境一般会提供这种机制，因为这真的很有用。例如，Node.JS 有 process.on(‘uncaughtException’) 。对于浏览器环境，我们可以绑定一个函数到 window.onerror，当遇到未知异常的时候，它就会执行。

语法如下：
```
window.onerror = function(message, url, line, col, error) {
  // ...
};
```

message
异常信息。
url
发生异常的代码的 URL。
line, col
错误发生的代码的行号和列号。
error
异常对象。


例如：
```
<script>
  window.onerror = function(message, url, line, col, error) {
    alert(`${message}\n At ${line}:${col} of ${url}`);
  };

  function readData() {
    badFunc(); // 哦，出问题了！
  }

  readData();
</script>
```

window.onerror 的目的不是去处理整个代码的执行中的所有异常 —— 这几乎是不可能的，这只是为了给开发者提供异常信息。

也有针对这种情况提供异常日志的 web 服务，比如 https://errorception.com 或者 http://www.muscula.com。

它们会这样运行：

我们注册这个服务，拿到一段 JS 代码（或者代码的 URL），然后插入到页面中。
这段 JS 代码会有一个客户端的 window.onerror 函数。
发生异常时，它会发送一个异常相关的网络请求到服务提供方。
我们只要登录服务方提供方的网络接口就可以看到这些异常。