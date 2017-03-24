package com.tlxxm.learing;

public class Callbacks {

    public static void main(String[] args) {
	    Callee1 c1=new Callee1();
	    Callee2 c2=new Callee2();

	    MyIncrement.f(c2);

	    System.out.println();
        Caller caller1=new Caller(c1);
        Caller caller2=new Caller(c2.getCallbackReference());

        caller1.go();
        System.out.println();
        caller1.go();
        System.out.println();
        caller2.go();
        System.out.println();
        caller2.go();
    }
}
interface Incrementable{
    void increment();
}
//接口的简单常规实现
class Callee1 implements Incrementable{
    private int i=0;
    public void increment(){
        i++;
        System.out.println(i);
    }
}

class MyIncrement{
    public void increment(){
        System.out.println("Other operation");
    }
    static void f(MyIncrement mi){
        mi.increment();
    }
}

class Callee2 extends MyIncrement{
    private int i=0;
    public void increment(){
        super.increment();
        i++;
        System.out.println(i);
    }
    private class Colsure implements Incrementable{
        public void increment(){
            //这里需要明确，调用的是外围类对象的方法，或者形成 无限递归。
            Callee2.this.increment();
        }
    }
    Incrementable getCallbackReference(){
        return  new Colsure();
    }
}

class Caller{
    private Incrementable callbackReference;
    Caller(Incrementable cbh){
        callbackReference=cbh;
    }
    void go(){
        callbackReference.increment();
    }
}
