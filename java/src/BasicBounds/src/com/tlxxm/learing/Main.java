package com.tlxxm.learing;

interface HasColor{
    java.awt.Color getColor();
}
class Colored<T extends HasColor>{
    T item;
    Colored(T item){
        this.item=item;
    }
    T getItem(){
        return item;
    }
    java.awt.Color color(){
        return item.getColor();// extends HasColor 边界允许这个方法的调用。
    }
}

public class Main {

    public static void main(String[] args) {
	// write your code here
    }
}
