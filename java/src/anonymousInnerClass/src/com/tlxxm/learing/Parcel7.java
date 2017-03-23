package com.tlxxm.learing;

interface IContents{
    int value();
}
public class Parcel7 {

    public IContents contents(){
        //实现了接口IContents的匿名内部类
        return new IContents() {
            private int i=11;
            @Override
            public int value() {
                return i;
            }
        };//分号记得加上
    }

    public static void main(String[] args) {
        Parcel7 p=new Parcel7();
        IContents c=p.contents();
        System.out.println(c.value());
    }
}
//与Parcel7中 contents方法相同的效果
class Parcel7b{
    class MyContents implements IContents{
        private int i=11;
        @Override
        public int value(){
            return i;
        }
    }
    public IContents contents(){
        return new MyContents();
    }
}
