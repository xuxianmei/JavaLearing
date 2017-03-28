package com.tlxxm.learing;

import java.util.*;

public class GenericVarargs {
    public static <T> List<T> makeList(T... args){
        List<T> result=new ArrayList<T>();
        for(T item : args){
            result.add(item);
        }
        return result;
    }

    public static void main(String[] args) {
	// write your code here
    }
}
