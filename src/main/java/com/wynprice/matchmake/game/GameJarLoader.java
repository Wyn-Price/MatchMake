package com.wynprice.matchmake.game;

import com.sun.jndi.toolkit.url.Uri;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
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
                    throw new IOException("Unable to find property game-class");
                }
                log.info("Added Game '{}' To Roster", fileName);
                this.entries.add(new GameJarEntry(file.toURI().toURL(), gameClass));
            } catch (IOException e) {
                log.error("Unable to read file:", e);
            }

        } else {
            log.warn("Tried to load {} as a game, not a JAR", file.getAbsolutePath());
        }
    }

    public void loadAll(GameServer server) {
        if(this.state == State.ACTIVE) {
            log.error("Tried to load the games after loading has finished", new IOException());
            return;
        }
        this.state = State.ACTIVE;

        try (URLClassLoader loader = new URLClassLoader(this.entries.stream().map(GameJarEntry::getJarURL).toArray(URL[]::new), GameJarLoader.class.getClassLoader())) {
            for (GameJarEntry entry : this.entries) {
                Class<?> entryClass = Class.forName(entry.getMainClass(), true, loader);
                Method createGameMethod = entryClass.getMethod("createGame", GameServer.class);
                log.info(createGameMethod.invoke(null, server));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.entries.clear();
    }


    private enum State {
        LOADING_JARS,
        ACTIVE;

    }

}
