package com.wynprice.matchmake.netty.packets.handshake.serverbound;

import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketPong;
import io.netty.buffer.ByteBuf;

public class PacketPing {
    public static void encode(PacketPing data, ByteBuf buf) {
        //NO-OP
    }

    public static PacketPing decode(ByteBuf buf) {
        return new PacketPing();
    }

    public static void handle(User user, PacketPing data) {
        user.getHandler().sendPacket(new PacketPong());
    }
}
