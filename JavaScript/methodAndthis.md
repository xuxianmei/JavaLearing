## 对象方法与this

对象通常用来表示真实世界中的实体，比如用户、订单等。
```
let user = {
  name: "John",
  age: 30
};
```

另外在现实世界中，用户还有一些行为，比如：登录，注销等。
在JavaScript中，行为通过属性中的函数来表示

## 方法

作为对象属性的函数称之为方法。
```
let user = {
  name: "John",
  age: 30
};

user.sayHi = function() {
  alert("Hello!");
};

user.sayHi(); // Hello!
```
那么，现在 user 对象有了一个 sayHi 方法。
当然我们也可以使用预先定义的函数作为方法，就像这样：
```
let user = {
  // ...
};

// 首先声明
function sayHi() {
  alert("Hello!");
};

// 然后将其作为一个方法
user.sayHi = sayHi;

user.sayHi(); // Hello!
```
### 方法的简写方式

在对象字面量中，有一种更短的（声明）方法的语法：
```
// 这些对象作用一样

let user = {
  sayHi: function() {
    alert("Hello");
  }
};

// 方法简写看起来更好，对吧？
let user = {
  sayHi() { // 与 "sayHi: function()" 一样
    alert("Hello");
  }
};
```

如所示，我们可以省略 "function" 只写了 sayHi()。
这种表示法还是有些不同。与对象集成有关的细微差别（稍后将会介绍），但现在它们无关紧要。在几乎所有的情况下，较短的语法是最好的。

## 方法内部的this

，对象方法需要访问对象中的存储的信息来完成其工作。

举个例子，user.sayHi() 中的代码可能需要用到 user 的 name 属性。

为了访问该对象，方法中可以使用 this 关键字。

this 的值就是在点之前的这个对象，即调用该方法的对象。
举个例子：
```
let user = {
  name: "John",
  age: 30,

  sayHi() {
    alert(this.name);
  }

};

user.sayHi(); // John
```
在这里 user.sayHi() 执行过程中，this 的值是 user。

技术上讲，也可以在不使用 this 的情况下，通过外部变量名来引用它：
```
let user = {
  name: "John",
  age: 30,

  sayHi() {
    alert(user.name); // "user" 替代 "this"
  }

};
```
但这样的代码是不可靠的。如果我们将 user 复制给另一个变量。例如 admin = user，并赋另外的值给 user，那么它将访问到错误的对象。

如下所示：
```
let user = {
  name: "John",
  age: 30,

  sayHi() {
    alert( user.name ); // 导致错误
  }

};
let admin = user;
user = null; // 覆盖让其更易懂
admin.sayHi(); // 噢哟！在 sayHi() 使用了旧的变量名。错误！
```
如果在 alert 中以 this.name 替换 user.name，那么代码就会正常运行。

### this不是绑定限制的

在JavaScript中，this的值不是固定绑定在某个对象上的。
this的值，不是在函数声明时就求值的，this是在运行时求值的。
也就是说，this只有在运行时才能确定它的具体值。
this代表的是在运行时，调用此函数的上下文对象。

首先，this可以在任何函数中使用（包括非对象属性方法的函数）

```
function sayHi() {
  alert(this);
}

sayHi(); // undefined
```

在这种情况下，严格模式下的 this 值为 undefined。如果我们尝试访问 this.name，将会出现错误。

在非严格模式（没有使用 use strict）的情况下，this 将会是全局对象（浏览器中的 window，我们稍后会进行讨论）。"use strict" 可以修复这个历史行为。

请注意，通常在没有对象的情况下使用 this 的函数调用是不常见的，会（导致）编程错误。如果函数中有 this，那么通常意味着它是在对象上下文环境中被调用的。


## 箭头函数没有自己的 “this”
箭头函数有些特别：它们没有自己的 this。如果我们在这样的函数中引用 this，this 值取决于调用此函数时的上下文。
举个例子，这里的 arrow() 使用的 this 来自外部的 user.sayHi() 方法：
```
 let user = {
  firstName: "Ilya",
  sayHi() {
    let arrow = () => alert(this.firstName);
    arrow();
  }
};

user.sayHi(); // Ilya
```

这是箭头函数的一个特征，当我们并不想要一个独立的 this值，反而想从外部上下文中获取时，它很有用。

