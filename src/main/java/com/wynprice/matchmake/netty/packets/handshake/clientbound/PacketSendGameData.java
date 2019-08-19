package com.wynprice.matchmake.netty.packets.handshake.clientbound;

import com.wynprice.matchmake.game.GameInstance;
import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;

import java.util.stream.IntStream;

@RequiredArgsConstructor
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

    public static PacketSendGameData decode(ByteBuf buf) {
        return new PacketSendGameData(
                IntStream.range(0, buf.readInt()).mapToObj(unused ->
                        new GameInstance.GameSyncedData(
                                ByteBufUtils.readString(buf), ByteBufUtils.readString(buf), buf.readInt(), buf.readInt(),
                                IntStream.range(0, buf.readShort()).mapToObj(unused1 -> ByteBufUtils.readString(buf)).toArray(String[]::new)
                        )
                ).toArray(GameInstance.GameSyncedData[]::new)
        );
    }

    public static void handle(User user, PacketSendGameData data) {
        for (GameInstance.GameSyncedData datum : data.data) {
            System.out.println("--------------------------------");
            System.out.println("ID:    " + datum.getId());
            System.out.println("Name:  " + datum.getGameName());
            System.out.println("Desc:  " + datum.getGameDescription());
            System.out.println("Users: " + datum.getCurrentUsers().length + "/" + datum.getMaxUsers() + (datum.getCurrentUsers().length != 0 ? " (" + String.join(", ", datum.getCurrentUsers()) + ")" : ""));
            System.out.println("--------------------------------");
        }
    }
}
