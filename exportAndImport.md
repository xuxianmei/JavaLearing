## Export and Import

Export与import指令非常的多才多艺。

前面的小节，简单的展示了一些。
现在来研究一下更多的例子。

## 在声明语句之前的export

我们可以使用export来对任何声明来进行标记，无论是变量、函数还是类。

比如，下面这些export都是有效的：

```
// export an array
export let months = ['Jan', 'Feb', 'Mar','Apr', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

// export a constant
export const MODULES_BECAME_STANDARD_YEAR = 2015;

// export a class
export class User {
  constructor(name) {
    this.name = name;
  }
}
```

>注意：这里export class, export function后面没有分号。

## 从声明中export一部分

我们可以单独使用export
这里，我们首先声明，然后再export：
```
// 📁 say.js
function sayHi(user) {
  alert(`Hello, ${user}!`);
}

function sayBye(user) {
  alert(`Bye, ${user}!`);
}

export {sayHi, sayBye}; // a list of exported variables
```

## import *

通过，我们我们将一个列表放入到```import {...}```中，像这样：

```
// 📁 main.js
import {sayHi, sayBye} from './say.js';

sayHi('John'); // Hello, John!
sayBye('John'); // Bye, John!
```

但是，如果这个列表很长，我们可以使用`import * as <obj>`导入所有来作为一个对象。

例如：
```
// 📁 main.js
import * as say from './say.js';

say.sayHi('John');
say.sayBye('John');
```



这样使用的原因：

Modern build tools (webpack and others) bundle modules together and optimize them to speedup loading and remove unused stuff.

Let’s say, we added a 3rd-party library lib.js to our project with many functions:
```
// 📁 lib.js
export function sayHi() { ... }
export function sayBye() { ... }
export function becomeSilent() { ... }
```
Now if we in fact need only one of them in our project:
```
// 📁 main.js
import {sayHi} from './lib.js';
````
…Then the optimizer will automatically detect it and totally remove the other functions from the bundled code, thus making the build smaller. That is called “tree-shaking”.

Explicitly listing what to import gives shorter names: sayHi() instead of lib.sayHi().

Explicit imports give better overview of the code structure: what is used and where. It makes code support and refactoring easier.

## import as

相当于取别名。
We can also use as to import under different names.

For instance, let’s import sayHi into the local variable hi for brevity, and same for sayBye:


```
// 📁 main.js
import {sayHi as hi, sayBye as bye} from './say.js';

hi('John'); // Hello, John!
bye('John'); // Bye, John!
```

### Export “as”

The similar syntax exists for export.

Let’s export functions as hi and bye:
```
// 📁 say.js
...
export {sayHi as hi, sayBye as bye};
```

Now hi and bye are official names for outsiders:

```
// 📁 main.js
import * as say from './say.js';

say.hi('John'); // Hello, John!
say.bye('John'); // Bye, John!
```

## export default

## “Default” alias
