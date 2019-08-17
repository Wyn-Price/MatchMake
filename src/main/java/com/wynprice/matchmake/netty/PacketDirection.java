package com.wynprice.matchmake.netty;

public enum PacketDirection {
    TO_CLIENT,
    TO_SERVER;

    public String getLoggerName() {
        return this == TO_CLIENT ? "ServerSide" : "ClientSide";
    }
}
