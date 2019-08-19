package com.wynprice.matchmake.netty;

import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.netty.packets.PacketEntry;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketPong;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketRejectionReason;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketSendGameData;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketServerAcceptUser;
import com.wynprice.matchmake.netty.packets.handshake.serverbound.PacketJoinServer;
import com.wynprice.matchmake.netty.packets.handshake.serverbound.PacketPing;
import com.wynprice.matchmake.netty.packets.handshake.serverbound.PacketRequestGameData;
import com.wynprice.matchmake.netty.packets.playing.clientbound.PacketAcceptDisconnect;
import com.wynprice.matchmake.netty.packets.playing.clientbound.PacketSendChat;
import com.wynprice.matchmake.netty.packets.playing.serverbound.PacketClientSayChat;
import com.wynprice.matchmake.netty.packets.playing.serverbound.PacketDisconnect;
import com.wynprice.matchmake.netty.packets.playing.clientbound.PacketSendInstanceData;
import com.wynprice.matchmake.netty.packets.playing.serverbound.PacketRequestInstanceData;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum ConnectionState {
    HANDSHAKING,
    PLAYING;

    private List<PacketEntry<?>> entries = new ArrayList<>();

    public <T> void registerPacket(Class<T> clazz, BiConsumer<T, ByteBuf> encoder, Function<ByteBuf, T> decoder, BiConsumer<User, T> handler) {
        this.entries.add(new PacketEntry<>(this.entries.size(), clazz, encoder, decoder, handler));
    }

    public PacketEntry<?> getEntry(int id) {
        return this.entries.get(id);
    }

    @SuppressWarnings("unchecked")
    public <T> PacketEntry<T> getEntry(T obj) {
        return (PacketEntry<T>) this.entries.stream().filter(e -> obj.getClass() == e.getClazz()).findAny().orElseThrow(() -> new IllegalArgumentException("Could not find packet registered with class " + obj.getClass() + " for state " + this.name()));
    }


    static {
        HANDSHAKING.registerPacket(PacketRequestGameData.class, PacketRequestGameData::encode, PacketRequestGameData::decode, PacketRequestGameData::handle);
        HANDSHAKING.registerPacket(PacketSendGameData.class, PacketSendGameData::encode, PacketSendGameData::decode, PacketSendGameData::handle);
        HANDSHAKING.registerPacket(PacketPing.class, PacketPing::encode, PacketPing::decode, PacketPing::handle);
        HANDSHAKING.registerPacket(PacketPong.class, PacketPong::encode, PacketPong::decode, PacketPong::handle);
        HANDSHAKING.registerPacket(PacketJoinServer.class, PacketJoinServer::encode, PacketJoinServer::decode, PacketJoinServer::handle);
        HANDSHAKING.registerPacket(PacketRejectionReason.class, PacketRejectionReason::encode, PacketRejectionReason::decode, PacketRejectionReason::handle);
        HANDSHAKING.registerPacket(PacketServerAcceptUser.class, PacketServerAcceptUser::encode, PacketServerAcceptUser::decode, PacketServerAcceptUser::handle);

        PLAYING.registerPacket(PacketRequestInstanceData.class, PacketRequestInstanceData::encode, PacketRequestInstanceData::decode, PacketRequestInstanceData::handle);
        PLAYING.registerPacket(PacketSendInstanceData.class, PacketSendInstanceData::encode, PacketSendInstanceData::decode, PacketSendInstanceData::handle);
        PLAYING.registerPacket(PacketDisconnect.class, PacketDisconnect::encode, PacketDisconnect::decode, PacketDisconnect::handle);
        PLAYING.registerPacket(PacketAcceptDisconnect.class, PacketAcceptDisconnect::encode, PacketAcceptDisconnect::decode, PacketAcceptDisconnect::handle);
        PLAYING.registerPacket(PacketClientSayChat.class, PacketClientSayChat::encode, PacketClientSayChat::decode, PacketClientSayChat::handle);
        PLAYING.registerPacket(PacketSendChat.class, PacketSendChat::encode, PacketSendChat::decode, PacketSendChat::handle);
    }

}
