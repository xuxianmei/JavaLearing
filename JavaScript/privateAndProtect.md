## 私有与保护属性和方法

OOP规则之一，封装 隐藏细节。
### protect

一般使用_开头，但不是技术上的实现，只是约定上的实现。
比如：

```
class CoffeeMachine {
  _waterAmount = 0;

  set waterAmount(value) {
    if (value < 0) throw new Error("Negative water");
    this._waterAmount = value;
  }

  get waterAmount() {
    return this._waterAmount;
  }

  constructor(power) {
    this._power = power;
  }

}

// create the coffee machine
let coffeeMachine = new CoffeeMachine(100);

// add water
coffeeMachine.waterAmount = -10; // Error: Negative water
```

### private

在JavaScript还不支持
