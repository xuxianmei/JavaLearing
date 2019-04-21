## 获取所有属性
获取一个对象的键/值有很多种方法。

它们中的大部分操作对象本身，但不包括原型。

下面是这些方法：

* Object.keys(obj) / Object.values(obj) / Object.entries(obj)————返回一个数组，包含所有可枚举字符串属性名称/值/键值对。这些方法只会列出可枚举属性，而且它们键名为字符串形式。
如果我们想要 symbol 属性：

* Object.getOwnPropertySymbols(obj) ————返回包含所有 symbol 属性名称的数组。

如果我们想要非可枚举属性：
* Object.getOwnPropertyNames(obj) ———— 返回包含所有字符串属性名的数组。


如果我们想要所有属性：
* Reflect.ownKeys(obj) ———— 返回包含所有属性名称的数组。

这些方法和它们返回的属性有些不同，但它们都是对对象本身进行操作。
prototype的属性没有包含在内。

## for...in loop

for...in 循环有所不同：它会对继承得来的属性也进行循环。

举个例子：
```
let animal = {
  eats: true
};

let rabbit = {
  jumps: true,
  __proto__: animal
};

// 这里只有自身的键
alert(Object.keys(rabbit)); // jumps


// 这里包含了继承得来的键
for(let prop in rabbit) alert(prop); // jumps，然后 eats
```

如果我们想要区分继承属性，有一个内置方法 obj.hasOwnProperty(key)：如果 obj 有名为 key 的自身属性（而非继承），返回值为 true。

因此我们可以找出继承属性（或者对它们进行一些操作）：
```
 let animal = {
  eats: true
};

let rabbit = {
  jumps: true,
  __proto__: animal
};

for(let prop in rabbit) {
  let isOwn = rabbit.hasOwnProperty(prop);
  alert(`${prop}: ${isOwn}`); // jumps:true, then eats:false
}
```

这个例子中我们有以下继承链：rabbit，然后 animal，然后 Object.prototype （因为 animal 是个字面量对象 {...}，因此默认是这样），然后最终到达 null：

![](https://javascript.info/article/getting-all-properties/rabbit-animal-object.png)

请注意：这里有一个有趣的现象。rabbit.hasOwnProperty 这个方法来自哪里？
观察继承链我们发现这个方法由 Object.prototype.hasOwnProperty 提供。换句话说，它是继承得来的。

但是如果说 for...in 列出了所有继承属性，为什么 hasOwnProperty 这个方法没有出现在其中？

答案很简单：它是不可枚举的。就像所有其他在 Object.prototype 中的属性一样。这是为什么它们没有被列出的原因。