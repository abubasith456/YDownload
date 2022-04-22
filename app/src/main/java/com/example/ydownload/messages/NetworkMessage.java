package com.example.ydownload.messages;

public class NetworkMessage {

    boolean isConnected;

    public NetworkMessage(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
