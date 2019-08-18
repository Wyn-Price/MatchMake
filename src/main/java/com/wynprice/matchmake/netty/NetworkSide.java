package com.wynprice.matchmake.netty;

public enum NetworkSide {
    CLIENTSIDE,
    SERVERSIDE;

    public String getLoggerName() {
        return this == SERVERSIDE ? "ServerSide" : "ClientSide";
    }
}
