package com.tlxxm.learing;

import java.util.*;

public class ListOfGenerics<T> {
    private List<T> array=new ArrayList<T>();
    public void add(T item){
        array.add(item);
    }
    public T get(int index){
        return array.get(index);
    }
    public static void main(String[] args) {
	// write your code here
    }
}
