package com.wynprice.matchmake.game;

import lombok.extern.log4j.Log4j2;

import java.util.function.Consumer;

@Log4j2
public class UserPurgatory extends GameInstance {

    public UserPurgatory(GameServer server) {
        super(server);
    }

    public void userConnect(User user) {
        user.setInstance(this);
        this.users.add(user);
    }

    @Override
    public int getMaxPlayers() {
        return 0;
    }

    @Override
    public void tick() {

    }

    @Override
    public void disconnect(User user, Consumer<String> errorReason) {
        if(!this.users.remove(user)) {
            errorReason.accept("User wasn't in the server? This should't be possible");
        }
    }

    @Override
    public String getGameName() {
        return "Internal Purgatory";
    }

    @Override
    public String getGameDescription() {
        return "User is not yet in a game";
    }

    @Override
    public boolean tryAddUser(User user, Consumer<String> rejectionReason) {
        rejectionReason.accept("Should not try and add a user to purgatory");
        return false;
    }

    @Override
    public void clientDataPacket(int dataID, byte[] data) {
        //Warn server of this happening?
    }
}
