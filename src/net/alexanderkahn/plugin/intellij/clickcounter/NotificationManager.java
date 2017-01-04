package net.alexanderkahn.plugin.intellij.clickcounter;

import com.intellij.notification.Notification;
import com.intellij.notification.Notifications;
import net.alexanderkahn.plugin.intellij.clickcounter.config.ClickActionInfo;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class NotificationManager {

    private final static Set<ClickNotification> displayedTips = new HashSet<>();

    public static void displayNotification(ClickActionInfo info) {
        ClickNotification tip = new ClickNotification(info);
        Notifications.Bus.notify(tip);
        displayedTips.add(tip);
    }

    public static void dismissAll() {
        synchronized (displayedTips) {
            displayedTips.forEach(Notification::expire);
            displayedTips.clear();
        }
    }

    public static void dismissMatching(ShortcutAction actionToMatch) {
        synchronized (displayedTips) {
            Collection<ClickNotification> matchingNotifications = displayedTips.stream().filter(notification -> notification.shouldExpire(actionToMatch)).collect(Collectors.toList());
            matchingNotifications.forEach(ClickNotification::expire);
            displayedTips.removeAll(matchingNotifications);
        }
    }
}
