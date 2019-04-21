## 属性的标签和描述符

我们知道，对象可以存储属性。

到目前为止，属性对我们来说是一个简单的“键-值”对。但对象属性实际上是更复杂可变的东西。

## 属性的flags

对象属性除了value外，还有三个特性属性（所谓的flags）。

* `writable`— 如果为 true，则可以修改，否则它是只读的。
* `enumerable`— 如果是 true，则可在循环中列出，否则不列出。
* `configurable`— 如果是true，则此属性可以被删除，这些flags(也可以被修改，否则不可以。


当我们用“常用的方式”创建一个属性时，它们都是 true。

我们可以修改它们（configurable为true）。

首先，让我们看看如何获得这些标志。

[Object.getOwnPropertyDescriptor](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Object/getOwnPropertyDescriptor) 方法允许查询有关属性的完整信息。

语法是：
```
let descriptor = Object.getOwnPropertyDescriptor(obj, propertyName);
```

obj：需要获取信息的对象。

propertyName：属性的名称。

返回值是一个所谓的“属性描述符”对象：它包含值和所有的flags标志。

例如：
```
 let user = {
  name: "John"
};

let descriptor = Object.getOwnPropertyDescriptor(user, 'name');

alert( JSON.stringify(descriptor, null, 2 ) );
/* property descriptor:
{
  "value": "John",
  "writable": true,
  "enumerable": true,
  "configurable": true
}
*/
```
为了修改flags，我们可以使用 [Object.defineProperty](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Object/defineProperty)。


语法是：
```
Object.defineProperty(obj, propertyName, descriptor)
```
obj，propertyName 要处理的对象和属性。
descriptor要应用的属性描述符。

如果该属性存在，则 defineProperty 更新其标志。否则，它会创建具有给定值和标志的属性；在这种情况下，如果没有提供标志，则会假定它是 false。

这里使用所有的伪造标志创建一个属性 name：
```
let user = {};

Object.defineProperty(user, "name", {
  value: "John"
});

let descriptor = Object.getOwnPropertyDescriptor(user, 'name');

alert( JSON.stringify(descriptor, null, 2 ) );
/*
{
  "value": "John",
  "writable": false,
  "enumerable": false,
  "configurable": false
}
 */
```

将它与上面的“以常用方式创建的” user.name 进行比较：现在所有标志都是false。
如果这不是我们想要的，那么我们最好在 descriptor 中将它们设置为 true。

## read-only

我们通过修改 writable 标志来把 user.name 设置为只读：
```
let user = {
  name: "John"
};

Object.defineProperty(user, "name", {
  writable: false
});

user.name = "Pete"; // 错误：不能设置只读属性'name'...
```
现在没有人可以改变name的value，除非用 defineProperty 来覆盖。

以下是相同的操作，因为默认为false

```
let user = { };

Object.defineProperty(user, "name", {
  value: "Pete",
  // 对于新的属性，需要明确的列出哪些为 true
  enumerable: true,
  configurable: true
});

alert(user.name); // Pete
user.name = "Alice"; // Error
```

## Non-enumerable

```enumerable:false```

### 不可配置


不可配置标志（configurable:false）有时会预设在内置对象和属性中。

一个不可配置的属性不能被 defineProperty 删除或修改。

## 设定一个全局的封闭对象
属性描述符可以在各个属性的级别上工作。

还有一些限制访问整个对象的方法：

Object.preventExtensions(obj)
禁止向对象添加属性。

Object.seal(obj)
禁止添加/删除属性，为所有现有的属性设置 configurable: false。

Object.freeze(obj)
禁止添加/删除/更改属性，为所有现有属性设置 configurable: false, writable: false。

还有对他们的测试：

Object.isExtensible(obj)
如果添加属性被禁止，则返回 false，否则返回 true。

Object.isSealed(obj)
如果禁止添加/删除属性，则返回 true，并且所有现有属性都具有 configurable: false。

Object.isFrozen(obj)
如果禁止添加/删除/更改属性，并且所有当前属性都是 configurable: false, writable:
false，则返回 true。

这些方法在实践中很少使用。