package com.tlxxm.learing;

public class Main {

    public static void main(String[] args) {
        Class intClass=int.class;
        Class<Integer> genericIntClass=int.class;
        intClass=double.class;
        //genericIntClass=double.class; 非法
        Class<?> intClass2=int.class;
        intClass2=double.class;
        Class<? extends Number> bounded=int.class;
        bounded=double.class;
        bounded=Number.class;

        Building b=new House();
        Class<House> houseType=House.class;
        House h= houseType.cast(b);
        houseType.isInstance(b);
        h=(House)h;
        // write your code here
    }
}
class Building{}
class House extends Building{}
