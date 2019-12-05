package com.example.jolin.afinal;

import java.io.*;
/*
 * This class defines the different type of messages that will be exchanged between the
 * Clients and the Server.
 * When talking from a Java Client to a Java Server a lot easier to pass Java objects, no
 * need to count bytes or to wait for a line feed at the end of the frame
 */

public class ChatMessage implements Serializable {

    // The different types of message sent by the Client
    // WHOISIN to receive the list of the users connected
    // MESSAGE an ordinary text message
    // LOGOUT to disconnect from the Server
    static final int MUSCLE = 0, BYTELEN = 1, BYTEFILE = 2;
    private int type;
    private int intMessage;
    private double doubleMessage;
    // private byte[] byteMessage;

    // constructor
    ChatMessage(int type, int intMessage) {
        this.type = type;
        this.intMessage = intMessage;
    }

    ChatMessage(int type, double doubleMessage) {
        this.type = type;
        this.doubleMessage = doubleMessage;
    }

    /*ChatMessage(int type, byte[] byteMessage) {
        this.type = type;
        this.byteMessage = byteMessage;
    }*/

    int getType() {
        return type;
    }

    int getIntMessage() {
        return intMessage;
    }

    double getDoubleMessage(){
        return doubleMessage;
    }

    /*byte[] getByteMessage(){
        return byteMessage;
    }*/
}