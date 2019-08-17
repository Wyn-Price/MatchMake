package com.wynprice.matchmake.netty;

import com.wynprice.matchmake.game.GameInstance;
import io.netty.buffer.ByteBuf;

import java.util.function.BiConsumer;
import java.util.function.Function;

public enum ConnectionState {
    SERVER_DATA,
    HANDSHAKING,
    PLAYING;

    public <T> void registerPacket(BiConsumer<T, ByteBuf> encoder, Function<ByteBuf, T> decoder, BiConsumer<GameInstance, T> handler) {

    }

    static {

//        SERVER_DATA.registerPacket();

    }

}
