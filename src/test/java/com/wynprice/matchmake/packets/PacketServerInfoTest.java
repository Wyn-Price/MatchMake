package com.wynprice.matchmake.packets;

import com.wynprice.matchmake.client.CallbackGameInstance;
import com.wynprice.matchmake.client.Reciever;
import com.wynprice.matchmake.game.GameInstance;
import com.wynprice.matchmake.netty.NetworkHandler;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketSendGameData;
import com.wynprice.matchmake.netty.packets.handshake.serverbound.PacketRequestGameData;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PacketServerInfoTest extends PacketBaseTest {

    @Test
    public void noSentData() {
        NetworkHandler clientHandler = this.getClientHandler();
        Reciever<PacketSendGameData> received = new Reciever<>();

        clientHandler.sendPacket(new PacketRequestGameData());
        clientHandler.addIntercept(PacketSendGameData.class, (user, data) -> {
            received.recieve(data);
            return true;
        });
        this.awaitRecieved(received);
        assertThat(received.getRecievedObject().getData().length, is(equalTo(0)));
    }

    @Test
    public void dataSent() {
        NetworkHandler clientHandler = this.getClientHandler();
        Reciever<PacketSendGameData> received = new Reciever<>();

        int maxPlayers = 71;
        String gameName = "someGameName";
        String gameDescription = "someGameDescription";

        this.server.addGameInstance(new CallbackGameInstance(this.server, maxPlayers, gameName, gameDescription, null, null));

        clientHandler.sendPacket(new PacketRequestGameData());
        clientHandler.addIntercept(PacketSendGameData.class, (user, data) -> {
            received.recieve(data);
            return true;
        });
        this.awaitRecieved(received);

        assertThat("Sent data doesn't have correct length", received.getRecievedObject().getData().length, is(equalTo(1)));
        GameInstance.GameSyncedData datum = received.getRecievedObject().getData()[0];

        assertThat("Game Name shouldn't change", datum.getGameName(), is(equalTo(gameName)));
        assertThat("Game Description shouldn't change", datum.getGameDescription(), is(equalTo(gameDescription)));
        assertThat("The instance ID should be 0", datum.getId(), is(equalTo(0)));
        assertThat("Current Users should be 0", datum.getCurrentUsers(), is(equalTo(0)));
        assertThat("Game Max Players shouldn't change", datum.getMaxUsers(), is(equalTo(maxPlayers)));
        assertThat("Current User-names should be 0", datum.getCurrentUsernames().length, is(equalTo(0)));
    }

}
