package com.tlxxm.learing;

public class LocalInnerClass  {
    private int count=0;
    private String name;

    Counter getCounter(final String name){
        //local inner class
        class LocalCounter implements Counter{
            public LocalCounter(){
                System.out.println("LocalCounter");
            }
            @Override
            public int next(){
                System.out.println(name);
                return count++;
            }
        }
        return new LocalCounter();
    }
    Counter getCounter2( String name){
        //Anonymous inner class
        return new Counter() {
            {
                System.out.println("Counter");
            }
            @Override
            public int next() {
                System.out.println(name);
                return count++;
            }
        };
    }

    public static void main(String[] args) {
            LocalInnerClass lic=new LocalInnerClass();
        Counter c1=lic.getCounter("Local inner"),
                c2=lic.getCounter2("Anonymous inner");
        c1.next();
        c2.next();
    }
}
interface Counter{
    int next();
}


