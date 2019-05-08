# 集合



## 是什么使类成为集合：IEnumerable<T>

根据定义，.NET中的集合本质上是一个类，它最起码实现了`IEnumerable<T>`(或非泛型类型IEnumerable）。

只要想支持对集合执行的遍历操作，最低的要求就是实现`IEnumerable<T>`接口。

`IEnumerable<T>`接口声明了一个方法：
````
IEnumerator<T> GetEnumerator();
````


## 集合的遍历

集合的遍历有两种，
### 基于**``[index](索引)``**

一般是有固定大小的集合，遍历时，需要进行越界检查

### 基于**iterator(迭代器)**

迭代器模式提供了这个能力，只要能确定第一个元素 、下一个元素和最后一个元素，

就不需要事先知道元素的总数，也不需要按照索引获取元素

### ``IEnumerator``与``IEnumerator<T>``

* ``IEnumerator``

	包含三个成员
````
public interface IEnumerator
{
   
    bool MoveNext();

    Object Current {get; }

    void Reset();
}
````
* ``IEnumerator<T>``

	同样是三个成员，但重新定义了支持泛型的Current
````
public interface IEnumerator<out T> : IDisposable, IEnumerator
{    
   
    new T Current {
        get; 
    }
}
````

集合在foreach遍历的时候，不允许修改，因为会影响迭代器的状态。


### 状态的共享

假如同时有两个循环交错的遍历同一个集合（比如，嵌套foreach，或者多线程），

那么集合必须维持当前元素的一个状态指示器，确定当调用MoveNext()时，能正确定位下一个元素。


为了解决这个问题，集合不直接支持（不继承实现）``IEnumerator<T>``和`IEnumerator`，

前面提到``IEnumerable<T>``有惟一的方法`GetEnumerator()`。

这个方法的作用是返回实现了`IEnumerator<T>`类型的一个对象（每次调用，都返回一个新的，不共用同一个）。

所以，这个集合的状态是由另外一个类来支持`IEnumerator<T>`接口，并负责维护遍历的状态。

迭代器相当于游标，可以有多个游标，来支持多个遍历。



### 清理状态

``IEnumerator<T>``支持了 IDisposable接口


### foreach循环内不能修改集合

如果在foreach循环内修改集合，比如删除一个，或者增加一个，或者修改集合项本身。

这会影响到集合迭代器的状态，迭代器不知道应该接受这个影响，还是忽略这个影响。

所以C#编译器禁止了这种形为。

但在for循环中，基于索引访问的，可以修改集合。


### 没有IEnumerable的foreach

C#编译器不要求一定实现`IEnumerable`或`IEnumerable<T>`才能用foreach对一个数据类型进行迭代。

相反，编译器采用一个称为Duck typing的概念，也就是查找一个GetEnumerator()方法，

这个方法返回包含Current属性和MoveNext()方法的类型。


## 更多的集合接口

* ``ICompare<T>``
	
	定义了比较方法 
````
public interface IComparer<in T>
{
   
    int Compare(T x, T y);
}
````

* ``ICollection<T>``
	
	继承了``IEnumerable<T>``,提供一个Count属性，和一些方法。
````
public interface ICollection<T> : IEnumerable<T>
{
    // Number of items in the collections.        
    int Count { get; }

    bool IsReadOnly { get; }

    void Add(T item);

    void Clear();

    bool Contains(T item); 

    void CopyTo(T[] array, int arrayIndex);

    bool Remove(T item);
}
````
* ``IList<T>``
	
	继承了``ICollection<T>``，提供了索引器
````
public interface IList<T> : ICollection<T>
{
    // The Item property provides methods to read and edit entries in the List.
    T this[int index] {
        get;
        set;
    }

    // Returns the index of a particular item, if it is in the list.
    // Returns -1 if the item isn't in the list.
    int IndexOf(T item);

    // Inserts value into the list at position index.
    // index must be non-negative and less than or equal to the 
    // number of elements in the list.  If index equals the number
    // of items in the list, then value is appended to the end.
    void Insert(int index, T item);
    
    // Removes the item at position index.
    void RemoveAt(int index);
}
````
* ``IDictionary<TKey, TValue>``

	继承了``ICollection<T>``，提供了索引器，但是索引器使用的是key而不是整数。
````
public interface IDictionary<TKey, TValue> : ICollection<KeyValuePair<TKey, TValue>>
{
    // Interfaces are not serializable
    // The Item property provides methods to read and edit entries 
    // in the Dictionary.
    TValue this[TKey key] {
        get;
        set;
    }

    // Returns a collections of the keys in this dictionary.
    ICollection<TKey> Keys {
        get;
    }

    // Returns a collections of the values in this dictionary.
    ICollection<TValue> Values {
        get;
    }

    // Returns whether this dictionary contains a particular key.
    //
    bool ContainsKey(TKey key);

    // Adds a key-value pair to the dictionary.
    // 
    void Add(TKey key, TValue value);

    // Removes a particular key from the dictionary.
    //
    bool Remove(TKey key);

    bool TryGetValue(TKey key, out TValue value);
}
````

## 索引器(有参属性)
`[ ]`

## 迭代器

迭代器提供了迭代器接口（也就是``IEnumerable<T>``和``IEnumerator<T>``接口的组合）的一个快捷实现。

````
public class Strings : IEnumerable<Int32>
{
    public  IEnumerator<Int32> GetEnumerator()
    {
        yield return 1;
        yield return 2;
        yield return 3;
        yield return 4;
        yield return 5;
        yield return 6;
    }
     System.Collections.IEnumerator
        System.Collections.IEnumerable.GetEnumerator()
    {
        return GetEnumerator();
    }
   
}
````

通过使用yield，编译器会自动为这个类，生成一个实现了`IEnumerator<T>`的类，来管理集合的状态。