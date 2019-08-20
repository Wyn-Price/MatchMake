package com.wynprice.matchmake.netty.packets.handshake.serverbound;

import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketPong;

public class PacketPing {
    public static void handle(User user, PacketPing data) {
        user.getHandler().sendPacket(new PacketPong());
    }
}
