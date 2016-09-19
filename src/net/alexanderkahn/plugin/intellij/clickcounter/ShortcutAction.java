package net.alexanderkahn.plugin.intellij.clickcounter;

import java.util.Objects;

public class ShortcutAction {
    private String shortcutText;
    private String description;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShortcutAction that = (ShortcutAction) o;
        return Objects.equals(shortcutText, that.shortcutText) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shortcutText, description);
    }
}
