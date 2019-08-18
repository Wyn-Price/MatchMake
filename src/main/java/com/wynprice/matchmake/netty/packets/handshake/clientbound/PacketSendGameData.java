package com.wynprice.matchmake.netty.packets.handshake.clientbound;

import com.wynprice.matchmake.game.GameInstance;
import com.wynprice.matchmake.game.User;
import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;

import java.nio.charset.Charset;
import java.util.Arrays;

@RequiredArgsConstructor
public class PacketSendGameData {

    private static final Charset CHARSET = Charset.forName("UTF-8");

    private final GameInstance.GameSyncedData[] data;

    public static void encode(PacketSendGameData data, ByteBuf buf) {
        buf.writeInt(data.data.length);
        for (GameInstance.GameSyncedData datum : data.data) {
            buf.writeInt(datum.getGameName().length());
            buf.writeCharSequence(datum.getGameName(), CHARSET);

            buf.writeInt(datum.getGameDescription().length());
            buf.writeCharSequence(datum.getGameDescription(), CHARSET);

            buf.writeInt(datum.getId());

            buf.writeInt(datum.getMaxUsers());
            buf.writeInt(datum.getCurrentUsers());
        }
    }

    public static PacketSendGameData decode(ByteBuf buf) {
        GameInstance.GameSyncedData[] data = new GameInstance.GameSyncedData[buf.readInt()];
        for (int i = 0; i < data.length; i++) {
            data[i] = new GameInstance.GameSyncedData(
                    buf.readCharSequence(buf.readInt(), CHARSET).toString(),
                    buf.readCharSequence(buf.readInt(), CHARSET).toString(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt()
            );
        }
        return new PacketSendGameData(data);
    }

    public static void handle(User user, PacketSendGameData data) {
        for (GameInstance.GameSyncedData datum : data.data) {
            System.out.println("--------------------------------");
            System.out.println("ID:    " + datum.getId());
            System.out.println("Name:  " + datum.getGameName());
            System.out.println("Desc:  " + datum.getGameDescription());
            System.out.println("Users: " + datum.getCurrentUsers() + "/" + datum.getMaxUsers());
            System.out.println("--------------------------------");
        }
    }
}
