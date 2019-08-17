package com.wynprice.matchmake.clienttest;

import com.wynprice.matchmake.netty.NetworkDataDecoder;
import com.wynprice.matchmake.netty.NetworkDataEncoder;
import com.wynprice.matchmake.netty.NetworkHandler;
import com.wynprice.matchmake.netty.PacketDirection;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;

@Getter
public class TestClient {

    private NetworkHandler handler;

    public void start(int port) {
        this.handler = new NetworkHandler(PacketDirection.TO_SERVER);
        new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast("encoder", new NetworkDataEncoder())
                                .addLast("decoder", new NetworkDataDecoder())
                                .addLast("handler", TestClient.this.handler);
                    }
                }).connect("localhost", port).syncUninterruptibly();
    }
}
