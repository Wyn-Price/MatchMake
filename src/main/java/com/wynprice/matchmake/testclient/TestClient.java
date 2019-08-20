package com.wynprice.matchmake.testclient;

import com.wynprice.matchmake.game.GameServer;
import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.netty.*;
import com.wynprice.matchmake.netty.packets.handshake.serverbound.PacketJoinServer;
import com.wynprice.matchmake.netty.packets.handshake.serverbound.PacketPing;
import com.wynprice.matchmake.netty.packets.handshake.serverbound.PacketRequestGameData;
import com.wynprice.matchmake.netty.packets.playing.serverbound.PacketClientSayChat;
import com.wynprice.matchmake.netty.packets.playing.serverbound.PacketDisconnect;
import com.wynprice.matchmake.netty.packets.playing.serverbound.PacketRequestInstanceData;
import com.wynprice.matchmake.testclient.netty.ClientNetworkDataDecoder;
import com.wynprice.matchmake.testclient.netty.ClientNetworkDataEncoder;
import com.wynprice.matchmake.testclient.netty.ClientNetworkHandler;
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
        NetworkHandler handler = new ClientNetworkHandler();
        this.user = new ClientUser( handler);
        this.endpoint = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast("encoder", new ClientNetworkDataEncoder())
                                .addLast("decoder", new ClientNetworkDataDecoder())
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
        String[] splitIn = command.split(" ");
        if(command.equals("server data")) {
            this.user.getHandler().sendPacket(new PacketRequestGameData());
        }
        if(command.equals("ping")) {
            pingStart = System.currentTimeMillis();
            this.user.getHandler().sendPacket(new PacketPing());
        }
        if(command.startsWith("join")) {
            String id = splitIn[1];
            String username = splitIn[2];

            this.user.setUserName(username);
            this.user.getHandler().sendPacket(new PacketJoinServer(Integer.parseInt(id), username));
        }

        if(command.equals("instance data")) {
            this.user.getHandler().sendPacket(new PacketRequestInstanceData());
        }

        if(command.equals("disconnect")) {
            this.user.getHandler().sendPacket(new PacketDisconnect());
        }

        if(command.startsWith("say")) {
            String[] arr = new String[splitIn.length - 1];
            System.arraycopy(splitIn, 1, arr, 0, arr.length);
            this.user.getHandler().sendPacket(new PacketClientSayChat(String.join(" ", arr)));
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
