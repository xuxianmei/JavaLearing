package com.tlxxm.learing;

class A{
    interface B{
        void bf();
    }
    public class BImp implements B{
        public void bf(){
            System.out.println("BImp");
        }
    }
    private class BImp2 implements B{
        public void bf(){
            System.out.println("BImp2");
        }
    }
    //public 访问权限
    public interface C{
        void cf();
    }
    class CImp implements C{
        public void cf(){

        }
    }
    private class CImp2 implements C{
        public void cf(){

        }
    }

    //private 访问权限
    private interface D{
        void df();
    }
    private class DImp implements D{
        public void df(){

        }
    }
    public class DImp2 implements D{
        public void df(){

        }
    }
    public D getD(){
        return new DImp2();
    }
    private D dRef;
    public void receiveD(D d){
        dRef=d;
        dRef.df();
    }
}