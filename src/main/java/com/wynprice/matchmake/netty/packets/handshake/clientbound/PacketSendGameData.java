package com.wynprice.matchmake.netty.packets.handshake.clientbound;

import com.wynprice.matchmake.game.GameInstance;
import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PacketSendGameData {

    private final GameInstance.GameSyncedData[] data;

    public static void encode(PacketSendGameData data, ByteBuf buf) {
        buf.writeInt(data.data.length);
        for (GameInstance.GameSyncedData datum : data.data) {
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
}
