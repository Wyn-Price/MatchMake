package com.wynprice.matchmake.netty.packets.playing.clientbound;

import com.wynprice.matchmake.game.User;
import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;

@Log4j2
@RequiredArgsConstructor
public class PacketServerEventHappen {

    private final int dataID;
    private final byte[] data;

    public static void encode(PacketServerEventHappen data, ByteBuf buf) {
        buf.writeInt(data.data.length);
        for (int i = 0; i < data.data.length; i++) {
            buf.writeByte(data.data[i]);
        }
        buf.writeInt(data.dataID);
    }

    public static PacketServerEventHappen decode(ByteBuf buf) {
        byte[] abyte = new byte[buf.readInt()];
        for (int i = 0; i < abyte.length; i++) {
            abyte[i] = buf.readByte();
        }
        return new PacketServerEventHappen(buf.readInt(), abyte);
    }

    public static void handle(User user, PacketServerEventHappen data) {
        log.info("Received server packet ID: {} with data {}", data.dataID, Arrays.toString(data.data));
    }
}
