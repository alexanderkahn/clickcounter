package net.alexanderkahn.plugin.intellij.clickcounter;

import net.alexanderkahn.plugin.intellij.clickcounter.config.ClickCounterConfig;
import net.alexanderkahn.plugin.intellij.clickcounter.event.EventType;
import org.apache.commons.lang.NotImplementedException;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ClickCounter {

    private final ClickCounterConfig config;
    
    private final Map<ShortcutAction, AtomicInteger> completedClicks = new ConcurrentHashMap<>();
    private final ClickAttemptCounter clickAttemptCounter = new ClickAttemptCounter();

    public ClickCounter(ClickCounterConfig config) {
        this.config = config;
    }
    
    public ClickCounterConfig getConfig() {
        return config;
    }

    public ClickActionInfo getClickActionInfo(ShortcutAction action) {
        int consecutiveClicks = clickAttemptCounter.getClickAttempts(action);
        int clickInstanceCount = getCompletedClicks(action, consecutiveClicks);

        return new ClickActionInfo(action, clickInstanceCount, consecutiveClicks);
    }

    public void registerCompleted(ShortcutAction action, EventType type) {
        switch (type) {
            case KEY_PRESS:
                registerCompletedWithShortcut(action);
                break;
            case MOUSE_CLICK:
                registerCompletedWithClick(action);
                break;
            default:
                throw new NotImplementedException("Unrecognized event type: " + type);
        }
    }

    private void registerCompletedWithShortcut(ShortcutAction action) {
        if (clickAttemptCounter.matchesCurrentAction(action)) {
            clickAttemptCounter.reset();
        }
    }

    private void registerCompletedWithClick(ShortcutAction action) {
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

        boolean matchesCurrentAction(ShortcutAction actionToTest) {
            return action != null && action.equals(actionToTest);
        }

        void reset() {
            action = null;
            count = 0;
        }
    }
}
