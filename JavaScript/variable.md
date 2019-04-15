# 变量

变量就是用来存储信息的。

## 变量定义
ES6语法格式：
```
let 名称=值;
```
>赋值与定义可以分开。

ES5语法格式：
```
var 名称=值;//var也可以不写
```

>名称区别大小写。

未采用 use strict 下的赋值
一般，需要在使用一个变量前定义它。
但是在ES6以前，可以简单地赋值来创建一个变量，而不需要 let。
如果不使用 use strict，这仍然正常工作，这种行为是为了保持与旧脚本的兼容。
```

 // 注意：这个例子中没有 "use strict"

num = 5; // 如果变量 "num" 不存在，就会被创建

alert(num); // 5
```
这是个糟糕的做法，严格模式下会抛出错误。
```
"use strict";

num = 5; // error: num is not defined
```

## 常量
声明一个常数（不变）变量，可以使用 const 而非 let。
语法格式一致，就是将let换成const。

一个普遍的做法是将常量用作别名，以便记住那些在执行之前就已知的难以记住的值。

这些常量使用大写和下划线命名。

就像这样：
```
const COLOR_RED = "#F00";
const COLOR_GREEN = "#0F0";
const COLOR_BLUE = "#00F";
const COLOR_ORANGE = "#FF7F00";

// ...当需要选择一个颜色
let color = COLOR_ORANGE;
alert(color); // #FF7F00
```
## 总结
我们可以声明变量来存储数据。可以通过使用 var、let 或者 const 来完成。

* let –ES6的变量声明方式。Chrome（V8）中代码必须开启严格模式以使用 let。
* var – ES5的变量声明方式。一般情况下，我们不会使用它。但是，我们会在 旧时的 "var" 章节介绍 var 和 let 的微妙差别，以防你需要它们。
* const – 类似于let，但是变量的值无法被修改。

变量应当以一种容易理解变量内部是什么的方式进行命名。