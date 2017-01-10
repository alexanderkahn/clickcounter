package net.alexanderkahn.plugin.intellij.clickcounter;

public class ClickActionInfo {

    private ShortcutAction shortcutAction;
    private int discreteClicks;
    private int consecutiveClickAttempts;

    private static final String DESCRIPTION_TEXT_FORMAT = "Press %s (or click %s more times)";

    public ClickActionInfo(ShortcutAction shortcutAction, int discreteClicks, int consecutiveClickAttempts) {
        this.shortcutAction = shortcutAction;
        this.discreteClicks = discreteClicks;
        this.consecutiveClickAttempts = consecutiveClickAttempts;
    }

    public boolean shouldBlockAction() {
        return discreteClicks > consecutiveClickAttempts;
    }

    public String getSubtitle() {
        return shortcutAction.getDescription();
    }

    public String getContent() {
        return String.format(DESCRIPTION_TEXT_FORMAT, shortcutAction.getShortcutText(), discreteClicks - consecutiveClickAttempts);
    }

    public ShortcutAction getShortcutAction() {
        return shortcutAction;
    }
}
