package com.github.jesusmrs05.macrotrigger.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class MacroPanelButton extends AbstractWidget {

    public enum Style {
        DEFAULT(0xFF5C5C5C, 0xFF2D2D2D, 0xFF8D8D8D, 0xFFE0E0E0),
        DANGER(0xFFC93131, 0xFF571414, 0xFFFF7A7A, 0xFFFFF2F2),
        SUBTLE(0xFF505050, 0xFF252525, 0xFF787878, 0xFFD8D8D8);

        private final int fillColor;
        private final int borderColor;
        private final int highlightColor;
        private final int textColor;

        Style(int fillColor, int borderColor, int highlightColor, int textColor) {
            this.fillColor = fillColor;
            this.borderColor = borderColor;
            this.highlightColor = highlightColor;
            this.textColor = textColor;
        }
    }

    @FunctionalInterface
    public interface PressAction {
        void onPress(MacroPanelButton button);
    }

    private final PressAction onPress;
    private final Style style;

    public MacroPanelButton(int x, int y, int width, int height, Component message, Style style, PressAction onPress) {
        super(x, y, width, height, message);
        this.style = style;
        this.onPress = onPress;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int left = this.getX();
        int top = this.getY();
        int right = left + this.width;
        int bottom = top + this.height;

        int fill = this.style.fillColor;
        int border = this.style.borderColor;
        int text = this.style.textColor;

        if (!this.active) {
            fill = 0xFF3F3F3F;
            border = 0xFF202020;
            text = 0xFF8A8A8A;
        } else if (this.isHoveredOrFocused()) {
            fill = brighten(fill, 18);
            border = brighten(border, 14);
            text = 0xFFFFFFFF;
        }

        guiGraphics.fill(left, top, right, bottom, fill);
        guiGraphics.fill(left, top, right, top + 1, this.style.highlightColor);
        guiGraphics.fill(left, top, left + 1, bottom, this.style.highlightColor);
        guiGraphics.fill(left, bottom - 1, right, bottom, border);
        guiGraphics.fill(right - 1, top, right, bottom, border);

        guiGraphics.drawCenteredString(
                net.minecraft.client.Minecraft.getInstance().font,
                this.getMessage(),
                left + this.width / 2,
                top + (this.height - 8) / 2,
                text
        );
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean bl) {
        if (this.active && this.visible) {
            this.playDownSound(net.minecraft.client.Minecraft.getInstance().getSoundManager());
            this.onPress.onPress(this);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }

    private static int brighten(int color, int amount) {
        int a = (color >>> 24) & 0xFF;
        int r = Math.min(255, ((color >>> 16) & 0xFF) + amount);
        int g = Math.min(255, ((color >>> 8) & 0xFF) + amount);
        int b = Math.min(255, (color & 0xFF) + amount);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
