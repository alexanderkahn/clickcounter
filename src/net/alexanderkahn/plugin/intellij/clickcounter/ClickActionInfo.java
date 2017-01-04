package net.alexanderkahn.plugin.intellij.clickcounter;

public class ClickActionInfo {

    private ShortcutAction shortcutAction;
    private int completedClicks;
    private int clickAttempts;
    
    private static final String DESCRIPTION_TEXT_FORMAT = "Press %s (or click %s more times)";

    public ClickActionInfo(ShortcutAction shortcutAction, int completedClicks, int clickAttempts) {
        this.shortcutAction = shortcutAction;
        this.completedClicks = completedClicks;
        this.clickAttempts = clickAttempts;
    }

    public boolean shouldConsume() {
        return completedClicks > clickAttempts;
    }

    public String getSubtitle() {
        return shortcutAction.getDescription();
    }

    public String getContent() {
        return String.format(DESCRIPTION_TEXT_FORMAT, shortcutAction.getShortcutText(), completedClicks - clickAttempts);
    }

    public ShortcutAction getShortcutAction() {
        return shortcutAction; //TODO: this is shitty
    }
}
