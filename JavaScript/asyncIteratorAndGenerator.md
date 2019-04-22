## Async iterators and generators

根据需要，异步迭代器可以迭代异步传输过来的数据。

比如，我们下载一个东西时分块下载，多次完成，或者只是期望事件异步地出现并希望在它们上面迭代时，异步迭代器和生成器可能会派上用场。

先看一个简单的例子来了解语法，再看一个真实场景的例子。

## Asynchronous iterators

异步迭代器与普通的迭代器差不多，在语法上就有一点点区别。

普通的迭代器像这样：
```
let range = {
  from: 1,
  to: 5,

  // for..of calls this method once in the very beginning
  [Symbol.iterator]() {
    // ...it returns the iterator object:
    // onward, for..of works only with that object, asking it for next values
    return {
      current: this.from,
      last: this.to,

      // next() is called on each iteration by the for..of loop
      next() { // (2)
        // it should return the value as an object {done:.., value :...}
        if (this.current <= this.last) {
          return { done: false, value: this.current++ };
        } else {
          return { done: true };
        }
      }
    };
  }
};

for(let value of range) {
  alert(value); // 1 then 2, then 3, then 4, then 5
}
```

为了构造一个异步迭代器：
1. 使用Symbol.asyncIterator替代[Symbol.iterator]
2. next()必须返回一个promise
3. 为了迭代这种对象，必须使用``for await (let item of iterable)``循环。

```
let range = {
  from: 1,
  to: 5,

  // for await..of calls this method once in the very beginning
  [Symbol.asyncIterator]() { // (1)
    // ...it returns the iterator object:
    // onward, for await..of works only with that object, asking it for next values
    return {
      current: this.from,
      last: this.to,

      // next() is called on each iteration by the for..of loop
      async next() { // (2)
        // it should return the value as an object {done:.., value :...}
        // (automatically wrapped into a promise by async)

        // can use await inside, do async stuff:
        await new Promise(resolve => setTimeout(resolve, 1000)); // (3)

        if (this.current <= this.last) {
          return { done: false, value: this.current++ };
        } else {
          return { done: true };
        }
      }
    };
  }
};

(async () => {

  for await (let value of range) { // (4)
    alert(value); // 1,2,3,4,5
  }

})()
```

As we can see, the components are similar to regular iterators:

1. To make an object asynchronously iterable, it must have a method Symbol.asyncIterator (1).
2. It must return the object with next() method returning a promise (2).
3. The next() method doesn’t have to be async, it may be a regular method returning a promise, but async allows to use await inside. Here we just delay for a second (3).
4. To iterate, we use for await(let value of range) (4), namely add “await” after “for”. It calls `range[Symbol.asyncIterator]()` once, and then its next() for values.

Here’s a small cheatsheet:

Iterators|	Async iterators
---|---
Object method to provide iteraterable|Symbol.iterator|Symbol.asyncIterator
next() return value is|any value|Promise
to loop, use|	for..of	|for await..of


## Async generators

Javascript also provides generators, that are also iterable.

Let’s recall a sequence generator from the chapter Generators. It generates a sequence of values from start to end (could be anything else):
```
 function* generateSequence(start, end) {
  for (let i = start; i <= end; i++) {
    yield i;
  }
}

for(let value of generateSequence(1, 5)) {
  alert(value); // 1, then 2, then 3, then 4, then 5
}
```

Normally, we can’t use await in generators. All values must come synchronously: there’s no place for delay in for..of.

But what if we need to use await in the generator body? To perform network requests, for instance.

No problem, just prepend it with async, like this:

```
async function* generateSequence(start, end) {

for (let i = start; i <= end; i++) {

    // yay, can use await!
    await new Promise(resolve => setTimeout(resolve, 1000));

    yield i;
  }

}

(async () => {

  let generator = generateSequence(1, 5);
  for await (let value of generator) {
    alert(value); // 1, then 2, then 3, then 4, then 5
  }

})();
```

Now we have an the async generator, iteratable with for await...of.

It’s indeed very simple. We add the async keyword, and the generator now can use await inside of it, rely on promises and other async functions.

Technically, another the difference of an async generator is that its generator.next() method is now asynchronous also, it returns promises.

Instead of result = generator.next() for a regular, non-async generator, values can be obtained like this:
```
result = await generator.next(); // result = {value: ..., done: true/false}
```


## Iterables via async generators

When we’d like to make an object iterable, we should add Symbol.iterator to it.
```

let range = {
  from: 1,
  to: 5,
  [Symbol.iterator]() { ...return object with next to make range iterable...  }
}
```
A common practice for Symbol.iterator is to return a generator, rather than a plain object with next as in the example before.

Let’s recall an example from the chapter Generators:
```
 let range = {
  from: 1,
  to: 5,

  *[Symbol.iterator]() { // a shorthand for [Symbol.iterator]: function*()
    for(let value = this.from; value <= this.to; value++) {
      yield value;
    }
  }
};

for(let value of range) {
  alert(value); // 1, then 2, then 3, then 4, then 5
}
```
Here a custom object range is iterable, and the generator *[Symbol.iterator] implements the logic for listing values.

If we’d like to add async actions into the generator, then we should replace Symbol.iterator with async Symbol.asyncIterator:
```
let range = {
  from: 1,
  to: 5,

  async *[Symbol.asyncIterator]() { // same as [Symbol.asyncIterator]: async function*()
    for(let value = this.from; value <= this.to; value++) {

      // make a pause between values, wait for something
      await new Promise(resolve => setTimeout(resolve, 1000));

      yield value;
    }
  }
};

(async () => {

  for await (let value of range) {
    alert(value); // 1, then 2, then 3, then 4, then 5
  }

})();
```
Now values come with a delay of 1 second between them.