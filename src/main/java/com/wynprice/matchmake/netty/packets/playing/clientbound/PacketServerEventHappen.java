package com.wynprice.matchmake.netty.packets.playing.clientbound;

import com.wynprice.matchmake.game.User;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;

@Getter
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

}
