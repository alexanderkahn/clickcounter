package net.alexanderkahn.plugin.intellij.clickcounter;

import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

public class PopUpNotifier {

    public static void firePopUp(ShortcutAction shortcut) {
        Notification tip = new Notification("Click Counter", AllIcons.General.BalloonInformation, "Click Counter", buildSubtitle(shortcut), shortcut.getShortcutText(), NotificationType.INFORMATION, null);
        Notifications.Bus.notify(tip);
    }

    private static String buildSubtitle(ShortcutAction shortcut) {
        return "Shortcut available for " + shortcut.getDescription();
    }
}
