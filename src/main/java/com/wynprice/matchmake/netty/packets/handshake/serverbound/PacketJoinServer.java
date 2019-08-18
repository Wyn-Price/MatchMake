package com.wynprice.matchmake.netty.packets.handshake.serverbound;

import com.wynprice.matchmake.game.GameInstance;
import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.netty.ConnectionState;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketPong;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketRejectionReason;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketServerAcceptUser;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PacketJoinServer {
    private final int serverID;
    public static void encode(PacketJoinServer data, ByteBuf buf) {
        buf.writeInt(data.serverID);
    }

    public static PacketJoinServer decode(ByteBuf buf) {
        return new PacketJoinServer(buf.readInt());
    }

    public static void handle(User user, PacketJoinServer data) {
        GameInstance instance = user.getServer().getGameInstances().get(data.serverID);
        if(instance.tryAddUser(user, reason -> user.getHandler().sendPacket(new PacketRejectionReason(reason)))) {
            user.getHandler().sendPacket(new PacketServerAcceptUser());
//            user.getHandler().setConnectionState(ConnectionState.PLAYING);
        }
    }
}
