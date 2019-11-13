package com.example.jolin.afinal;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

public class ChatClientThread extends Thread {

    private static Socket socket = null;
    private static Client client = null;
    private static InputStream is = null;
    private FileOutputStream fos = null;
    private DataInputStream dis = null;
    private byte[] buffer;
    private File file = null;
    private RandomAccessFile rand = null;
    private int photoCount = 0;

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
            dis.close();
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
                if(photoCount != 0){
                    break;
                }

                file = new File(StartGameActivity.imageFileReceivePath);
                rand = new RandomAccessFile(file, "rw");
                int fileLen = dis.readInt();
                System.out.println("Receive image file length: " + fileLen);
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e.getMessage());
                }

                buffer = new byte[fileLen];
                int count = 0;
                while(count < fileLen){
                    count += is.read(buffer, count, fileLen - count);
                }
                rand.write(buffer); //Writes bytes to output stream
                System.out.println("Receive image from Server..." + count);

                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e.getMessage());
                }

                // fos.flush();
                rand.close();
                System.out.println("Receive image FINISH.");
                // 送多少byte先說，收完要停
                // 讀一個整數，否則就須把很多byte合起來得到整數
                photoCount++;
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error" + e.getMessage());
            client.stop();
        }
    }
}