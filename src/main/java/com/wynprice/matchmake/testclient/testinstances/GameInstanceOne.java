package com.wynprice.matchmake.testclient.testinstances;

import com.wynprice.matchmake.game.GameInstance;

public class GameInstanceOne extends GameInstance {

    @Override
    public String getGameName() {
        return "Dummy Game One";
    }

    @Override
    public String getGameDescription() {
        return "This is one gamemode, with it's own rules and player limit";
    }

    @Override
    public void tick() {

    }

    @Override
    public void clientDataPacket(int dataID, byte[] data) {

    }

}
