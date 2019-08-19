package com.wynprice.matchmake.netty.packets.playing.clientbound;

import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.netty.ConnectionState;
import io.netty.buffer.ByteBuf;

public class PacketAcceptDisconnect {

    public static void encode(PacketAcceptDisconnect data, ByteBuf buf) {
    }

    public static PacketAcceptDisconnect decode(ByteBuf buf) {
        return new PacketAcceptDisconnect();
    }

    public static void handle(User user, PacketAcceptDisconnect data) {
        user.getHandler().setConnectionState(ConnectionState.HANDSHAKING);
    }
}
