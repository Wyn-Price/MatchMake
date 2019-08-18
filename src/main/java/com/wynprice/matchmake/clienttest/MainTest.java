package com.wynprice.matchmake.clienttest;

import com.wynprice.matchmake.clienttest.TestClient;
import com.wynprice.matchmake.clienttest.testinstances.GameInstanceOne;
import com.wynprice.matchmake.clienttest.testinstances.GameInstanceTwo;
import com.wynprice.matchmake.game.GameServer;
import com.wynprice.matchmake.netty.packets.handshake.serverbound.PacketRequestGameData;

public class MainTest {
    public static GameServer server;
    public static void main(String[] args) {
        final int port = 5152;
        setupServer(port);
        createSysInClient(port);
    }

    private static void setupServer(int port) {
        server = new GameServer(port);
        server.addGameInstance(new GameInstanceOne());
        server.addGameInstance(new GameInstanceTwo());
    }

    private static void createSysInClient(int port) {
        TestClient client = new TestClient();
        client.start(port);

        new Thread(client::startSysInput, "Client Sys Input Thread").start();

    }
}
