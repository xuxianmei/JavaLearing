package com.tlxxm.learing;

public class Cartoon extends Drawing {
    Cartoon(){
        System.out.println("Cartoon constructor");
    }
    public static void main(String[] args) {
	 Cartoon cartoon=new Cartoon();
    }
}
class Art{
    Art(){
        System.out.println("Art constructor");
    }
}
class Drawing extends Art{
    Drawing(){
        System.out.println("Drawing constructor");
    }
}