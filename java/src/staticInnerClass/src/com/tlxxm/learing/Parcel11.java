package com.tlxxm.learing;

public class Parcel11 {

    protected static class ParcelContents implements IContents{
        public static int Count=0;
        private int i=1;
        @Override
        public int value(){
            Count++;
            return i;
        }
    }
    public IContents getContents(){
        return new ParcelContents();
    }
    public static void main(String[] args) {
        IContents c=new Parcel11().getContents();
        c.value();
        c.value();
        System.out.println(Parcel11.ParcelContents.Count);
    }
}

interface IContents{
    int value();
}
