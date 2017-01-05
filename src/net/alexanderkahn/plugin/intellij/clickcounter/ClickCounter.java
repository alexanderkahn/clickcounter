package net.alexanderkahn.plugin.intellij.clickcounter;

import net.alexanderkahn.plugin.intellij.clickcounter.config.ClickCounterConfig;
import net.alexanderkahn.plugin.intellij.clickcounter.event.EventType;
import net.alexanderkahn.plugin.intellij.clickcounter.notification.NotificationManager;
import org.apache.commons.lang.NotImplementedException;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ClickCounter {

    private final ClickCounterConfig config;
    private final NotificationManager notificationManager;

    private final Map<ShortcutAction, AtomicInteger> completedClicks = new ConcurrentHashMap<>();
    private final ClickAttemptCounter clickAttemptCounter = new ClickAttemptCounter();

    public ClickCounter(ClickCounterConfig config, NotificationManager notificationManager) {
        this.config = config;
        this.notificationManager = notificationManager;
    }

    public ClickCounterConfig getConfig() {
        return config;
    }

    public ActionResult processAction(ShortcutAction action, EventType type) {
        notificationManager.dismissExpired();
        
        switch (type) {
            case KEY_PRESS:
                return processKeyAction(action);
            case MOUSE_CLICK:
                return processClickAction(action);
            default:
                throw new NotImplementedException("Unrecognized event type: " + type);
        }
    }

    private ClickActionInfo getClickActionInfo(ShortcutAction action) {
        int consecutiveClicks = clickAttemptCounter.getConsecutiveClickAttempts(action);
        int distinctClicks = getDistinctClicks(action, consecutiveClicks);
        return new ClickActionInfo(action, distinctClicks, consecutiveClicks);
    }

    private ActionResult processKeyAction(ShortcutAction action) {
        registerCompletedWithShortcut(action);
        return new ActionResult(false);
    }

    private ActionResult processClickAction(ShortcutAction action) {
        ClickActionInfo clickActionInfo = getClickActionInfo(action);
        if (clickActionInfo.shouldBlockAction()) {
            notificationManager.displayNotification(clickActionInfo);
        } else {
            registerCompletedWithClick(clickActionInfo.getShortcutAction());
        }
        return new ActionResult(clickActionInfo.shouldBlockAction());
    }

    private void registerCompletedWithShortcut(ShortcutAction action) {
        if (clickAttemptCounter.matchesCurrentAction(action)) {
            clickAttemptCounter.reset();
        }
        notificationManager.dismissMatching(action);
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
        notificationManager.dismissMatching(action);
    }

    private int getDistinctClicks(ShortcutAction action, int consecutiveClicks) {
        int distinctClicks = completedClicks.getOrDefault(action, new AtomicInteger(0)).get();
        if (clickAttemptCounter.matchesCurrentAction(action)) {
            distinctClicks++; //don't increment it in the store until the action is completed
        }
        return distinctClicks;
    }

    private class ClickAttemptCounter {
        private ShortcutAction action;
        private int count;

        int getConsecutiveClickAttempts(ShortcutAction action) {
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
