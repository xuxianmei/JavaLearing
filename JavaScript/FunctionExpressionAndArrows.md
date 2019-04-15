## 函数表达式与=>

函数是一个特殊的值。

## 函数声明与函数表达式

下面的语法我们通常叫函数声明：
```
function sayHi() {
  alert( "Hello" );
}
```
下面是另一种创建函数的方法叫函数表达式。

通常会写成这样：
```
let sayHi = function() {
  alert( "Hello" );
};//注意，这里因为是变量赋值，而不是普通的代码块，所以建议加上分号。
```
在这里，函数被创建并像其他赋值一样，明确的分配给了一个变量。不管函数如何定义，它只是一个存储在变量中的值 sayHi。
简单说就是 "创建一个函数并放进变量 sayHi"。
我们还可以用 alert 打印值：
```
function sayHi() {
  alert( "Hello" );
}

console.log( sayHi ); //输出的是函数代码，而不是函数调用返回的值
```

注意，最后并没有运行函数。sayHi 后没有括号函数是不会运行的。

JavaScript 中，函数是一个值，所以我们可以把它当成值对待。
上面代码显示了一段字符串值，即函数的源码。

函数是一个特殊的值
---

虽然我们可以这样调用 sayHi()，但它依然是一个特殊值。
所以我们可以像使用其他类型的值一样使用它。

我们可以复制函数到其他变量
```
function sayHi() {   // (1) create
  alert( "Hello" );
}

let func = sayHi;    // (2) copy

func(); // Hello     // (3) run the copy (it works)!
sayHi(); // Hello    //     this still works too (why wouldn't it)
```
上段代码发生的细节：

(1) 中声明创建了函数，并把它放入变量 sayHi。
(2) 复制进变量 func。
>sayHi 旁边没有括号。 如果有括号， func = sayHi() 会把 sayHi() 的调用结果写进func,
而不是 sayHi 函数本身。 3. 现在调用 sayHi() 和 func()。

## 函数作为参数值传递

因为函数是一个特殊的值，所以可以传递给任何变量和参数。

比如，使用函数参数，实现函数回调

```
function ask(question, yes, no) {
  if (confirm(question)) yes()
  else no();
}

ask(
  "Do you agree?",
  function() { alert("You agreed."); },//这是匿名函数
  function() { alert("You canceled the execution."); }
);
```

## 函数表达式VS函数声明

让我们来阐述函数声明和表达式之间的关键区别。
细微差别是在 JavaScript 引擎中在什么时候创建函数。


* 函数声明
函数在主代码流中单独声明。
```
// Function Declaration
function sum(a, b) {
  return a + b;
}
```
函数声明可用于整个脚本/代码块。
当 JavaScript 准备运行脚本或代码块时，它首先在其中查找函数声明并创建函数。
我们可以将其视为“初始化阶段”。
在处理完所有函数声明后，执行继续。
因此，声明为函数声明的函数可以比定义的更早调用。
当一个函数声明在一个代码块内运行时，它在该块内的任何地方都是可见的。

* 函数表达式
一个函数，在一个表达式中或另一个语法结构中创建。这里，该函数由赋值表达式 = 右侧创建：
```
// Function Expression
let sum = function(a, b) {
  return a + b;
};
```
函数表达式，只能执行到这个函数表达式语句。后续才可以使用。
函数表达式在执行到达时创建并可用。
一旦执行到右侧分配 let sum = function…，就会创建并可以使用(复制，调用等)。


>当我们需要声明一个函数时，首先要考虑的是函数声明语法，这是我们之前使用的语法。
>它给如何组织我们的代码提供了更多的自由，因为我们可以在声明它们之前调用这些函数。
>如果有一些特殊需要，可以使用函数表达式，比如在在某个语句块中的定义函数，需要在外部
>引用，这时可以使用函数表达式，赋值给一个外部变量。

## 箭头函数 (...)=>{... }
创建函数的另一种语法(在其他语言中有时候叫lambda表达式)。


它被称为箭头函数，因为它看起来像这样：
```
let func = (arg1, arg2, ...argN) => expression
```
样会创建一个函数 func 参数是 arg1..argN，运行右侧 expression 并返回结果。

换句话说，它大致与以下相同：
```
let func = function(arg1, arg2, ...argN) {
  return expression;
}
```

精简例子：
```
let sum = (a, b) => a + b;

/* 箭头函数更短：
let sum = function(a, b) {
  return a + b;
};
*/

alert( sum(1, 2) ); // 3
```
如果我们只有一个参数，那么括号可以省略，甚至更短：
```
// same as
// let double = function(n) { return n * 2 }
let double = n => n * 2;

alert( double(3) ); // 6
```
如果没有参数，括号应该是空的（但它们应该存在）：
```
let sayHi = () => alert("Hello!");
sayHi();
```
箭头函数的使用方式与函数表达式相同。
```
let sum = (a, b) => {  // 花括号打开多线功能
  let result = a + b;
  return result; // 如果我们使用花括号，使用返回来获得结果
};

alert( sum(1, 2) ); // 3
```
