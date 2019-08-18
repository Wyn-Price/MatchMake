package com.wynprice.matchmake.game;

import java.util.function.Consumer;

public class UserPurgatory extends GameInstance {

    private final User user;

    public UserPurgatory(User user) {
        this.user = user;
    }

    @Override
    public int getMaxPlayers() {
        return 1;
    }

    @Override
    public void tick() {

    }

    @Override
    public boolean onUserAdd(User user, Consumer<String> rejectionReason) {
        rejectionReason.accept("Should not try and add a user to purgatory");
        return false;
    }
}
