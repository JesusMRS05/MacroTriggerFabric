package com.github.jesusmrs05.macrotrigger.util;

import com.github.jesusmrs05.MacroTrigger;
import com.github.jesusmrs05.macrotrigger.client.ChatState;
import net.minecraft.client.Minecraft;

import java.util.function.Consumer;

public enum MacroAction {
    SEND_TO_CHAT((param) -> {
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
    });


    private Consumer<String> action;

    MacroAction(Consumer<String> action) {
        this.action = action;
    }

    public void execute(String param) {
        this.action.accept(param);
    }

    @Override
    public String toString() {
        return this.name().charAt(0) + this.name().substring(1).toLowerCase();
    }
}
