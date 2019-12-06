package com.example.jolin.afinal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

public class ChatClientThread extends Thread {

    private static Socket socket = null;
    private static Client client = null;
    private static InputStream is = null;
    private FileOutputStream fos = null;
    // private DataInputStream dis = null;
    private ObjectInputStream sInput;
    private byte[] buffer;
    private File file = null;
    private RandomAccessFile rand = null;
    private int photoCount = 0;
    public static boolean threadStatus = true;
    private Message cm;
    private double muscleData = 0;
    private int byteLenData = 0;



    public ChatClientThread(Client _client, Socket _socket) {
        client = _client;
        socket = _socket;
        open();
        start();
        // main thread 用另個buffer存收到的影像
        // 此thread 收到影像後，看main thread的lock是否正在收。
    }

    public void open() {
        try {
            is = socket.getInputStream();
            // dis = new DataInputStream(is);
            sInput  = new ObjectInputStream(is);
        } catch (IOException e) {
            System.out.println("Error getting input stream : " + e.getMessage());
            client.stop();
        }
    }

    public void close() {
        try {
            is.close();
            // dis.close();
        } catch (IOException e) {
            System.out.println("Error closing input stream : " + e.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.print("----------------ChatClientThread----------------");
        /*從Server傳入影像byte，再輸出成file*/
        try {
            while(threadStatus){
                if(photoCount != 0){
                    break;
                }

                if(Client.allowReceive == false){
                    // 照片還沒被拍下，還沒有影像要傳
                    // System.out.println("Not yet to Receive file");
                    continue;
                }
                // 用lock 鎖住，放到另個地方存(buffer) main thread較順
                // 收送都要

                file = new File(StartGameActivity.imageFileReceivePath); // 開檔給main thread，連線前先開檔
                if(!file.exists()){
                    System.out.println("StartGameActivity.imageFileReceivePath is not exist!");
                    continue;
                }
                rand = new RandomAccessFile(file, "rw");

                try {
                    cm = (Message) sInput.readObject();
                    sleep(500);
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e.getMessage());
                }

                switch(cm.getType()) {
                    case Message.MUSCLE:
                        muscleData = cm.getDoubleMessage();
                        break;
                    case Message.BYTELEN:
                        byteLenData = cm.getIntMessage();
                        System.out.println("Receive image file length: " + byteLenData);

                        buffer = new byte[byteLenData];
                        int count = 0;
                        while(count < byteLenData) {
                            count += is.read(buffer, count, byteLenData - count);
                        }
                        rand.write(buffer);
                        System.out.println("Receive image from Server..." + count);

                        try {
                            sleep(500);
                        } catch (InterruptedException e) {
                            System.out.println("Error : " + e.getMessage());
                        }
                        rand.close();
                        System.out.println("Receive image FINISH.");

                        Client.readBuffer = new byte[buffer.length];
                        Client.readBuffer = buffer;
                        break;
        			/*case ChatMessage.BYTEFILE:
        				byteFileData = cm.getByteMessage();
        				break;*/
                }

                /*int fileLen = dis.readInt();
                System.out.println("Receive image file length: " + fileLen);
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e.getMessage());
                }

                buffer = new byte[fileLen];
                int count = 0;
                while(count < fileLen){
                    count += is.read(buffer, count, fileLen - count);
                }
                rand.write(buffer); //Writes bytes to output stream
                System.out.println("Receive image from Server..." + count);

                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e.getMessage());
                }

                // fos.flush();
                rand.close();
                System.out.println("Receive image FINISH.");*/
                // 送多少byte先說，收完要停
                // 讀一個整數，否則就須把很多byte合起來得到整數
                photoCount++;

                Client.allowReceive = false;

                // StartGameActivity.mShowReceiveImage.setImageBitmap(Bytes2Bimap(buffer));
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error" + e.getMessage());
            threadStatus = false;
            client.stop();
        }
    }


}