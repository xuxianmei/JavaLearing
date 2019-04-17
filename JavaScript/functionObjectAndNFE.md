## 函数对象，NFE
在JavaScript中，函数就是值，可以任意赋值给变量。
每个值都有其数据类型。

函数的数据类型是对象。

可以把函数想象成可被调用的「动作对象」。
我们不仅可以调用函数，还能把函数当作对象来处理：增／删属性，引用传参等。

## 属性 name

函数对象包含一些有用的属性。

比如，一个函数的名字可以通过属性"name"来访问
 
```
function sayHi(){
	alert("Hi");
}

alert(sayHi.name);//sayHi
```

更有趣的是，名称赋值的逻辑很智能。在函数被用于赋值时也能将正确的名字赋给它：
```
 let sayHi = function() {
  alert("Hi");
}

alert(sayHi.name); // sayHi（生效了!）
```
当以默认值的方式赋值时，它也有效：
```
 function f(sayHi = function() {}) {
  alert(sayHi.name); // sayHi （生效了！）
}

f();
```
规范中把这种特性叫做「上下文命名」。如果函数自己没有提供，那么在赋值中，会根据上下文来推测一个。

对象方法也有名字。

但有时会出现无法推测名字的情况。此时，属性 name 会是空，比如：
```
// 函数在数组中创建
let arr = [function() {}];

alert( arr[0].name ); // <空字符串>
// 引擎无法得到正确的名字，所以没有值
```
而实际上，大多数函数都是有名字的。

## 属性 length

还有另一个内置属性 “length”，它返回函数入参的个数。
```
function f1(a) {}
function f2(a, b) {}
function many(a, b, ...more) {}

alert(f1.length); // 1
alert(f2.length); // 2
alert(many.length); // 2
```
可以看到，余参不参与计数。

属性 length 有时用于在函数中操作其它函数的内省。

比如，下面的代码中函数 ask 接受一个询问的 question 和任意个会被调用的 handler 函数。

当用户提供了自己的答案后，函数会调用那些 handlers。我们可以传入两种 handler：

* 一个无参函数，它在用户回答「是」时调用。
* 一个有参函数，它在每种情况都会被调用，并且返回一个答案。

我们的想法是，一个简单无参的处理程序处理正向情况（最常见的变体），但也要能提供通用性的处理程序。

为了正确的调用 handlers，我们检查属性 length：
```
 function ask(question, ...handlers) {
  let isYes = confirm(question);

  for(let handler of handlers) {
    if (handler.length == 0) {
      if (isYes) handler();
    } else {
      handler(isYes);
    }
  }

}

// 正向回答，两个 handler 都会被调用
// 负向回答，只有个第二个被调用
ask("Question?", () => alert('You said yes'), result => alert(result));
```
这种特别的情况就是所谓的多态性 —— 根据参数的类型，或者根据在我们这种情况下的 length 来做不同的处理。这种思想在 JavaScript 的库里有应用。

## 自定义属性

我们也可以添加我们自己的属性。

这里我们增加了 counter 属性，用来跟踪总的调用次数：
```
function sayHi() {
  alert("Hi");

  // 我们记录一下运行次数
  sayHi.counter++;
}
sayHi.counter = 0; // 初始值

sayHi(); // Hi
sayHi(); // Hi

alert( `调用了 ${sayHi.counter} 次` ); // 调用了 2 次
```
属性不是变量一个被赋值的函数属性，比如 sayHi.counter = 0 没有在函数内定义一个局部变量 counter。或者说，一个 counter 属性与一个 let counter 的变量是毫不相关的两个东西。

我们可以把函数当作对象，在它里面存储属性，但是这对它的执行没有任何影响。
变量不会使用函数属性，反之亦然。它们是不相干的词。

函数属性有时会用来替代闭包。比如，我们可以将 闭包 章节中计数函数的例子改写为用函数属性实现：
```
 function makeCounter() {
  // 不再用：
  // let count = 0

  function counter() {
    return counter.count++;
  };

  counter.count = 0;

  return counter;
}

let counter = makeCounter();
alert( counter() ); // 0
alert( counter() ); // 1
```

count 直接被存储在函数里，而不是它外部的词法环境。

那么它和闭包谁好谁赖？

两者最大的不同就是如果 count 的值位于外层（函数）变量中，那么外部的代码无法访问到它，只有嵌套的函数可以修改它，而如果它是绑定给函数的，那么就很容易：
```
function makeCounter() {

  function counter() {
    return counter.count++;
  };

  counter.count = 0;

  return counter;
}

let counter = makeCounter();

counter.count = 10;
alert( counter() ); // 10
```
所以，如何实现取决于我们的目标。


## 命名函数表达式（NFE）
命名函数表达式（NFE，Named Function Expression），指代有名字的函数表达式的术语。

比如，我们看一个一般的函数表达式：
```
let sayHi = function(who) {
  alert(`Hello, ${who}`);
};
```
然后给它加一个名字：
```
let sayHi = function func(who) {
  alert(`Hello, ${who}`);
};
```
我们这里得到了什么？为它添加一个 "func" 名字的目的是什么？

首先注意，它仍然是一个函数表达式。在 function 后面的名字 "func" 没有使它成为函数声明，因为它仍然是作为赋值表达式中的一部分被创建的。

增加这个名字没有破坏任何东西。

函数依然可以通过 sayHi() 来调用：
```
let sayHi = function func(who) {
  alert(`Hello, ${who}`);
};

sayHi("John"); // Hello, John
```
关于名字 func，有两个特殊的地方：

* 它允许函数在内部引用自己。
* 它在函数外是不可见的。

比如，下面的函数 sayHi 会在没有入参 who 时，以 "Guest" 为入参调用自己：
```
let sayHi = function func(who) {
  if (who) {
    alert(`Hello, ${who}`);
  } else {
    func("Guest"); // 使用 func 再次调用自己
  }
};

sayHi(); // Hello, Guest

// 但这个无法生效
func(); // Error, func is not defined（在函数外不可见）
```
我们为什么使用 func 呢？为什么不直接在嵌套调用里使用 sayHi？

事实上，大多数情况下我们可以：
```
let sayHi = function(who) {
  if (who) {
    alert(`Hello, ${who}`);
  } else {
    sayHi("Guest");
  }
};
```
使用上面代码的问题在于 sayHi 的值可能会改变。
且那个函数可能会被赋给其它变量，那么函数就会开始报错：
```
let sayHi = function(who) {
  if (who) {
    alert(`Hello, ${who}`);
  } else {
    sayHi("Guest"); // Error: sayHi is not a function
  }
};

let welcome = sayHi;
sayHi = null;

welcome(); // Error，嵌套调用 sayHi 不再有效！
```
那是因为函数从它的外部词法环境获取 sayHi。没有局部的 sayHi，所以外部变量被使用。而当调用时，外部的 sayHi 是 null。

我们给函数表达式增加的可选的名字正是用来解决这个问题的。

我们使用它来修复我们的代码：
```
let sayHi = function func(who) {
  if (who) {
    alert(`Hello, ${who}`);
  } else {
    func("Guest"); // 现在一切正常
  }
};

let welcome = sayHi;
sayHi = null;

welcome(); // Hello, Guest（嵌套调用有效）
```
现在它可以正常运行了，因为名字 func 是函数局部域的。它不会从外部获取（而且在外部也不可见）。规范确保它只会引用当前函数。

外部代码仍然只有自己的 sayHi 或 welcome 变量。而且 func 是一个「内部函数名」，代表函数可以在其内部调用自己。