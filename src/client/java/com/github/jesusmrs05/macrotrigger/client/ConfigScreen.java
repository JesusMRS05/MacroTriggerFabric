package com.github.jesusmrs05.macrotrigger.client;

import com.github.jesusmrs05.macrotrigger.config.Config;
import com.github.jesusmrs05.macrotrigger.config.ConfigManager;
import com.github.jesusmrs05.macrotrigger.util.Macro;
import com.github.jesusmrs05.macrotrigger.util.MacroFactory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen {

    private static final int OUTER_MARGIN = 16;
    private static final int HEADER_HEIGHT = 44;
    private static final int FOOTER_HEIGHT = 32;
    private static final int PANEL_SPACING = 10;

    private final Screen parent;
    private final Config config;

    private final List<MacroEntryPanel> visiblePanels = new ArrayList<>();

    private int scrollOffset = 0;
    private int contentHeight = 0;

    public ConfigScreen(Screen parent) {
        super(Component.literal("MacroTrigger"));
        this.parent = parent;
        this.config = ConfigManager.get();
    }

    @Override
    protected void init() {
        rebuildWidgets();
    }

    protected void rebuildWidgets() {
        this.clearWidgets();
        this.visiblePanels.clear();

        int contentX = OUTER_MARGIN;
        int contentWidth = this.width - OUTER_MARGIN * 2;

        int visibleTop = HEADER_HEIGHT + OUTER_MARGIN;
        int visibleBottom = this.height - FOOTER_HEIGHT - OUTER_MARGIN;

        int currentY = visibleTop;
        contentHeight = 0;

        for (Macro macro : config.macros) {
            MacroEntryPanel panel = new MacroEntryPanel(
                    macro,
                    contentX,
                    currentY + contentHeight,
                    contentWidth,
                    () -> {
                        config.macros.remove(macro);
                        scrollOffset = Math.max(0, scrollOffset - 24);
                        rebuildWidgets();
                    },
                    this::rebuildWidgets
            );

            int panelTop = panel.getBaseY() - scrollOffset;
            int panelBottom = panelTop + panel.getHeight();
            if (panelBottom >= visibleTop && panelTop <= visibleBottom) {
                visiblePanels.add(panel);
                for (var widget : panel.buildWidgetsAt(panel.getBaseX(), panelTop)) {
                    this.addRenderableWidget(widget);
                }
            }

            contentHeight += panel.getHeight() + PANEL_SPACING;
        }

        int addButtonWidth = 110;
        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Add Macro"),
                        btn -> {
                            config.macros.add(MacroFactory.defaultMacro());
                            int visibleHeight = Math.max(0, visibleBottom - visibleTop);
                            scrollOffset = Math.max(0, contentHeight - visibleHeight + PANEL_SPACING);
                            rebuildWidgets();
                        }
                ).bounds(this.width - OUTER_MARGIN - addButtonWidth, OUTER_MARGIN + 10, addButtonWidth, 20).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Done"),
                        btn -> {
                            ConfigManager.save();
                            this.minecraft.setScreen(parent);
                        }
                ).bounds(this.width - OUTER_MARGIN - 90, this.height - OUTER_MARGIN - 20, 90, 20).build()
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, 0xCC101010);

        int contentLeft = OUTER_MARGIN;
        int contentRight = this.width - OUTER_MARGIN;
        int visibleTop = HEADER_HEIGHT + OUTER_MARGIN;
        int visibleBottom = this.height - FOOTER_HEIGHT - OUTER_MARGIN;

        guiGraphics.fill(contentLeft, visibleTop - 4, contentRight, visibleBottom + 4, 0x66101010);
        guiGraphics.hLine(contentLeft, contentRight - 1, visibleTop - 4, 0xFF3A3A3A);
        guiGraphics.hLine(contentLeft, contentRight - 1, visibleBottom + 4, 0xFF3A3A3A);

        guiGraphics.drawString(this.font, this.title, OUTER_MARGIN, OUTER_MARGIN + 6, 0xFFFFFF, false);
        guiGraphics.drawString(
                this.font,
                Component.literal(config.macros.size() + " macros configured"),
                OUTER_MARGIN,
                OUTER_MARGIN + 22,
                0xA0A0A0,
                false
        );

        if (config.macros.isEmpty()) {
            int centerX = this.width / 2;
            int centerY = (visibleTop + visibleBottom) / 2;
            guiGraphics.drawCenteredString(this.font, Component.literal("No macros yet"), centerX, centerY - 10, 0xFFFFFF);
            guiGraphics.drawCenteredString(
                    this.font,
                    Component.literal("Use Add Macro to create your first rule"),
                    centerX,
                    centerY + 4,
                    0xA0A0A0
            );
        }

        for (MacroEntryPanel panel : visiblePanels) {
            panel.renderDecorations(guiGraphics, panel.getBaseX(), panel.getBaseY() - scrollOffset, mouseX, mouseY);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int visibleHeight = this.height - HEADER_HEIGHT - FOOTER_HEIGHT - (OUTER_MARGIN * 2);
        int maxOffset = Math.max(0, contentHeight - visibleHeight);

        int delta = (int) (-verticalAmount * 18);
        scrollOffset = Math.max(0, Math.min(maxOffset, scrollOffset + delta));

        rebuildWidgets();
        return true;
    }
}
