package com.example.jolin.afinal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Client implements Runnable {

    private String serverName = "140.121.197.165";
    private int serverPort = 5002;
    private Socket socket = null;
    private Thread thread = null;
    private OutputStream os = null;
    private DataOutputStream dos = null;
    // private FileInputStream fis = null;
    private ChatClientThread client = null;
    // private byte[] buffer;
    // private File file = null;
    // private RandomAccessFile rand = null;
    private int photoCount = 0;
    public static boolean allowReceive = false;
    private int byteCount = 0;
    private byte[] byteFile = null;

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

        // 用一個boolean 控制是否可以更新
    }

    @Override
    public void run() {
        System.out.print("----------------ClientThread----------------");
        while (thread != null) {
            if(photoCount != 0){
                break;
            }
            allowReceive = false;
            /*將影像byte讀入，再傳出到Server端*/
            try {
                /*file = new File(StartGameActivity.imageFilePath);
                if(!file.exists()){
                    // 檢查檔案在不在，不在不要做
                    System.out.println("StartGameActivity.imageFilePath is not exist!");
                    continue;
                }
                rand = new RandomAccessFile(file, "r");*/
                dos = new DataOutputStream(os); // 搬去外面
                // while((int)rand.length() == 0) { }
                while(CameraSurfaceView.getByteFile() == null || CameraSurfaceView.getByteCount() == 0){ }
                byteCount = CameraSurfaceView.getByteCount();
                byteFile = CameraSurfaceView.getByteFile();
                try {
                    thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e.getMessage());
                }
                /*dos.writeInt((int)rand.length());
                System.out.println("Send image file length: " + (int)rand.length());*/

                dos.writeInt(byteCount);
                System.out.println("Send image file length: " + byteCount);

                // 拍下影像downsize!!不需要這麼高
                try {
                    thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e.getMessage());
                }

                /*buffer = new byte[(int)rand.length()];
                int count = 0;
                while(count < (int)rand.length()){
                    count += rand.read(buffer, count, (int)rand.length() - count);
                }
                os.write(buffer);
                System.out.println("Send image to Server..." + count);*/

                os.write(byteFile);
                os.flush();
                System.out.println("Send image to Server...");

                try {
                    thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e.getMessage());
                }
                // os.flush();
                // rand.close();
                System.out.println("Send image FINISH.");

                // Sleep, because this thread must wait ChatClientThread to show the message first
                try {
                    thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e.getMessage());
                }

                allowReceive = true;

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error " + e.getMessage());
                stop();
            }

            photoCount++;

            // StartGameActivity.mShowReceiveImage.setImageBitmap(Bytes2Bimap(byteFile));
        }
    }

    public void stop() {
        try {
            thread = null;
            os.close();
            ChatClientThread.threadStatus = false;
            socket.close();
        } catch (IOException e) {
            System.out.println("Error closing : " + e.getMessage());
        }
        client.close();
    }

    private Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }
}