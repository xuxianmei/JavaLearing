# 类型转换

大多数情况下，运算符和函数会自动转换将值转换为正确的类型。称之为“类型转换”。

比如，alert 会自动将任何值转换为字符串。算术运算符会将值转换为数字。

还有些例子，需要显式进行类型转换，以得到正确的结果。

>TODO:对象类型，还未讨论

## ToString()
当需要一个值的字符串形式，就会进行string类型转换。


比如，alert(value) 将 value 转换为 string 类型，然后显示这个值。

也可以显式地调用 String(value) 来达到这一目的：
```
let value = true;
alert(typeof value); // boolean
value = String(value); // 现在，值是一个字符串形式的 "true"
alert(typeof value); // string
```
转换为 string 类型的值往往是可预见的。false 变成 "false"，null 变成 "null" 等。

## ToNumer
在算术函数和表达式中，会自动进行 number 类型转换。
比如，当使用 / 用于非 number 类型：
```
alert( "6" / "2" ); // 3, strings are converted to numbers
```
也可以使用 Number(value) 显式地将这个值转换为 number 类型。
```
let str = "123";
alert(typeof str); // string
let num = Number(str); // 变成 number 类型 123
alert(typeof num); // number
```
当从 string 类型源读取值时，比如一个文本表单，但是我们期待数字输入，就经常进行显式转换。

如果字符串不是一个有效的数字，转换的结果会是 NaN，例如：
```
let age = Number("an arbitrary string instead of a number");
alert(age); // NaN, conversion failed
```
number 类型转换规则：

值|转换成number类型时的值
---|---
undefined|NaN
null|0
true 和 false|1 and 0
string|字符串开始和末尾的空白会被移除，剩下的如果是空字符串，结果为 0，否则 —— 从字符串中读出数字。错误返回 NaN。

例如：
```
alert( Number("   123   ") ); // 123
alert( Number("123z") );      // NaN (error reading a number at "z")
alert( Number(true) );        // 1
alert( Number(false) );       // 0
```
请注意 null 和 undefined 有点不同。null 变成数字 0，undefined 变成 NaN。

## ToBoolean

转换为 boolean 类型是最为简单的一个。
逻辑操作或显式调用 Boolean(value) 会触发 boolean 类型转换。

转换规则如下：

* 假值，比如 0、空的字符串、null、undefined 和 NaN 变成 false。
* 其他值变成 true。
比如：
```
alert( Boolean(1) ); // true
alert( Boolean(0) ); // false

alert( Boolean("hello") ); // true
alert( Boolean("") ); // false
```
> 包含 0 的字符串 "0" 是 true