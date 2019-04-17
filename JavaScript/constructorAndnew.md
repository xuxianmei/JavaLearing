## 构造函数与操作符new

构造函数的主要目的 — 实现可重用的对象创建代码。

常规的 {...} 语法允许创建一个对象。但是我们经常需要创建许多类似的对象，例如多个用户或菜单项等等。

这可以使用构造函数和 "new" 操作符。

## 构造函数

构造函数在技术上是常规函数。不过有两个约定：
1. 他们首先用大写字母命名。
2. 它们只能用 "new" 操作符来执行。
例如：
```
function User(name) {
  this.name = name;
  this.isAdmin = false;
}

let user = new User("Jack");

alert(user.name); // Jack
alert(user.isAdmin); // false
```
当一个函数作为 new User(...)执行时，它执行以下步骤：

1. 一个新的空对象被创建并分配给 this。
2. 函数体执行。通常它会修改 this，为其添加新的属性。
3. 返回 this 的值。
换句话说，new User(...) 做类似的事情：
```
function User(name) {
  // this = {};（隐式创建）

  // 添加属性到 this
  this.name = name;
  this.isAdmin = false;

  // return this;（隐式返回）
}
```
>如果没有参数使用 new 可以省略构造函数后面的括号：
```
let user = new User; // <-- no parentheses
// same as
let user = new User();
```

### *new function() { … }*

如果我们有许多关于创建单个复杂对象的代码行，我们可以将它们封装在构造函数中，如下所示：
```
let user = new function() {
  this.name = "John";
  this.isAdmin = false;

  // ...用户创建的其他代码
  // 也许是复杂的逻辑和陈述
  // 局部变量等
};
```
构造函数不能被再次调用，因为它不保存在任何地方，只是被创建和调用。所以这个技巧的目的是封装构建单个对象的代码，而不是将来重用。

### 双语法构造函数：new.target
在一个函数内部，我们可以使用 new.target 属性来检查它被调用时，是否使用了 new。
这可以使 new 和常规语法的工作原理相同：
```
 function User(name) {
  if (!new.target) { // 如果你没有运行 new
    return new User(name); // ...会为你添加 new
  }

  this.name = name;
}

let john = User("John"); // 重新调用 new User
alert(john.name); // John
```

这种方法有时用在库中以使语法更加灵活。但因为省略 new 使得它不易阅读，这可不是一件好事。 而通过 new 我们可以都知道这个新对象正在创建。

###  构造函数Return

通常，构造函数没有 return 语句。他们的任务是将所有必要的东西写入 this，并自动转换。

但是，如果有 return 语句，那么规则很简单：

如果 return 对象，则返回它，而不是 this。
如果 return 基本类型，则忽略。

例如，这里 return 通过返回一个对象覆盖 this：
```
 function BigUser() {

  this.name = "John";

  return { name: "Godzilla" };  // <-- returns 一个 object
}

alert( new BigUser().name );  // 哇哦，得到了对象，name 属性值为 Godzilla ^^
```
这里有一个 return 空的例子（或者我们可以在它之后放置一个原函数）：

```
 function SmallUser() {

  this.name = "John";

  return; // 完成执行，returns this

  // ...

}

alert( new SmallUser().name );  // John
```

## 构造函数中的方法：

使用构造函数来创建对象会带来很大的灵活性。通过构造函数的参数传递定义构造对象。

当然，我们不仅可以将属性添加到 this 中，而且还可以添加方法。

例如，new User(name) 下面用给定的 name 和方法 sayHi 创建一个对象：
```
function User(name) {
  this.name = name;

  this.sayHi = function() {
    alert( "My name is: " + this.name );
  };
}

let john = new User("John");

john.sayHi(); // My name is: John

/*
john = {
   name: "John",
   sayHi: function() { ... }
}
*/
```

## 总结
* 构造函数或简言之，就是常规函数，但构造函数有个共同的约定，命名它们首字母要大写。
* 构造函数只能使用 new 来调用。这样的调用意味着在开始时创建空的 this，并在最后返回填充的对象。
* 
我们可以使用构造函数来创建多个类似的对象。

JavaScript 为许多内置的对象提供了构造函数：比如日期 Date，设置集合 Set 以及其他我们计划学习的内容。