package com.wynprice.matchmake;

import com.wynprice.matchmake.netty.NetworkDataDecoder;
import com.wynprice.matchmake.netty.NetworkDataEncoder;
import com.wynprice.matchmake.netty.NetworkHandler;
import com.wynprice.matchmake.netty.PacketDirection;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;

@Getter
public class NetworkServer {

    private ChannelFuture endpoint;
    private NetworkHandler handler;//TODO: this is per-client, so this needs to be on the user class

    public void start(int port) {
        this.handler = new NetworkHandler(PacketDirection.TO_CLIENT);
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        this.endpoint = new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast("decoder", new NetworkDataDecoder())
                                .addLast("encoder", new NetworkDataEncoder())
                                .addLast("handler", NetworkServer.this.handler);
                    }
                })
                .group(bossGroup, workerGroup)
                .bind(port).syncUninterruptibly();
    }

}
