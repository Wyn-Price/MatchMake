package com.wynprice.matchmake.netty.packets.playing.serverbound;

import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.netty.ConnectionState;
import com.wynprice.matchmake.netty.packets.playing.clientbound.PacketAcceptDisconnect;
import io.netty.buffer.ByteBuf;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PacketDisconnect {
    public static void encode(PacketDisconnect data, ByteBuf buf) {
        //NO-OP
    }

    public static PacketDisconnect decode(ByteBuf buf) {
        return new PacketDisconnect();
    }

    public static void handle(User user, PacketDisconnect data) {
        log.info("User: '{}' disconnected from server '{}'", user.getUserName(), user.getInstance().getGameName());
        user.getInstance().disconnect(user, log::error);
        user.getHandler().sendPacket(new PacketAcceptDisconnect());
        user.getHandler().setConnectionState(ConnectionState.HANDSHAKING);
    }
}
