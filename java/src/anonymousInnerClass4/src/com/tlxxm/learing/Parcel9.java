package com.tlxxm.learing;

public class Parcel9 {

    public Destination destionation(String dest){
        return new Destination() {
            private String label=dest;
            @Override
            public String readLabel() {
                return label;
            }
        };
    }


    public static void main(String[] args) {
        Parcel9 p=new Parcel9();
        Destination d=p.destionation("Beijing");
        System.out.println(d.readLabel());
    }
}
interface Destination{
    String readLabel();
}