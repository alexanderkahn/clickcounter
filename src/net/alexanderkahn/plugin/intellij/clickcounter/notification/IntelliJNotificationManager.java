package net.alexanderkahn.plugin.intellij.clickcounter.notification;

import com.intellij.notification.Notifications;
import net.alexanderkahn.plugin.intellij.clickcounter.ClickActionInfo;
import net.alexanderkahn.plugin.intellij.clickcounter.ShortcutAction;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class IntelliJNotificationManager implements NotificationManager {

    private final Set<ClickNotification> displayedTips = new HashSet<>();

    @Override
    public void displayNotification(ClickActionInfo info) {
        ClickNotification tip = new ClickNotification(info);
        Notifications.Bus.notify(tip);
        displayedTips.add(tip);
    }

    @Override
    public void dismissMatching(ShortcutAction actionToMatch) {
        synchronized (displayedTips) {
            Collection<ClickNotification> matchingNotifications = displayedTips.stream().filter(notification -> notification.shouldExpire(actionToMatch)).collect(Collectors.toList());
            matchingNotifications.forEach(ClickNotification::expire);
            displayedTips.removeAll(matchingNotifications);
        }
    }

    @Override
    public void dismissExpired() {
        synchronized (displayedTips) {
            displayedTips.removeAll(displayedTips.stream().filter(ClickNotification::isExpired).collect(Collectors.toList()));
        }
    }
}
