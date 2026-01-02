package com.github.jesusmrs05.macrotrigger.client;

import com.github.jesusmrs05.macrotrigger.config.Config;
import com.github.jesusmrs05.macrotrigger.config.ConfigManager;
import com.github.jesusmrs05.macrotrigger.util.Macro;
import com.github.jesusmrs05.macrotrigger.util.MacroFactory;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen {

    private static final int FOOTER_HEIGHT = 60;
    private static final int TOP_MARGIN = 10;

    private final Screen parent;
    private final Config config;

    private int scrollOffset = 0;
    private int contentHeight = 0;

    public ConfigScreen(Screen parent) {
        super(Component.literal("MacroTrigger Options"));
        this.parent = parent;
        this.config = ConfigManager.get();
    }

    @Override
    protected void init() {
        rebuildWidgets();
    }

    protected void rebuildWidgets() {
        this.clearWidgets();

        int x = 20;
        int width = this.width - 40;

        int visibleTop = TOP_MARGIN;
        int visibleBottom = this.height - FOOTER_HEIGHT;

        // ---- Build macro panels ----
        List<MacroEntryPanel> panels = new ArrayList<>();
        contentHeight = 0;

        for (Macro macro : config.macros) {
            MacroEntryPanel panel = new MacroEntryPanel(
                    macro,
                    x,
                    visibleTop + contentHeight,
                    width,
                    () -> { // onRemoveMacro
                        config.macros.remove(macro);
                        scrollOffset = Math.max(0, scrollOffset - 20);
                        rebuildWidgets();
                    },
                    this::rebuildWidgets
            );
            panels.add(panel);
            contentHeight += panel.getHeight() + 20;
        }

        // ---- Add only visible widgets (clipped scroll area) ----
        for (MacroEntryPanel panel : panels) {
            int panelTop = panel.getBaseY() - scrollOffset;
            int panelBottom = panelTop + panel.getHeight();

            if (panelBottom >= visibleTop && panelTop <= visibleBottom) {
                for (var widget : panel.buildWidgetsAt(panel.getBaseX(), panelTop)) {
                    this.addRenderableWidget(widget);
                }
            }
        }

        // ---- FOOTER (fixed) ----
        int footerY = this.height - FOOTER_HEIGHT + 20;

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("+ Add Macro"),
                        btn -> {
                            config.macros.add(MacroFactory.defaultMacro());
                            scrollOffset = Math.max(
                                    0,
                                    contentHeight - (visibleBottom - visibleTop)
                            );
                            rebuildWidgets();
                        }
                ).bounds(20, footerY, 140, 20).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Done"),
                        btn -> {
                            ConfigManager.save();
                            this.minecraft.setScreen(parent);
                        }
                ).bounds(this.width - 120 - 20, footerY, 120, 20).build()
        );
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY,
                                 double horizontalAmount, double verticalAmount) {

        int visibleHeight = this.height - FOOTER_HEIGHT - TOP_MARGIN;
        int maxOffset = Math.max(0, contentHeight - visibleHeight);

        int delta = (int) (-verticalAmount * 24);
        scrollOffset = Math.max(0, Math.min(maxOffset, scrollOffset + delta));

        rebuildWidgets();
        return true;
    }
}
