package com.wynprice.matchmake.client.netty;

import com.wynprice.matchmake.client.MatchMakeClient;
import com.wynprice.matchmake.game.GameInstance;
import com.wynprice.matchmake.game.User;
import com.wynprice.matchmake.netty.ConnectionState;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketPong;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketRejectionReason;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketSendGameData;
import com.wynprice.matchmake.netty.packets.handshake.clientbound.PacketServerAcceptUser;
import com.wynprice.matchmake.netty.packets.playing.clientbound.PacketAcceptDisconnect;
import com.wynprice.matchmake.netty.packets.playing.clientbound.PacketSendChat;
import com.wynprice.matchmake.netty.packets.playing.clientbound.PacketSendInstanceData;
import com.wynprice.matchmake.netty.packets.playing.clientbound.PacketServerEventHappen;

import java.util.Arrays;

public class ClientPacketHandler {

    public static void handleSentGameData(User user, PacketSendGameData data) {
        for (GameInstance.GameSyncedData datum : data.getData()) {
            System.out.println("--------------------------------");
            System.out.println("ID:    " + datum.getId());
            System.out.println("Name:  " + datum.getGameName());
            System.out.println("Desc:  " + datum.getGameDescription());
            System.out.println("Users: " + datum.getCurrentUsers() + "/" + datum.getMaxUsers() + (datum.getCurrentUsernames().length != 0 ? " (" + String.join(", ", datum.getCurrentUsernames()) + ")" : ""));
            System.out.println("--------------------------------");
        }
    }


    public static void handlePong(User user, PacketPong data) {
        System.out.println("Ping result: " + (System.currentTimeMillis() - MatchMakeClient.pingStart) + "ms");
    }

    public static void handleRejectionReason(User user, PacketRejectionReason data) {
        System.out.println("Rejected from server for reason " + data.getReason());
    }

    public static void handleAcceptUser(User user, PacketServerAcceptUser data) {
        System.out.println("Server Accepted User");
        user.getHandler().setConnectionState(ConnectionState.PLAYING);
    }

    public static void handleSentInstanceData(User user, PacketSendInstanceData data) {
        GameInstance.GameSyncedData datum = data.getData();
        System.out.println("--------------------------------");
        System.out.println("ID:    " + datum.getId());
        System.out.println("Name:  " + datum.getGameName());
        System.out.println("Desc:  " + datum.getGameDescription());
        System.out.println("Users: " + datum.getCurrentUsers() + "/" + datum.getMaxUsers() + (datum.getCurrentUsernames().length != 0 ? " (" + String.join(", ", datum.getCurrentUsernames()) + ")" : ""));
        System.out.println("--------------------------------");
    }

    public static void handleAcceptDisconnect(User user, PacketAcceptDisconnect data) {
        user.getHandler().setConnectionState(ConnectionState.HANDSHAKING);
    }

    public static void handleChatEvent(User user, PacketSendChat data) {
        System.out.println(data.getUser() + ": " + data.getMessage());
    }

    public static void handleServerEventHappen(User user, PacketServerEventHappen data) {
        System.out.println("Received server packet ID: " + data.getDataID() + " with data " + Arrays.toString(data.getData()));
    }


}
