package com.example.jolin.afinal;

import android.os.Environment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
    private ByteArrayInputStream bais = null;
    private ByteArrayOutputStream baos = null;
    private ChatClientThread client = null;
    private int readLength = 0;
    private byte[] buffer;
    private byte[] byteArray;

    public Client() {
        try {
            socket = new Socket(serverName, serverPort);
            System.out.println("Client started on port " + socket.getLocalPort() + "...");
            System.out.println("Connected to server " + socket.getRemoteSocketAddress());

            // test();
            // dis = new DataInputStream(System.in);
            // dos = new DataOutputStream(socket.getOutputStream());
            os = socket.getOutputStream();
            is = socket.getInputStream();
            bais = null;
            baos = new ByteArrayOutputStream();
            buffer = null;
            System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
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
            // dos.writeUTF(dis.readLine());
            // dos.flush();
            /*while(setByteArray()){
                System.out.print("mmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
                baos.write(getByteArray(), 0, getByteArray().length);
            }*/

            /*將影像byte讀入，再傳出到Server端*/
            setByteArray();
            bais = new ByteArrayInputStream(getByteArray());
            while ((readLength = bais.read()) != -1) {
                System.out.println("///////////////////////GGGGGGGGGGGGGGG////////////////////////");
                try {
                    os.write(readLength); //Writes bytes to output stream
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            /*System.out.println("yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy");
            while((readLength = bais.read(buffer, 0, getByteArray().length))!= -1){
                System.out.print("mmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
                baos.write(buffer, 0, readLength);
            }
            System.out.print("444444444444444444444444444444444444");*/

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

    public void setByteArray(){
        byteArray = CameraSurfaceView.getByteArray();
    }

    /*public boolean setByteArray(){
        byteArray = CameraSurfaceView.getByteArray();
        if(byteArray != null)
            return true;
        else
            return false;
    }*/

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
            bais.close();
            baos.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Error closing : " + e.getMessage());
        }
        client.close();
    }

    public void test() throws IOException {
        System.out.println("/////////////////////////Test////////////////////////");
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); //Gets information about a said directory on your device - currently downloads
        File photoPath = new File(directory, "test.jpeg"); //Define your image name I used png but other formats should also work - make sure to specify file extension on server
        InputStream input = new FileInputStream(photoPath.getAbsolutePath()); //Gets the true path of your image
        System.out.println("/////////////////////////AAAAAAAAAAAAAAAAAAA////////////////////////");
        try {
            try {
                //Reads bytes (all together)
                int bytesRead;
                while ((bytesRead = input.read()) != -1) {
                    System.out.println("/////////////////////////BBBBBBBBBBBBBBBBBB////////////////////////");
                    socket.getOutputStream().write(bytesRead); //Writes bytes to output stream
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.out.println("/////////////////////////CCCCCCCCCCCCCCCCCCCCC////////////////////////");
                //Flushes and closes socket
                socket.getOutputStream().flush();
                socket.close();
                System.out.println("/////////////////////////DDDDDDDDDDDDDDDDDDDDD////////////////////////");
            }
        } finally {
            input.close();
            System.out.println("/////////////////////////EEEEEEEEEEEEEEEEEEE////////////////////////");
        }
    }

    /*public static void main(String args[]) {
        Client client = new Client();
    }*/
}