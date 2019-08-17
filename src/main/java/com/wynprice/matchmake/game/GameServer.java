package com.wynprice.matchmake.game;

import com.wynprice.matchmake.NetworkServer;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class GameServer {

    public static GameServer INSTANCE;

    private final List<GameInstance> gameInstances = new ArrayList<>();
    @Getter private final NetworkServer server;

    public GameServer(int port) {
        INSTANCE = this;
        this.server = new NetworkServer();
        this.server.start(port);
    }

}
