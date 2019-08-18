package com.wynprice.matchmake.netty;

import com.wynprice.matchmake.netty.packets.PacketEntry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NetworkDataEncoder extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        this.encode0(ctx, msg, out);
    }

    private <T> void encode0(ChannelHandlerContext ctx, T msg, ByteBuf out) {
        PacketEntry<T> entry = ctx.channel().attr(NetworkHandler.CONNECTION_STATE_ATTRIBUTE_KEY).get().getEntry(msg);
        out.writeInt(entry.getIndex());
        entry.getEncoder().accept(msg, out);
    }
}
