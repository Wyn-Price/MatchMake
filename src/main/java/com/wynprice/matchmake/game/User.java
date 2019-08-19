package com.wynprice.matchmake.game;

import com.wynprice.matchmake.netty.NetworkHandler;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private final GameServer server;
    private final NetworkHandler handler;
    private GameInstance instance;

    private String userName;

    public User(GameServer server, NetworkHandler handler) {
        this.server = server;
        this.handler = handler;
        this.handler.setUser(this);
        this.instance = new UserPurgatory(this);
    }

}
