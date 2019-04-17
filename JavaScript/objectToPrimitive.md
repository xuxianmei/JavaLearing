## 对象类型转换为简单类型

对于对象，不存在 to-boolean 转换，因为所有对象在布尔上下文中都是 true。所以只有字符串和数值转换。

数值转换发生在对象相减或应用数学函数时。例如，Date 对象（将在 日期和时间 章节中介绍）可以相减，而 date1 - date2 的结果是两个日期之间的时间差。

至于字符串转换 —— 它通常发生在我们输出像 alert(obj) 这样的对象和类似的上下文中。

## 转换为简单类型

当一个对象被用在需要简单类型的值的上下文中时，它会使用 ToPrimitive 算法进行。

该算法允许我们使用一个特殊的对象方法来进行自定义转换。

取决于上下文，转换具有所谓的hint

这里有三种变体：

* "string"

当一个操作期望一个字符串时，对于对象到字符串的转换，比如 
```
alert：
// output
alert(obj);

// 使用对象作为属性键
anotherObj[obj] = 123;
```

* "number"
当一个操作需要一个数字时，用于对象到数字的转换，如 maths：
```
// 显式转换
let num = Number(obj);

// maths（除了二进制加法）
let n = +obj; // 一元加法
let delta = date1 - date2;

// 小于/大于的比较
let greater = user1 > user2;
```

* "default"
在少数情况下发生，当操作者“不确定”期望的类型时。
例如，二进制加 + 可以和字符串（连接）和数字（相加）发生作用，所以类型是字符串和数字都可以。或者当一个对象用 == 与一个字符串、数字或符号进行比较时。
```
// 二进制加
let total = car1 + car2;

// obj == string/number/symbol
if (user == 1) { ... };
```
大于/小于运算符 <> 也可以同时用于字符串和数字。不过，它使用 “number” hint，而不是 “default”。这是历史原因。

实际上，除了一种情况（Date对象，我们稍后会学到它）之外，所有内置对象都实现了一个和 "number" 一样的 "default" 转换。可能我们也应该这样做。

请注意 —— 只有三种hint。就这么简单。没有 “boolean” hint（所有对象在布尔上下文中都是 true）或其他任何东西。
如果我们将 "default" 和"number"视为相同，就像大多数内置函数一样，那么只有两种转换了。

为了进行转换，JavaScript 尝试查找并调用三个对象方法：

* 调用 obj[Symbol.toPrimitive](hint) 如果这个方法存在的话，
* 如果hint是 "string",尝试 obj.toString() 和 obj.valueOf()，无论哪个存在。
* 如果hint "number" 或者 "default" 尝试 obj.valueOf() 和obj.toString()，无论哪个存在。


### Symbol.toPrimitive
我们从第一个方法开始。有一个名为 Symbol.toPrimitive的内置符号应该用来命名转换方法，像这样
```

obj[Symbol.toPrimitive] = function(hint) {
  // 返回一个原始值
  // hint = "string"，"number" 和 "default" 中的一个
}
```
例如，这里 user 对象实现它：
```
 let user = {
  name: "John",
  money: 1000,

  [Symbol.toPrimitive](hint) {
    alert(`hint: ${hint}`);
    return hint == "string" ? `{name: "${this.name}"}` : this.money;
  }
};
```

// 转换演示：
alert(user); // hint: string -> {name: "John"}
alert(+user); // hint: number -> 1000
alert(user + 500); // hint: default -> 1500
从代码中我们可以看到，根据转换的不同，user 变成一个自描述字符串或者一个金额。单个方法 user[Symbol.toPrimitive] 处理所有的转换情况。

### toString/valueOf
方法 toString 和 valueOf来自历史版的ES。
它们不是符号（那时候还没有符号这个概念），而是“常规的”字符串命名的方法。
它们提供了一种老派的方式来实现转换。

如果没有 Symbol.toPrimitive 那么 JavaScript 尝试找到它们并且按照下面的顺序进行尝试：

对于"string"暗示，toString -> valueOf。
其他情况，valueOf -> toString。
例如，在这里 user 使用 toString 和 valueOf 的组合，上面的效果相同：
````
 let user = {
  name: "John",
  money: 1000,

  // 对于 hint="string"
  toString() {
    return `{name: "${this.name}"}`;
  },

  // 对于 hint="number" 或 "default"
  valueOf() {
    return this.money;
  }

};


alert(user); // toString -> {name: "John"}
alert(+user); // valueOf -> 1000
alert(user + 500); // valueOf -> 1500
````

通常我们希望有一个“全能”的地方来处理所有原始转换。在这种情况下，我们可以只实现 toString，就像这样：
```
 let user = {
  name: "John",

  toString() {
    return this.name;
  }
};

alert(user); // toString -> John
alert(user + 500); // toString -> John500
```
如果没有 Symbol.toPrimitive 和 valueOf，toString 将处理所有原始转换。

>关于所有转换方法，有一个重要的点需要知道，就是它们不一定会返回“hint”原始值。
没有限制 toString() 是否返回字符串，或 Symbol.toPrimitive 方法是否为 “number” 返回数字。
唯一强制性的事情是：这些方法必须返回一个原始值。

由于历史原因，toString 或 valueOf方法应该返回一个原始值：如果它们中的任何一个返回了一个对象，虽然不会报错，但是该对象被忽略（就像该方法不存在一样）。

相反，Symbol.toPrimitive 必须返回一个原始值，否则会出现错误。

在实践中，为了记录或调试目的，仅实现 obj.toString() 作为“全捕获"方法通常就够了，这样所有转换都能返回一种“人类可读”的对象表达形式。