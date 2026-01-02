package com.github.jesusmrs05.macrotrigger.client;

import com.github.jesusmrs05.macrotrigger.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class MacroEntryPanel {

    private static final int PADDING = 8;
    private static final int ROW_HEIGHT = 20;
    private static final int ROW_GAP = 4;

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

    public int getBaseX() { return baseX; }
    public int getBaseY() { return baseY; }

    public int getHeight() {
        int conditionRows = Math.max(1, macro.getConditions().size());
        int actionRows = Math.max(1, macro.getActions().size());

        // cada condición ocupa 2 filas
        return
                24 +
                        conditionRows * (ROW_HEIGHT * 2 + ROW_GAP * 2) + 28 +
                        24 +
                        actionRows * (ROW_HEIGHT + ROW_GAP) + 28;
    }

    public List<AbstractWidget> buildWidgetsAt(int x, int y) {
        List<AbstractWidget> widgets = new ArrayList<>();

        int cx = x + PADDING;
        int usableWidth = width - PADDING * 2;
        int cy = y;

        int gap = 4;
        int trashWidth = 24;
        int notWidth = 48;

        int colOp = cx;
        int colTarget = colOp + 64 + gap;
        int colCondition = colTarget + 120 + gap;
        int colNot = colCondition + 140 + gap;
        int colTrash = cx + usableWidth - trashWidth;

        // ===== DELETE MACRO =====
        widgets.add(
                Button.builder(Component.literal("🗑 Delete Macro"), b -> onRemoveMacro.run())
                        .bounds(cx + usableWidth - 140, cy, 140, ROW_HEIGHT)
                        .build()
        );
        cy += ROW_HEIGHT + 6;

        // ===== CONDITIONS HEADER =====
        widgets.add(disabledHeader("CONDITIONS", cx, cy, usableWidth));
        cy += ROW_HEIGHT + ROW_GAP;

        // ===== CONDITIONS =====
        for (int i = 0; i < macro.getConditions().size(); i++) {
            int index = i;
            FormalizedConditon cond = macro.getConditions().get(i);

            // ---- FIRST ROW (selectors) ----

            // Operator
            if (i > 0) {
                widgets.add(
                        CycleButton.builder(v -> Component.literal(v.toString()),
                                        macro.getOperators().get(i))
                                .withValues(LogicOperator.values())
                                .create(colOp, cy, 64, ROW_HEIGHT, Component.literal("Op"),
                                        (btn, val) -> {
                                            macro.getOperators().set(index, val);
                                            onChange.run();
                                        })
                );
            } else {
                widgets.add(disabledSpacer(colOp, cy, 64));
            }

            // Target
            widgets.add(
                    CycleButton.builder(v -> Component.literal(v.toString()),
                                    cond.getMacroTarget())
                            .withValues(MacroTarget.values())
                            .create(colTarget, cy, 120, ROW_HEIGHT, Component.literal("Target"),
                                    (btn, val) -> {
                                        cond.setMacroTarget(val);
                                        onChange.run();
                                    })
            );

            // Condition
            widgets.add(
                    CycleButton.builder(v -> Component.literal(v.toString()),
                                    cond.getCondition())
                            .withValues(cond.getMacroTarget().getConditions())
                            .create(colCondition, cy, 140, ROW_HEIGHT, Component.literal("Condition"),
                                    (btn, val) -> {
                                        cond.setCondition(val);
                                        onChange.run();
                                    })
            );

            // NOT
            widgets.add(
                    CycleButton.booleanBuilder(
                            Component.literal("Yes"),
                            Component.literal("No"),
                            cond.isNegate()
                    ).create(colNot, cy, notWidth, ROW_HEIGHT, Component.literal("Not"),
                            (btn, val) -> {
                                cond.setNegate(val);
                                onChange.run();
                            })
            );

            // Remove condition
            widgets.add(
                    Button.builder(Component.literal("🗑"), b -> {
                                macro.removeCondition(index);
                                onChange.run();
                            })
                            .bounds(colTrash, cy, trashWidth, ROW_HEIGHT)
                            .build()
            );

            cy += ROW_HEIGHT + ROW_GAP;

            // ---- SECOND ROW (target value) ----
            EditBox valueBox = new EditBox(
                    Minecraft.getInstance().font,
                    cx,
                    cy,
                    usableWidth,
                    ROW_HEIGHT,
                    Component.literal("Value")
            );
            valueBox.setValue(cond.getTargetValue());
            valueBox.setResponder(v -> {
                cond.setTargetValue(v);
                onChange.run();
            });
            widgets.add(valueBox);

            cy += ROW_HEIGHT + ROW_GAP;
        }

        // Add condition
        widgets.add(
                Button.builder(Component.literal("+ Add Condition"), b -> {
                            MacroTarget t = MacroTarget.HEALTH;
                            macro.addCondition(
                                    new FormalizedConditon(t, "0", t.getConditions()[0], false),
                                    LogicOperator.AND
                            );
                            onChange.run();
                        })
                        .bounds(cx, cy, 160, ROW_HEIGHT)
                        .build()
        );
        cy += ROW_HEIGHT + 8;

        // ===== ACTIONS HEADER =====
        widgets.add(disabledHeader("ACTIONS", cx, cy, usableWidth));
        cy += ROW_HEIGHT + ROW_GAP;

        // ===== ACTIONS =====
        for (int i = 0; i < macro.getActions().size(); i++) {
            int index = i;
            FormalizedAction action = macro.getActions().get(i);

            widgets.add(
                    CycleButton.builder(v -> Component.literal(v.toString()),
                                    action.getAction())
                            .withValues(MacroAction.values())
                            .create(cx, cy, 160, ROW_HEIGHT, Component.literal("Action"),
                                    (btn, val) -> {
                                        macro.getActions().set(index,
                                                new FormalizedAction(val, action.getActionData()));
                                        onChange.run();
                                    })
            );

            EditBox data = new EditBox(
                    Minecraft.getInstance().font,
                    cx + 168, cy,
                    usableWidth - 168 - trashWidth - gap,
                    ROW_HEIGHT,
                    Component.literal("Data")
            );
            data.setValue(action.getActionData());
            data.setResponder(v -> {
                macro.getActions().set(index,
                        new FormalizedAction(action.getAction(), v));
                onChange.run();
            });
            widgets.add(data);

            widgets.add(
                    Button.builder(Component.literal("🗑"), b -> {
                                macro.removeAction(index);
                                onChange.run();
                            })
                            .bounds(cx + usableWidth - trashWidth, cy, trashWidth, ROW_HEIGHT)
                            .build()
            );

            cy += ROW_HEIGHT + ROW_GAP;
        }

        // Add action
        widgets.add(
                Button.builder(Component.literal("+ Add Action"), b -> {
                            macro.addAction(
                                    new FormalizedAction(MacroAction.SEND_TO_CHAT, "")
                            );
                            onChange.run();
                        })
                        .bounds(cx, cy, 160, ROW_HEIGHT)
                        .build()
        );

        return widgets;
    }

    // ===== helpers =====

    private Button disabledHeader(String text, int x, int y, int width) {
        Button b = Button.builder(Component.literal(text), btn -> {})
                .bounds(x, y, width, ROW_HEIGHT)
                .build();
        b.active = false;
        return b;
    }

    private Button disabledSpacer(int x, int y, int width) {
        Button b = Button.builder(Component.literal(""), btn -> {})
                .bounds(x, y, width, ROW_HEIGHT)
                .build();
        b.active = false;
        return b;
    }
}
