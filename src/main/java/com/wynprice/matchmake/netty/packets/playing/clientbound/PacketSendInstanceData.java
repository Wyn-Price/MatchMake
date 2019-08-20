package com.wynprice.matchmake.netty.packets.playing.clientbound;

import com.wynprice.matchmake.game.GameInstance;
import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.IntStream;

@Getter
@RequiredArgsConstructor
public class PacketSendInstanceData {

    private final GameInstance.GameSyncedData data;

    public static void encode(PacketSendInstanceData data, ByteBuf buf) {
        GameInstance.GameSyncedData datum = data.data;
        ByteBufUtils.writeString(datum.getGameName(), buf);
        ByteBufUtils.writeString(datum.getGameDescription(), buf);

        buf.writeInt(datum.getId());
        buf.writeInt(datum.getMaxUsers());

        buf.writeShort(datum.getCurrentUsers().length);
        for (String user : datum.getCurrentUsers()) {
            ByteBufUtils.writeString(user, buf);
        }
    }
}
