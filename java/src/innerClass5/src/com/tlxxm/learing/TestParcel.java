package com.tlxxm.learing;


public class TestParcel {

    public static void main(String[] args) {
        Parcel4 p=new Parcel4();
        Contents c=p.contents();//获取内部类实现的接口
        System.out.println(c.value());

        Destination d=p.destination("Beijing");
        System.out.println(d.readLabel());
        //Parcel4.PContents pc=p.new PContents(); 此处操作不合法，因为PContents是private修饰的，但是并不影响它实现的接口。

    }
}


interface Destination{
    String readLabel();
}
interface Contents{
    int value();
}
class Parcel4{
    //内部类
    private class PContents implements Contents{
        private int i=1;
        @Override
        public int value(){
            return i;
        }
    }
    //内部类
    protected class PDestination implements Destination{
        private String label;
        private PDestination(String whereTo){
            label=whereTo;
        }
        public String readLabel(){
            return label;
        }
    }
    public Destination destination(String s){
        return new PDestination(s);
    }
    public Contents contents(){
        return new PContents();
    }
}
