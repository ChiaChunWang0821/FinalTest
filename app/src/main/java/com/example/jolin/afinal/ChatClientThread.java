package com.example.jolin.afinal;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ChatClientThread extends Thread {

    private int byteInt = 64;
    private static Socket socket = null;
    private static Client client = null;
    private static InputStream is = null;
    private FileOutputStream fos = null;
    private DataInputStream dis = null;
    private byte[] buffer = new byte [1024];

    public ChatClientThread(Client _client, Socket _socket) {
        client = _client;
        socket = _socket;
        open();
        start();
    }

    public void open() {
        try {
            is = socket.getInputStream();
            dis = new DataInputStream(is);
        } catch (IOException e) {
            System.out.println("Error getting input stream : " + e.getMessage());
            client.stop();
        }
    }

    public void close() {
        try {
            is.close();
        } catch (IOException e) {
            System.out.println("Error closing input stream : " + e.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.print("----------------ChatClientThread----------------");
        /*從Server傳入影像byte，再輸出成file*/
        try {
            while(true){
                fos = new FileOutputStream(StartGameActivity.imageFileReceivePath); //Gets the true path of your image
                int fileLen = dis.readInt();
                System.out.println(fileLen);

                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e.getMessage());
                }

                int count = 0;
                while(count < fileLen){
                    is.read(buffer, count, byteInt);
                    fos.write(buffer); //Writes bytes to output stream
                    System.out.println(count);
                    count += byteInt;
                }
                System.out.println("--------------4444444444444444444444444----------------");
                fos.flush();
                // fos.close();
                // 送多少byte先說，收完要停
                // 讀一個整數，否則就須把很多byte合起來得到整數
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("--------------777777777777777777777----------------");
            client.stop();
        }
    }
}