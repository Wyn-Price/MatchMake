package com.wynprice.matchmake.netty.packets.playing.clientbound;

import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.netty.ConnectionState;
import com.wynprice.matchmake.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
public class PacketSendChat {

    private final String user;
    private final String message;

    public static void encode(PacketSendChat data, ByteBuf buf) {
        ByteBufUtils.writeString(data.user, buf);
        ByteBufUtils.writeString(data.message, buf);
    }

    public static PacketSendChat decode(ByteBuf buf) {
        return new PacketSendChat(ByteBufUtils.readString(buf), ByteBufUtils.readString(buf));
    }

    public static void handle(User user, PacketSendChat data) {
        System.out.println(data.user + ": " + data.message);
    }
}
