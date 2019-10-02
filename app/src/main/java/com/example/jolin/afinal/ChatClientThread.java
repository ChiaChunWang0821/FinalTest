package com.example.jolin.afinal;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
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
        open();
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
        try {
            /*while (true) {
                client.handleMessage(dis.readUTF());
            }*/
            while((readLength = is.read()) != -1){
                baos.write(getByteArray(), 0, readLength);
            }
            byteArray = baos.toByteArray();
            System.out.println(byteArray);
        } catch (IOException e) {
            client.stop();
        }

    }
}