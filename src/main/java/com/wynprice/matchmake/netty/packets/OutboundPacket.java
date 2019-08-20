package com.wynprice.matchmake.netty.packets;

import com.wynprice.matchmake.game.User;
import io.netty.buffer.ByteBuf;
import lombok.Value;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Value
public class OutboundPacket<T> implements BasePacketEntry {
    private final int index;
    private final Class<T> clazz;
    private final BiConsumer<T, ByteBuf> encoder;
}
