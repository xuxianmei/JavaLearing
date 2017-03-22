package com.tlxxm.learing;

public class NestingInterfaces {
    public class BImp implements A.B{
        public void bf(){

        }
    }
    //error : A.D private access
//    public class DImp implements A.D{
//        public void df(){
//
//        }
//    }
    class EImp implements E{
        public void g(){

        }
    }
    public static void main(String[] args) {
        A a=new A();
        // A.D=a.getD(); ERROR  private
        //a.getD().df(); ERRIR  private
        A a2=new A();
        a.receiveD(a.getD());
    }
}

