package com.github.jesusmrs05.macrotrigger.client;

public class ChatState {

    public static boolean lockChatEvent = false;
    
    private static String lastMessage = "";

    public static void setLastMessage(String message) {
        lastMessage = message;
    }

    public static String getLastMessage() {
        return lastMessage;
    }
}
