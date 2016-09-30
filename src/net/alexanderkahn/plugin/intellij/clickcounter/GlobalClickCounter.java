package net.alexanderkahn.plugin.intellij.clickcounter;

import net.alexanderkahn.plugin.intellij.clickcounter.config.ClickActionInfo;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GlobalClickCounter {

    private static final GlobalClickCounter instance = new GlobalClickCounter();
    private final Map<ShortcutAction, AtomicInteger> completedClicks = new ConcurrentHashMap<>();
    private final ClickAttemptCounter clickAttemptCounter = new ClickAttemptCounter();

    private GlobalClickCounter() {
        //don't allow singleton construction
    }

    public static GlobalClickCounter getInstance() {
        return instance;
    }

    public ClickActionInfo getClickActionInfo(ShortcutAction action) {
        int consecutiveClicks = clickAttemptCounter.getClickAttempts(action);
        int clickInstanceCount = getCompletedClicks(action, consecutiveClicks);

        ClickActionInfo info = new ClickActionInfo(action, clickInstanceCount, consecutiveClicks);
        return info;
    }

    public void registerCompleted(ShortcutAction action) {
        clickAttemptCounter.reset();
        synchronized (completedClicks) {
            if (!completedClicks.containsKey(action)) {
                completedClicks.put(action, new AtomicInteger(1));
            } else {
                completedClicks.get(action).incrementAndGet();
            }
        }
    }

    private int getCompletedClicks(ShortcutAction action, int consecutiveClicks) {
        return completedClicks.getOrDefault(action, new AtomicInteger(0)).get();
    }

    private class ClickAttemptCounter {
        private ShortcutAction action;
        private int count;

        int getClickAttempts(ShortcutAction action) {
            if (!Objects.equals(this.action, action)) {
                this.action = action;
                this.count = 0;
            }
            return ++this.count;
        }

        void reset() {
            action = null;
            count = 0;
        }
    }
}
