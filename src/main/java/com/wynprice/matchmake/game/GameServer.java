package com.wynprice.matchmake.game;

import com.wynprice.matchmake.NetworkServer;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@Getter
public class GameServer {

    public static GameServer INSTANCE;

    private final List<GameInstance> gameInstances = new LinkedList<>();
    private final NetworkServer server;

    public GameServer(int port) {
        INSTANCE = this;
        this.server = new NetworkServer(this);
        this.server.start(port);
    }


    public void addGameInstance(GameInstance instance) {
        new Thread(instance::startInstanceTicking, "Game Instance (" + instance.getGameName() + ") Thread").start();
    }

    public GameInstance.GameSyncedData[] createInstances() {
        GameInstance.GameSyncedData[] data = new GameInstance.GameSyncedData[this.gameInstances.size()];
        for (int i = 0; i < this.gameInstances.size(); i++) {
            data[i] = this.gameInstances.get(i).createSyncData(i);
        }
        return data;
    }
}
