package com.wynprice.matchmake.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Getter
public abstract class GameInstance {

    @Getter(AccessLevel.NONE) private final Set<User> users = new HashSet<>();

    private int maxPlayers = 50;
    private String gameName = "Untitled Game";
    private String gameDescription = "Unset Game Description";


    //Per ms
    public abstract void tick();

    public boolean tryAddUser(User user, Consumer<String> rejectionReason) {
        if(this.users.size() >= this.maxPlayers) {
            rejectionReason.accept("Server Full!");
            return false;
        }

        if(!this.users.add(user)) {
            rejectionReason.accept("Client already in server?");
            return false;
        }
        return this.onUserAdd(user, rejectionReason);
    }

    public boolean onUserAdd(User user, Consumer<String> rejectionReason) {
        return true;
    }

    public GameSyncedData createSyncData(int id) {
        return new GameSyncedData(this.getGameName(), this.getGameDescription(), id, this.getMaxPlayers(), this.users.size());
    }

    @Value
    public static final class GameSyncedData {
        private final String gameName;
        private final String gameDescription;
        private final int id;
        private final int maxUsers;
        private final int currentUsers;
    }
}
