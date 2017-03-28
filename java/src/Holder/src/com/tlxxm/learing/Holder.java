package com.tlxxm.learing;

class Man{}
public class Holder<T> {
    private T a;
    public  Holder(T t){
        a=t;
    }
    public void set(T t){
        a=t;
    }
    public T get(){
        return a;
    }

    public static void main(String[] args) {
	    Holder<Man> holder=new Holder(new Man());
	    Man man=holder.get();
    }
}
