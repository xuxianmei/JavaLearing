## 数组

对象允许存储键值化的集合。

有时候，我们有序集合，里面的元素都是按顺序排列的。

这时使用对象就很不方便了，因为对象不提供能够管理属性顺序的方法。

使用数组，能实现有序集合的概念。

## 数据声明

声明一个空数组类型有两种语法
```
let arr=new Array();//()中可以放置一个整数，或一个初始化元素列表（以逗号隔开）。
let arr=[];//大部分时使用这种方式。
```

我们也可以在方括号中添加初始元素。
```
let fruits["apple","orange","plum"];
//或
let arr = new Array("Apple", "Pear", "etc");
```
数据元素key为整数类型，编号从0开始。也称为索引

### 元素访问

使用方括号与key（index）来获取元素：
```
let fruits["apple","orange","plum"];
console.log(fruits[0]);
console.log(fruits[1]);
console.log(fruits[2]);
```

可以替换元素
```
fruits[2]="Pear";
```


### 添加新元素

```
fruits[3]="Lemon";
```
一个数组中的数据元素，可以是任何类型的。

```
// 混合值
let arr = [ 'Apple', { name: 'John' }, true, function() { alert('hello'); } ];

// 获取索引为 1 的对象然后显示它的 name
alert( arr[1].name ); // John

// 获取索引为 3 的函数并执行
arr[3](); // hello
```

以逗号结尾：数组和对象一样，都可以在末尾冗余一个逗号（良好风格）：
```
let fruits = [
  "Apple",
  "Orange",
  "Plum",
];
```
因为每一行都是相似的，所以这种以“逗号结尾”的方式使得插入/移除项变得更加简单。


## 数组类型的属性length

**length实际存储是最大的索引值加一，而不是数组里元素的个数（虽然大多数情况符合）。**

当我们修改数组的时候，length 属性会自动更新。它实际上不是数组里元素的个数，而是最大的数字索引值加一。

例如，一个数组只有一个元素，但是这个元素的索引值很大，那么这个数组的 length 也会很大：
```
 let fruits = [];
fruits[99] = "Apple";

alert( fruits.length ); // 值为100，实际0-98处的元素值都为undefined。
```
要知道的是我们通常不会这样使用数组。

### length可写

length 属性的另一个有意思的点是它是可写的。

如果我们手动增加长度，一切正常。但是如果我们减少长度，数组就会变短。这种处理是不可逆的，下面是一个例子：
```
 let arr = [1, 2, 3, 4, 5];

arr.length = 2; // 只剩 2 个元素
alert( arr ); // [1, 2]

arr.length = 5; // 又把 length 加回来
alert( arr[3] ); // undefined: 被截断的那些数值并没有回来
```

## 内部

数组本身是一种特殊的对象。
记住，在 JavaScript 中只有 7 种基本类型。数组是一个对象因此其行为也像一个对象。

使用方括号来访问属性arr[0]实际上是来自对象的语法。这个数字被用作键值。

数据扩展了对象，提供了特殊的方法来处理有序的数据集合，还添加了length属性。
但是本质止还是一个对象。

例如，它是通过引用来复制的：
```
let fruits = ["Banana"]

let arr = fruits; // 通过引用复制 (两个变量引用的是相同的数组)

alert( arr === fruits ); // true

arr.push("Pear"); // 通过引用修改数组

alert( fruits ); // Banana, Pear — 现在只有 2 项了
```

但是数组真正特殊的是它们的内部实现。
JavaScript 引擎尝试把这些元素一个接一个地存储在连续的内存区域。

但是如果我们放弃以“有序集合”的方式使用数组，而是像一个常规对象一样使用它，这些就都不生效了。

例如可以这样做:
```
let fruits = []; // 创建一个数组

fruits[99999] = 5; // 用一个远大于数组长度的索引分配属性

fruits.age = 25; // 用任意的名字创建属性
```

这是可能的，因为数组是基于对象的。我们可以给它们添加任何属性。

但是 Javascript 引擎会发现我们在像使用常规对象一样使用数组，那么针对数组的优化就不再适用了，而且还会被关闭，这些优化所带来的优势也就荡然无存了。

数组误用的几种方式:

添加一个非数字的属性比如 arr.test = 5。
制造空洞，比如：添加 arr[0] 后添加 arr[1000] (它们中间什么都没有)。
以倒序填充数组, 比如 arr[1000]，arr[999] 等等。

**请将数组视为作用于有序数据的特殊结构，它们为此提供了特殊的方法。数组在 JavaScript 引擎内部是经过特殊调整的，使得更好的作用于连续的有序数据，所以请以这种方式使用数组。如果你需要任意键值，那很有可能实际上你需要的是常规对象 {}。**

## 数据的遍历

* for(... ;... ; ...)

```
let arr = ["Apple", "Orange", "Pear"];

for (let i = 0; i < arr.length; i++) {
  alert( arr[i] );
}
```

* for(let ... of ...)

```
let arr = ["Apple", "Orange", "Pear"];

for (let fruit of fruits) {
  alert( fruit);
}
```


* for( let ... in ...) 不建议使用

```
let arr = ["Apple", "Orange", "Pear"];

for (let key in fruits) {
  alert( fruits[key]);
}
```

但这其实不是个好想法。会有一些潜在问题存在：

for..in 循环会迭代所有属性，不仅仅是这些数字属性。

在浏览器和其它环境中有一种“类数组”的对象，它们看似是数组，也就是说，它们有 length 和索引属性，但是也可能有其它的非数字的属性和方法，这通常是我们不需要的。for..in 循环会把它们都列出来。所以如果我们需要处理类数组对象，这些“额外”的属性就会存在问题。

for..in 循环适用于普通对象，不适用于数组，而且会慢 10-100 倍。当然即使是这样也依然非常快。只有在遇到瓶颈或者一些不相关的场景增速可能会有问题。但是我们仍然应该了解这其中的不同。

通常来说，我们不应该用 for..in 来处理数组。

## 多维数组
数组里的项也可以是数组。我们可以以多维数组的方式存储矩阵：
```
 let matrix = [
  [1, 2, 3],
  [4, 5, 6],
  [7, 8, 9],
];


alert( matrix[1][1] ); // 最中间的那个数

```
## toString
数组有自己的 toString 方法的实现，会返回以逗号隔开的元素列表。

例如：
```
 let arr = [1, 2, 3];

alert( arr ); // 1,2,3
alert( String(arr) === '1,2,3' ); // true
```
或者尝试一下这个：
```
alert( [] + 1 ); // "1"
alert( [1] + 1 ); // "11"
alert( [1,2] + 1 ); // "1,21"
```
数组没有 Symbol.toPrimitive，也没有 valueOf，它们只能执行 toString 进行转换，所以这里 [] 就变成了一个空字符串，[1] 变成了 "1" 然后 [1,2] 变成了 "1,2"。

当 "+" 操作符把一些项加到字符串后面时，加号后面的项也会被转换成字符串，所以下一步就会是这样：
```
alert( "" + 1 ); // "1"
alert( "1" + 1 ); // "11"
alert( "1,2" + 1 ); // "1,21"
```

## 使用数组方法实现队列与栈

队列
---

FIFO
* push 在末端添加一个元素.
* shift 取出队列最前端的一个元素，整个队列往前移

栈
---
LIFO
* push 在数组的前端添加元素
* pop 取出并返回数组的最后一个元素

JavaScript 中的数组既可以用作队列，也可以用作栈。它们允许从前端/末端来添加/删除元素。

作用于数组末端的方法：

pop

取出并返回数组的最后一个元素：
```
 let fruits = ["Apple", "Orange", "Pear"];

alert( fruits.pop() ); // 移除 "Pear" 然后 alert 显示出来

alert( fruits ); // Apple, Orange
```
push
在数组末端添加元素：
```
 let fruits = ["Apple", "Orange"];

fruits.push("Pear");

alert( fruits ); // Apple, Orange, Pear
```
调用 fruits.push(...) 与 fruits[fruits.length] = ... 是一样的。

作用于数组前端的方法：
shift
取出数组的第一个元素并返回它：
```
let fruits = ["Apple", "Orange", "Pear"];

alert( fruits.shift() ); // 移除 Apple 然后 alert 显示出来

alert( fruits ); // Orange, Pear
```
unshift
在数组的前端添加元素：
```
let fruits = ["Orange", "Pear"];

fruits.unshift('Apple');

alert( fruits ); // Apple, Orange, Pear
push 和 unshift 可以一次添加多个元素：

 let fruits = ["Apple"];

fruits.push("Orange", "Peach");
fruits.unshift("Pineapple", "Lemon");

// ["Pineapple", "Lemon", "Apple", "Orange", "Peach"]
alert( fruits );
```