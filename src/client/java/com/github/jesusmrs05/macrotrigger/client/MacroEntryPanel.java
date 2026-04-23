package com.github.jesusmrs05.macrotrigger.client;

import com.github.jesusmrs05.macrotrigger.util.FormalizedAction;
import com.github.jesusmrs05.macrotrigger.util.FormalizedConditon;
import com.github.jesusmrs05.macrotrigger.util.LogicOperator;
import com.github.jesusmrs05.macrotrigger.util.Macro;
import com.github.jesusmrs05.macrotrigger.util.MacroAction;
import com.github.jesusmrs05.macrotrigger.util.MacroCondition;
import com.github.jesusmrs05.macrotrigger.util.MacroTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MacroEntryPanel {

    private static final int PADDING = 8;
    private static final int ROW_HEIGHT = 20;
    private static final int ROW_GAP = 3;
    private static final int SECTION_GAP = 8;
    private static final int LABEL_HEIGHT = 12;

    private final Macro macro;
    private final int baseX;
    private final int baseY;
    private final int width;
    private final Runnable onRemoveMacro;
    private final Runnable onChange;

    public MacroEntryPanel(
            Macro macro,
            int baseX,
            int baseY,
            int width,
            Runnable onRemoveMacro,
            Runnable onChange
    ) {
        this.macro = macro;
        this.baseX = baseX;
        this.baseY = baseY;
        this.width = width;
        this.onRemoveMacro = onRemoveMacro;
        this.onChange = onChange;
    }

    public int getBaseX() {
        return baseX;
    }

    public int getBaseY() {
        return baseY;
    }

    public int getHeight() {
        int conditionRows = Math.max(1, macro.getConditions().size());
        int actionRows = Math.max(1, macro.getActions().size());

        return (PADDING * 2)
                + ROW_HEIGHT
                + SECTION_GAP
                + LABEL_HEIGHT
                + conditionRows * ((ROW_HEIGHT * 2) + ROW_GAP)
                + ROW_HEIGHT
                + SECTION_GAP
                + LABEL_HEIGHT
                + actionRows * (ROW_HEIGHT + ROW_GAP)
                + ROW_HEIGHT;
    }

    public List<AbstractWidget> buildWidgetsAt(int x, int y) {
        List<AbstractWidget> widgets = new ArrayList<>();

        int left = x + PADDING;
        int top = y + PADDING;
        int usableWidth = width - (PADDING * 2);
        int gap = 4;
        int deleteMacroWidth = 70;
        int removeWidth = 20;
        int compactWidth = 58;
        int targetWidth = 112;
        int conditionWidth = 132;

        int contentY = top;

        int nameWidth = usableWidth - deleteMacroWidth - gap;
        EditBox nameBox = new EditBox(
                Minecraft.getInstance().font,
                left,
                contentY,
                nameWidth,
                ROW_HEIGHT,
                Component.literal("Macro name")
        );
        nameBox.setValue(macro.getName() == null ? "" : macro.getName());
        nameBox.setResponder(macro::setName);
        nameBox.setTooltip(Tooltip.create(Component.literal("Name shown in the macro list")));
        widgets.add(nameBox);

        MacroPanelButton deleteMacro = new MacroPanelButton(
                left + nameWidth + gap,
                contentY,
                deleteMacroWidth,
                ROW_HEIGHT,
                Component.literal("Delete"),
                MacroPanelButton.Style.DANGER,
                b -> onRemoveMacro.run()
        );
        deleteMacro.setTooltip(Tooltip.create(Component.literal("Remove this macro")));
        widgets.add(deleteMacro);

        contentY += ROW_HEIGHT + SECTION_GAP + LABEL_HEIGHT;

        int colOp = left;
        int colTarget = colOp + compactWidth + gap;
        int colCondition = colTarget + targetWidth + gap;
        int colNot = colCondition + conditionWidth + gap;
        int colRemove = left + usableWidth - removeWidth;
        int valueWidth = usableWidth;

        for (int i = 0; i < macro.getConditions().size(); i++) {
            int index = i;
            FormalizedConditon cond = macro.getConditions().get(i);

            if (i > 0) {
                MacroPanelCycleButton<LogicOperator> opBtn = new MacroPanelCycleButton<>(
                        colOp,
                        contentY,
                        compactWidth,
                        ROW_HEIGHT,
                        Arrays.asList(LogicOperator.values()),
                        macro.getOperators().get(i),
                        value -> Component.literal(value.toString()),
                        value -> {
                            macro.getOperators().set(index, value);
                            onChange.run();
                        }
                );
                widgets.add(opBtn);
            } else {
                widgets.add(disabledSpacer(colOp, contentY, compactWidth));
            }

            MacroPanelCycleButton<MacroTarget> targetBtn = new MacroPanelCycleButton<>(
                    colTarget,
                    contentY,
                    targetWidth,
                    ROW_HEIGHT,
                    Arrays.asList(MacroTarget.values()),
                    cond.getMacroTarget(),
                    value -> Component.literal(value.toString()),
                    value -> {
                        cond.setMacroTarget(value);
                        onChange.run();
                    }
            );
            widgets.add(targetBtn);

            MacroPanelCycleButton<MacroCondition> condBtn = new MacroPanelCycleButton<>(
                    colCondition,
                    contentY,
                    conditionWidth,
                    ROW_HEIGHT,
                    Arrays.asList(cond.getMacroTarget().getConditions()),
                    cond.getCondition(),
                    value -> Component.literal(value.toString()),
                    value -> {
                        cond.setCondition(value);
                        onChange.run();
                    }
            );
            widgets.add(condBtn);

            MacroPanelCycleButton<Boolean> notBtn = new MacroPanelCycleButton<>(
                    colNot,
                    contentY,
                    72,
                    ROW_HEIGHT,
                    List.of(Boolean.FALSE, Boolean.TRUE),
                    cond.isNegate(),
                    value -> value ? Component.literal("Not") : Component.literal("Match"),
                    value -> {
                        cond.setNegate(value);
                        onChange.run();
                    }
            );
            widgets.add(notBtn);

            MacroPanelButton removeCond = new MacroPanelButton(
                    colRemove,
                    contentY,
                    removeWidth,
                    ROW_HEIGHT,
                    Component.literal("X"),
                    MacroPanelButton.Style.DANGER,
                    b -> {
                        macro.removeCondition(index);
                        onChange.run();
                    }
            );
            removeCond.setTooltip(Tooltip.create(Component.literal("Remove condition")));
            widgets.add(removeCond);

            contentY += ROW_HEIGHT + ROW_GAP;

            EditBox valueBox = new EditBox(
                    Minecraft.getInstance().font,
                    left,
                    contentY,
                    valueWidth,
                    ROW_HEIGHT,
                    Component.literal("Value")
            );
            valueBox.setValue(cond.getTargetValue());
            valueBox.setResponder(cond::setTargetValue);
            valueBox.setTooltip(Tooltip.create(Component.literal("Value used by this condition")));
            widgets.add(valueBox);

            contentY += ROW_HEIGHT + ROW_GAP;
        }

        MacroPanelButton addCond = new MacroPanelButton(
                left,
                contentY,
                120,
                ROW_HEIGHT,
                Component.literal("Add Condition"),
                MacroPanelButton.Style.DEFAULT,
                b -> {
                    MacroTarget target = MacroTarget.HEALTH;
                    macro.addCondition(
                            new FormalizedConditon(target, "0", target.getConditions()[0], false),
                            LogicOperator.AND
                    );
                    onChange.run();
                }
        );
        widgets.add(addCond);

        contentY += ROW_HEIGHT + SECTION_GAP + LABEL_HEIGHT;

        int actionTypeWidth = 160;
        int actionDataWidth = usableWidth - actionTypeWidth - removeWidth - (gap * 2);

        for (int i = 0; i < macro.getActions().size(); i++) {
            int index = i;
            FormalizedAction action = macro.getActions().get(i);

            MacroPanelCycleButton<MacroAction> actionBtn = new MacroPanelCycleButton<>(
                    left,
                    contentY,
                    actionTypeWidth,
                    ROW_HEIGHT,
                    Arrays.asList(MacroAction.values()),
                    action.getAction(),
                    value -> Component.literal(value.toString()),
                    value -> {
                        macro.getActions().set(index, new FormalizedAction(value, action.getActionData()));
                        onChange.run();
                    }
            );
            widgets.add(actionBtn);

            EditBox dataBox = new EditBox(
                    Minecraft.getInstance().font,
                    left + actionTypeWidth + gap,
                    contentY,
                    actionDataWidth,
                    ROW_HEIGHT,
                    Component.literal("Data")
            );
            dataBox.setValue(action.getActionData());
            dataBox.setResponder(value ->
                    macro.getActions().set(index, new FormalizedAction(action.getAction(), value))
            );
            dataBox.setTooltip(Tooltip.create(Component.literal("Parameters for the selected action")));
            widgets.add(dataBox);

            MacroPanelButton removeAction = new MacroPanelButton(
                    left + usableWidth - removeWidth,
                    contentY,
                    removeWidth,
                    ROW_HEIGHT,
                    Component.literal("X"),
                    MacroPanelButton.Style.DANGER,
                    b -> {
                        macro.removeAction(index);
                        onChange.run();
                    }
            );
            removeAction.setTooltip(Tooltip.create(Component.literal("Remove action")));
            widgets.add(removeAction);

            contentY += ROW_HEIGHT + ROW_GAP;
        }

        MacroPanelButton addAction = new MacroPanelButton(
                left,
                contentY,
                120,
                ROW_HEIGHT,
                Component.literal("Add Action"),
                MacroPanelButton.Style.DEFAULT,
                b -> {
                    macro.addAction(new FormalizedAction(MacroAction.SEND_TO_CHAT, ""));
                    onChange.run();
                }
        );
        widgets.add(addAction);

        return widgets;
    }

    public void renderDecorations(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        int panelBottom = y + getHeight();
        guiGraphics.fill(x, y, x + width, panelBottom, 0x66171717);
        guiGraphics.fill(x, y, x + width, y + 1, 0xFF474747);
        guiGraphics.fill(x, panelBottom - 1, x + width, panelBottom, 0xFF2A2A2A);
        guiGraphics.fill(x, y, x + 1, panelBottom, 0xFF6A6A6A);
        guiGraphics.fill(x + 1, y, x + 2, panelBottom, 0xFF2A2A2A);
        guiGraphics.fill(x + width - 2, y, x + width - 1, panelBottom, 0xFF2A2A2A);
        guiGraphics.fill(x + width - 1, y, x + width, panelBottom, 0xFF6A6A6A);

        int textX = x + PADDING;
        int textY = y + PADDING + ROW_HEIGHT + 3;
        int actionLabelY = textY + LABEL_HEIGHT + (Math.max(1, macro.getConditions().size()) * ((ROW_HEIGHT * 2) + ROW_GAP)) + ROW_HEIGHT + SECTION_GAP;

        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("Conditions"), textX, textY, 0xA0A0A0, false);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("Actions"), textX, actionLabelY, 0xA0A0A0, false);
    }

    private MacroPanelButton disabledSpacer(int x, int y, int width) {
        MacroPanelButton button = new MacroPanelButton(
                x,
                y,
                width,
                ROW_HEIGHT,
                Component.empty(),
                MacroPanelButton.Style.SUBTLE,
                b -> {}
        );
        button.active = false;
        button.visible = false;
        return button;
    }
}
