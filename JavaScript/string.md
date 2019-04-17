## 字符串

在 JavaScript 中，文本数据被作为字符串存储，字符没有单独的类型。

字符串的内部格式总是 UTF-16，它不会绑定到页面编码中。

## 引号

字符串可以包含在单引号、双引号或反引号中
```
let single = 'single-quoted';
let double = "double-quoted";
let backticks = `backticks`;
```
单引号和双引号本质上是一样的。但是，反引号允许我们将任何表达式嵌入到字符串中，包括函数调用：
```
 function sum(a, b) {
  return a + b;
}

alert(`1 + 2 = ${sum(1, 2)}.`); // 1 + 2 = 3.
使用反引号的另一个优点是它们允许字符串跨行：

 let guestList = `Guests:
 * John
 * Pete
 * Mary
`;

alert(guestList); // 客人清单，多行
```

## 特殊字符

通过使用换行符 \n 来创建带有单引号的多行字符串，它表示中断：
```
 let guestList = "Guests:\n * John\n * Pete\n * Mary";

alert(guestList); // a multiline list of guests
```
例如，这两行描述相同：
```
 alert( "Hello\nWorld" ); // two lines using a "newline symbol"

// two lines using a normal newline and backticks
alert( `Hello
World` );
```
还有其他不常见的“特殊字符”，列表如下：
字符|描述
---|---
\b|Backspace
\f|Form feed
\n|New line
\r|Carriage return
\t|Tab
\uNNNN|16 进制的 NNNN 的unicode 符号，例如 \u00A9—— 是版权符号的 unicode ©。它必须是 4 个16 进制数字。
\u{NNNNNNNN}	一些罕见字符使用两个 unicode 符号进行编码，最多占用 4 个字节。这个长的 unicode 需要它周围的括号。
unicode 示例：
```
 alert( "\u00A9" ); // ©
alert( "\u{20331}" ); // 佫, a rare chinese hieroglyph (long unicode)
alert( "\u{1F60D}" ); // 😍, a smiling face symbol (another long unicode)
```
所有的特殊字符都以反斜杠字符 \ 开始。它也被称为“转义字符”。

如果我们想要在字符串中插入一个引号，我们也会使用它。

例如：
```
alert( 'I\'m the Walrus!' ); // I'm the Walrus!
```
正如你所看到的，我们必须用反斜杠 \' 来预设值内部引号，否则就表示字符串结束。

当然，这只不过是指与上文相同的引文。因此，作为更优雅的解决方案，我们可以改用双引号或反引号。
```
alert( `I'm the Walrus!` ); // I'm the Walrus!
```
注意反斜杠 \ 在 JavaScript 中用于正确读取字符串，然后消失。内存中的字符串没有 \。从上述示例中的 alert 可以清楚地看到 。

但是如果我们需要在字符串中显示一个实际的反斜杠 \ 应该怎么做？

我们可以这样做，只需要将其书写两次 \\：
```
alert( `The backslash: \\` ); // The backslash: \
```

## 字符串长度

length属性保存字符串长度值。
```
alert( `My\n`.length ); // 3
```
注意 \n 是一个单独的“特殊”字符，所以长度确实是 3。

## 单个字符访问

在 pos 位置获取一个字符，可以使用方括号 [pos] 或者调用 str.charAt(pos) 方法。第一个字符从零位置开始：
```
 let str = `Hello`;

// the first character
alert( str[0] ); // H
alert( str.charAt(0) ); // H

// the last character
alert( str[str.length - 1] ); // o
```

方括号是获取字符的一种现代化方法，而 charAt 是历史原因才存在的。
它们之间的唯一区别是，如果没有找到字符，[] 返回 undefined，而 charAt 返回一个空字符串：
```
let str = `Hello`;

alert( str[1000] ); // undefined
alert( str.charAt(1000) ); // '' (an empty string)
```

### 字符遍历

使用 for..of 遍历字符,或者使用for ... in
```

 for (let char of "Hello") {
  alert(char); // H,e,l,l,o (char becomes "H", then "e", then "l" etc)
}

 for (let key in "Hello") {
  alert("Hello"[key]); // H,e,l,l,o (char becomes "H", then "e", then "l" etc)
}
```

## 字符串是不可变的

在 JavaScript 中，字符串不可更改。改变字符是不可能的。
>这里的字符串指的是字符串值本身，而不是变量。

我们证明一下为什么不可能：
```
let str = 'Hi';

str[0] = 'h'; // error
alert( str[0] ); // 无法运行
```
通常的解决方法是创建一个新的字符串，并将其分配给 str 而不是以前的字符串。

例如：
```
let str = 'Hi';

str = 'h' + str[1];  // 字符串替换

alert( str ); // hi
```

>所有字符串上调用的方法，如果返回的字符串相关内容，都是创建一个新的字符串return。

## 字符串方法

* toLowerCase() 和 toUpperCase() 可以改变大小写
* 查找子字符串
* 获取子字符串

更多方法：[String参考手册](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/String)

## 字符串比较与编码
字符串按字母顺序逐字比较(比较Unicode码)。
所有的字符串都使用 UTF-16编码(Unicode存储的一种方式)。
即：每个字符都有相应的数字代码。有特殊的方法可以获取代码的字符并返回。
* str.codePointAt(pos)

返回在 pos 位置的字符代码 
```
 // 不同的字母有不同的代码
alert( "z".codePointAt(0) ); // 122
alert( "Z".codePointAt(0) ); // 90
```
* String.fromCodePoint(code)

通过数字 code 创建字符
```
 alert( String.fromCodePoint(90) ); // Z
 ```
我们还可以用
```
 // 90 is 5a in hexadecimal system
alert( '\u005a' ); // Z
```