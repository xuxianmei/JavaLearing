package com.tlxxm.learing;
public class Main {

    public static void main(String[] args) {
	 Point p1=new Point();
     Point p2=new Point();
    }
}
class Point{
    static int i;
    static{
        i=47;
        System.out.println("静态成员显示初始化：i="+i);
    }
    int x;//自动初始化
    int y=0;//指定初始化
    {
        x=1;
        y=2;
        System.out.println("显示初始化：x="+x+",y="+y);
    }
    Point(){
        x=-1;
        y=-2;
        System.out.println("构造器初始化：x="+x+",y="+y);
    }
}
