package com.wynprice.matchmake.testclient.testinstances;

import com.wynprice.matchmake.game.GameInstance;

public class GameInstanceTwo extends GameInstance {

    @Override
    public String getGameName() {
        return "Dummy Game Two";
    }

    @Override
    public String getGameDescription() {
        return "This is a different gamemode, with it's own different rules and data";
    }

    @Override
    public void tick() {

    }

    @Override
    public void clientDataPacket(int dataID, byte[] data) {

    }

}
