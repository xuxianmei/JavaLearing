## 日期和时间

让我一起探讨一个新的内置对象：[日期](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Date)。该对象存储日期、时间以及提供管理它们的方法。

举个例子，我们可以使用它来存储创建、修改事件的时间，或者用来度量时间开销，再或者用来打印当前时间。

## 创建

创建一个新的 Date 对象，只需要调用 new Date()，附加下列参数中的其中一个：

new Date() :不带参数 —— 创建一个表示当前日期和时间的 Date 对象：
```
let now = new Date();
alert( now ); // 显示当前的日期/时间
```
new Date(milliseconds) :创建一个 Date 对象，参数是从 1970-01-01 00:00:00 UTC+0 开始所经过的毫秒（一秒的千分之一）数。

```
// 0 表示 01.01.1970 UTC+0
let Jan01_1970 = new Date(0);
alert( Jan01_1970 );

// 增加 24 小时，得到 02.01.1970 UTC+0
let Jan02_1970 = new Date(24 * 3600 * 1000);
alert( Jan02_1970 );
```

传入的参数是自``1970-01-01 00:00:00``开始计算的毫秒数，被称为**时间戳**。

这是一种日期的轻量级表示方法。我们通常使用时间戳来创建一个日期，比如 `new Date(timestamp)`，以及使用 `date.getTime()` 来将现有的 `Date` 对象转化为时间戳（下面将提到）。
new Date(datestring) :如果只有一个参数，并且是字符串，那么该参数会通过 Date.parse 算法解析（下面会提到）。

```
let date = new Date("2017-01-26");
alert(date); // Thu Jan 26 2017 ...
```
new Date(year, month, date, hours, minutes, seconds, ms) :创建一个 Date 对象，参数是当地时区的日期组合信息。只有前两个参数是必须的。

注意：

- `year` 必须是四位数：`2013` 是合法的，`98` 是不合法的。
- `month` 计数从 `0` （一月） 开始，到 `11` （12月）。
- `date` 是当月的具体某一天，如果缺失，默认是 `1`。
- 如果 `hours/minutes/seconds/ms` 缺失的话，它们默认值是 `0`。

举个例子：
```
new Date(2011, 0, 1, 0, 0, 0, 0); // // 1 Jan 2011, 00:00:00
new Date(2011, 0, 1); // 同样，时分秒等默认为 0
```

时间度量最小精确到 1 毫秒（千分之一秒）:
```
let date = new Date(2011, 0, 1, 2, 3, 4, 567);
alert( date ); // 1.01.2011, 02:03:04.567
```

## 访问日期组件
从 Date 对象中访问年、月等信息有很多种方式。通过分类可以很容易记忆。

- getFullYear()
获取年份（4 位数）

- getMonth()
获取月份从 0 到 11。

- getDate()
获取当月的日期，从 1 到 31，这个方法名称可能看起来有些令人疑惑。

- getHours(), getMinutes(), getSeconds(), getMilliseconds()
获取相应的时间信息。

不是 getYear()，而是 getFullYear()很多 JavaScript 引擎都实现了一个非标准化的方法 getYear().
这个方法不建议使用。它有可能返回 2 位的年份信息。请不要使用它。获取年份，使用 getFullYear()。

另外，我们还可以获取一周中的第几天：

- getDay()

获取一周中的第几天，从 0（星期天）到 6 （星期六）。
第一天始终是星期天，在某些国家可能不是这样的习惯，但是这不能被改变。

以上所有的方法返回的信息都是基于当地时区的。

当然，也有与之对应的 UTC 版本方法，它们会返回基于 UTC+0 时区的天数、月份、年份等等信息：
getUTCFullYear()， getUTCMonth()， getUTCDay()。只需要在 "get" 之后插入 "UTC"。

如果你当地时区相对于 UTC 有偏移，那么下面代码会显示不同的小时数：
```
 //  当前日期
let date = new Date();

// 当地时区的小时数
alert( date.getHours() );

// 在 UTC+0 时区的小时数（非夏令时的伦敦时间）
alert( date.getUTCHours() );
```
在以上给出的方法中，有两个与众不同的，它们没有 UTC 版本：
- getTime()

返回日期的时间戳 —— 从 1970-1-1 00:00:00 UTC+0 开始的毫秒数。

- getTimezoneOffset()

返回时区偏移数，以分钟为单位：
```
 // 如果你在时区 UTC-1，输出 60
// 如果你在时区 UTC+3，输出 -180
alert( new Date().getTimezoneOffset() );
```

## 设置日期信息
以下方法可以设置日期/时间信息：

- setFullYear(year [, month, date])
- setMonth(month [, date])
- setDate(date)
- setHours(hour [, min, sec, ms])
- setMinutes(min [, sec, ms])
- setSeconds(sec [, ms])
- setMilliseconds(ms)
- setTime(milliseconds) （使用自 `1970-01-01 00:00:00 UTC+0` 开始的毫秒数来设置整个日期对象）

以上方法除了 setTime() 都有 UTC 版本，比如 setUTCHours()。

我们可以看到，有些方法可以一次性设置多个信息，比如 setHours。

## 自动校准
```
let date = new Date(2016, 0, 2); // 2016 年 1 月 2 日

date.setDate(1); // 设置为当月的第一天
alert( date );

date.setDate(0); // 天数最小可以设置为 1，所以这里设置为上一月的最后一天
alert( date ); // 2015 年 12 月 31 日
```

## 转化为数字，日期差值

当 Date 对象转化为数字时，得到的是对应的时间戳，相当于 date.getTime()：
```
let date = new Date();
alert(+date); // 以毫秒为单位的数值，相当于 date.getTime()
```
有一个重要的副作用：日期可以相减，它们相减的结果是以毫秒为单位。

这个作用可以用来做时间度量：
```
let start = new Date(); // 起始时间

// 做一些操作
for (let i = 0; i < 100000; i++) {
  let doSomething = i * i * i;
}

let end = new Date(); // 结束时间

alert( `The loop took ${end - start} ms` );
```

## Date.now()
如果我们仅仅想要度量时间间隔，我们不需要整个 Date 对象。

有一个特殊的方法 Date.now()，它会返回当前的时间戳。

它相当于 new Date().getTime()，但它不会在中间创建一个 Date 对象。因此它更快，而且不会对垃圾处理造成额外的压力。

这种方法很多时候因为方便而被采用，又或者从性能上考虑，像 JavaScript 中的游戏以及其他的特殊应用。

对一个字符串使用 Date.parse
Date.parse(str) 方法可以从一个字符串中读取日期。

字符串的格式是：``YYYY-MM-DDTHH:mm:ss.sssZ``，其中：

YYYY-MM-DD —— 日期：年-月-日。
字符串 "T" 是一个分隔符。
``HH:mm:ss.sss`` —— 时间：小时，分钟，秒，毫秒。
可选字符 'Z' 代表时区。单个字符 Z 代表 UTC+0。
简短形式也是可以的，比如 YYYY-MM-DD 或者 YYYY-MM 又或者 YYYY。

## Date.parse(str) 

方法会转化一个特定格式的字符串，返回一个时间戳（自 1970-01-01 00:00:00 起的毫秒数），如果格式不正确，返回 NaN。

举个例子：
```
 let ms = Date.parse('2012-01-26T13:51:50.417-07:00');

alert(ms); // 1327611110417  (时间戳)
```
我们可以通过时间戳来立即创建一个 new Date 对象戳：
```
 let date = new Date( Date.parse('2012-01-26T13:51:50.417-07:00') );

alert(date);
```

