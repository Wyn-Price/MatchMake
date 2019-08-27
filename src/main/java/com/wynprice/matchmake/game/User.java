package com.wynprice.matchmake.game;

import com.wynprice.matchmake.netty.NetworkHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@Log4j2
public class User {
    private final GameServer server;
    private final NetworkHandler handler;
    private GameInstance instance;

    private String userName;

    public User(GameServer server, NetworkHandler handler) {
        this.server = server;
        this.handler = handler;
        this.handler.setUser(this);
        this.connectToPurgetory();
    }

    public void connectToPurgetory() {
        this.server.getPurgetory().userConnect(this);
    }

    public void disconnectFromServer() {
        this.connectToPurgetory();
        this.instance.disconnect(this, log::error);
    }
}
