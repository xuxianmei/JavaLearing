package com.tlxxm.learing;

import java.util.ArrayList;
import java.util.List;

class Fruit{}
class Apple extends Fruit{}
class Jonathan extends Apple{}
class Orange extends Fruit{}
public class CovariantArrays {

    public static void main(String[] args) {
        List<Fruit> flist=new ArrayList<Apple>();

        flist.add(new Fruit());
    }
}
