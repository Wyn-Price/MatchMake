package com.wynprice.matchmake.game;

import com.wynprice.matchmake.NetworkServer;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Getter
public class GameServer implements AutoCloseable {

    private final UserPurgatory purgetory = new UserPurgatory(this);
    private final List<GameInstance> gameInstances = new LinkedList<>();
    private final NetworkServer server;

    public GameServer(int port) {
        this.server = new NetworkServer(this);
        this.server.start(port);
    }


    public void addGameInstance(GameInstance instance) {
        this.gameInstances.add(instance);
        new Thread(instance::startInstanceTicking, "Game Instance (" + instance.getGameName() + ") Thread").start();
    }

    public List<GameInstance> getGameInstances() {
        return Collections.unmodifiableList(this.gameInstances);
    }

    public GameInstance.GameSyncedData[] createInstances() {
        GameInstance.GameSyncedData[] data = new GameInstance.GameSyncedData[this.gameInstances.size()];
        for (int i = 0; i < this.gameInstances.size(); i++) {
            data[i] = this.gameInstances.get(i).createSyncData(i);
        }
        return data;
    }

    @Override
    public void close() {
        this.server.close();
    }
}
