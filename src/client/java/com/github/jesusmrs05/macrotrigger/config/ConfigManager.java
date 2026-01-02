package com.github.jesusmrs05.macrotrigger.config;

import com.github.jesusmrs05.macrotrigger.util.Macro;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {

    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().create();

    private static final Path CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve("macrotrigger.json");

    private static Config config;

    private ConfigManager() {}

    public static Config get() {
        if (config == null) load();
        return config;
    }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                config = GSON.fromJson(Files.readString(CONFIG_PATH), Config.class);

                // 🔴 NORMALIZE AFTER LOAD
                if (config.macros != null) {
                    for (Macro macro : config.macros) {
                        if (macro != null) {
                            macro.normalize();
                        }
                    }
                }

            } catch (IOException e) {
                config = new Config();
            }
        } else {
            config = new Config();
            save();
        }
    }

    public static void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(config));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
