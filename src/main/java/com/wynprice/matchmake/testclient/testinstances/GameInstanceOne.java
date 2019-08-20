package com.wynprice.matchmake.testclient.testinstances;

import com.wynprice.matchmake.game.GameInstance;

public class GameInstanceOne extends GameInstance {

    @Override
    public String getGameName() {
        return "Fake Game ONe";
    }

    @Override
    public String getGameDescription() {
        return "This is a Gamemode";
    }

    @Override
    public void tick() {

    }

    @Override
    public void clientDataPacket(int dataID, byte[] data) {

    }

}
