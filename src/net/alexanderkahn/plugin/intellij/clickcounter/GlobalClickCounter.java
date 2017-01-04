package net.alexanderkahn.plugin.intellij.clickcounter;

import net.alexanderkahn.plugin.intellij.clickcounter.config.ClickActionInfo;

import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GlobalClickCounter {

    private static final GlobalClickCounter instance = new GlobalClickCounter();
    private final Map<ShortcutAction, AtomicInteger> completedClicks = new ConcurrentHashMap<>();
    private final ClickAttemptCounter clickAttemptCounter = new ClickAttemptCounter();

    private GlobalClickCounter() {
        //fudge you, I'm a singleton
    }

    public static GlobalClickCounter getInstance() {
        return instance;
    }

    public ClickActionInfo getClickActionInfo(ShortcutAction action) {
        int consecutiveClicks = clickAttemptCounter.getClickAttempts(action);
        int clickInstanceCount = getCompletedClicks(action, consecutiveClicks);

        return new ClickActionInfo(action, clickInstanceCount, consecutiveClicks);
    }

    public void registerCompletedWithShortcut(KeyEvent event) {
        if (clickAttemptCounter.matchesAction(event)) {
            clickAttemptCounter.reset();
        }
    }

    public void registerCompletedWithClick(ShortcutAction action) {
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

        boolean matchesAction(KeyEvent event) {
            return action != null && action.matchesKeyEvent(event);
        }

        void reset() {
            action = null;
            count = 0;
        }
    }
}
