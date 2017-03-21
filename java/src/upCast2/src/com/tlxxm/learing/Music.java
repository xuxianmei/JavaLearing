package com.tlxxm.learing;

public class Music {

    public static void tune(Instrument i){
        i.play(Note.MIDDLE_C);
    }
    public static void main(String[] args) {
        Wind flute=new Wind();
	 tune(flute);
    }
}
enum Note{
    MIDDLE_C,
    C_SHARP,
    B_FLAT
}
class Instrument{
    public void play(Note n){
        System.out.println(n);
    }
}
class Wind extends Instrument{
    @Override
    public void play(Note n){
        System.out.println("Wind.play() "+n);
    }
}


