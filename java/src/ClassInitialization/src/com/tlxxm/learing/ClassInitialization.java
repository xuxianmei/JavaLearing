package com.tlxxm.learing;

import java.util.Random;
class Initable{
    static final int staticFinal=47;
    static final int staticFinal2=ClassInitialization.rand.nextInt(1000);
    static {
        System.out.println("Ininializing Initable");
    }
}
class Initable2{
    static int staticNonFinal=147;
    static {
        System.out.println("Initializing Initable2");
    }
}
class Initable3{
    static int staticNonFinal=74;
    static {
        System.out.println("Initializing Initable3");
    }
}
public class ClassInitialization {

    public static Random rand=new Random(47);
    public static void main(String[] args) throws ClassNotFoundException {
        Class initable=Initable.class;
        System.out.println("After creating Initable ref");
        //不会触发初始化
        System.out.println(Initable.staticFinal);
        //会触发初始化
        System.out.println(Initable.staticFinal2);

        //不会触发初始化
        System.out.println(Initable2.staticNonFinal);

        try {
            Class initabl3 = Class.forName("");
        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        System.out.println("After creating Initable3 ref");
        System.out.println(Initable3.staticNonFinal);

    }
}
