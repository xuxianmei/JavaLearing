package com.tlxxm.learing;

class Candy{
    static {
        System.out.println("Loading Candy");
    }
}

class Gum{
    static {
        System.out.println("Loading Gum");
    }
}

class Cookie{
    static {
        System.out.println("Loading Cookie");
    }
}
public class Main {
    static{
        System.out.println("Loading Main");
    }
    public static void main(String[] args) {

        new Candy();
        System.out.println("After creating Candy");
        try{
            Class.forName("Gum");
        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
        System.out.println("After Class.forName(\"Gum\")");
        new Cookie();
        System.out.println("After creating Cookie");
    }
}
