package com.tlxxm.learing;

public class Factories {

    public static void serviceConsumer(ServiceFactory factory){
        Service s=factory.getService();
        s.methodA();
        s.methodB();

    }
    public static void main(String[] args) {
        serviceConsumer(Implemeation001.factory);
        serviceConsumer(Implemeation002.factory);
    }
}
interface Service{
    void methodA();
    void methodB();
}
interface ServiceFactory{
    Service getService();
}
class Implemeation001 implements Service{
    private Implemeation001(){

    }
    public void methodA(){
        System.out.println("Implemeation001 methodA()");
    }
    public void methodB(){
        System.out.println("Implemeation001 methodB()");
    }
    //在此作用域，定义一个匿名内部类，并返回这个匿名内部类的一个对象引用
    public static ServiceFactory factory= new ServiceFactory() {
        @Override
        public Service getService() {
            return new Implemeation001();
        }
    };
}

class Implemeation002 implements Service{
    private Implemeation002(){

    }
    public void methodA(){
        System.out.println("Implemeation002 methodA()");
    }
    public void methodB(){
        System.out.println("Implemeation002 methodB()");
    }
    public static ServiceFactory factory=new ServiceFactory() {
        @Override
        public Service getService() {
            return new Implemeation002();
        }
    };
}