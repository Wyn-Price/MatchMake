package com.wynprice.matchmake.netty;

import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.netty.packets.InboundPacketEntry;
import com.wynprice.matchmake.netty.packets.InboundPacketIntercept;
import io.netty.channel.*;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;


public class NetworkHandler extends SimpleChannelInboundHandler<Object> {

    public static final AttributeKey<ConnectionState> CONNECTION_STATE_ATTRIBUTE_KEY = AttributeKey.newInstance("connection_state");
    private final Logger logger;
    private final Set<InboundPacketIntercept> intercepts = new HashSet<>();
    @Getter private Channel activeChannel;
    @Setter @Getter @NonNull private User user;
    private final NetworkSide direction;

    public NetworkHandler(NetworkSide direction) {
        this.direction = direction;
        this.logger = LogManager.getLogger("Network Handler (" + this.direction.getLoggerName() + ")");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        this.channelReadGeneric(ctx, msg);
    }

    protected <T> void channelReadGeneric(ChannelHandlerContext ctx, T msg) {
        InboundPacketEntry<T> entry = ctx.channel().attr(CONNECTION_STATE_ATTRIBUTE_KEY).get().getInboundPacket(msg);
        for (InboundPacketIntercept intercept : this.intercepts) {
            if(intercept.getClazz() == entry.getClazz() && this.channelReadIntercept(intercept, msg)) {
                return;
            }
        }
        entry.getHandler().accept(this.user, msg);
    }

    protected <T> boolean channelReadIntercept(InboundPacketIntercept<T> intercept, T msg) {
        return intercept.getHandler().apply(this.user, msg);
    }

    public Set<InboundPacketIntercept> getIntercepts() {
        return Collections.unmodifiableSet(this.intercepts);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.activeChannel = ctx.channel();
        this.setConnectionState(ConnectionState.HANDSHAKING);
        this.logger.info("Established Connection to " + this.activeChannel.remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.logger.info("Connection to " + this.activeChannel.remoteAddress() + " halted.");
        this.user.disconnectFromServer();
    }

    public void setConnectionState(ConnectionState state) {
        this.activeChannel.attr(CONNECTION_STATE_ATTRIBUTE_KEY).set(state);
    }

    public boolean isChannelOpen() {
        return this.activeChannel != null && this.activeChannel.isOpen();
    }

    public <T> void addIntercept(Class<T> clazz, BiFunction<User, T, Boolean> handler) {
        this.intercepts.add(new InboundPacketIntercept<>(clazz, handler));
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
