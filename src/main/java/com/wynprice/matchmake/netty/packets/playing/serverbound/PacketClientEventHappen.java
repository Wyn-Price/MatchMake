package com.wynprice.matchmake.netty.packets.playing.serverbound;

import com.wynprice.matchmake.game.User;
import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class PacketClientEventHappen {

    private final int dataID;
    private final byte[] data;

    public static void encode(PacketClientEventHappen data, ByteBuf buf) {
        buf.writeInt(data.data.length);
        for (int i = 0; i < data.data.length; i++) {
            buf.writeByte(data.data[i]);
        }
        buf.writeInt(data.dataID);
    }

    public static PacketClientEventHappen decode(ByteBuf buf) {
        byte[] abyte = new byte[buf.readInt()];
        for (int i = 0; i < abyte.length; i++) {
            abyte[i] = buf.readByte();
        }
        return new PacketClientEventHappen(buf.readInt(), abyte);
    }

    public static void handle(User user, PacketClientEventHappen data) {
        user.getInstance().clientDataPacket(data.dataID, data.data);
    }
}
