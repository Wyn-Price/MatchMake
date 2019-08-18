package com.wynprice.matchmake.clienttest.testinstances;

import com.wynprice.matchmake.game.GameInstance;
import com.wynprice.matchmake.game.User;

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

}
