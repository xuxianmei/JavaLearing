package com.tlxxm.learing;

import java.util.*;
class Fruit{}
class Apple extends Fruit{}
class Jonathan extends Apple{}
class Orange extends Fruit{}
public class GenericAndCovaricance {

    public static void main(String[] args) {
	    List<? extends Fruit> flist=new ArrayList<Apple>();
	    //不能添加任何类的对象
//        flist.add(new Apple());
//        flist.add(new Fruit());
//        flist.add(new Object());
        flist.add(null);//合法，但是没有意义
        Fruit f=flist.get(0);//至少知道它的返回类型是Fruit。
    }
}
