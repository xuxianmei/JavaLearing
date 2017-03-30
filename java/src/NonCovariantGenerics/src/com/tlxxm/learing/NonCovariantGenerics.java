package com.tlxxm.learing;

import java.util.*;

class Fruit{}
class Apple extends Fruit{}
class Jonathan extends Apple{}
class Orange extends Fruit{}
public class NonCovariantGenerics {

    public static void main(String[] args) {
        List<Fruit> fruitList=new ArrayList<Apple>();

    }
}
