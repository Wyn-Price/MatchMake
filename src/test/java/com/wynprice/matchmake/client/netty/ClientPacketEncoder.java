package com.wynprice.matchmake.client.netty;

import com.wynprice.matchmake.netty.packets.handshake.serverbound.PacketJoinServer;
import com.wynprice.matchmake.netty.packets.playing.serverbound.PacketClientEventHappen;
import com.wynprice.matchmake.netty.packets.playing.serverbound.PacketClientSayChat;
import com.wynprice.matchmake.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;

public class ClientPacketEncoder {
    public static void encodeJoinServer(PacketJoinServer data, ByteBuf buf) {
        buf.writeInt(data.getServerID());

        buf.writeBoolean(data.getUsername() != null);
        if(data.getUsername() != null) {
            ByteBufUtils.writeString(data.getUsername(), buf);
        }
    }

    public static void encodeSayChat(PacketClientSayChat data, ByteBuf buf) {
        ByteBufUtils.writeString(data.getMessage(), buf);
    }

    public static void encodeEventHappen(PacketClientEventHappen data, ByteBuf buf) {
        buf.writeInt(data.getData().length);
        for (int i = 0; i < data.getData().length; i++) {
            buf.writeByte(data.getData()[i]);
        }
        buf.writeInt(data.getDataID());
    }


}
