package com.wynprice.matchmake.netty.packets.handshake.clientbound;

import com.wynprice.matchmake.clienttest.TestClient;
import com.wynprice.matchmake.game.User;
import io.netty.buffer.ByteBuf;

public class PacketPong {
    public static void encode(PacketPong data, ByteBuf buf) {
        //NO-OP
    }

    public static PacketPong decode(ByteBuf buf) {
        return new PacketPong();
    }

    public static void handle(User user, PacketPong data) {
        System.out.println("Ping result: " + (System.currentTimeMillis() - TestClient.pingStart) + "ms");
    }
}
