package com.wynprice.matchmake.netty.packets.playing.serverbound;

import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.netty.packets.playing.clientbound.PacketSendChat;
import com.wynprice.matchmake.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PacketClientSayChat {

    private final String message;

    public static PacketClientSayChat decode(ByteBuf buf) {
        return new PacketClientSayChat(ByteBufUtils.readString(buf));
    }

    public static void handle(User user, PacketClientSayChat data) {
        user.getInstance().getUsers().forEach(u -> u.getHandler().sendPacket(new PacketSendChat(user.getUserName(), data.message)));
    }
}
