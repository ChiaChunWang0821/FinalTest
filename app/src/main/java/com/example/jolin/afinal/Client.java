package com.example.jolin.afinal;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Client implements Runnable {

    private String serverName = "140.121.197.165";
    private int serverPort = 5002;
    private Socket socket = null;

    private ChatClientThread client = null;
    private Thread thread = null;

    private OutputStream os = null;
    private static DataOutputStream dos = null;

    private int photoCount = 0;
    public static boolean allowReceive = false;

    private int byteCount = 0;
    private byte[] byteFile = null;
    private static double muscleData;

    private byte[] writeBuffer = null;
    public static Lock lock = new ReentrantLock();
    public static Condition condition = lock.newCondition();

    public Client() {
        try {
            socket = new Socket(serverName, serverPort);
            System.out.println("Client started on port " + socket.getLocalPort() + "...");
            System.out.println("Connected to server " + socket.getRemoteSocketAddress());

            os = socket.getOutputStream();
            dos = new DataOutputStream(os);

            // 控制多久沒有更新 就不會在skip -> counter去控制

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
                /*while(CameraSurfaceView.getByteFile() == null || CameraSurfaceView.getByteCount() == 0) {}
                // 要有人鎖空byte，有東西就解

                lock.lock();
                writeBuffer = new byte[CameraSurfaceView.getByteCount()];
                writeBuffer = CameraSurfaceView.getByteFile();
                lock.unlock();
                */

                setByteFile();

                if(lock.tryLock()){
                    try{
                        byteFile = writeBuffer;
                        byteCount = byteFile.length;
                    }finally {
                        lock.unlock();
                    }
                }

                try {
                    thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e.getMessage());
                }

                // false 表示傳送影像array
                dos.writeBoolean(false);
                System.out.println("Send Boolean: FALSE");

                dos.writeInt(byteCount);
                System.out.println("Send image file length: " + byteCount);
                try {
                    thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e.getMessage());
                }

                // 拍下影像downsize!!不需要這麼高
                System.out.println("Start Send image file");

                /*buffer = new byte[(int)rand.length()];
                int count = 0;
                while(count < (int)rand.length()){
                    count += rand.read(buffer, count, (int)rand.length() - count);
                }
                os.write(buffer);
                System.out.println("Send image to Server..." + count);*/

                os.write(byteFile);
                os.flush();
                System.out.println("Send image to Server..." + byteFile.length);

                try {
                    thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e.getMessage());
                }
                System.out.println("Send image FINISH.");

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
            // true 表示傳送muscleData
            dos.writeBoolean(true);
            System.out.println("Send Boolean: TRUE");
            dos.writeDouble(muscleData);
            System.out.println("Send muscleData: " + muscleData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setByteFile(){
        System.out.println("Client Thread Lock!");
        lock.lock();
        try{
            System.out.println("Client Thread Await!");
            condition.await();

            writeBuffer = new byte[CameraSurfaceView.getByteCount()];
            writeBuffer = CameraSurfaceView.getByteFile();
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            lock.unlock();
            System.out.println("Client Thread UnLock!");
        }
    }
}