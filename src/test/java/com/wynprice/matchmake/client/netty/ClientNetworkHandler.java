package com.wynprice.matchmake.client.netty;

import com.wynprice.matchmake.netty.ConnectionState;
import com.wynprice.matchmake.netty.NetworkHandler;
import com.wynprice.matchmake.netty.NetworkSide;
import com.wynprice.matchmake.netty.packets.InboundPacketEntry;
import com.wynprice.matchmake.netty.packets.InboundPacketIntercept;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class ClientNetworkHandler extends NetworkHandler {

    public static final AttributeKey<ClientConnectionState> CLIENT_CONNECTION_STATE_ATTRIBUTE_KEY = AttributeKey.newInstance("client_connection_state");

    private final ClientConnectionState handshakeing;
    private final ClientConnectionState playing;

    public ClientNetworkHandler(ClientConnectionState handshakeing, ClientConnectionState playing) {
        super(NetworkSide.CLIENTSIDE);
        this.handshakeing = handshakeing;
        this.playing = playing;
    }

    @Override
    protected <T> void channelReadGeneric(ChannelHandlerContext ctx, T msg) {
        InboundPacketEntry<T> entry = ctx.channel().attr(CLIENT_CONNECTION_STATE_ATTRIBUTE_KEY).get().getInboundPacket(msg);
        for (InboundPacketIntercept intercept : this.getIntercepts()) {
            if(intercept.getClazz() == entry.getClazz() && this.channelReadIntercept(intercept, msg)) {
                return;
            }
        }
        entry.getHandler().accept(this.getUser(), msg);
    }

    public void setClientConnectionState(ClientConnectionState state) {
        this.getActiveChannel().attr(CLIENT_CONNECTION_STATE_ATTRIBUTE_KEY).set(state);
    }

    @Override
    public void setConnectionState(ConnectionState state) {
        this.setClientConnectionState(state == ConnectionState.HANDSHAKING ? this.handshakeing : this.playing);
    }
}
