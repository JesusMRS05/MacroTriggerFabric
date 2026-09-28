package com.github.jesusmrs05.macrotrigger.util;

import java.util.HashMap;
import java.util.Map;

public class TimerManager {

    private static final Map<String, Timer> timers = new HashMap<>();

    public static void start(String id, long duration) {
        timers.put(id, new Timer(System.currentTimeMillis(), duration));
    }

    public static boolean isFinished(String id) {
        Timer timer = timers.get(id);

        if (timer == null) {
            return true;
        }

        return System.currentTimeMillis() >= timer.startEpoch + timer.duration;
    }

    private static class Timer {

        private final long startEpoch;
        private final long duration;

        private Timer(long startEpoch, long duration) {
            this.startEpoch = startEpoch;
            this.duration = duration;
        }
    }
}