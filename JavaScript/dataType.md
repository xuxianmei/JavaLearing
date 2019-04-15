# 数据类型
JavaScript 中的变量可以保存任何数据。
变量在前一刻可以是个字符串，然后又收到一个数值：
```
let message = "hello";
message = 123456;
```
允许这种操作的编程语言称为“动态类型”（dynamically typed）的编程语言。
意思是，拥有数据类型，但是变量并不限于数据类型中的任何一个。
>变量不拥有数据类型，实际拥有数据类型的是值。

JavaScript中有七种基本数据类型

## number
```
let n = 123;
n = 12.345;
```
number 类型用于整数和浮点数。

除了常规的数字，还包括所谓的**特殊数值**也属于这种类型：**Infinity**、**-Infinity** 和 **NaN**。

Infinity 代表数学概念中的无穷大 ∞。是一个比任何数字都大的特殊值。

我们可以通过除以 0 来得到它。
```
 alert( 1 / 0 ); // Infinity
或者在代码中直接提及它。

 alert( Infinity ); // Infinity
NaN 代表一个计算错误。它是一个不对的或者一个未定义的数学操作所得到的结果，比如：

 alert( "not a number" / 2 ); // NaN, 这样的除法是错误的
NaN 是粘性的。任何对 NaN 的进一步操作都会给出 NaN：

 alert( "not a number" / 2 + 5 ); // NaN
所以，如果在数学表达式中有一个 NaN，会被传播到最终结果。
```

>数学运算是安全的
>在 JavaScript 中做数学运算是安全的。我们可以做任何事：除以 0，将非数字字符串视为数字，等等。脚本永远不会有致命的错误(“死亡”)。最坏的情况下，会得到 NaN 作为结果。

特殊的数值属于 number 类型。当然，对这个词的一般认识是，它们并不是数字。

## string

```
let str = "Hello";
let str2 = 'Single quotes are ok too';
let phrase = `can embed ${str}`;
```
JavaScript 中的字符串必须被包含在引号里面。
在 JavaScript 中，有三种包含字符串的方式。

* 双引号： "Hello".
* 单引号： 'Hello'.
* 反引号： `Hello`.
双引号和单引号都是“简单”引用，在 JavaScript 中两者并没有什么差别。

反引号是功能扩展的引用，允许通过```${…}```，将**变量**和**表达式**嵌入到字符串中。
例如：
```
let name = "John";

// embed a variable
alert( `Hello, ${name}!` ); // Hello, John!

// embed an expression
alert( `the result is ${1 + 2}` ); // 结果是 3
```
${…} 内的表达式会被计算，结果成为字符串的一部分。可以在 ${…} 内放置任何东西：诸如 name 的变量，或者诸如 1 + 2 的算数表达式，或者其他一些更复杂的。

需要注意的是，这仅仅在反引号内有效，其他引号不允许这种嵌入。
>没有 character 类型。
>在一些语言中，单个字符有一个特殊的 “character” 类型，在 C 语言和 Java 语言中是 char。
>JavaScript 中没有这种类型。只有一种 string 类型，一个字符串可以包含一个或多个字符。

## boolean

boolean 类型仅包含两个值：true 和 false。

比如：
```
let nameFieldChecked = true; // yes, name field is checked
let ageFieldChecked = false; // no, age field is not checked
```
布尔值也可作为比较的结果：
```
let isGreater = 4 > 1;
```

## ``null``值

特殊的 null 值不属于上述任何一种类型。

它构成一个独立的类型，只包含 null 值：
```
let age = null;
```
相比较于其他语言，JavaScript 中的 null 不是一个“对不存在对象的引用”或者 “null 指针”。
仅仅是一个含义为“无”、“空”或“值未知”的特殊值。
上面的代码表示，由于某些原因，age 是未知的。

## ``undefined``值
特殊值和 null 一样，自成类型。
undefined 的含义是 未被赋值。

如果变量被声明，而未被赋值，那么它的值就是 undefined：
```
let x;
alert(x); // 弹出 "undefined"
```
原理上来说，可以为任何变量赋值为 undefined：
```
let x = 123;
x = undefined;
alert(x); // "undefined"
```
但是不建议这样做。
通常，使用使用 null 将一个“空”或者“未知”的值写入变量中/
undefined 仅仅用于检验，以查看变量是否被赋值或者其他类似的操作。

## object和 symbol
object 类型是特殊的类型。

其他所有的类型都称为“原生类型”.
因为它们的值只包含一个单独的东西（字符串、数字或者其他）。
相反，对象用于储存数据集合和更复杂的实体。

symbol 类型用于创建对象的唯一标识符。

## typeof 运算符
typeof 运算符返回参数的类型。当我们想要分别处理不同类型值的时候，或者简单地进行检验，就很有用。

它支持两种语法形式：
```
作为运算符：typeof x。
函数形式：typeof(x)。
```
换言之，有括号和没有括号，结果是一样的。

对 typeof x 的调用返回数据类型的字符串。
```

typeof undefined // "undefined"

typeof 0 // "number"

typeof true // "boolean"

typeof "foo" // "string"

typeof Symbol("id") // "symbol"

typeof Math // "object"  (1)

typeof null // "object"  (2)

typeof alert // "function"  (3)
```

最后三行可能需要额外的说明：

Math 是一个提供数学运算的内建对象。
typeof null 的结果是 "object"。这是不对的。这是官方在 typeof 方面承认的错误，只是为了兼容性而保留。
typeof alert 的结果是 "function"，因为 alert 在语言中是一个函数。我们会在下一章学习函数，那时我们会了解到，在语言中没有一个特别的 “function” 类型。函数隶属于 object 类型。但是 typeof 会对函数区分对待。这不正确，但在实践中非常方便。
## 总结
JavaScript 中有七种基本的类型。
* number 用于任何类型的数字：整数或者浮点数。
* string 用于字符串。一个字符串可以包含一个或多个字符，所以没有单独的单字符类型。
* boolean 用于 true 和 false。
* null 用于未知的值 —— 只有一个 null 值的独立类型。
* undefined 用于未定义的值 —— 只有一个 undefined 值的独立类型。
* object 用于更复杂的数据结构。
* symbol 用于唯一的标识符。

 typeof 运算符可以查看变量的类型。
两种形式：typeof x 或者 typeof(x)。
返回的类型的字符串，比如 "string"。
null 返回 "object" —— 这是语言中的一个错误，实际上它并不是一个对象。
