package net.alexanderkahn.plugin.intellij.clickcounter;

import com.intellij.notification.Notification;
import com.intellij.notification.Notifications;
import net.alexanderkahn.plugin.intellij.clickcounter.config.ClickActionInfo;

import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PopUpNotifier {

    private final static Set<ClickNotification> displayedTips = new HashSet<>();

    public static void firePopUp(ClickActionInfo info) {
        ClickNotification tip = new ClickNotification(info);
        Notifications.Bus.notify(tip);
        displayedTips.add(tip);
    }

    public static void dismissExistingPopUps() {
        synchronized (displayedTips) {
            displayedTips.forEach(Notification::expire);
            displayedTips.clear();
        }
    }

    public static void dismissMatchingEvents(KeyEvent event) {
        synchronized (displayedTips) {
            Collection<ClickNotification> matchingNotifications = displayedTips.stream().filter(notification -> notification.shouldExpire(event)).collect(Collectors.toList());
            matchingNotifications.forEach(ClickNotification::expire);
            displayedTips.removeAll(matchingNotifications);
        }
    }
}
