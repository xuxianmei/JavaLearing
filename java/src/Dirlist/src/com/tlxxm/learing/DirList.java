package com.tlxxm.learing;

import java.io.File;
import java.awt.*;
import javax.swing.*;
public class DirList {

    public static void main(String[] args) {
        File path=new File(".");

        String[] list=path.list();
        for(String item:list){
            System.out.println(item);
        }
        File[] fileList= path.listFiles();
        for(File file:fileList ){
            System.out.println(file.getName());
        }
    }
}
