package com.tlxxm.learing;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 9002);
            OutputStream os=socket.getOutputStream();
            boolean autoflush=true;
            PrintWriter out=new PrintWriter(socket.getOutputStream(),autoflush);
            BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //发送请求到服务器
            out.println("GET / HTTP/1.1");
            out.println("Host:localhost:9002");
            out.println("Connection:Close");
            out.println();

            boolean loop=true;
            StringBuffer sb=new StringBuffer(8096);
            while (loop){
                if(in.ready()){
                    int i=0;
                    while(i!=-1)
                    {
                        i=in.read();
                        sb.append((char)i);
                    }
                    loop=false;
                }
                Thread.currentThread().sleep(50);
            }
            System.out.println(sb.toString());
            socket.close();
        }catch(Exception e){
            System.out.print(e);
        }
    }
}
