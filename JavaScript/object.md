# 对象
JavaScript 中有很七种类型。
其中六种基本类型，因为他们的值只包含一种东西（字符串，数值或者其他）。 
对象用来存储键值对和更复杂的实体。
在 JavaScript 中，对象深入到这门语言的方方面面。所以在我们深入理解这门语言之前，必须先理解对象。

对象通过花括号{...}和其中包含一些可选的属性来创建。
属性是一个键值对，键是一个字符串（也叫属性名），值可以是任何类型。

我们可以用下面两种语法的任一种来创建一个空的对象：
```
let user = new Object(); // “构造函数” 的语法
let user = {};  // “字面量” 的语法
```

## 属性

我们可以在创建的时候立即给对象一些属性，在 {...} 里面放置一些键值对。
```
let user = {     // 一个对象
  name: "John",  // 键 "name"，值 "John"
  age: 30        // 键 "age"，值 30
};
```
其中name和age为这个对象包含的属性名称，后面的是这个属性的值。

>属性的名称必须是字符串，可以使用双引号(或单引号)，也可以不使用，但最终都会转换成字符串类型。
>属性的值可以是任意类型（包括对象类型的值）。

属性名支持变量或表达式，但是需要使用[]。

```
let name="School"
let student={
	[name]:"MIT",
	[name+1]:"COCO"
}
console.log(student[name]);
console.log(student[name+1]);
```

### 属性操作

* 访问属性

可以通过点语法来使用属性：
```
// 读取文件的属性：
alert( user.name ); // John
alert( user.age ); // 30
```
还可以使用方括号来使用属性，可以对任何字符串有效（特别是属性名包含空格之类的）

```
// 读取文件的属性：
alert( user["name"] ); // John
alert( user["age"] ); // 30
```
>方括[]中的内容，可以是字面量，也可以变量，或者是表达式。

* 属性添加

除了在创建对象时，可以指定创建属性，可以在任意可访问对象的地方，创建新属性。
比如：
```
user.isAdmin = true;
```
或
```
user["isAdmin"] = true;
```

* 属性移除

```
delete user.age;
```


注：方括号比点符号更强大。它允许任何属性名和变量，但写起来也更加麻烦
大部分时间里，当属性名是已知且简单的时候，用点方法。如果有一些复杂的操作，那么就用方括号。

## 属性的简写

在实际应用中，我们通常用存在的变量当做属性名。

例如：
```
 function makeUser(name, age) {
  return {
    name: name,
    age: age
    // 其他的属性
  };
}

let user = makeUser("John", 30);
alert(user.name); // John
```
在上面的例子中，属性名跟变量名一样。这种应用场景很常见，所以提供一种很便利的方式来定义对象的属性值。

可以用 name 来代替 name:name 像下面那样：
```
function makeUser(name, age) {
  return {
    name, // 与 name: name 相同
    age   // 与 age: age 相同
    // ...
  };
}
````
我们可以把简写方式和正常方式混用：
```
let user = {
  name,  // 与 name:name 相同
  age: 30
};
```

## 属性存在检查
对象的一个显著的特点就是可以访问任何属性，如果这个属性名没有值也不会有错误。

访问一个不存在的属性会返回 undefined。
---
基于此，可以使用undefined比较来判定值存在与否。
````
 let user = {};

alert( user.noSuchProperty === undefined ); // true 意思是没有这个属性
```

同样也有一个特别的操作符 "in" 来检查是否属性存在。

语法是：
```
"key" in object
```
例如：
```
 let user = { name: "John", age: 30 };

alert( "age" in user ); // true，user.age 存在
alert( "blabla" in user ); // false，user.blabla 不存在。
```
注意 in 的左边必须是属性名。通常是一个字符串，如果不用字符串，那就是一个字符串变量。
```
let user = { age: 30 };
let key = "age";
alert( key in user ); // true，获取键的名字和检查这个键的属性
```
 * Using “in” 属性中存储 undefined
通常，严格比较 "=== undefined" 就够用，但是也有一些特殊情况，"in" 就可以胜任。
那就是属性存在，但是存储 undefined：
```
 let obj = {
  test: undefined
};

alert( obj.test ); //  它是 undefined，所以难道它不存在吗？

alert( "test" in obj ); // true，属性存在！
```
在上面的代码中，属性 obj.test 事实上是存在的，所以 in 操作符可以。
这种情况很少发生，因为 undefined 通常情况下是不会被赋值到对象的，我们经常会用 null 来表示未知的或者空的值。


## for ... in 循环遍历对象属性

语法：
```
for(key in object) {
  // 各个属性键值的执行区
}
```
例如，我们列出 user 所有的属性值：
```
 let user = {
  name: "John",
  age: 30,
  isAdmin: true
};

for(let key in user) {
  // keys
  alert( key );  // name, age, isAdmin
  // 属性键的值
  alert( user[key] ); // John, 30, true
}
```
注意，所有的 “for” 都允许我们在循环中定义变量，像 let key 这样。

同样，我们可以用其他属性名来代替 key。例如 "for(let prop in obj)" 也很常用。

### 属性排序
如果我们遍历一个对象，我们会按照赋值属性的顺序来获得属性吗？
简短的回答是：”有特别的顺序“：整数属性有顺序，其他是按照创建的顺序

## 对象是引用类型

变量存储的不是对象本身，而是对象的“内存地址”，是对象的引用。

当对象被赋值给其他变量时，引用被复制了一份, 对象并没有被复制。

此时，相当于有两个变量，但只有一个对象，这两个变量存储了这个对象的引用（内存地址）。

## 引用比较

等号 == 和严格等 === 对于对象来说没差别。

当两个引用指向同一个对象的时候他们相等。

例如，两个引用指向同一个对象，他们相等：
```
 let a = {};
let b = a; // 复制引用

alert( a == b ); // true，两个变量指向同一个对象
alert( a === b ); // true
```
如果是两个不同的属性，他们就不相等，即使都是空的。
```
 let a = {};
let b = {}; // 两个独立的对象

alert( a == b ); // false
```
如果比较两个对象 obj1 > obj2 或者用一个对象比较原始值 obj == 5，对象被转换成原始值。


## 常量对象

```
const user= {
  name: "John"
};

user.age = 25; // (*)

alert(user.age); // 25
```

这里的常量是指user存储的值不可改（也就是不能指向新的对象），但对象本身的状态可以修改。

## 对象的复制与合并，Object.assign
复制一个对象的变量也等同于创建了此对象的另一个引用。

那么我们该怎么复制一个对象呢？创建一份独立的拷贝，一份复制？

这也是可行的，但是有一点麻烦，因为JS并没有原生的方法支持这么做。实际上，我们很少这么做。复制引用很多时候是好用的。

如果我们真的想这么做，就需要创建一个新的对象，遍历现有对象的属性，在原始值的状态下复制给新的对象。

像这样：
```
let user = {
  name: "John",
  age: 30
};

let clone = {}; // 新的空对象

// 复制所有的属性值
for (let key in user) {
  clone[key] = user[key];
}

// 现在复制是独立的复制了
clone.name = "Pete"; // 改变它的值

alert( user.name ); // 原对象属性值不变
```
我们也可以用Object.assign 来实现。

语法是：
```
Object.assign(dest[, src1, src2, src3...])
```
参数 dest 和 src1, ..., srcN（可以有很多个）是对象。
这个方法复制了 src1, ..., srcN 的所有对象到 dest。换句话说，从第二个参数开始，所有对象的属性都复制给了第一个参数对象，然后返回 dest。
例如，我们可以用这个方法来把几个对象合并成一个：
```
let user = { name: "John" };

let permissions1 = { canView: true };
let permissions2 = { canEdit: true };

// 把 permissions1 和 permissions2 的所有属性都拷贝给 user
Object.assign(user, permissions1, permissions2);

// 现在 user = { name: "John", canView: true, canEdit: true }
如果接收的对象（user）已经有了同样属性名的属性，前面的会被覆盖：

let user = { name: "John" };

// 覆盖 name，增加 isAdmin
Object.assign(user, { name: "Pete", isAdmin: true });

// 现在 user = { name: "Pete", isAdmin: true }
```

我们可以用 Object.assign 来代理简单的复制方法：
```
let user = {
  name: "John",
  age: 30
};

let clone = Object.assign({}, user);
```
它复制了 user 对象所有的属性给了一个空对象，然后返回拷贝后的对象。事实上，这跟循环赋值一样，但是更短。

直到现在，我们是假设所有的 user 属性都是原始值，但是如果对象属性指向对象呢？

像这样：
```
 let user = {
  name: "John",
  sizes: {
    height: 182,
    width: 50
  }
};

alert( user.sizes.height ); // 182
```
现在，并不能拷贝 clone.sizes = user.sizes，因为 user.sizes 是一个对象，它按引用拷贝。所以 clone 和 user 共享了一个对象。

像这样：
```
 let user = {
  name: "John",
  sizes: {
    height: 182,
    width: 50
  }
};

let clone = Object.assign({}, user);

alert( user.sizes === clone.sizes ); // true，同一个对象

// user 和 clone 共享 sizes 对象
user.sizes.width++;       // 在这里改变一个属性的值
alert(clone.sizes.width); // 51，在这里查看属性的值
```
为了解决上面的的问题，我们在复制的时候应该检查 user[key] 的每一个值，如果是一个对象，我们再复制一遍这个对象，这叫做深拷贝。

有一个标准的深拷贝算法，解决上面和一些更复杂的情况，叫做 Structured cloning algorithm。为了不重复造轮子，我们使用它的一个 JS 实现的库 lodash, 方法名叫做 _.cloneDeep(obj)。