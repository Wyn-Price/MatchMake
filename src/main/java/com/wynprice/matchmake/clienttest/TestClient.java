package com.wynprice.matchmake.clienttest;

import com.wynprice.matchmake.game.GameServer;
import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.netty.NetworkDataDecoder;
import com.wynprice.matchmake.netty.NetworkDataEncoder;
import com.wynprice.matchmake.netty.NetworkHandler;
import com.wynprice.matchmake.netty.NetworkSide;
import com.wynprice.matchmake.netty.packets.handshake.serverbound.PacketJoinServer;
import com.wynprice.matchmake.netty.packets.handshake.serverbound.PacketPing;
import com.wynprice.matchmake.netty.packets.handshake.serverbound.PacketRequestGameData;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.Scanner;

@Getter
@Log4j2
public class TestClient {

    public static long pingStart = 0;

    private User user;
    private ChannelFuture endpoint;


    public void start(int port) {
        NetworkHandler handler = new NetworkHandler(NetworkSide.CLIENTSIDE);
        this.user = new ClientUser( handler);
        this.endpoint = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast("encoder", new NetworkDataEncoder())
                                .addLast("decoder", new NetworkDataDecoder())
                                .addLast("handler", handler);
                    }
                }).connect("localhost", port).syncUninterruptibly();
    }

    public void startSysInput() {
        Scanner scanner = new Scanner(System.in);
        String line;
        while(!(line = scanner.nextLine()).equals("exitsys")) {
            try {
                this.processCommand(line);
            } catch (Exception e) {
                log.error("Error processing command " + line, e);
            }
        }

        this.endpoint.channel().close().syncUninterruptibly();
        MainTest.server.getServer().stopSever();

        System.exit(0);
    }

    private void processCommand(String command) {
        if(command.equals("server data")) {
            this.user.getHandler().sendPacket(new PacketRequestGameData());
        }
        if(command.equals("ping")) {
            pingStart = System.currentTimeMillis();
            this.user.getHandler().sendPacket(new PacketPing());
        }
        if(command.startsWith("join")) {
            String id = command.split(" ")[1];
            String username = command.split(" ")[2];

            this.user.setUserName(username);

            this.user.getHandler().sendPacket(new PacketJoinServer(Integer.parseInt(id), username));
        }
    }

    private class ClientUser extends User {

        public ClientUser(NetworkHandler handler) {
            super(null, handler);
        }

        @Override
        public GameServer getServer() {
            throw new IllegalArgumentException("Unable to get server clientside");
        }
    }
}
