package com.wynprice.matchmake.packets;

import com.wynprice.matchmake.client.Reciever;
import com.wynprice.matchmake.netty.NetworkHandler;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketPong;
import com.wynprice.matchmake.netty.packets.handshake.serverbound.PacketPing;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import javax.xml.ws.Holder;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@Log4j2(topic = "PingPongTest")
public class PacketPingPongTest extends PacketBaseTest {

    @Test
    public void doPingPacket() {
        NetworkHandler clientHandler = this.getClientHandler();
        NetworkHandler serverHandler = this.getServerHandler();

        Reciever<Boolean> received = new Reciever<>();

        clientHandler.sendPacket(new PacketPing());
        serverHandler.addIntercept(PacketPing.class, (user, packetPing) -> {
            log.info("Server Received Ping packet. Sending Pong packet.");
            return false;
        });

        clientHandler.addIntercept(PacketPong.class, (user, packetPong) -> {
            log.info("Pong packet received.");
            received.recieve(true);
            return true;
        });

        await("Pong Received").atMost(2, SECONDS).until(received::isRecieved);
    }
}
