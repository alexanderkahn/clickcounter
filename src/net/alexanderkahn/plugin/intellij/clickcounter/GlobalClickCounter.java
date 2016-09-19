package net.alexanderkahn.plugin.intellij.clickcounter;

import net.alexanderkahn.plugin.intellij.clickcounter.config.ClickInfo;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GlobalClickCounter {

    private static final GlobalClickCounter instance = new GlobalClickCounter();
    private final Map<ShortcutAction, AtomicInteger> clickInstances = new ConcurrentHashMap<>();
    private final ConsecutiveClickCounter consecutiveClickCounter = new ConsecutiveClickCounter();

    private GlobalClickCounter() {
        //don't allow singleton construction
    }

    public static GlobalClickCounter getInstance() {
        return instance;
    }

    public ClickInfo getClickInfo(ShortcutAction action) {
        int consecutiveClicks = consecutiveClickCounter.getConsecutiveClicks(action);
        int clickInstanceCount = getClickInstanceCount(action, consecutiveClicks);

        ClickInfo info = new ClickInfo(action, clickInstanceCount, consecutiveClicks);
        if (!info.shouldConsume()) {
            consecutiveClickCounter.reset();
        }
        return info;
    }

    private int getClickInstanceCount(ShortcutAction action, int consecutiveClicks) {
        int clickInstanceCount;
        synchronized (clickInstances) {
            if (!clickInstances.containsKey(action)) {
                clickInstances.put(action, new AtomicInteger(0));
            }

            if (consecutiveClicks > 1) {
                //consecutive clicks should not increment the count
                clickInstanceCount = clickInstances.get(action).get();
            } else {
                clickInstanceCount = clickInstances.get(action).incrementAndGet();
            }
        }
        return clickInstanceCount;
    }

    private class ConsecutiveClickCounter {
        private ShortcutAction action;
        private int count;

        int getConsecutiveClicks(ShortcutAction action) {
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
