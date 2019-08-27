package com.wynprice.matchmake.client;

import com.wynprice.matchmake.client.netty.ClientConnectionState;
import com.wynprice.matchmake.client.netty.ClientNetworkDataDecoder;
import com.wynprice.matchmake.client.netty.ClientNetworkDataEncoder;
import com.wynprice.matchmake.client.netty.ClientNetworkHandler;
import com.wynprice.matchmake.game.GameServer;
import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.netty.NetworkHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Getter
@Log4j2
public class MatchMakeClient implements AutoCloseable {

    public static long pingStart = 0;

    private User user;
    private ChannelFuture endpoint;

    private final ClientConnectionState handshakeing;
    private final ClientConnectionState playing;

    public MatchMakeClient() {
        this.handshakeing = ClientConnectionState.HANDSHAKING.copy();
        this.playing = ClientConnectionState.PLAYING.copy();
    }


    public void start(String address, int port) {
        NetworkHandler handler = new ClientNetworkHandler(this.handshakeing, this.playing);
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
                }).connect(address, port).syncUninterruptibly();
    }

    @Override
    public void close() {
        this.endpoint.channel().close().syncUninterruptibly();
    }

//    private void processCommand(String command) {
//        String[] splitIn = command.split(" ");
//        if(command.equals("server data")) {
//            this.user.getHandler().sendPacket(new PacketRequestGameData());
//        }
//        if(command.equals("ping")) {
//            pingStart = System.currentTimeMillis();
//            this.user.getHandler().sendPacket(new PacketPing());
//        }
//        if(command.startsWith("join")) {
//            String id = splitIn[1];
//            String username = splitIn[2];
//
//            this.user.setUserName(username);
//            this.user.getHandler().sendPacket(new PacketJoinServer(Integer.parseInt(id), username));
//        }
//
//        if(command.equals("instance data")) {
//            this.user.getHandler().sendPacket(new PacketRequestInstanceData());
//        }
//
//        if(command.equals("disconnectFromServer")) {
//            this.user.getHandler().sendPacket(new PacketDisconnect());
//        }
//
//        if(command.startsWith("say")) {
//            String[] arr = new String[splitIn.length - 1];
//            System.arraycopy(splitIn, 1, arr, 0, arr.length);
//            this.user.getHandler().sendPacket(new PacketClientSayChat(String.join(" ", arr)));
//        }
//
//        if(command.equals("state")) {
//            System.out.println(this.user.getHandler().getActiveChannel().attr(ClientNetworkHandler.CLIENT_CONNECTION_STATE_ATTRIBUTE_KEY).get());
//        }
//    }

    private class ClientUser extends User {

        public ClientUser(NetworkHandler handler) {
            super(null, handler);
        }

        @Override
        public void connectToPurgetory() {
            //NON
        }

        @Override
        public void disconnectFromServer() {
            //NON
        }

        @Override
        public GameServer getServer() {
            throw new IllegalArgumentException("Unable to get server clientside");
        }
    }
}
