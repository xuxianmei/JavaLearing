package com.tlxxm.learing;

public class AnonymousConstructor {

    public static Base getBase(int i){
        return new Base(i) {
            {
                //成员变量的初始化在此进行
                System.out.println("匿名类实例初始化");
                if(1>0){
                    System.out.println("匿名类测试if");
                }
            }
            @Override
            public void f() {
                System.out.println("In anonymous f()");
            }
        };
    }

    public static void main(String[] args) {
         Base b=getBase(1);
    }
}

abstract class Base{
    public Base(int i){
        System.out.println("Base contructor . i="+i);
    }
    public abstract void f();
}

