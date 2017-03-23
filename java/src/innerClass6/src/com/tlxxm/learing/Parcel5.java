package com.tlxxm.learing;
interface Destination{
    String readLabel();
}
public class Parcel5{
    public Destination destination(String s){
        //内部类
        class PDestination implements Destination{
            private String label;
            private PDestination(String whereTo){
                label=whereTo;
            }
            public String readLabel(){
                return label;
            }
        }
        return new PDestination(s);
    }

    public static void main(String[] args) {
        Parcel5 p=new Parcel5();
        Destination d=p.destination("Beijing");
    }
}
