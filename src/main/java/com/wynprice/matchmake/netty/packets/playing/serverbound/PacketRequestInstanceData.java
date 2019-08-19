package com.wynprice.matchmake.netty.packets.playing.serverbound;

import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.netty.packets.playing.clientbound.PacketSendInstanceData;
import io.netty.buffer.ByteBuf;
import lombok.extern.log4j.Log4j2;

public class PacketRequestInstanceData {
    public static void encode(PacketRequestInstanceData data, ByteBuf buf) {
        //NO-OP
    }

    public static PacketRequestInstanceData decode(ByteBuf buf) {
        return new PacketRequestInstanceData();
    }

    public static void handle(User user, PacketRequestInstanceData data) {
        user.getHandler().sendPacket(new PacketSendInstanceData(user.getInstance().createSyncData(user.getServer().getGameInstances().indexOf(user.getInstance()))));
    }
}
