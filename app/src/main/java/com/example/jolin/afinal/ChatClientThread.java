package com.example.jolin.afinal;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
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
    private FileOutputStream fos = null;
    private int readLength = 0;
    private static byte[] byteArray;
    private byte[] buffer = new byte[1024];

    public ChatClientThread(Client _client, Socket _socket) {
        client = _client;
        socket = _socket;
        open();
        System.out.println("--------------2222222222222222----------------");
        start();
    }

    public void open() {
        os = null;
        is = null;
        /*try {
            // dis = new DataInputStream(socket.getInputStream());
            // os = socket.getOutputStream();
            // is = socket.getInputStream();
            os = null;
            is = null;
            // baos = new ByteArrayOutputStream();
        } catch (IOException e) {
            System.out.println("Error getting input stream : " + e.getMessage());
            client.stop();
        }*/
    }

    public void close() {
        try {
            // dis.close();
            is.close();
            // os.close();
            // baos.close();
        } catch (IOException e) {
            System.out.println("Error closing input stream : " + e.getMessage());
        }
    }

    public byte[] getByteArray(){
        return byteArray;
    }

    @Override
    public void run() {
        System.out.print("----------------ChatClientThread----------------");
        /*從Server傳入影像byte，再輸出成file*/
        try {
            is = socket.getInputStream();
            System.out.print(StartGameActivity.imageFileReceivePath);
            os = new FileOutputStream(StartGameActivity.imageFileReceivePath); //Gets the true path of your image
            System.out.println("--------------333333333333333333----------------");
            while ((readLength = is.read()) != -1) {
                os.write(readLength); //Writes bytes to output stream
                System.out.println("--------------4444444444444444444----------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("--------------777777777777777777777----------------");
            client.stop();
        }


        /*try {
            is = socket.getInputStream();
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); //Gets information about a said directory on your device - currently downloads
            File photoPath = new File(StartGameActivity.imageFileReceivePath); //Define your image name I used png but other formats should also work - make sure to specify file extension on server
            OutputStream output = new FileOutputStream(photoPath.getAbsolutePath()); //Gets the true path of your image
            // os = new FileOutputStream("C:\\Users\\User\\Desktop\\test\\receive.jpg"); //Gets the true path of your image
            // os = new FileOutputStream(StartGameActivity.imageFileReceivePath); //Gets the true path of your image
            System.out.println("--------------333333333333333333----------------");
            while ((readLength = is.read()) != -1) {
                output.write(readLength); //Writes bytes to output stream
                // os.write(readLength); //Writes bytes to output stream
                System.out.println("--------------4444444444444444444----------------");
            }*/
            /*try {
                try {
                    while ((readLength = is.read()) != -1) {
                        os.write(readLength); //Writes bytes to output stream
                        System.out.println("--------------4444444444444444444----------------");
                    }
                } finally {
                    System.out.println("--------------5555555555555555555555----------------");
                    //Flushes and closes socket
                    os.flush();
                    os.close();
                }
            } finally {
                System.out.println("--------------6666666666666666666666----------------");
                is.close();
            }*/
        /*} catch (IOException e) {
            e.printStackTrace();
            System.out.println("--------------777777777777777777777----------------");
            client.stop();
        }*/

        // try {
            /*while (true) {
                client.handleMessage(dis.readUTF());
            }*/

            /*fos = new FileOutputStream(StartGameActivity.imageFileReceivePath);
            while (true) {
                while((readLength = is.read()) != -1){
                    fos.write(buffer, 0, readLength);
                }
                fos.flush();
            }*/

            /*while (true){
                while((readLength = is.read(buffer)) != -1){
                    baos.write(buffer, 0, readLength);
                }
                System.out.println("8888888888888888888888888888888");
                byteArray = baos.toByteArray();
                System.out.println(byteArray);
                createFile(StartGameActivity.imageFileReceivePath, byteArray);
            }*/

        // } catch (IOException e) {
            // client.stop();
        // }

    }

    //將byte陣列寫入檔案
    public void createFile(String path, byte[] content) throws IOException {
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(content);
        fos.close();
    }
}