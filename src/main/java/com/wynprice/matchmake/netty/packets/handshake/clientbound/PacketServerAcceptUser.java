package com.wynprice.matchmake.netty.packets.handshake.clientbound;

import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.netty.ConnectionState;
import io.netty.buffer.ByteBuf;

public class PacketServerAcceptUser {
    public static void encode(PacketServerAcceptUser data, ByteBuf buf) {
        //NO-OP
    }

    public static PacketServerAcceptUser decode(ByteBuf buf) {
        return new PacketServerAcceptUser();
    }

    public static void handle(User user, PacketServerAcceptUser data) {
        System.out.println("Server Accepted User");
        user.getHandler().setConnectionState(ConnectionState.PLAYING);
    }
}
