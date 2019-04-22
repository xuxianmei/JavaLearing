## Modeules介绍
随着我们程序的变大，我们需要将其拆分为多个文件，也称为模块。

一个模块通常包含一个类或一个包含有用函数的库。

在很长的一段时间里，JavaScript在语言级别上，不存在模块语法。

社区创建了一结方法来组织代码到模块中去。

比如:
* AMD
* CommonJS
* UMD

不过，现在这些正在慢慢成为历史。
在2015的标准上，出现了语言级别的语法，现在基本上主流的服务器和Node.js都支持。

## 什么是模块

一个模块就是一个文件，一个单独的脚本，就这么简单。

通过export和import可以在模块之前交换功能。

* export
导出变量与函数，允许在文件外访问。

* import
允许从别的模块导入功能。

比如，如果我们有一个文件saiHi.js导出一个函数：
```
//saiHi.js
export function sayHi(user){
	alert(`Hello ${user}!`);	
} 
```

另外一个文件可以导入并使用它：

```
//main.js
import {sayHi} from `./sayHi.js`

alert(sayHi);//function
sayHi("John");//Hello John!
```

在上面我们只集中于语言本身，但是我们也可以使用浏览器作为demo环境。

```
//saiHi.js
export function sayHi(user){
	alert(`Hello ${user}!`);	
} 
```
html中使用：
```
index.html
<!doctype html>
<script type="module">
  import {sayHi} from './say.js';

  document.body.innerHTML = sayHi('John');
</script>
```
浏览器自动取和计算导入，然后运行脚本。

## 核心模块特性

和普通的脚本相比，在模块中有哪些不同点？

有一些核心特性对于浏览器和服务端js都有效。

### Always "use strict"

模块始终使用 "use strict"。
比如，赋值给一个未定义的变量会发生错误。

```
<script type="module">
a=5;//error
</script>
```

### 模块级作用域

每个模块有它自己的顶级作用域。
换句话说，一个模块内的顶级的变量和函数在其他模块不可见。

在下面的例子中，两个脚本被导入，hello.js试图使用在user.js中声明的user变量会失败。

hello.js：
```
alert(user); // no such variable (each module has independent variables)
```
user.js：
```
let user = "John";
```
index.html：
```
<!doctype html>
<script type="module" src="user.js"></script>
<script type="module" src="hello.js"></script>
```

如果想要访问一个外部变量，需要模块先export，再在另外一个模块内import。

在浏览器中，对于每个`<script type="module">`也有彼此独立的顶级作用域：

```
<script type="module">
  // The variable is only visible in this module script
  let user = "John";
</script>

<script type="module">
  alert(user); // Error: user is not defined
</script>
```

如果在浏览器环境中，想要声明一个全局变量，可以声明一个window属性。
比如window.user。


### 一个模块的代码，只有在第一次被导入时，才会执行。

如果同一个模块在多个位置多次导入，它的代码只会在第一次导入的地方执行，
然后export会给到所有导入者。

这有重要的影响，我们在下面这个例子看。

首先，如果执行一个模块的代码会带来"副作用"，比如显示一个消息，
那么导入这个模块多次，也只会在第一次导入时执行一次：

```
//  alert.js
alert("Module is evaluated!");
```

```
// Import the same module from different files

//  1.js
import `./alert.js`; // Module is evaluated!

//  2.js
import `./alert.js`; // (nothing)
```

实际应用中，顶级作用域的代码主要用来初始化。
我们创建一些数据结构，并提前填充它们，如果我们想要某些可以重用，可以使用export导出。

让我们看一下，一个模块export一个对象：
```
// 📁 admin.js
export let admin = {
  name: "John"
};
```

所有的导入者共享这一个外部导入的对象。

比如1.js和2.js两个模块文件都导入了这个对象：
```
// 📁 1.js
import {admin} from './admin.js';
admin.name = "Pete";

// 📁 2.js
import {admin} from './admin.js';
alert(admin.name); // Pete

// Both 1.js and 2.js imported the same object
// Changes made in 1.js are visible in 2.js
```

模块只有第一次导入执行一次，export的对象或函数被生成了，并且他们在导入者之间共享。
如果admin对象发生改变，在其他模块也可见这个改变。

这种行为对于那些需要配置的模块是很棒的。
我们可以在第一次导入中设置必须的属性，然后在其他导入处，这就已经准备好了。

比如：

```
// 📁 admin.js
export let admin = { };

export function sayHi() {
  alert(`Ready to serve, ${admin.name}!`);
```

```
// 📁 init.js
import {admin} from './admin.js';
admin.name = "Pete";
```

```
// 📁 other.js
import {admin, sayHi} from './admin.js';

alert(admin.name); // Pete

sayHi(); // Ready to serve, Pete!
```

### import.meta

import.meta对象包含了当前模块的信息。

它的内容依赖于运行环境。在浏览器中，它包含了脚本的url，或者当前html页面的url。

<script type="module">
  alert(import.meta.url); // script url (url of the html page for an inline script)
</script>

### Top-level "this" is undefined

That’s kind of a minor feature, but for completeness we should mention it.

In a module, top-level this is undefined, as opposed to a global object in non-module scripts:
```

 <script>
  alert(this); // window
</script>

<script type="module">
  alert(this); // undefined
</script>
```

## 基于浏览器的特性

当模块脚本`<script type="modeule">`运行在浏览器中，相对于普通脚本，有一些基于浏览器的特性。

### 模块脚本都是延迟的

模块脚本都是延迟加载的（外部或inline script），与defer特性相同。

换句话说：

* 外部模块脚本`<script type="module" src="…“>`不阻塞HTML处理。

* 等到HTML文档完全准备好，模块脚本才会加载。

* 保持相对顺序:首先在文档中执行的脚本，会首先执行。

作为一个副作用，模块脚本总是能访问它们下面出现的HTML元素。

比如：

```
<script type="module">
  alert(typeof button); // object: the script can 'see' the button below
  // as modules are deferred, the script runs after the whole page is loaded
</script>

<script>
  alert(typeof button); // Error: button is undefined, the script can't see elements below
  // regular scripts run immediately, before the rest of the page is processed
</script>

<button id="button">Button</button>
```

注意：第二个脚本实际上比第一个脚本先运行，所以我们首先会看到undefined，然后才是object。

这是因为模块脚本全是延迟加载的，直到文档加载完成，而普通脚本，会立即执行。

在使用模块时，我们应该注意HTML-document可以在Javascript应用程序准备好之前显示出来。有些功能可能还不能工作。所以需要给用户一些提示，比如加载中等。

### Async works on inline scripts

Async attribute `<script async type="module">` is allowed on both inline and external scripts. Async scripts run immediately when imported modules are processed, independantly of other scripts or the HTML document.

For example, the script below has async, so it doesn’t wait for anyone.

It performs the import (fetches ./analytics.js) and runs when ready, even if HTML document is not finished yet, or if other scripts are still pending.

That’s good for functionality that doesn’t depend on anything, like counters, ads, document-level event listeners.
```
 <!-- all dependencies are fetched (analytics.js), and the script runs -->
<!-- doesn't wait for the document or other <script> tags -->
<script async type="module">
  import {counter} from './analytics.js';

  counter.count();
</script>
```

### External scripts

对于外部模块脚本，有两个值得注意的不同点。

1. External scripts with same src run only once
```
<!-- the script my.js is fetched and executed only once -->
<script type="module" src="my.js"></script>
<script type="module" src="my.js"></script>
```
2. External scripts that are fetched from another domain require CORS headers. 
In other words, if a module script is fetched from another domain, the remote server must supply a header Access-Control-Allow-Origin: * (may use fetching domain instead of *) to indicate that the fetch is allowed.
```
<!-- another-site.com must supply Access-Control-Allow-Origin -->
<!-- otherwise, the script won't execute -->
<script type="module" src="http://another-site.com/their.js"></script>
```

### No bare modules allowed

In the browser, in scripts (not in HTML), import must get either a relative or absolute URL. So-called “bare” modules, without a path, are not allowed.

For instance, this import is invalid:
```
import {sayHi} from 'sayHi'; // Error, "bare" module
// must be './sayHi.js' or wherever the module is
```
### Compatibility, “nomodule”
Old browsers do not understand type="module". Scripts of the unknown type are just ignored. For them, it’s possible to provide a fallback using nomodule attribute:
```
 <script type="module">
  alert("Runs in modern browsers");
</script>
```
```
<script nomodule>
  alert("Modern browsers know both type=module and nomodule, so skip this")
  alert("Old browsers ignore script with unknown type=module, but execute this.");
</script>
```
If we use bundle tools, then as modules are bundled together, their import/export statements are replaced by special bundler calls, so the resulting build does not require type="module", and we can put it into a regular script:
```
<!-- Assuming we got bundle.js from a tool like Webpack -->
<script src="bundle.js"></script>
```

## Build tools

In real-life, browser modules are rarely used in their “raw” form. Usually, we bundle them together with a special tool such as Webpack and deploy to the production server.

One of the benefits of using bundlers – they give more control over how modules are resolved, allowing bare modules and much more, like CSS/HTML modules.

Build tools do the following:

1. Take a “main” module, the one intended to be put in <script type="module"> in HTML.
2. Analyze its dependencies: imports and then imports of imports etc.
3. Build a single file with all modules (or multiple files, that’s tunable), replacing native import calls with bundler functions, so that it works. “Special” module types like HTML/CSS modules are also supported.
4. In the process, other transforms and optimizations may be applied:
	* Unreachable code removed.
	* Unused exports removed (“tree-shaking”).
	* Development-specific statements like console and debugger removed.
	* Modern, bleeding-edge Javascript syntax may be transformed to older one with similar functionality using Babel.
	* The resulting file is minified (spaces removed, variables replaced with shorter named etc).

That said, native modules are also usable. So we won’t be using Webpack here: you can configure it later.