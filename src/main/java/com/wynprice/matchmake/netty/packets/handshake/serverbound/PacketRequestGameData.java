package com.wynprice.matchmake.netty.packets.handshake.serverbound;

import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketSendGameData;
import io.netty.buffer.ByteBuf;

public class PacketRequestGameData {
    public static void encode(PacketRequestGameData data, ByteBuf buf) {
        //NO-OP
    }

    public static PacketRequestGameData decode(ByteBuf buf) {
        return new PacketRequestGameData();
    }

    public static void handle(User user, PacketRequestGameData data) {
        user.getHandler().sendPacket(new PacketSendGameData(user.getServer().createInstances()));
    }
}
