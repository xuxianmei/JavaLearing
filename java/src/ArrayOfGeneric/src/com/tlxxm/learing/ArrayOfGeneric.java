package com.tlxxm.learing;

class Generic<T>{

}
public class ArrayOfGeneric {
    static final int SIZE=100;
    static Generic<Integer>[] gia;
    public static void main(String[] args) {
        //gia=(Generic<Integer>[]) new Object[SIZE]; 运行时错误：[Ljava.lang.Object; cannot be cast to [Lcom.tlxxm.learing.Generic
        gia=(Generic<Integer>[]) new Generic[SIZE];

        System.out.println(gia.getClass().getSimpleName());

        gia[0]=new Generic<Integer>();

        //gia[1]=new Object(); 编译时错误
        //gia[2]=new Generic<Double>(); 编译时错误
    }
}

