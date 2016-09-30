package net.alexanderkahn.plugin.intellij.clickcounter;

import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import net.alexanderkahn.plugin.intellij.clickcounter.config.ClickActionInfo;

import java.util.HashSet;
import java.util.Set;

public class PopUpNotifier {

    private static final String TITLE = "Click Counter";
    private final static Set<Notification> displayedTips = new HashSet<>();

    public static void firePopUp(ClickActionInfo info) {
        Notification tip = new Notification(TITLE, AllIcons.General.BalloonInformation, TITLE, info.getSubtitle(), info.getContent(), NotificationType.INFORMATION, null);
        Notifications.Bus.notify(tip);
        displayedTips.add(tip);
    }

    public static void dismissExistingPopUps() {
        synchronized (displayedTips) {
            displayedTips.forEach(Notification::expire);
            displayedTips.clear();
        }
    }
}
