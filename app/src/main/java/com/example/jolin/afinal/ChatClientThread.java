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
    private byte[] buffer = new byte[1024];

    public ChatClientThread(Client _client, Socket _socket) {
        client = _client;
        socket = _socket;
        open();
        System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhh");
        start();
    }

    public void open() {
        try {
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
        System.out.print("----------------ChatClientThread----------------");
        try {
            /*while (true) {
                client.handleMessage(dis.readUTF());
            }*/

            /*從Server傳入影像byte，再輸出成file*/
            while (true){
                while((readLength = is.read(buffer)) != -1){
                    baos.write(buffer, 0, readLength);
                }
                System.out.println("8888888888888888888888888888888");
                byteArray = baos.toByteArray();
                System.out.println(byteArray);
                createFile(StartGameActivity.imageFileReceivePath, byteArray);
            }
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