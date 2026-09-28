package com.github.jesusmrs05.macrotrigger.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;

import java.util.function.Consumer;

public enum MacroAction {
    SEND_TO_CHAT(
            (param) -> {
                if (param == null || param.isBlank()) return;

                Minecraft mc = Minecraft.getInstance();
                if (mc.player == null || mc.player.connection == null) return;

                if (param.startsWith("/")) {
                    // Execute as command (remove leading '/')
                    mc.player.connection.sendCommand(param.substring(1));
                } else {
                    // Send as normal chat message
                    mc.player.connection.sendChat(param);
                }
            },
            "Hello world!"
    ),

    START_TIMER(
            (param) -> {
                if (param == null || param.isBlank()) return;

                JsonObject json = JsonParser.parseString(param).getAsJsonObject();

                String id = json.get("id").getAsString();
                long duration = json.get("duration").getAsLong();

                TimerManager.start(id, duration);
            },
            "{\"id\":\"someTimerThatLasts1second\",\"duration\":\"1000\"}"
    );


    private final Consumer<String> action;
    private final String exampleData;

    MacroAction(Consumer<String> action, String exampleData) {
        this.action = action;
        this.exampleData = exampleData;
    }

    public void execute(String param) {
        this.action.accept(param);
    }

    public String getExampleData() {
        return this.exampleData;
    }

    @Override
    public String toString() {
        return this.name().charAt(0) + this.name().substring(1).toLowerCase();
    }
}