package com.tlxxm.learing;

public class Main {

    public static void main(String[] args) {
	    new Person().eat(new Apple());
    }
}
//人
class Person{
    public void eat(Apple apple){
        Apple peeled=apple.getPeeled();
        System.out.println("yummy");
    }
}
//削皮器
class Peeler{
    static Apple peel(Apple apple){
        return apple;
    }
}
//苹果
class Apple{
    Apple getPeeled(){
        return Peeler.peel(this);
    }
}
