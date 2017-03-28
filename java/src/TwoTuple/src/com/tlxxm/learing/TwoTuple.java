package com.tlxxm.learing;

public class TwoTuple<A,B> {
    public final A first;
    public final B second;
    public TwoTuple(A a,B b){
        first=a;
        second=b;
    }
    public String toString(){
        return "("+first+","+second+")";
    }

    public static void main(String[] args) {
	// write your code here
    }
}
