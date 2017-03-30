package com.tlxxm.learing;

class SimpleException extends Exception{

}

public class InnheritingExceptions {
    public void f() throws SimpleException{
        System.out.println("Throw SimpleException from f()");
        throw new SimpleException();
    }
    public static void main(String[] args) {
        InnheritingExceptions sed=new InnheritingExceptions();
        try{
            sed.f();
        }
        catch(SimpleException e){
            System.out.println("捕获异常");
        }
    }
}
