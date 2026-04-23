package com.github.jesusmrs05.macrotrigger.client;

import com.github.jesusmrs05.macrotrigger.config.Config;
import com.github.jesusmrs05.macrotrigger.config.ConfigManager;
import com.github.jesusmrs05.macrotrigger.util.Macro;
import com.github.jesusmrs05.macrotrigger.util.MacroFactory;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen {

    private static final int OUTER_MARGIN = 16;
    private static final int HEADER_HEIGHT = 36;
    private static final int FOOTER_HEIGHT = 36;
    private static final int PANEL_SPACING = 10;
    private static final int BAR_INSET = 6;
    private static final int BODY_INSET = 4;

    private final Screen parent;
    private final Config config;

    private final List<MacroEntryPanel> visiblePanels = new ArrayList<>();
    private final List<AbstractWidget> bodyWidgets = new ArrayList<>();

    private Button addMacroButton;
    private Button doneButton;

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
        this.bodyWidgets.clear();
        this.addMacroButton = null;
        this.doneButton = null;

        int contentX = OUTER_MARGIN;
        int contentWidth = this.width - OUTER_MARGIN * 2;

        int headerTop = OUTER_MARGIN;
        int headerBottom = headerTop + HEADER_HEIGHT;
        int footerBottom = this.height - OUTER_MARGIN;
        int footerTop = footerBottom - FOOTER_HEIGHT;
        int visibleTop = headerBottom + BODY_INSET;
        int visibleBottom = footerTop - BODY_INSET;

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
                    this.bodyWidgets.add(this.addWidget(widget));
                }
            }

            contentHeight += panel.getHeight() + PANEL_SPACING;
        }

        int addButtonWidth = 110;
        this.addMacroButton = this.addRenderableWidget(
                Button.builder(
                        Component.literal("Add Macro"),
                        btn -> {
                            config.macros.add(MacroFactory.defaultMacro());
                            int visibleHeight = Math.max(0, visibleBottom - visibleTop);
                            scrollOffset = Math.max(0, contentHeight - visibleHeight + PANEL_SPACING);
                            rebuildWidgets();
                        }
                ).bounds(this.width - OUTER_MARGIN - BAR_INSET - addButtonWidth, headerTop + (HEADER_HEIGHT - 20) / 2, addButtonWidth, 20).build()
        );

        this.doneButton = this.addRenderableWidget(
                Button.builder(
                        Component.literal("Done"),
                        btn -> {
                            ConfigManager.save();
                            this.minecraft.setScreen(parent);
                        }
                ).bounds(this.width - OUTER_MARGIN - BAR_INSET - 90, footerTop + (FOOTER_HEIGHT - 20) / 2, 90, 20).build()
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, 0xCC101010);

        int contentLeft = OUTER_MARGIN;
        int contentRight = this.width - OUTER_MARGIN;
        int headerTop = OUTER_MARGIN;
        int headerBottom = headerTop + HEADER_HEIGHT;
        int footerBottom = this.height - OUTER_MARGIN;
        int footerTop = footerBottom - FOOTER_HEIGHT;
        int bodyTop = headerBottom;
        int bodyBottom = footerTop;
        int visibleTop = bodyTop + BODY_INSET;
        int visibleBottom = bodyBottom - BODY_INSET;

        this.renderMenuBackground(guiGraphics, contentLeft, headerTop, contentRight, headerBottom);
        this.renderMenuBackground(guiGraphics, contentLeft, footerTop, contentRight, footerBottom);
        guiGraphics.fill(contentLeft, bodyTop, contentRight, bodyBottom, 0x66101010);

        guiGraphics.hLine(contentLeft, contentRight - 1, headerTop, 0xFF474747);
        guiGraphics.hLine(contentLeft, contentRight - 1, headerBottom, 0xFF3A3A3A);
        guiGraphics.hLine(contentLeft, contentRight - 1, footerTop, 0xFF3A3A3A);
        guiGraphics.hLine(contentLeft, contentRight - 1, footerBottom, 0xFF2E2E2E);
        guiGraphics.vLine(contentLeft, headerTop, footerBottom, 0xFF2E2E2E);
        guiGraphics.vLine(contentRight - 1, headerTop, footerBottom, 0xFF2E2E2E);

        guiGraphics.drawString(this.font, this.title, contentLeft + BAR_INSET, headerTop + 7, 0xFFFFFF, false);
        guiGraphics.drawString(
                this.font,
                Component.literal(config.macros.size() + " macros configured"),
                contentLeft + BAR_INSET + 96,
                headerTop + 7,
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

        guiGraphics.enableScissor(contentLeft + 1, visibleTop, contentRight - 1, visibleBottom);
        for (MacroEntryPanel panel : visiblePanels) {
            panel.renderDecorations(guiGraphics, panel.getBaseX(), panel.getBaseY() - scrollOffset, mouseX, mouseY);
        }
        for (AbstractWidget widget : bodyWidgets) {
            widget.render(guiGraphics, mouseX, mouseY, partialTick);
        }
        guiGraphics.disableScissor();

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int visibleHeight = this.height - HEADER_HEIGHT - FOOTER_HEIGHT - (OUTER_MARGIN * 2) - (BODY_INSET * 2);
        int maxOffset = Math.max(0, contentHeight - visibleHeight);

        int delta = (int) (-verticalAmount * 18);
        scrollOffset = Math.max(0, Math.min(maxOffset, scrollOffset + delta));

        rebuildWidgets();
        return true;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if (this.addMacroButton != null && this.addMacroButton.mouseClicked(event, bl)) {
            this.setFocused(this.addMacroButton);
            return true;
        }

        if (this.doneButton != null && this.doneButton.mouseClicked(event, bl)) {
            this.setFocused(this.doneButton);
            return true;
        }

        if (!isPointInsideBody(event.x(), event.y())) {
            return false;
        }

        return super.mouseClicked(event, bl);
    }

    private boolean isPointInsideBody(double mouseX, double mouseY) {
        int headerBottom = OUTER_MARGIN + HEADER_HEIGHT;
        int footerTop = this.height - OUTER_MARGIN - FOOTER_HEIGHT;
        int visibleTop = headerBottom + BODY_INSET;
        int visibleBottom = footerTop - BODY_INSET;
        int contentLeft = OUTER_MARGIN;
        int contentRight = this.width - OUTER_MARGIN;

        return mouseX >= contentLeft
                && mouseX < contentRight
                && mouseY >= visibleTop
                && mouseY < visibleBottom;
    }
}
