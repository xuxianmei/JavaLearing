## 静态属性与方法

我们也可以将一个函数赋值到class函数上，而不是原型上。
这样的方法是静态的。

比如：
```
class User {
  static staticMethod() {
    alert(this === User);
  }
}

User.staticMethod(); // true
```
实际上是下面这样设置一个函数属性值。

```
function User() { }

User.staticMethod = function() {
  alert(this === User);
};
```
User.staticMethod函数内的this是class constructor User 它本身（在.号之前的对象）。


通常，静态方法用来实现属于类的函数，而不是类创建的所有对象。

比如，有一个Article对象，需要一个函数来进行比较。

很自然的会用Article.compare，像这样：
```
class Article {
  constructor(title, date) {
    this.title = title;
    this.date = date;
  }

  static compare(articleA, articleB) {
    return articleA.date - articleB.date;
  }
}

// usage
let articles = [
  new Article("Mind", new Date(2019, 1, 1)),
  new Article("Body", new Date(2019, 0, 1)),
  new Article("JavaScript", new Date(2019, 11, 1))
];

articles.sort(Article.compare);

alert( articles[0].title ); // Body
```

这里的Article.compare是整个类的方法，而不是某一些对象的方法。

另一个情况是叫工厂的方法，
想像一下，我们需要一些方式来创建一个articel：

1. 通过给定的tile,date等参数来创建
2. 根据today's date创建一个空对象
3. 等等

第一种方式，可以通过构造函数实现。
第二种方式，我们可以为其在类中创建一个静态方法

比如这里的Article.createTodays()：

```
class Article {
  constructor(title, date) {
    this.title = title;
    this.date = date;
  }

  static createTodays() {
    // remember, this = Article
    return new this("Today's digest", new Date());
  }
}

let article = Article.createTodays();

alert( article.title ); // Todays digest
```

现在，每次创建一个基于today's date的对象，我们都可以调用Article.createTodays()来完成。

再次说明，它不是对象的方法，而是整个类Article的一个方法。




静态方法也用于在数据库相关的类中，来对数据库中的实体，进行search/save/remove。
像这样：
```
// assuming Article is a special class for managing articles
// static method to remove the article:
Article.remove({id: 12345});
```

### 静态属性

>A recent addition
This is a recent addition to the language. Examples work in the recent Chrome.

Static properties are also possible, just like regular class properties:
```
 class Article {
  static publisher = "Ilya Kantor";
}

alert( Article.publisher ); // Ilya Kantor
That is the same as a direct assignment to Article:

Article.publisher = "Ilya Kantor";
```


## 静态与继承

静态也支持继承，我们可以通过Child.method访问Parent.method。

比如，在下面的代码中Animal.compare继承自Rabbit.compare，访问Animal.compare就相当于
访问了Rabbit.compare
```
class Animal {

  constructor(name, speed) {
    this.speed = speed;
    this.name = name;
  }

  run(speed = 0) {
    this.speed += speed;
    alert(`${this.name} runs with speed ${this.speed}.`);
  }

  static compare(animalA, animalB) {
    return animalA.speed - animalB.speed;
  }

}

// Inherit from Animal
class Rabbit extends Animal {
  hide() {
    alert(`${this.name} hides!`);
  }
}

let rabbits = [
  new Rabbit("White Rabbit", 10),
  new Rabbit("Black Rabbit", 5)
];

rabbits.sort(Rabbit.compare);

rabbits[0].run(); // Black Rabbit runs with speed 5.
```

它是如何工作的呢？还是使用原型。
extends也将Rabbit的原型指向了Animal。

![](images/animal-rabbit-static.png)

所以，现在Rabbit 函数继承自Animal函数。
Animal函数的[[Prototype]]引用指向Function.prototype。
因为Animal没有继承其它。

让我们来检查一下它：

```
class Animal {}
class Rabbit extends Animal {}

// for static properties and methods
alert(Rabbit.__proto__ === Animal); // true

// and the next step is Function.prototype
alert(Animal.__proto__ === Function.prototype); // true

// that's in addition to the "normal" prototype chain for object methods
alert(Rabbit.prototype.__proto__ === Animal.prototype);
```
