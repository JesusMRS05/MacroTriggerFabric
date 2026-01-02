package com.github.jesusmrs05.macrotrigger.client;

import com.github.jesusmrs05.macrotrigger.config.Config;
import com.github.jesusmrs05.macrotrigger.config.ConfigManager;
import com.github.jesusmrs05.macrotrigger.util.Macro;
import com.github.jesusmrs05.macrotrigger.util.MacroFactory;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {

    private final Screen parent;
    private final Config config;

    public ConfigScreen(Screen parent) {
        super(Component.literal("MacroTrigger Options"));
        this.parent = parent;
        this.config = ConfigManager.get();
    }

    @Override
    protected void init() {
        this.clearWidgets();

        int y = 20;
        int entryWidth = this.width - 80;
        int x = 40;

        for (Macro macro : config.macros) {
            MacroEntryWidget entry = new MacroEntryWidget(
                    macro,
                    x,
                    y,
                    entryWidth,
                    () -> {
                        config.macros.remove(macro);
                        this.rebuildWidgets();
                    }
            );

            for (var widget : entry.getWidgets()) {
                this.addRenderableWidget(widget);
            }

            y += entry.getHeight() + 10;
        }

        // ADD MACRO
        this.addRenderableWidget(
                Button.builder(
                        Component.literal("+ Add Macro"),
                        btn -> {
                            config.macros.add(MacroFactory.defaultMacro());
                            this.rebuildWidgets();
                        }
                ).bounds(x, y, 120, 20).build()
        );

        // DONE
        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Done"),
                        btn -> {
                            ConfigManager.save();
                            this.minecraft.setScreen(parent);
                        }
                ).bounds(this.width - 140, this.height - 30, 100, 20).build()
        );
    }
}