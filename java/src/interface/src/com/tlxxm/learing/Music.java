package com.tlxxm.learing;

public class Music {

    static void tune(Instrument i){
        i.play(Note.MIDDLE_C);
    }
    static void tuneAll(Instrument[] e){
        for(Instrument i : e){
            tune(i);
        }
    }
    public static void main(String[] args) {
        Instrument[] orchestra={
                new Wind(),
                new Percussion(),
                new Stringed(),
                new Brass(),
                new WoodWind()
        };
        tuneAll(orchestra);
    }
}
enum Note{
    MIDDLE_C,
    C_SHARP,
    B_FLAT
}
interface Instrument{
     int VALUE=5;// static & final
     void play(Note n);//automatically public
     void adjust();
}
class Wind implements Instrument{
    public void play(Note n){
        System.out.println("Wind.play() "+n);
    }
    public String toString(){
        return "Wind";
    }
    public void adjust(){

    }
}
class Percussion implements Instrument{
    public void play(Note n){
        System.out.println("Percussion.play() "+n);
    }
    public String toString(){
        return "Percussion";
    }
    public void adjust(){

    }
}
class Stringed implements Instrument{
    public void play(Note n){
        System.out.println("Stringed.play() "+n);
    }
    public String toString(){
        return "Stringed";
    }
    public void adjust(){

    }
}
class Brass extends Wind{
    public void play(Note n){
        System.out.println("Brass.play() "+n);
    }
    public void adjust(){
        System.out.println( "Brass.adjust");
    }
}
class WoodWind extends Wind{
    public void play(Note n){
        System.out.println("WoodWind.play() "+n);
    }
    public void adjust(){
        System.out.println( "WoodWind.adjust");
    }
}