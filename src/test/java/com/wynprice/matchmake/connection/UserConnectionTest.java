package com.wynprice.matchmake.connection;

import com.wynprice.matchmake.client.MatchMakeClient;
import com.wynprice.matchmake.game.GameServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.*;

public class UserConnectionTest {
    private static final int port = 5020;

    private static GameServer server;

    private static MatchMakeClient client1;
    private static MatchMakeClient client2;

    @BeforeClass
    public static void setup() {
        server = new GameServer(port);

        client1 = new MatchMakeClient();
        client1.start("localhost", port);

        client2 = new MatchMakeClient();
        client2.start("localhost", port);
    }

    @Test
    public void testUserConnection() {
        await("User Connection").atMost(2, SECONDS).until(() -> server.getPurgetory().getUsers().size(), is(equalTo(2)));
    }

    @Test
    public void testUserDisconnect() {
        client1.close();
        await("User Disconnect").atMost(2, SECONDS).until(() -> server.getPurgetory().getUsers().size(), is(equalTo(1)));
    }

    @AfterClass
    public static void close() {
        server.close();
        client2.close();
    }
}
