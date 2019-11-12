package com.example.jolin.afinal;

import android.os.Environment;

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
    private OutputStream os = null;
    private InputStream is = null;
    private FileInputStream fis = null;
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
            os = socket.getOutputStream();
            // os = null;
            is = socket.getInputStream();
            // is = null;
            buffer = null;
            System.out.println("---------------1111111111111111----------------");
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
                // os = socket.getOutputStream();
                // is = new FileInputStream(StartGameActivity.imageFilePath); //Gets the true path of your image
                fis = new FileInputStream(StartGameActivity.imageFilePath);
                System.out.println("--------------888888888888888888888----------------");
                while ((readLength = fis.read()) != -1) {
                    os.write(readLength); //Writes bytes to output stream
                    System.out.println("--------------999999999999999999999----------------");
                }
                os.flush();

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
            os.close();
            is.close();
            fis.close();
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