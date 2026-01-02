package com.github.jesusmrs05.macrotrigger.util;

import java.util.function.Consumer;

public enum MacroAction {
    SEND_TO_CHAT((param) -> {
        //TODO: add logic to send param to chat
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
