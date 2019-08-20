package com.wynprice.matchmake.netty.packets.handshake.serverbound;

import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketSendGameData;

public class PacketRequestGameData {
    public static void handle(User user, PacketRequestGameData data) {
        user.getHandler().sendPacket(new PacketSendGameData(user.getServer().createInstances()));
    }
}
