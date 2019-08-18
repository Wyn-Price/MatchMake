package com.wynprice.matchmake.game;

import lombok.Value;

import java.net.URL;

@Value
public class GameJarEntry {
    private final URL jarURL;
    private final String mainClass;
    private final String fileName;
}
