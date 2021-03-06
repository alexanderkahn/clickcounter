package net.alexanderkahn.plugin.intellij.clickcounter.notification;

import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import net.alexanderkahn.plugin.intellij.clickcounter.ClickActionInfo;
import net.alexanderkahn.plugin.intellij.clickcounter.ShortcutAction;

public class ClickNotification extends Notification {

    private ClickActionInfo clickActionInfo;

    private static final String TITLE = "Click Counter";


    public ClickNotification(ClickActionInfo clickActionInfo) {
        super(TITLE, AllIcons.General.BalloonInformation, TITLE, clickActionInfo.getSubtitle(), clickActionInfo.getContent(), NotificationType.INFORMATION, null);
        this.clickActionInfo = clickActionInfo;
    }

    public boolean shouldExpire(ShortcutAction action) {
        return this.clickActionInfo.getShortcutAction().matchesShortcut(action);
    }
}
