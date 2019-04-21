## 回调
JavaScipt 中的许多行为都是异步的。

比如，这个 loadScript(src) 函数：

```
function loadScript(src) {
  let script = document.createElement('script');
  script.src = src;
  document.head.append(script);
}
```

这个函数的作用是加载一个新的脚本。当使用 `<script src="…">` 将其添加到文档中时，浏览器就会对它进行加载和执行。

我们可以像这样使用：
```
// loads and executes the script
loadScript('/my/script.js');
```

函数是异步调用的，因为脚本加载不是此刻完成的，而是之后。

这个调用先初始化脚本加载，然后继续执行。当脚本正在被加载时，下面的代码可能已经完成了执行，如果加载需要时间，那么同一时间，其他脚本可能也会被运行。
```
loadScript('/my/script.js');
// 下面的代码在加载脚本时，不会等待脚本被加载完成
// ...
code
```
这个脚本可能定义了新函数，我们想要在脚本加载后，使用这个新函数。



但如果我们在 loadScript(…) 调用后，立即调用里面的函数，会失败。

```
loadScript('/my/script.js'); // the script has "function newFunction() {…}"

newFunction(); // no such function!
```

很明显，浏览器可能没有时间去加载脚本。
因此，对新函数的立即调用失败了。
目前，loadScript 函数并没有提供追踪加载完成时的方法。
脚本加载然后运行，仅此而已。

但我们希望了解脚本何时加载完成，以使用其中的新函数和新变量。

让我们来将callback 函数作为第二个参数添加至 loadScript中，函数在脚本加载完成时被执行：

```
function loadScript(src, callback) {
  let script = document.createElement('script');
  script.src = src;

  script.onload = () => callback(script);

  document.head.append(script);
}
```

现在，如果我们想调用脚本中的的新函数，我们应该在回调函数中这么写

```
loadScript('/my/script.js', function() {
  // the callback runs after the script is loaded
  newFunction(); // so now it works
  ...
});
```

这是一个可运行的真实脚本示例：

```
function loadScript(src, callback) {
  let script = document.createElement('script');
  script.src = src;
  script.onload = () => callback(script);
  document.head.append(script);
}

loadScript('https://cdnjs.cloudflare.com/ajax/libs/lodash.js/3.2.0/lodash.js', script => {
  alert(`Cool, the ${script.src} is loaded`);
  alert( _ ); // 在加载的脚本中声明的函数
});
```

这被称为“基于回调”的异步编程风格。
回调是异步之间的一种同步方式。

## 处理错误

上述示例中，我们并没有考虑错误因素。假如加载失败会如何？
我们的回调应该可以立即对其做出响应。

这是可以跟踪错误的 loadScript 改进版：
```
function loadScript(src, callback) {
  let script = document.createElement('script');
  script.src = src;

  script.onload = () => callback(null, script);
  script.onerror = () => callback(new Error(`Script load error for ${src}`));

  document.head.append(script);
}
```

成功时，调用 callback(null, script)，否则调用 callback(error)。

用法：
```
loadScript('/my/script.js', function(error, script) {
  if (error) {
    // handle error
  } else {
    // 成功加载脚本
  }
});
```
再一次强调，我们使用的 loadScript 方法是非常常规的。它被称为 “error-first callback” 风格。

惯例是：

callback 的第一个参数是为了错误发生而保留的。一旦发生错误，callback(err) 就会被调用。
第二个参数（如果有需要）用于成功的结果。此时 callback(null, result1, result2…) 将被调用。
因此单个 callback 函数可以同时具有报告错误以及传递返回结果的作用。