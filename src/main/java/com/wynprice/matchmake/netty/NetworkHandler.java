package com.wynprice.matchmake.netty;

import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.netty.packets.InboundPacketEntry;
import io.netty.channel.*;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class NetworkHandler extends SimpleChannelInboundHandler<Object> {

    public static final AttributeKey<ConnectionState> CONNECTION_STATE_ATTRIBUTE_KEY = AttributeKey.newInstance("connection_state");

    private final Logger logger;

    @Getter private Channel activeChannel;
    @Setter @NonNull private Runnable whenReady = () -> {};
    @Setter @Getter @NonNull private User user;
    private final NetworkSide direction;

    public NetworkHandler(NetworkSide direction) {
        this.direction = direction;
        this.logger = LogManager.getLogger("Network Handler (" + this.direction.getLoggerName() + ")");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        this.channelRead1(ctx, msg);
    }

    protected <T> void channelRead1(ChannelHandlerContext ctx, T msg) {
        InboundPacketEntry<T> entry = ctx.channel().attr(CONNECTION_STATE_ATTRIBUTE_KEY).get().getInboundPacket(msg);
        entry.getHandler().accept(this.user, msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.activeChannel = ctx.channel();
        this.setConnectionState(ConnectionState.HANDSHAKING);
        this.whenReady.run();
        this.logger.info("Established Connection to " + this.activeChannel.remoteAddress());
    }

    public void setConnectionState(ConnectionState state) {
        this.activeChannel.attr(CONNECTION_STATE_ATTRIBUTE_KEY).set(state);
    }

    public boolean isChannelOpen() {
        return this.activeChannel != null && this.activeChannel.isOpen();
    }

    public void sendPacket(Object msg) {
        if(this.isChannelOpen()) {
            if(this.activeChannel.eventLoop().inEventLoop()) {
                this.dispatchPacket(msg);
            } else {
                this.activeChannel.eventLoop().execute(() -> this.dispatchPacket(msg));
            }
        }
    }

    private void dispatchPacket(Object msg) {
        ChannelFuture future = this.activeChannel.writeAndFlush(msg);
        future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }
}
