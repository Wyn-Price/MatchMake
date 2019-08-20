package com.wynprice.matchmake.testclient.netty;

import com.wynprice.matchmake.netty.ConnectionState;
import com.wynprice.matchmake.netty.NetworkHandler;
import com.wynprice.matchmake.netty.NetworkSide;
import com.wynprice.matchmake.netty.packets.InboundPacketEntry;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class ClientNetworkHandler extends NetworkHandler {

    public static final AttributeKey<ClientConnectionState> CLIENT_CONNECTION_STATE_ATTRIBUTE_KEY = AttributeKey.newInstance("client_connection_state");


    public ClientNetworkHandler() {
        super(NetworkSide.CLIENTSIDE);
    }

    @Override
    protected <T> void channelRead1(ChannelHandlerContext ctx, T msg) {
        InboundPacketEntry<T> entry = ctx.channel().attr(CLIENT_CONNECTION_STATE_ATTRIBUTE_KEY).get().getInboundPacket(msg);
        entry.getHandler().accept(this.getUser(), msg);
    }

    public void setClientConnectionState(ClientConnectionState state) {
        this.getActiveChannel().attr(CLIENT_CONNECTION_STATE_ATTRIBUTE_KEY).set(state);
    }

    @Override
    public void setConnectionState(ConnectionState state) {
        this.setClientConnectionState(state == ConnectionState.HANDSHAKING ? ClientConnectionState.HANDSHAKING : ClientConnectionState.PLAYING);
    }
}
