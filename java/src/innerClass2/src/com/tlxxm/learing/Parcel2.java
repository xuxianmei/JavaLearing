package com.tlxxm.learing;

public class Parcel2 {
    class Contents{
        private int i=11;
        public int value(){
            return i;
        }
    }
    class Destination{
        private String label;
        Destination(String whereTo){
            label=whereTo;
        }
        String readLabel(){
            return label;
        }
    }
    public Contents contents(){
        return new Contents();
    }
    public Destination to(String s){
        return new Destination(s);
    }

    //在Parcel1中使用内部类，就像使用其它类一样。
    public void ship(String dest){
        Contents c=contents();
        Destination d=to(dest);
        System.out.println(d.readLabel());
    }
    public static void main(String[] args) {
        Parcel2 p=new Parcel2();
        p.ship("Beijing");
        Parcel2 q=new Parcel2();
        //声明指向内部类的对象的引用
        Parcel2.Contents c=q.contents();
        Parcel2.Destination d=q.to("Shanghai");

    }
}
