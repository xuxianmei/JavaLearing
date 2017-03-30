package com.tlxxm.learing;

interface HasColor{
    java.awt.Color getColor();
}
class Colored<T extends HasColor>{
    T item;
    Colored(T item){
        this.item=item;
    }
    T getItem(){
        return item;
    }
    java.awt.Color color(){
        return item.getColor();// extends HasColor 边界允许这个方法的调用。
    }
}
class Dimension{
    public int x,y,z;
}
//class ColoredDimension<T extends HasColor& Dimension >{  第一个必须是类，后面才是接口，顺序不可反。
class ColoredDimension<T extends Dimension & HasColor >{
    T item;
    ColoredDimension(T item){
        this.item=item;
    }
    T getItem(){

        return item;
    }
    java.awt.Color color()    {
            return item.getColor();
    }
    int getX(){
        return item.x;
    }
    int getY(){
        return item.y;
    }
    int getZ(){
        return item.z;
    }
}
interface Weight{
    int weight();
}
//只能有一个类，但是可以有多个接口
class Solid<T extends Dimension & HasColor & Weight>{
    T item;
    Solid(T item){

        this.item=item;
    }
    T getItem(){
        return item;
    }
    java.awt.Color color()    {
        return item.getColor();
    }
    int getX(){
        return item.x;
    }
    int getY(){
        return item.y;
    }
    int getZ(){
        return item.z;
    }
    int weight(){
        return item.weight();
    }
}
class Bounded extends Dimension implements HasColor,Weight{
    @Override
    public java.awt.Color getColor(){
        return null;
    }
    @Override
    public int weight(){
        return 0;
    }
}
public class BasicBounds {

    public static void main(String[] args) {
	    Solid<Bounded> solid=new Solid<>(new Bounded());
	    solid.color();
	    solid.getY();
	    solid.weight();
    }
}
