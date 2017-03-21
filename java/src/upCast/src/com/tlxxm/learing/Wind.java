package com.tlxxm.learing;

public class Wind extends Instrument {

    public static void main(String[] args) {
        Wind flute=new Wind();
        Instrument.tune(flute);

    }

}
class Instrument{
    public void play(){
        System.out.println("Instrument play");
    }
    static void tune(Instrument i){
        i.play();
    }
}
