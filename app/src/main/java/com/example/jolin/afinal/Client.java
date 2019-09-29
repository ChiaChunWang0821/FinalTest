package com.example.jolin.afinal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


//The Client that can be run as a console
public class Client  {

    // notification
    private String notif = " *** ";

    // for I/O
    private static ObjectInputStream sInput;		// to read from the socket
    private static ObjectOutputStream sOutput;		// to write on the socket
    private static Socket socket;					// socket object

    private String server, username;	// server and username
    private int port;					//port

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /*
     *  Constructor to set below things
     *  server: the server address
     *  port: the port number
     *  username: the username
     */

    Client(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.username = username;
    }

    /*
     * To start the chat
     */
    public boolean start() {
        // try to connect to the server
        try {
            socket = new Socket(server, port);
        }
        // exception handler if it failed
        catch(Exception ec) {
            display("Error connection to server:" + ec);
            return false;
        }

        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        display(msg);

        /* Creating both Data Stream */
        try
        {
            sInput  = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException eIO) {
            display("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        // creates the Thread to listen from the server
        new ListenFromServer().start();
        // Send our username to the server this is the only message that we
        // will send as a String. All other messages will be ChatMessage objects
        try
        {
            sOutput.writeObject(username);
        }
        catch (IOException eIO) {
            display("Exception doing login : " + eIO);
            disconnect();
            return false;
        }
        // success we inform the caller that it worked
        return true;
    }

    /*
     * To send a message to the console
     */
    private static void display(String msg) {

        System.out.println(msg);

    }

    /*
     * To send a message to the server
     */
    static void sendMessage(ChatMessage msg) {
        try {
            System.out.println("senddddddddddddddddddddddddddddd");
            System.out.println(msg);
            sOutput.writeObject(msg);
            System.out.println("send############################");
        }
        catch(IOException e) {
            display("Exception writing to server: " + e);
        }
    }

    /*
     * When something goes wrong
     * Close the Input/Output streams and disconnect
     */
    public static void disconnect() {
        try {
            if(sInput != null) sInput.close();
        }
        catch(Exception e) {}
        try {
            if(sOutput != null) sOutput.close();
        }
        catch(Exception e) {}
        try{
            if(socket != null) socket.close();
        }
        catch(Exception e) {}

    }
    /*
     * To start the Client in console mode use one of the following command
     * > java Client
     * > java Client username
     * > java Client username portNumber
     * > java Client username portNumber serverAddress
     * at the console prompt
     * If the portNumber is not specified 1500 is used
     * If the serverAddress is not specified "localHost" is used
     * If the username is not specified "Anonymous" is used
     */
    public static void connect() {
        // default values if not entered
        int portNumber = 5002;
        String serverAddress = "140.121.197.165";
        String userName = "Player1";
        try {
            InetAddress addr = InetAddress.getLocalHost();
            userName = addr.toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // create the Client object
        Client client = new Client(serverAddress, portNumber, userName);
        // try to connect to the server and return if not connected
        if(!client.start())
            return;
    }

    /*
     * a class that waits for the message from the server
     */
    class ListenFromServer extends Thread {

        public void run() {
            while(true) {
                try {
                    // read the message form the input datastream
                    String msg = (String) sInput.readObject();
                    // print the message
                    System.out.println(msg);
                    System.out.print("> ");
                }
                catch(IOException e) {
                    display(notif + "Server has closed the connection: " + e + notif);
                    break;
                }
                catch(ClassNotFoundException e2) {
                }
            }
        }
    }
}

/*import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

// Client for Server4
public class Client implements Runnable {

    private String serverName = "localhost";
    private int serverPort = 8081;
    private Socket socket = null;
    private Thread thread = null;
    private DataInputStream dis = null;
    private DataOutputStream dos = null;
    private ChatClientThread client = null;

    public Client() {
        try {
            socket = new Socket(serverName, serverPort);
            System.out.println("Client started on port " + socket.getLocalPort() + "...");
            System.out.println("Connected to server " + socket.getRemoteSocketAddress());

            dis = new DataInputStream(System.in);
            dos = new DataOutputStream(socket.getOutputStream());
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
            try {
                System.out.print("Message to server : ");
                dos.writeUTF(dis.readLine());
                dos.flush();

                // Sleep, because this thread must wait ChatClientThread to show the message first
                try {
                    thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e.getMessage());
                }
            } catch (IOException e) {
                System.out.println("Sending error : " + e.getMessage());
                stop();
            }
        }
    }

    public void handleMessage(String message) {
        if (message.equals("exit")) {
            stop();
        } else {
            System.out.println(message);
            System.out.print("Message to server : ");
        }
    }

    public void stop() {
        try {
            thread = null;
            dis.close();
            dos.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Error closing : " + e.getMessage());
        }
        client.close();
    }

    // public static void main(String args[]) {
       //  Client client = new Client();
    // }
}*/