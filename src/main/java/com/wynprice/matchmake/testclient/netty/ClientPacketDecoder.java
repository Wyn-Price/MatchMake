package com.wynprice.matchmake.testclient.netty;

import com.wynprice.matchmake.game.GameInstance;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketRejectionReason;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketSendGameData;
import com.wynprice.matchmake.netty.packets.playing.clientbound.PacketSendChat;
import com.wynprice.matchmake.netty.packets.playing.clientbound.PacketSendInstanceData;
import com.wynprice.matchmake.netty.packets.playing.clientbound.PacketServerEventHappen;
import com.wynprice.matchmake.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;

import java.util.stream.IntStream;

public class ClientPacketDecoder {

    public static PacketSendGameData decodeSendData(ByteBuf buf) {
        return new PacketSendGameData(
                IntStream.range(0, buf.readInt()).mapToObj(unused ->
                        new GameInstance.GameSyncedData(
                                ByteBufUtils.readString(buf), ByteBufUtils.readString(buf), buf.readInt(), buf.readInt(),
                                IntStream.range(0, buf.readShort()).mapToObj(unused1 -> ByteBufUtils.readString(buf)).toArray(String[]::new)
                        )
                ).toArray(GameInstance.GameSyncedData[]::new)
        );
    }

    public static PacketRejectionReason decodeRejectionReason(ByteBuf buf) {
        return new PacketRejectionReason(ByteBufUtils.readString(buf));
    }

    public static PacketSendInstanceData decodeSendInstanceData(ByteBuf buf) {
        return new PacketSendInstanceData(new GameInstance.GameSyncedData(
                ByteBufUtils.readString(buf), ByteBufUtils.readString(buf), buf.readInt(), buf.readInt(),
                IntStream.range(0, buf.readShort()).mapToObj(unused1 -> ByteBufUtils.readString(buf)).toArray(String[]::new)
        ));
    }

    public static PacketSendChat decodeSendChat(ByteBuf buf) {
        return new PacketSendChat(ByteBufUtils.readString(buf), ByteBufUtils.readString(buf));
    }

    public static PacketServerEventHappen decodeServerEvent(ByteBuf buf) {
        byte[] abyte = new byte[buf.readInt()];
        for (int i = 0; i < abyte.length; i++) {
            abyte[i] = buf.readByte();
        }
        return new PacketServerEventHappen(buf.readInt(), abyte);
    }


}
