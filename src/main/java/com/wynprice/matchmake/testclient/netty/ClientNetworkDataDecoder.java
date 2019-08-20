package com.wynprice.matchmake.testclient.netty;

import com.wynprice.matchmake.netty.NetworkHandler;
import com.wynprice.matchmake.netty.packets.InboundPacketEntry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ClientNetworkDataDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        InboundPacketEntry<?> entry = ctx.channel().attr(ClientNetworkHandler.CLIENT_CONNECTION_STATE_ATTRIBUTE_KEY).get().getInboundPacket(in.readInt());
        out.add(entry.getDecoder().apply(in));
    }
}
