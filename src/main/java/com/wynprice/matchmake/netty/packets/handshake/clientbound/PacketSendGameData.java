package com.wynprice.matchmake.netty.packets.handshake.clientbound;

import com.wynprice.matchmake.game.GameInstance;
import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;

import java.nio.charset.Charset;
import java.util.stream.IntStream;

import static com.wynprice.matchmake.util.ByteBufUtils.writeString;

@RequiredArgsConstructor
public class PacketSendGameData {

    private static final Charset CHARSET = Charset.forName("UTF-8");

    private final GameInstance.GameSyncedData[] data;

    public static void encode(PacketSendGameData data, ByteBuf buf) {
        buf.writeInt(data.data.length);
        for (GameInstance.GameSyncedData datum : data.data) {
            writeString(datum.getGameName(), buf);
            writeString(datum.getGameDescription(), buf);

            buf.writeInt(datum.getId());
            buf.writeInt(datum.getMaxUsers());

            buf.writeShort(datum.getCurrentUsers().length);
            for (String user : datum.getCurrentUsers()) {
                writeString(user, buf);
            }
        }
    }

    public static PacketSendGameData decode(ByteBuf buf) {
        GameInstance.GameSyncedData[] data = new GameInstance.GameSyncedData[buf.readInt()];
        for (int i = 0; i < data.length; i++) {
            data[i] = new GameInstance.GameSyncedData(
                    ByteBufUtils.readString(buf),
                    ByteBufUtils.readString(buf),
                    buf.readInt(),
                    buf.readInt(),
                    IntStream.range(0, buf.readInt()).mapToObj(_o -> ByteBufUtils.readString(buf)).toArray(String[]::new)
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
            System.out.println("Users: " + datum.getCurrentUsers().length + "/" + datum.getMaxUsers() + (datum.getCurrentUsers().length != 0 ? " (" + String.join(", ", datum.getCurrentUsers()) + ")" : ""));
            System.out.println("--------------------------------");
        }
    }
}
