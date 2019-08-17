package com.wynprice.matchmake.netty;

import io.netty.channel.*;
import lombok.NonNull;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class NetworkHandler extends SimpleChannelInboundHandler<String> {

    private final Logger logger;

    private Channel activeChannel;
    @Setter @NonNull private Runnable whenReady = () -> {};

    private final PacketDirection direction;

    public NetworkHandler(PacketDirection direction) {
        this.direction = direction;
        this.logger = LogManager.getLogger("Network Handler (" + this.direction.getLoggerName() + ")");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        this.logger.info(msg);
        if(this.direction == PacketDirection.TO_SERVER) {
            this.sendPacket("Received Message, Hello Client!");
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.activeChannel = ctx.channel();
        this.whenReady.run();
        this.logger.info("Established Connection to " + this.activeChannel.remoteAddress());
    }

    public boolean isChannelOpen() {
        return this.activeChannel != null && this.activeChannel.isOpen();
    }

    public void sendPacket(String msg) {
        if(this.isChannelOpen()) {
            if(this.activeChannel.eventLoop().inEventLoop()) {
                this.dispatchPacket(msg);
            } else {
                this.activeChannel.eventLoop().execute(() -> this.dispatchPacket(msg));
            }
        }
    }

    private void dispatchPacket(String msg) {
        ChannelFuture future = this.activeChannel.writeAndFlush(msg);
        future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }
}
