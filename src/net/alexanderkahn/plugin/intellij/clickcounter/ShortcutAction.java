package net.alexanderkahn.plugin.intellij.clickcounter;

public class ShortcutAction {
    private String shortcutText = "";
    private String description = "";

    public ShortcutAction(String shortcutText, String description) {
        this.shortcutText = shortcutText;
        this.description = description;
    }

    public String getShortcutText() {
        return shortcutText;
    }

    public String getDescription() {
        return description;
    }
}
