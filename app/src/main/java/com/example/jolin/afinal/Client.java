package com.example.jolin.afinal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

// Client for Server4
public class Client implements Runnable {

    private String serverName = "140.121.197.165";
    private int serverPort = 5002;
    private Socket socket = null;
    private Thread thread = null;
    // private DataInputStream dis = null;
    // private DataOutputStream dos = null;
    private OutputStream os = null;
    private InputStream is = null;
    private ByteArrayOutputStream baos = null;
    private ChatClientThread client = null;
    private int readLength = 0;
    private byte[] byteArray;

    public Client() {
        try {
            socket = new Socket(serverName, serverPort);
            System.out.println("Client started on port " + socket.getLocalPort() + "...");
            System.out.println("Connected to server " + socket.getRemoteSocketAddress());

            // dis = new DataInputStream(System.in);
            // dos = new DataOutputStream(socket.getOutputStream());
            os = socket.getOutputStream();
            is = socket.getInputStream();
            baos = new ByteArrayOutputStream();
            byteArray = new byte[1024];
            client = new ChatClientThread(this, socket);
            thread = new Thread(this);
            thread.start();
        } catch (IOException e) {
            System.out.println("Error : " + e.getMessage());
        }
    }

    @Override
    public void run() {
        while (thread != null) {
            System.out.print("Transfer image to server...");
            // dos.writeUTF(dis.readLine());
            // dos.flush();
                /*while((readLength = is.read(byteArray))!= -1){
                    baos.write(setByteArray(), 0, readLength);
                }*/
                /*while(setByteArray()){
                System.out.print("mmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
                baos.write(getByteArray(), 0, readLength);
            }*/

            try {
                while(setByteArray()){
                    while((readLength = is.read(getByteArray()))!= -1){
                        System.out.print("mmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
                        baos.write(getByteArray(), 0, readLength);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.print("444444444444444444444444444444444444");

            // Sleep, because this thread must wait ChatClientThread to show the message first
            try {
                thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Error : " + e.getMessage());
            }
        }
    }

    /*public void handleMessage(String message) {
        if (message.equals("exit")) {
            stop();
        } else {
            System.out.println(message);
            System.out.print("Message to server : ");
        }
    }*/

    /*public byte[] setByteArray(){
        byteArray = CameraSurfaceView.getByteArray();
        return byteArray;
    }*/

    public boolean setByteArray(){
        byteArray = CameraSurfaceView.getByteArray();
        if(byteArray != null)
            return true;
        else
            return false;
    }

    public byte[] getByteArray(){
        return byteArray;
    }

    public void stop() {
        try {
            thread = null;
            // dis.close();
            // dos.close();
            os.close();
            is.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Error closing : " + e.getMessage());
        }
        client.close();
    }

    /*public static void main(String args[]) {
        Client client = new Client();
    }*/
}