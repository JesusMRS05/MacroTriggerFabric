package com.github.jesusmrs05.macrotrigger.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class MacroPanelCycleButton<T> extends AbstractWidget {

    private final List<T> values;
    private final Function<T, Component> formatter;
    private final Consumer<T> onValueChange;

    private int currentIndex;

    public MacroPanelCycleButton(
            int x,
            int y,
            int width,
            int height,
            List<T> values,
            T initialValue,
            Function<T, Component> formatter,
            Consumer<T> onValueChange
    ) {
        super(x, y, width, height, Component.empty());
        this.values = values;
        this.formatter = formatter;
        this.onValueChange = onValueChange;
        this.currentIndex = Math.max(0, values.indexOf(initialValue));
        updateMessage();
    }

    @Override
    protected void extractWidgetRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        int left = this.getX();
        int top = this.getY();
        int right = left + this.width;
        int bottom = top + this.height;

        int fill = 0xFF5C5C5C;
        int border = 0xFF2D2D2D;
        int highlight = 0xFF8D8D8D;
        int text = 0xFFE0E0E0;

        if (!this.active) {
            fill = 0xFF3F3F3F;
            border = 0xFF202020;
            text = 0xFF8A8A8A;
        } else if (this.isHoveredOrFocused()) {
            fill = brighten(fill, 18);
            border = brighten(border, 14);
            text = 0xFFFFFFFF;
        }

        graphics.fill(left, top, right, bottom, fill);
        graphics.fill(left, top, right, top + 1, highlight);
        graphics.fill(left, top, left + 1, bottom, highlight);
        graphics.fill(left, bottom - 1, right, bottom, border);
        graphics.fill(right - 1, top, right, bottom, border);

        graphics.centeredText(
                Minecraft.getInstance().font,
                this.getMessage(),
                left + this.width / 2,
                top + (this.height - 8) / 2,
                text
        );
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean bl) {
        if (!this.active || !this.visible || this.values.isEmpty()) {
            return;
        }

        this.playDownSound(Minecraft.getInstance().getSoundManager());

        if (event.button() == 1) {
            cycle(-1);
        } else {
            cycle(1);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }

    @Override
    protected boolean isValidClickButton(MouseButtonInfo mouseButtonInfo) {
        return mouseButtonInfo.button() == 0 || mouseButtonInfo.button() == 1;
    }

    private void cycle(int step) {
        int size = this.values.size();
        this.currentIndex = Math.floorMod(this.currentIndex + step, size);
        T value = this.values.get(this.currentIndex);
        updateMessage();
        this.onValueChange.accept(value);
    }

    private void updateMessage() {
        if (this.values.isEmpty()) {
            this.setMessage(Component.empty());
            return;
        }

        this.setMessage(this.formatter.apply(this.values.get(this.currentIndex)));
    }

    public void setTooltipText(Component tooltip) {
        this.setTooltip(Tooltip.create(tooltip));
    }

    private static int brighten(int color, int amount) {
        int a = (color >>> 24) & 0xFF;
        int r = Math.min(255, ((color >>> 16) & 0xFF) + amount);
        int g = Math.min(255, ((color >>> 8) & 0xFF) + amount);
        int b = Math.min(255, (color & 0xFF) + amount);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
