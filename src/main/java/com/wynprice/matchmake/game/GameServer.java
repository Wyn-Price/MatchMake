package com.wynprice.matchmake.game;

import com.wynprice.matchmake.NetworkServer;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.*;

@Log4j2
@Getter
public class GameServer {

    public static GameServer INSTANCE;

    private final GameTimer timer = new GameTimer();
    private final Queue<Runnable> scheduledTasks = new ArrayDeque<>();
    private final List<GameInstance> gameInstances = new LinkedList<>();
    @Getter private final NetworkServer server;

    public GameServer(int port) {
        INSTANCE = this;
        this.server = new NetworkServer(this);
        this.server.start(port);

        new Thread(this::startServer, "Game Server Thread").start();
    }

    public void startServer() {
        log.info("Server Thread Started");

        while(true) {
            this.timer.update();
            for (int i = 0; i < this.timer.getTicks(); i++) {
                this.onTick();
            }
        }
    }

    private void onTick() {
        synchronized (this.scheduledTasks) {
            while(!this.scheduledTasks.isEmpty()) {
                Runnable runnable = this.scheduledTasks.poll();
                runnable.run();
            }
        }
        this.gameInstances.forEach(GameInstance::tick);
    }

    public GameInstance.GameSyncedData[] createInstances() {
        GameInstance.GameSyncedData[] data = new GameInstance.GameSyncedData[this.gameInstances.size()];
        for (int i = 0; i < this.gameInstances.size(); i++) {
            data[i] = this.gameInstances.get(i).createSyncData(i);
        }
        return data;
    }
}
