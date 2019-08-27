package com.wynprice.matchmake.netty.packets;

import com.wynprice.matchmake.game.User;
import lombok.Value;

import java.util.function.BiFunction;

@Value
public class InboundPacketIntercept<T> {
    private final Class<T> clazz;
    private final BiFunction<User, T, Boolean> handler;
}
