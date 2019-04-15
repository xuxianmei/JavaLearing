# 函数 function

我们经常需要在脚本的许多地方执行类似的操作。


函数是程序的主要“构建模块”，它们允许相同代码的多次重复调用。

## 函数声明

```
function Name(arguments){
	...
}
```
function 关键字首先出现，然后是函数名，然后是括号（在上述示例中为空）之间的参数列表，最后是花括号之间的代码（即“函数体”）。

>函数的主要目的之一是：避免代码重复。

## 局部变量
在函数中声明的变量只在该函数内部可见。

例如：
```
function showMessage() {
  let message = "Hello, I'm JavaScript!"; // 局部变量

  alert( message );
}

showMessage(); // Hello, I'm JavaScript!

alert( message ); // <-- 错误！变量是函数的局部变量
```

## 外部变量


函数也可以访问外部变量，例如：
```
let userName = 'John';

function showMessage() {
  let message = 'Hello, ' + userName;
  alert(message);
}
showMessage(); // Hello, John
```
函数可以访问外部变量，也可以修改它。

例如： 
```
let userName = 'John';

function showMessage() {
  userName = "Bob"; // (1) 改变外部变量

  let message = 'Hello, ' + userName;
  alert(message);
}

alert( userName ); // John 在函数调用之前

showMessage();

alert( userName ); // Bob, 值被函数修改
```
只有在没有本地变量的情况下才会使用外部变量。因此如果我们忘记了 let，可能会发生意外修改的情况。

如果在函数中声明了同名变量，那么它遮蔽外部变量。例如，在如下代码中，函数使用本地的 userName，外部部分被忽略：
```
let userName = 'John';

function showMessage() {
  let userName = "Bob"; // 声明一个局部变量

  let message = 'Hello, ' + userName; // Bob
  alert(message);
}

//  函数会创建并使用它自己的 userName
showMessage();

alert( userName ); // John，未更改，函数没有访问外部变量。
```

## 全局变量
任何函数之外声明的变量，例如上述代码中的外部 userName 都称为全局。

全局变量在任意函数中都是可见的(除非被局部变量遮蔽)。

通常，函数声明与任务相关的所有变量。全局变量只存储项目级的数据，所以这些变量从任何地方都可以访问是很重要的事情。现代的代码有很少或没有全局变量。大多数变量存在于它们的函数中。

## 参数
我们可以使用参数（也称“函数参数”）来将任意数据传递给函数。

在如下示例中，函数有两个参数：from 和 text。
```
function showMessage(from, text) { // 参数：from、text
  alert(from + ': ' + text);
}

showMessage('Ann', 'Hello!'); // Ann: Hello! (*)
showMessage('Ann', "What's up?"); // Ann: What's up? (**)
```
当在行 (*) 和 (**) 中调用函数时，将给定的值复制到局部变量 from 和 text。然后函数使用它们。

这里有一个例子：我们有一个变量 from，将它传递给函数。请注意：函数会修改 from，但在外部看不到更改，因为函数修改的是变量的副本：
```
function showMessage(from, text) {

  from = '*' + from + '*'; // 让 "from" 看起来更优雅

  alert( from + ': ' + text );
}

let from = "Ann";

showMessage(from, "Hello"); // *Ann*: Hello

//外部变量"from" 值不变，函数修改了本地副本。
alert( from ); // Ann
```

### 函数默认值

如果未提供参数，则其值是 undefined。

如果我们想在本例中使用“默认” text,那么我们可以在 = 之后指定它：
```
function showMessage(from, text = "no text given") {
  alert( from + ": " + text );
}

showMessage("Ann"); // Ann: 无文本
```
现在如果 text 参数未被传递，它将会得到 "no text given" 的值。

这里 "no text given" 是 string，但它可以是更复杂的表达式，只有在缺少参数时才会计算和分配改表达式。
因此，这也是可能的：
```
function showMessage(from, text = anotherFunction()) {
  // anotherFunction() 仅在没有给定文本时执行
  // 其结果变成文本值
}
```
>默认值，可以是任意表达式。

>ES6以前旧版本的 JavaScript 不支持默认参数。

## 返回值

函数可以将一个值返回到调用代码中作为结果。

最简单的例子是将两个值相加的函数：
```
function sum(a, b) {
  return a + b;
}


let result = sum(1, 2);
alert( result ); // 3
```
指令 return 可以在函数的任意位置。当执行到达时，函数停止，并将值返回给调用代码（分配给上述 result）。

在一个函数中可能会出现很多次 return。例如：
```
function checkAge(age) {
  if (age > 18) {
    return true;
  } else {
    return confirm('Got a permission from the parents?');
  }
}

let age = prompt('How old are you?', 18);

if ( checkAge(age) ) {
  alert( 'Access granted' );
} else {
  alert( 'Access denied' );
}
```
在没有值的情况下使用 return 可能会导致函数立即退出。

例如：
```
function showMovie(age) {
  if ( !checkAge(age) ) {
    return;
  }

  alert( "Showing you the movie" ); // (*)
  // ...
}
```
在上述代码中，如果 checkAge(age) 返回 false，那么 showMovie 将不会进入 alert。

如果函数无返回值，它就会像返回 undefined 一样：
```
 function doNothing() { /* empty */ }

alert( doNothing() === undefined ); // true
```
空 return 也和 return undefined 一样：
```
 function doNothing() {
  return;
}
alert( doNothing() === undefined ); // true
```

