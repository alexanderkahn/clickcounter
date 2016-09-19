package net.alexanderkahn.plugin.intellij.clickcounter.config;

import net.alexanderkahn.plugin.intellij.clickcounter.ShortcutAction;

public class ClickInfo {

    private ShortcutAction shortcutAction;
    private int clickInstances;
    private int consecutiveClicks;
    private static final String DESCRIPTION_TEXT_FORMAT = "Press %s (or click %s more times)";

    public ClickInfo(ShortcutAction shortcutAction, int clickInstances, int consecutiveClicks) {
        this.shortcutAction = shortcutAction;
        this.clickInstances = clickInstances;
        this.consecutiveClicks = consecutiveClicks;
    }

    public boolean shouldConsume() {
        return clickInstances > consecutiveClicks;
    }

    public String getSubtitle() {
        return shortcutAction.getDescription();
    }

    public String getContent() {
        return String.format(DESCRIPTION_TEXT_FORMAT, shortcutAction.getShortcutText(), clickInstances - consecutiveClicks);
    }
}
