# 条件运算符：if、'?'
有时我们需要根据不同条件执行不同的操作。

if 语句可以解决这个问题，条件运算符（三元），简称为“问号”运算符也可以解决。

## if语句
if 语句获得一个条件，计算这个条件表达式，如果计算结果是 true，就会执行对应的代码。

如下：
```
let year = prompt('In which year was ECMAScript-2015 specification published?', '');
if (year == 2015) alert( 'You are right!' );
```
在上面的例子中，条件是一个简单的相等性检查：year == 2015，但它可能更复杂。

如果有多个语句要执行，我们必须将要执行的代码块封装在大括号内：
```
if (year == 2015) {
  alert( "That's correct!" );
  alert( "You're so smart!" );
}
```
建议每次使用 if 语句都用大括号 {} 来包装代码块，即使只有一条语句也是。这样能提高代码可读性。

## 布尔转换
if（……） 语句会计算圆括号包围的表达式的结果并将其转换为布尔类型。

* 数字 0、一个空字符串 ""、null、undefined 和 NaN 都会转换成 false。因为他们被称为 “falsy” 值。
* 其他值变成 true，所以它们被称为 “truthy”。

所以，下面条件下的代码永远不会执行：
```
if (0) { // 0 是 falsy
  ...
}
```
……但下面的条件 — 始终有效：
```
if (1) { // 1 是 truthy
  ...
}
```
我们也可以将未计算的布尔值传入 if 语句，如下所示：
```
let cond = (year == 2015); // 相等运算符的结果是 true 或 false

if (cond) {
  ...
}
```

其它还有 else if 、else

## 三元操作符 ?

有时我们需要根据一个条件去声明变量。

如下所示：
```
let accessAllowed;
let age = prompt('How old are you?', '');

if (age > 18) {
  accessAllowed = true;
} else {
  accessAllowed = false;
}

alert(accessAllowed);
```
所谓的“三元”或“问号”操作符让我们可以更简便地达到目的。

它用问号 ? 表示。“三元”意味着操作符有三个操作数。它实际上是JavaScript 中唯一一个有这么多操作数的操作符。

语法如下：
```
let result = condition ? value1 : value2
```
计算条件结果，如果结果为真，则返回 value1，否则返回 value2。

如下所示：
```
let accessAllowed = (age > 18) ? true : false;
```
技术上，我们可以省略 age > 18 外面的括号。问号运算符的优先级较低。它在比较 > 后执行，所以也会执行相同的操作：
```
// 比较运算符 “age > 18” 首先执行
//（不需要将其包含在括号中）
let accessAllowed = age > 18 ? true : false;
```
但括号使代码更具可读性，所以建议使用它们。

## ‘?’ 的非传统使用
有时可以使用问号 ? 来代替 if 语句：
```
let company = prompt('Which company created JavaScript?', '');

(company == 'Netscape') ?alert('Right!') : alert('Wrong.');
```
根据 company =='Netscape' 条件，要么执行 ? 后面的第一部分方法并显示对应内容，要么执行第二部分的方法并显示对应内容。

在这里我们不是把结果赋值给变量。而是根据条件执行不同的代码。

不建议以这种方式使用问号运算符。

这种写法比 if 语句更短，对一些程序员很有吸引力。但它的可读性较差。

下面是使用 if 语句实现的相同功能的代码，进行下比较：
```
let company = prompt('Which company created JavaScript?', '');

if (company == 'Netscape') {
  alert('Right!');
} else {
  alert('Wrong.');
}
```
因为我们的眼睛垂直扫描代码。跨越多行的结构比长的水平代码更容易理解。
问号 ? 的作用是根据条件返回一个或另一个值,请正确使用它。
if 还可以用来执行代码的不同分支。