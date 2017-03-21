package com.tlxxm.learing;

public class RoundGlyph extends Glyph {
    private int radius=1;
    RoundGlyph(int r){
        System.out.println("before RoundGlyph.RoundGlyph().radius="+radius);
        radius=r;
        System.out.println("after RoundGlyph.RoundGlyph().radius="+radius);
    }
    void draw(){
        System.out.println("RoundGlyph.draw().radius="+radius);
    }

    public static void main(String[] args) {
	 new RoundGlyph(100);
    }
}
class Glyph{
    void draw(){
        System.out.println("Glyph.draw()");
    }
    Glyph(){
        System.out.println("Glyph() before draw()");
        draw();
        System.out.println("Glyph() after draw()");
    }
}
