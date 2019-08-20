package com.wynprice.matchmake.testclient.testinstances;

import com.wynprice.matchmake.game.GameInstance;

public class GameInstanceTwo extends GameInstance {

    @Override
    public String getGameName() {
        return "Fake Game TWO";
    }

    @Override
    public String getGameDescription() {
        return "This is also a Gamemode";
    }

    @Override
    public void tick() {

    }

    @Override
    public void clientDataPacket(int dataID, byte[] data) {

    }

}
