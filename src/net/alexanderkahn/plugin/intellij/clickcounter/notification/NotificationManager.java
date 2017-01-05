package net.alexanderkahn.plugin.intellij.clickcounter.notification;

import net.alexanderkahn.plugin.intellij.clickcounter.ClickActionInfo;
import net.alexanderkahn.plugin.intellij.clickcounter.ShortcutAction;

public interface NotificationManager {

    void displayNotification(ClickActionInfo info);

    void dismissMatching(ShortcutAction actionToMatch);

    void dismissExpired();
}
