## 类型检测

``instanceof`` 操作符用于检测对象是否属于某个 class，同时，检测过程中也会将继承关系考虑在内。

## instanceof

用法：
```
obj instanceof Class
```
如果obj隶属于Class，返回true。
或者obj隶属于Class的子类。


举例说明：
```
class Rabbit {}
let rabbit = new Rabbit();

// rabbit 是 Rabbit 类的实例对象吗?
alert( rabbit instanceof Rabbit ); // true
```
再来看看内置类型 Array：
```
 let arr = [1, 2, 3];
alert( arr instanceof Array ); // true
alert( arr instanceof Object ); // true
```
有一点需要留意，arr 同时还隶属于 Object 类。因为从原型上来讲，Array 是继承自 Object 类的。

instanceof 在检测中会将原型链考虑在内，此外，还能借助静态方法 Symbol.hasInstance 来改善检测效果。

### obj instance of Class 执行过程

obj instanceof Class 语句的大致执行过程如下：

 1. 如果提供了静态方法 Symbol.hasInstance，那就直接用这个方法进行检测：

```
 // 假设具有 canEat 属性的对象为动物类
class Animal {
  static [Symbol.hasInstance](obj) {
    if (obj.canEat) return true;
  }
}

let obj = { canEat: true };
alert(obj instanceof Animal); // 返回 true：调用 Animal[Symbol.hasInstance](obj)
```

2. 大部分的类是没有 Symbol.hasInstance 方法的，这时会检查 Class.prototype 是否与 obj 的原型链中的任何一个原型相等。

简而言之，是这么比较的：
```
obj.__proto__ === Class.prototype
obj.__proto__.__proto__ === Class.prototype
obj.__proto__.__proto__.__proto__ === Class.prototype
...
```

在上一个例子中有 Rabbit.prototype === rabbit.__proto__ 成立，所以结果是显然的。

再比如下面一个继承的例子，rabbit 对象同时也是父类的一个实例：
```
class Animal {}
class Rabbit extends Animal {}

let rabbit = new Rabbit();
alert(rabbit instanceof Animal); // true
// rabbit.__proto__ === Rabbit.prototype
// rabbit.__proto__.__proto__ === Animal.prototype (match!)
```

下图展示了 rabbit instanceof Animal 的执行过程中，Animal.prototype 是如何参与比较的：

![](images/instanceof.png)

这里还要提到一个方法 objA.isPrototypeOf(objB)，如果 objA 处在 objB 的原型链中，调用结果为 true。所以，obj instanceof Class 也可以被视作为是调用 Class.prototype.isPrototypeOf(obj)。

虽然有点奇葩，其实 Class 的构造器自身是不参与检测的！检测过程只和原型链以及 Class.prototype 有关。

所以，当 prototype 改变时，会产生意想不到的结果。

就像这样：
```
function Rabbit() {}
let rabbit = new Rabbit();

// 修改其 prototype
Rabbit.prototype = {};

// ...再也不是只兔子了！
alert( rabbit instanceof Rabbit ); // false
```
所以，为了谨慎起见，最好避免修改 prototype。


## 使用 Object 的 toString 方法来揭示类型

大家都知道，一个普通对象被转化为字符串时为 [object Object]：
```
 let obj = {};

alert(obj); // [object Object]
alert(obj.toString()); // 同上
```

这也是它们的 toString 方法的实现。

但是，有一个隐藏特性让toString变得更强大。
我们可以用它来替代 instanceof，也可以视作为 typeof 的增强版。


按照 规范 上所讲，内置的 toString方法可以从对象中提取出来，以其他值作为上下文（context）对象进行调用，调用结果取决于传入的上下文对象。

* 如果传入的是 number 类型，返回 [object Number]
* 如果传入的是 boolean 类型，返回 [object Boolean]
* 如果传入 null，返回 [object Null]
* 传入 undefined，返回 [object Undefined]
* 传入数组，返回 [object Array]

…等等（例如一些自定义类型）

下面进行演示：
```

 // 保存 toString 方法的引用，方便后面使用
let objectToString = Object.prototype.toString;
// 猜猜是什么类型？
let arr = [];

alert( objectToString.call(arr) ); // [object Array]
````

这里用到了章节 装饰和转发，call/apply 里提到的 call 方法来调用 this=arr 上下文的方法 objectToString。

toString 的内部算法会检查 this 对象，返回对应的结果。
再如：

```
let s = Object.prototype.toString;

alert( s.call(123) ); // [object Number]
alert( s.call(null) ); // [object Null]
alert( s.call(alert) ); // [object Function]

```

### Symbol.toStringTag

对象的 toString 方法可以使用 Symbol.toStringTag这个特殊的对象属性进行自定义输出。

举例说明：

```
let user = {
  [Symbol.toStringTag]: "User"
};


alert( {}.toString.call(user) ); // [object User]
```

大部分和环境相关的对象也有这个属性。以下输出可能因浏览器不同而异：
```
 // 环境相关对象和类的 toStringTag：
alert( window[Symbol.toStringTag]); // window
alert( XMLHttpRequest.prototype[Symbol.toStringTag] ); // XMLHttpRequest

alert( {}.toString.call(window) ); // [object Window]
alert( {}.toString.call(new XMLHttpRequest()) ); // [object XMLHttpRequest]
```

输出结果和 Symbol.toStringTag（前提是这个属性存在）一样，只不过被包裹进了 [object ...] 里。

这样一来，typeof，不仅能检测基本数据类型，内置对象类型也不在话下，更可贵的是还支持自定义。

所以，如果希望以字符串的形式获取内置对象类型信息，而不仅仅只是检测类型的话，可以用这个方法来替代 instanceof。

## 总结

下面，来总结下大家学到的类型检测方式：

&nbsp;|用于|返回|
---|---|---
``typeof``|primitives|string|
``{}.toString``|primitives, built-in objects, objects with Symbol.toStringTag|string	
``instanceof``|objects|true/false

看样子，{}.toString 基本就是一增强版 typeof。

instanceof 在涉及多层类结构的场合中比较实用，这种情况下需要将类的继承关系考虑在内。
