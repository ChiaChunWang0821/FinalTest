package com.example.jolin.afinal;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

public class Client implements Runnable {

    private String serverName = "140.121.197.165";
    private int serverPort = 5002;
    private Socket socket = null;
    private Thread thread = null;
    private OutputStream os = null;
    // private InputStream is = null;
    private DataOutputStream dos = null;
    private FileInputStream fis = null;
    private ChatClientThread client = null;
    private byte[] buffer;
    private File file = null;
    private RandomAccessFile rand = null;
    private int photoCount = 0;

    public Client() {
        try {
            socket = new Socket(serverName, serverPort);
            System.out.println("Client started on port " + socket.getLocalPort() + "...");
            System.out.println("Connected to server " + socket.getRemoteSocketAddress());

            os = socket.getOutputStream();
            client = new ChatClientThread(this, socket);
            thread = new Thread(this);
            thread.start();
        } catch (IOException e) {
            System.out.println("Error : " + e.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.print("----------------ClientThread----------------");
        while (thread != null) {
            if(photoCount != 0){
                break;
            }

           /*將影像byte讀入，再傳出到Server端*/
            try {
                file = new File(StartGameActivity.imageFilePath);
                rand = new RandomAccessFile(file, "r");
                // fis = new FileInputStream(StartGameActivity.imageFilePath);
                dos = new DataOutputStream(os);
                dos.writeInt((int)rand.length());
                System.out.println("Send image file length: " + (int)rand.length());

                try {
                    thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e.getMessage());
                }

                buffer = new byte[(int)rand.length()];
                int count = 0;
                while(count < (int)rand.length()){
                    count += rand.read(buffer, count, (int)rand.length() - count);
                }
                os.write(buffer);
                System.out.println("Send image to Server..." + count);

                try {
                    thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e.getMessage());
                }
                // os.flush();
                rand.close();
                System.out.println("Send image FINISH.");

                // Sleep, because this thread must wait ChatClientThread to show the message first
                try {
                    thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e.getMessage());
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error " + e.getMessage());
                stop();
            }

            photoCount++;
        }
    }

    public void stop() {
        try {
            thread = null;
            os.close();
            // is.close();
            // dos.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Error closing : " + e.getMessage());
        }
        client.close();
    }
}