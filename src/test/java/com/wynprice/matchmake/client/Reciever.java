package com.wynprice.matchmake.client;

public class Reciever<T> {
    private T object;

    public boolean isRecieved() {
        return this.object != null;
    }

    public void recieve(T object) {
        this.object = object;
    }

    public T getRecievedObject() {
        return this.object;
    }
}
