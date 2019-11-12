package com.example.jolin.afinal;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Client implements Runnable {

    private int byteInt = 64;
    private String serverName = "140.121.197.165";
    private int serverPort = 5002;
    private Socket socket = null;
    private Thread thread = null;
    private OutputStream os = null;
    // private InputStream is = null;
    private DataOutputStream dos = null;
    private FileInputStream fis = null;
    private ChatClientThread client = null;
    private byte[] buffer = new byte [1024];

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
            System.out.print("Transfer image to server...");

            /*將影像byte讀入，再傳出到Server端*/
            try {
                fis = new FileInputStream(StartGameActivity.imageFilePath);
                dos = new DataOutputStream(os);
                dos.writeInt(StartGameActivity.imageFilePath.length());
                System.out.println(StartGameActivity.imageFilePath.length());

                try {
                    thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e.getMessage());
                }

                System.out.println("--------------888888888888888888888----------------");
                int count = 0;
                while(count < StartGameActivity.imageFilePath.length()){
                    fis.read(buffer, count, byteInt);
                    os.write(buffer);
                    System.out.println(count);
                    count += byteInt;
                }
                // os.flush();
                // fis.close();

                // Sleep, because this thread must wait ChatClientThread to show the message first
                try {
                    thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e.getMessage());
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("--------------yyyyyyyyyyyyyyyyyyyyyyyy----------------");
                stop();
            }
        }
    }

    public void stop() {
        try {
            thread = null;
            os.close();
            // is.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Error closing : " + e.getMessage());
        }
        client.close();
    }
}