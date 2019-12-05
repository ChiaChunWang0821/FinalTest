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
    private int message;

    // constructor
    ChatMessage(int type, int message) {
        this.type = type;
        this.message = message;
    }

    int getType() {
        return type;
    }

    int getMessage() {
        return message;
    }
}