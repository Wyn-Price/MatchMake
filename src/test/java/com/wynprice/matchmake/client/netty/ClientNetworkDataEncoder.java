package com.wynprice.matchmake.client.netty;

import com.wynprice.matchmake.netty.packets.OutboundPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ClientNetworkDataEncoder extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        this.encode0(ctx, msg, out);
    }

    private <T> void encode0(ChannelHandlerContext ctx, T msg, ByteBuf out) {
        OutboundPacket<T> entry = ctx.channel().attr(ClientNetworkHandler.CLIENT_CONNECTION_STATE_ATTRIBUTE_KEY).get().getOutboundPacket(msg);
        out.writeInt(entry.getIndex());
        entry.getEncoder().accept(msg, out);
    }
}