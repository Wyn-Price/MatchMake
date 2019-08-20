package com.wynprice.matchmake.netty.packets.handshake.serverbound;

import com.wynprice.matchmake.game.GameInstance;
import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.netty.ConnectionState;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketRejectionReason;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketServerAcceptUser;
import com.wynprice.matchmake.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.Charset;

@Log4j2
@AllArgsConstructor
@Getter
public class PacketJoinServer {
    private final int serverID;
    private final String username;


    public static PacketJoinServer decode(ByteBuf buf) {
        return new PacketJoinServer(buf.readInt(), buf.readBoolean() ? ByteBufUtils.readString(buf) : null);
    }

    public static void handle(User user, PacketJoinServer data) {
        if(data.username == null) {
            user.getHandler().sendPacket(new PacketRejectionReason("Username not set!"));
            return;
        }
        user.setUserName(data.username);
        GameInstance instance = user.getServer().getGameInstances().get(data.serverID);
        if(instance.tryAddUser(user, reason -> user.getHandler().sendPacket(new PacketRejectionReason(reason)))) {
            log.info("User: '{}' added to server: '{}'", user.getUserName(), instance.getGameName());
            user.getHandler().sendPacket(new PacketServerAcceptUser());
            user.getHandler().setConnectionState(ConnectionState.PLAYING);
        }
    }
}
