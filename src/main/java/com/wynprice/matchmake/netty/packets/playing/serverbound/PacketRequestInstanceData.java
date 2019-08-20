package com.wynprice.matchmake.netty.packets.playing.serverbound;

import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.netty.packets.playing.clientbound.PacketSendInstanceData;

public class PacketRequestInstanceData {
    public static void handle(User user, PacketRequestInstanceData data) {
        user.getHandler().sendPacket(new PacketSendInstanceData(user.getInstance().createSyncData(user.getServer().getGameInstances().indexOf(user.getInstance()))));
    }
}
