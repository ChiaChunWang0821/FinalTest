package com.example.jolin.afinal;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ChatClientThread extends Thread {

    private static Socket socket = null;
    private static Client client = null;
    // private static DataInputStream dis = null;
    private static OutputStream os = null;
    private static InputStream is = null;
    private ByteArrayOutputStream baos = null;
    private int readLength = 0;
    private static byte[] byteArray;

    public ChatClientThread(Client _client, Socket _socket) {
        client = _client;
        socket = _socket;
        System.out.println("333333333333333333333333333");
        open();
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        start();
    }

    public void open() {
        try {
            System.out.println("666666666666666666666666666");
            // dis = new DataInputStream(socket.getInputStream());
            os = socket.getOutputStream();
            is = socket.getInputStream();
            baos = new ByteArrayOutputStream();
        } catch (IOException e) {
            System.out.println("Error getting input stream : " + e.getMessage());
            client.stop();
        }
    }

    public void close() {
        try {
            // dis.close();
            is.close();
            os.close();
            baos.close();
        } catch (IOException e) {
            System.out.println("Error closing input stream : " + e.getMessage());
        }
    }

    public byte[] getByteArray(){
        return byteArray;
    }

    @Override
    public void run() {
        try {
            /*while (true) {
                client.handleMessage(dis.readUTF());
            }*/
            System.out.println("??????????????????????????");
            while((readLength = is.read()) != -1){
                baos.write(getByteArray(), 0, readLength);
            }
            System.out.println("8888888888888888888888888888888");
            byteArray = baos.toByteArray();
            System.out.println("receive");
            createFile(StartGameActivity.imageFileReceivePath, byteArray);
            System.out.println(byteArray);
            // System.out.println(byteArray.length);
        } catch (IOException e) {
            client.stop();
        }

    }

    //將byte陣列寫入檔案
    public void createFile(String path, byte[] content) throws IOException {
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(content);
        fos.close();
    }
}