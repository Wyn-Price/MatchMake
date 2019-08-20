package com.wynprice.matchmake.netty.packets;

import com.wynprice.matchmake.game.User;
import io.netty.buffer.ByteBuf;
import lombok.Value;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Value
public class InboundPacketEntry<T> implements BasePacketEntry {
    private final int index;
    private final Class<T> clazz;
    private final Function<ByteBuf, T> decoder;
    private final BiConsumer<User, T> handler;
}
