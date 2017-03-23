package com.tlxxm.learing;

public class Parcel8 {

    public Wrapping wrapping(int x){
        return new Wrapping(x){
          public int value(){
              return super.value()*100;
          }
        };
    }
    public static void main(String[] args) {
        Parcel8 p=new Parcel8();
        Wrapping w=p.wrapping(8);
    }
}

class Wrapping{
    private int i;
    public Wrapping(int x){
        i=x;
    }
    public int value(){
        return i;
    }
}
