package com.example.jolin.afinal;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Client implements Runnable {

    private String serverName = "140.121.197.165";
    private int serverPort = 5002;
    private Socket socket = null;
    private Thread thread = null;
    private OutputStream os = null;
    // private DataOutputStream dos = null;
    private static ObjectOutputStream sOutput;
    private ChatClientThread client = null;
    private int photoCount = 0;
    public static boolean allowReceive = false;
    private int byteCount = 0;
    private byte[] byteFile = null;
    private static double muscleData;

    private byte[] writeBuffer = null;
    public static byte[] readBuffer = null;
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public Client() {
        try {
            socket = new Socket(serverName, serverPort);
            System.out.println("Client started on port " + socket.getLocalPort() + "...");
            System.out.println("Connected to server " + socket.getRemoteSocketAddress());

            os = socket.getOutputStream();
            // dos = new DataOutputStream(os);
            sOutput = new ObjectOutputStream(os);

            rwLock.writeLock().lock();
            writeBuffer = new byte[CameraSurfaceView.getByteCount()];
            writeBuffer = CameraSurfaceView.getByteFile();
            rwLock.writeLock().unlock();

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
                while(CameraSurfaceView.getByteFile() == null || CameraSurfaceView.getByteCount() == 0) {
                    rwLock.writeLock().lock();
                    writeBuffer = new byte[CameraSurfaceView.getByteCount()];
                    writeBuffer = CameraSurfaceView.getByteFile();
                    rwLock.writeLock().unlock();
                }

                byteFile = writeBuffer;
                byteCount = byteFile.length;

                try {
                    thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e.getMessage());
                }

                // dos.writeInt(byteCount);
                System.out.println("Start Send image file");
                sOutput.writeObject(new ChatMessage(ChatMessage.BYTELEN, (double) byteCount));
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

                // sOutput.writeObject(new ChatMessage(ChatMessage.BYTEFILE, byteFile));
                os.write(byteFile);
                os.flush();
                System.out.println("Send image to Server...");

                try {
                    thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e.getMessage());
                }

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

    public static void checkMuscle(){
        muscleData = StartMuscle.getMove();
        try {
            sOutput.writeObject(new ChatMessage(ChatMessage.MUSCLE, muscleData));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] getReadBuffer(){
        return readBuffer;
    }
}