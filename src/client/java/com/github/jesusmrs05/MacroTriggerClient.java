package com.github.jesusmrs05;

import com.github.jesusmrs05.macrotrigger.client.ChatState;
import com.github.jesusmrs05.macrotrigger.config.ConfigManager;
import com.github.jesusmrs05.macrotrigger.util.Macro;
import com.github.jesusmrs05.macrotrigger.util.MacroTarget;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

public class MacroTriggerClient implements ClientModInitializer {

    private int lastHealth = -1;
    private int lastHunger = -1;

    @Override
    public void onInitializeClient() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            ChatState.setLastMessage(message.getString());
            if (!ChatState.lockChatEvent) {
                runMacrosContainingTarget(new MacroTarget[]{MacroTarget.CHAT});
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            int currentHealth = (int) client.player.getHealth();
            int currentHunger = client.player.getFoodData().getFoodLevel();

            if (currentHealth != lastHealth) {
                lastHealth = currentHealth;
                runMacrosContainingTarget(new MacroTarget[]{MacroTarget.HEALTH});
            }

            if (currentHunger != lastHunger) {
                lastHunger = currentHunger;
                runMacrosContainingTarget(new MacroTarget[]{MacroTarget.HUNGER});
            }
        });
    }

    private void runMacrosContainingTarget(MacroTarget[] targets) {
        if (ConfigManager.get() != null) {
            ChatState.lockChatEvent = true;
            for (Macro macro : ConfigManager.get().macros) {
                if (macro.containsAnyTarget(targets)) {
                    try {
                        if (macro.shouldExecute()) {
                            macro.execute();
                        }
                    } catch (Exception e) {
                        MacroTrigger.LOGGER.error(e.getMessage());
                    }
                }
            }
            ChatState.lockChatEvent = false;
        }
    }
}