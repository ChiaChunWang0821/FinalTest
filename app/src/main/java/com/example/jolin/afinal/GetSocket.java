package com.example.jolin.afinal;

import java.net.Socket;

public class GetSocket {
    private static Socket msocket;

    public static synchronized void setSocket(Socket socket){
        GetSocket.msocket = socket;
    }

    public static synchronized Socket getSocket(){
        return msocket;
    }
}
