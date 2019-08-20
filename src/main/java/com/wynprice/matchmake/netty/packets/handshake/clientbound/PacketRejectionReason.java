package com.wynprice.matchmake.netty.packets.handshake.clientbound;

import com.wynprice.matchmake.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PacketRejectionReason {
    private final String reason;
    public static void encode(PacketRejectionReason data, ByteBuf buf) {
        ByteBufUtils.writeString(data.reason, buf);
    }
}
