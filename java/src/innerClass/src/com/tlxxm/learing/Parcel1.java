package com.tlxxm.learing;

public class Parcel1 {
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
    //在Parcel1中使用内部类，就像使用其它类一样。
    public void ship(String dest){
        Contents c=new Contents();
        Destination d=new Destination(dest);
        System.out.println(d.readLabel());
    }
    public static void main(String[] args) {
        Parcel1 p=new Parcel1();
        p.ship("Beijing");
    }
}
