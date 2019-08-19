package com.wynprice.matchmake.netty.packets.handshake.clientbound;

import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;

import java.nio.charset.Charset;

@RequiredArgsConstructor
public class PacketRejectionReason {
    private static final Charset CHARSET = Charset.forName("UTF-8");

    private final String reason;

    public static void encode(PacketRejectionReason data, ByteBuf buf) {
        ByteBufUtils.writeString(data.reason, buf);
    }

    public static PacketRejectionReason decode(ByteBuf buf) {
        return new PacketRejectionReason(ByteBufUtils.readString(buf));
    }

    public static void handle(User user, PacketRejectionReason data) {
        System.out.println("Rejected from server for reason " + data.reason);
    }
}
