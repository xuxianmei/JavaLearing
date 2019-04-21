## 函数的原型

我们已经知道，可以通过一个构造函数（像：``new F()``）创建新对象。

>注：这里的F代表一个函数名称。

如果函数原型（F.prototype）是一个对象，new操作符会用它来设置创建的对象的``[[Prototype]]``。

JavaScript 从一开始就有了原型继承。这是该语言的核心特征之一。

但在过去，没有能够直接访问他们的方式，唯有一种设置方法：使用构造函数的 "prototype" 属性。而且现在还有很多使用它的脚本。

>**F.prototype** 
>在这里，F.prototype表示一个F上的常规的属性，属性名为prototype。
>听起来，它很像术语prototype的东西。
>但在这里，我们说的就是一个名为prototype的常规属性。

当用 new F() 创建一个新对象时，该对象的 [[Prototype]] 被设置为 F.prototype（前提是该属性指向一个对象）。
比如：
```
let animal = {
  eats: true
};

function Rabbit(name) {
  this.name = name;
}

Rabbit.prototype = animal;

let rabbit = new Rabbit("White Rabbit"); //  rabbit.__proto__ == animal

alert( rabbit.eats ); // true

```

设置 Rabbit.prototype = animal 的这段代码表达的意思是：“当 new Rabbit 创建时，把它的 [[Prototype]] 赋值为 animal” 。

![](https://zh.javascript.info/article/function-prototype/proto-constructor-animal-rabbit.png)

在图片上，"prototype" 是一个水平箭头，它是一个常规属性，[[Prototype]] 是垂直的，意味着是继承自 animal 的 rabbit。

>F.protoype only used at new F time
>F.protoype只会在new F被调用时，才会使用到，它赋值给新对象的 [[Prototype]]。
>在此过后F.prototype与这个新对象没有任何关系，可以理解为一次性的礼物。
>如果在创建以后，F.prototype属性值改变，也不会影响到刚创建的对象。

## 默认的F.prototype，构造函数属性

每个函数都有 "prototype" 属性，即使我们不设置它。

默认的 "prototype" 是只有属性 constructor 的一个对象，constructor它指向函数本身。


像这样：
```
function Rabbit() {}

/* default prototype
Rabbit.prototype = { constructor: Rabbit };
*/
```

![](https://javascript.info/article/function-prototype/function-prototype-constructor.png)

我们可以检查一下：
```
 function Rabbit() {}
// by default:
// Rabbit.prototype = { constructor: Rabbit }

alert( Rabbit.prototype.constructor == Rabbit ); // true
```

当然，如果我们什么都不做，constructor 属性可以通过 [[Prototype]] 给所有 rabbits 对象使用：

```
function Rabbit() {}
// by default:
// Rabbit.prototype = { constructor: Rabbit }

let rabbit = new Rabbit(); // inherits from {constructor: Rabbit}

alert(rabbit.constructor == Rabbit); // true (from prototype)
```

![](https://zh.javascript.info/article/function-prototype/rabbit-prototype-constructor.png)


我们可以用 constructor 属性使用与现有构造函数相同的构造函数创建一个新对象。

像这样：
```
function Rabbit(name) {
  this.name = name;
  alert(name);
}

let rabbit = new Rabbit("White Rabbit");

let rabbit2 = new rabbit.constructor("Black Rabbit");
```

当我们有一个对象，但不知道为它使用哪个构造函数（比如它来自第三方库），而且我们需要创建另一个相似的函数时，用这种方法就很方便。

但关于 "constructor" 最重要的是:**JavaScript 本身并不能确保正确的 "constructor" 函数值**。

它存在于函数的默认 "prototype"中，但仅此而已。之后会发生什么,完全取决于我们自己。

特别是，如果我们将整个默认原型替换掉，那么其中就不会有构造函数。

例如：
```
function Rabbit() {}
Rabbit.prototype = {
  jumps: true
};

let rabbit = new Rabbit();
alert(rabbit.constructor === Rabbit); // false
```

因此，为了确保正确的 "constructor"，我们可以选择添加/删除属性到默认 "prototype" 而不是将其整个覆盖：
```
function Rabbit() {}

// Not overwrite Rabbit.prototype totally
// just add to it
Rabbit.prototype.jumps = true
// the default Rabbit.prototype.constructor is preserved
```

或者，也可以手动重新创建 constructor 属性：
```
Rabbit.prototype = {
  jumps: true,
  constructor: Rabbit
};

// now constructor is also correct, because we added it
```

## 总结

在本章中，我们简要介绍了如何为通过构造函数创建的对象设置一个 [[Prototype]]。稍后我们将看到更多依赖于它的高级编程模式。

一切都很简单，只需要几条笔记就能说清楚：

* F.prototype 属性与 [[Prototype]] 不同。F.prototype 唯一的作用是：当 new F() 被调用时，它设置新对象的 [[Prototype]]。
* F.prototype 的值应该是一个对象或 null：其他值将不起作用。
* "prototype" 属性在设置为构造函数时仅具有这种特殊效果，并且用 new 调用。

在常规对象上，prototype 没什么特别的：
```
let user = {
  name: "John",
  prototype: "Bla-bla" // no magic at all
};
```
默认情况下，所有函数都有``` F.prototype ={constructor：F}```，所以我们可以通过访问它的 "constructor"属性来获得对象的构造函数。


