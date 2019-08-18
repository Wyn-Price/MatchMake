package com.wynprice.matchmake.game;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Log4j2
public class GameJarLoader {

    private final List<GameJarEntry> entries = new ArrayList<>();
    private State state = State.LOADING_JARS;

    public void scanDirForGames(@NonNull File baseDirs, boolean subDirs) {
        for (File file : Objects.requireNonNull(baseDirs.listFiles(File::isFile))) {
            this.addGameInstance(file);
        }

        if(subDirs) {
            for (File file : Objects.requireNonNull(baseDirs.listFiles(File::isDirectory))) {
                this.scanDirForGames(file, true);
            }
        }
    }

    public void addGameInstance(File file) {
        if(this.state == State.ACTIVE) {
            log.error("Tried to load file " + file.getName() + " after jar loading has been finished", new IOException());
            return;
        }
        int dotindex = file.getName().lastIndexOf('.');
        String fileName = file.getName().substring(0, dotindex);
        String extension = file.getName().substring(dotindex + 1);

        if(extension.equals("jar")) {
            log.info("Starting loading of {}", file.getAbsolutePath());

            try(ZipFile zip = new ZipFile(file)) {
                ZipEntry entry = zip.stream()
                        .filter(e -> "META-INF/matchmake.properties".equals(e.toString()))
                        .findFirst()
                        .orElseThrow(() -> new IOException("Unable to find META-INF/matchmake.properties"));
                Properties properties = new Properties();
                properties.load(zip.getInputStream(entry));
                String gameClass = properties.getProperty("game-class");
                if(gameClass == null) {
                    throw new IOException("Unable to find property game-class in matchmake.properties");
                }
                log.info("Added Game '{}' To Roster", fileName);
                this.entries.add(new GameJarEntry(file.toURI().toURL(), gameClass, fileName));
            } catch (IOException e) {
                log.error("Unable to read file:", e);
            }

        } else {
            log.warn("Tried to load {} as a game, not a JAR", file.getAbsolutePath());
        }
    }

    public void loadAll(GameServer server) {
        log.info("Started loading roster of {}.", Arrays.toString(this.entries.stream().map(GameJarEntry::getFileName).toArray()));
        if(this.state == State.ACTIVE) {
            log.error("Tried to load the games after loading has finished", new IOException());
            return;
        }
        this.state = State.ACTIVE;

        try (URLClassLoader loader = new URLClassLoader(this.entries.stream().map(GameJarEntry::getJarURL).toArray(URL[]::new), GameJarLoader.class.getClassLoader())) {
            for (GameJarEntry entry : this.entries) {
                this.createInstance(server, entry, loader).ifPresent(server.getGameInstances()::add);
            }
        } catch (IOException e) {
            log.error("Error in reading game entries", e);
        }
        log.info("Finished loading jars. End roster is {}.", Arrays.toString(server.getGameInstances().stream().map(GameInstance::getGameName).toArray()));
        this.entries.clear();
    }

    private Optional<GameInstance> createInstance(GameServer server, GameJarEntry entry, ClassLoader loader) {
        Class<?> entryClass;
        try {
            entryClass = Class.forName(entry.getMainClass(), true, loader);
        } catch (ClassNotFoundException e) {
            log.error("Unable to find class " + entry.getMainClass() + " in jar " + entry.getJarURL().getPath(), e);
            return Optional.empty();
        }

        Method createGameMethod;
        try {
            createGameMethod = entryClass.getMethod("createGame", GameServer.class);
            if(!Modifier.isStatic(createGameMethod.getModifiers())) {
                log.error("Method createGame in class " + entry.getMainClass() + " must be static.");
                return Optional.empty();
            }
        } catch (NoSuchMethodException e) {
            log.error("Could not find method `static createGame(GameServer)` in class " + entry.getMainClass() + ". Please refer to the specification for more details", e);
            return Optional.empty();
        }

        Object result;
        try {
            result = createGameMethod.invoke(null, server);
        } catch (IllegalAccessException e) {
            log.error("Unable to access createGame method. Is it public and static?", e);
            return Optional.empty();
        } catch (InvocationTargetException e) {
            log.error(e);
            return Optional.empty();
        }

        if(result instanceof GameInstance) {
            return Optional.of((GameInstance) result);
        } else {
            log.error("createGame result returned an object of class: " + result.getClass() + " which does not extend GameInstance");
            return Optional.empty();
        }
    }


    private enum State {
        LOADING_JARS,
        ACTIVE
    }

}
