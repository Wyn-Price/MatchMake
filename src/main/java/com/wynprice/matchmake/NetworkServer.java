package com.wynprice.matchmake;

import com.wynprice.matchmake.game.GameServer;
import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.netty.NetworkDataDecoder;
import com.wynprice.matchmake.netty.NetworkDataEncoder;
import com.wynprice.matchmake.netty.NetworkHandler;
import com.wynprice.matchmake.netty.NetworkSide;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;

@Getter
public class NetworkServer implements AutoCloseable {

    private final GameServer server;
    private ChannelFuture endpoint;

    public NetworkServer(GameServer server) {
        this.server = server;
    }

    public void start(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        this.endpoint = new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        NetworkHandler handler = new NetworkHandler(NetworkSide.SERVERSIDE);
                        new User(NetworkServer.this.server, handler);
                        ch.pipeline()
                                .addLast("decoder", new NetworkDataDecoder())
                                .addLast("encoder", new NetworkDataEncoder())
                                .addLast("handler", handler);
                    }
                })
                .group(bossGroup, workerGroup)
                .bind(port).syncUninterruptibly();
    }

    @Override
    public void close() {
        this.endpoint.channel().close().syncUninterruptibly();
    }

}
