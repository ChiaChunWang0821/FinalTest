package com.example.jolin.afinal;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ChatClientThread extends Thread {

    private static Socket socket = null;
    private static Client client = null;
    private static OutputStream os = null;
    private static InputStream is = null;
    private FileOutputStream fos = null;
    private int readLength = 0;
    private static byte[] byteArray;
    private byte[] buffer = new byte[1024];

    public ChatClientThread(Client _client, Socket _socket) {
        client = _client;
        socket = _socket;
        open();
        System.out.println("--------------2222222222222222----------------");
        start();
    }

    public void open() {
        try {
            os = socket.getOutputStream();
            is = socket.getInputStream();
        } catch (IOException e) {
            System.out.println("Error getting input stream : " + e.getMessage());
            client.stop();
        }
    }

    public void close() {
        try {
            is.close();
            os.close();
        } catch (IOException e) {
            System.out.println("Error closing input stream : " + e.getMessage());
        }
    }

    public byte[] getByteArray(){
        return byteArray;
    }

    @Override
    public void run() {
        System.out.print("----------------ChatClientThread----------------");
        /*從Server傳入影像byte，再輸出成file*/
        try {
            System.out.print(StartGameActivity.imageFileReceivePath);
            // os = new FileOutputStream(StartGameActivity.imageFileReceivePath); //Gets the true path of your image
            // os = new FileOutputStream("C:\\Users\\User\\Desktop\\test\\receive.jpg"); //Gets the true path of your image
            fos = new FileOutputStream(StartGameActivity.imageFileReceivePath); //Gets the true path of your image

            System.out.println("--------------333333333333333333----------------");
            while ((readLength = is.read()) != -1) {
                fos.write(readLength); //Writes bytes to output stream
                System.out.println(readLength);
                System.out.println("--------------RRRRRRRRRRRRRRRRRRRRRRRRR----------------");
            }
            // 送多少byte先說，收完要停
            // 讀一個整數，否則就須把很多byte合起來得到整數
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("--------------777777777777777777777----------------");
            client.stop();
        }
    }

    //將byte陣列寫入檔案
    /*public void createFile(String path, byte[] content) throws IOException {
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(content);
        fos.close();
    }*/
}