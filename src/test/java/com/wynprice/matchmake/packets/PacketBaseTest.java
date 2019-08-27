package com.wynprice.matchmake.packets;

import com.wynprice.matchmake.client.MatchMakeClient;
import com.wynprice.matchmake.client.Reciever;
import com.wynprice.matchmake.game.GameServer;
import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.netty.NetworkHandler;
import org.junit.After;
import org.junit.Before;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

public class PacketBaseTest {

    protected static final int port = 5020;

    protected GameServer server;
    protected MatchMakeClient client;

    @Before
    public void setup() {
        this.server = new GameServer(port);
        this.client = new MatchMakeClient();
        this.client.start("localhost", port);
    }

    @After
    public void close() {
        this.server.close();
        this.client.close();
    }

    protected void waitForNetworkReady(NetworkHandler clientHandler, NetworkHandler serverHandler) {
        await("Network Ready").atMost(2, SECONDS).until(() -> clientHandler.isChannelOpen() && serverHandler.isChannelOpen());
    }

    protected NetworkHandler getClientHandler() {
        return await("Client Network Handler").atMost(2, SECONDS).until(() -> this.client.getUser().getHandler(), NetworkHandler::isChannelOpen);
    }

    protected NetworkHandler getServerHandler() {
        return await("Server Network Handler")
                .atMost(2, SECONDS)
                .until(() -> this.server.getPurgetory().getUsers().stream().findFirst().map(User::getHandler).orElse(null), handler -> handler != null && handler.isChannelOpen());
    }

    protected void awaitRecieved(Reciever<?> reciever) {
        await("Packet Receiver").atMost(2, SECONDS).until(reciever::isRecieved);
    }
}
