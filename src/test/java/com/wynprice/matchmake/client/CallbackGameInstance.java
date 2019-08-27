package com.wynprice.matchmake.client;

import com.sun.istack.internal.Nullable;
import com.wynprice.matchmake.game.GameInstance;
import com.wynprice.matchmake.game.GameServer;

import java.util.function.BiConsumer;

public class CallbackGameInstance extends GameInstance {

    private final int maxPlayers;
    private final String gameName;
    private final String gameDescription;

    @Nullable private final Runnable onTick;
    @Nullable private final BiConsumer<Integer, byte[]> clientPacket;

    public CallbackGameInstance(GameServer server, int maxPlayers, String gameName, String gameDescription, @Nullable Runnable onTick, @Nullable BiConsumer<Integer, byte[]> clientPacket) {
        super(server);
        this.maxPlayers = maxPlayers;
        this.gameName = gameName;
        this.gameDescription = gameDescription;
        this.onTick = onTick;
        this.clientPacket = clientPacket;
    }

    @Override
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    @Override
    public String getGameName() {
        return this.gameName;
    }

    @Override
    public String getGameDescription() {
        return this.gameDescription;
    }

    @Override
    public void tick() {
        if(this.onTick != null) {
            this.onTick.run();
        }
    }

    @Override
    public void clientDataPacket(int dataID, byte[] data) {
        if(this.clientPacket != null) {
            this.clientPacket.accept(dataID, data);
        }
    }
}
