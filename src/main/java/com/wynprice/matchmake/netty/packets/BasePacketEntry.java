package com.wynprice.matchmake.netty.packets;

public interface BasePacketEntry {
    int getIndex();
    Class<?> getClazz();
}
