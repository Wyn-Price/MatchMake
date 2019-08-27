package com.wynprice.matchmake.game;

import lombok.Value;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.function.Consumer;

@Log4j2
public abstract class GameInstance {

    private final GameServer server;

    private final Queue<Runnable> scheduledTasks = new ArrayDeque<>();

    private final GameTimer timer = new GameTimer();
    protected final Set<User> users = new HashSet<>();

    protected GameInstance(GameServer server) {
        this.server = server;
    }

    //Called once per 10 ms
    public abstract void tick();

    public abstract void clientDataPacket(int dataID, byte[] data);

    public boolean tryAddUser(User user, Consumer<String> rejectionReason) {
        if(this.users.size() >= this.getMaxPlayers()) {
            rejectionReason.accept("Server Full!");
            return false;
        }

        if (this.users.stream().map(User::getUserName).anyMatch(s -> Objects.equals(s, user.getUserName()))) {
            rejectionReason.accept("Username " + user.getUserName() + " is already taken. Please pick another one");
            return false;
        }

        if(!this.users.add(user)) {
            rejectionReason.accept("Client already in server?");
            return false;
        }

        user.setInstance(this);
        return this.onUserAdd(user, rejectionReason);
    }

    public void disconnect(User user, Consumer<String> errorReason) {
        if(!this.users.remove(user)) {
            errorReason.accept("User wasn't in the server? This should't be possible");
        }
        user.connectToPurgetory();
    }

    public void scheduleTask(Runnable runnable) {
        this.scheduledTasks.add(runnable);
    }

    public boolean onUserAdd(User user, Consumer<String> rejectionReason) {
        return true;
    }

    public GameSyncedData createSyncData(int id) {
        return new GameSyncedData(this.getGameName(), this.getGameDescription(), id, this.users.size(), this.getMaxPlayers(), this.users.stream().map(User::getUserName).limit(5).toArray(String[]::new));
    }

    public Set<User> getUsers() {
        return Collections.unmodifiableSet(this.users);
    }

    public GameTimer getTimer() {
        return this.timer;
    }

    public int getMaxPlayers() {
        return 50;
    }

    public String getGameName() {
        return "Untitled Game";
    }

    public String getGameDescription() {
        return "Unset Game Description";
    }

    public void startInstanceTicking() {
        log.info("Started Game Instance Ticking Thread");

        while(true) {
            this.timer.update();
            for (int i = 0; i < this.timer.getTicks(); i++) {
                synchronized (this.scheduledTasks) {
                    while(!this.scheduledTasks.isEmpty()) {
                        this.scheduledTasks.poll().run();
                    }
                }
                this.tick();
            }
        }
    }

    @Value
    public static final class GameSyncedData {
        private final String gameName;
        private final String gameDescription;
        private final int id;
        private final int currentUsers;
        private final int maxUsers;
        private final String[] currentUsernames;
    }
}
