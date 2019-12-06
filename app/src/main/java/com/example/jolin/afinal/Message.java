package com.example.jolin.afinal;

import java.io.Serializable;

public class Message implements Serializable {
    static final int MUSCLE = 0, BYTELEN = 1;
    private int type;
    private double Message;
    private int intMessage;

    // constructor
    Message(int type, double Message) {
        this.type = type;
        this.Message = Message;
    }

    int getType() {
        return type;
    }

    int getIntMessage() {
        intMessage = (int) Message;
        return intMessage;
    }

    double getDoubleMessage(){
        return Message;
    }
}
