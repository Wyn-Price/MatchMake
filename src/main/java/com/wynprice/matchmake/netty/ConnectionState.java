package com.wynprice.matchmake.netty;

import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.netty.packets.BasePacketEntry;
import com.wynprice.matchmake.netty.packets.InboundPacketEntry;
import com.wynprice.matchmake.netty.packets.OutboundPacket;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketPong;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketRejectionReason;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketSendGameData;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketServerAcceptUser;
import com.wynprice.matchmake.netty.packets.handshake.serverbound.PacketJoinServer;
import com.wynprice.matchmake.netty.packets.handshake.serverbound.PacketPing;
import com.wynprice.matchmake.netty.packets.handshake.serverbound.PacketRequestGameData;
import com.wynprice.matchmake.netty.packets.playing.clientbound.PacketAcceptDisconnect;
import com.wynprice.matchmake.netty.packets.playing.clientbound.PacketSendChat;
import com.wynprice.matchmake.netty.packets.playing.clientbound.PacketSendInstanceData;
import com.wynprice.matchmake.netty.packets.playing.clientbound.PacketServerEventHappen;
import com.wynprice.matchmake.netty.packets.playing.serverbound.PacketClientEventHappen;
import com.wynprice.matchmake.netty.packets.playing.serverbound.PacketClientSayChat;
import com.wynprice.matchmake.netty.packets.playing.serverbound.PacketDisconnect;
import com.wynprice.matchmake.netty.packets.playing.serverbound.PacketRequestInstanceData;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public enum ConnectionState {
    HANDSHAKING,
    PLAYING;

    private final List<OutboundPacket> outboundPackets = new ArrayList<>();
    private final List<InboundPacketEntry> inboundPackets = new ArrayList<>();

    public <T> void registerOutboundPacket(int id, Class<T> clazz, BiConsumer<T, ByteBuf> encoder) {
        this.outboundPackets.add(new OutboundPacket<>(id, clazz, encoder));
    }

    public <T> void registerInboundPacket(int id, Class<T> clazz, Function<ByteBuf, T> decoder, BiConsumer<User, T> handler) {
        this.inboundPackets.add(new InboundPacketEntry<>(id, clazz, decoder, handler));
    }

    @SuppressWarnings("unchecked")
    public <T> OutboundPacket<T> getOutboundPacket(T obj) {
        return (OutboundPacket<T>) this.getFromObj(obj, this.outboundPackets);
    }

    public InboundPacketEntry getInboundPacket(int id) {
        return this.getFromID(id, this.inboundPackets);
    }

    @SuppressWarnings("unchecked")
    public <T> InboundPacketEntry<T> getInboundPacket(T obj) {
        return (InboundPacketEntry<T>) this.getFromObj(obj, this.inboundPackets);
    }

    private <T extends BasePacketEntry> T getFromID(int id, List<T> list) {
        return this.getEntry(e -> e.getIndex() == id, list, "Could not find packet for ID " + id);
    }

    private <T extends BasePacketEntry> T getFromObj(Object object, List<T> list) {
        return this.getEntry(e -> e.getClazz() == object.getClass(), list, "Could not find packet for class " + object.getClass());
    }
    private <T> T getEntry(Predicate<T> predicate, List<T> list, String errorMessage) {
        return list.stream().filter(predicate).findAny().orElseThrow(() -> new IllegalArgumentException(errorMessage + " for state " + this.name()));
    }


    private static <T> Function<ByteBuf, T> emptyDecoder(Supplier<T> supplier) {
        return buff -> supplier.get();
    }

    private static <T> BiConsumer<T, ByteBuf> emptyEncoder() {
        return (o, buf) -> {};
    }

    static {
        HANDSHAKING.registerInboundPacket(0, PacketRequestGameData.class, emptyDecoder(PacketRequestGameData::new), PacketRequestGameData::handle);
        HANDSHAKING.registerInboundPacket(1, PacketPing.class, emptyDecoder(PacketPing::new), PacketPing::handle);
        HANDSHAKING.registerInboundPacket(2, PacketJoinServer.class, PacketJoinServer::decode, PacketJoinServer::handle);

        HANDSHAKING.registerOutboundPacket(0, PacketSendGameData.class, PacketSendGameData::encode);
        HANDSHAKING.registerOutboundPacket(1, PacketPong.class, emptyEncoder());
        HANDSHAKING.registerOutboundPacket(2, PacketRejectionReason.class, PacketRejectionReason::encode);
        HANDSHAKING.registerOutboundPacket(3, PacketServerAcceptUser.class, emptyEncoder());

        PLAYING.registerInboundPacket(0, PacketRequestInstanceData.class, emptyDecoder(PacketRequestInstanceData::new), PacketRequestInstanceData::handle);
        PLAYING.registerInboundPacket(1, PacketDisconnect.class, emptyDecoder(PacketDisconnect::new), PacketDisconnect::handle);
        PLAYING.registerInboundPacket(2, PacketClientSayChat.class, PacketClientSayChat::decode, PacketClientSayChat::handle);
        PLAYING.registerInboundPacket(3, PacketClientEventHappen.class, PacketClientEventHappen::decode, PacketClientEventHappen::handle);

        PLAYING.registerOutboundPacket(0, PacketSendInstanceData.class, PacketSendInstanceData::encode);
        PLAYING.registerOutboundPacket(1, PacketAcceptDisconnect.class, emptyEncoder());
        PLAYING.registerOutboundPacket(2, PacketSendChat.class, PacketSendChat::encode);
        PLAYING.registerOutboundPacket(3, PacketServerEventHappen.class, PacketServerEventHappen::encode);
    }

}
