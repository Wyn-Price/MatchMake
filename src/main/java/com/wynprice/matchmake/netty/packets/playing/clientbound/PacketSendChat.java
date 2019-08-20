package com.wynprice.matchmake.netty.packets.playing.clientbound;

import com.wynprice.matchmake.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PacketSendChat {

    private final String user;
    private final String message;

    public static void encode(PacketSendChat data, ByteBuf buf) {
        ByteBufUtils.writeString(data.user, buf);
        ByteBufUtils.writeString(data.message, buf);
    }

}

