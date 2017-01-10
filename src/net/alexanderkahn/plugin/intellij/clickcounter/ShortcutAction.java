package net.alexanderkahn.plugin.intellij.clickcounter;

import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Objects;

public class ShortcutAction {
    private List<String> shortcutKeys;
    private String description;

    public ShortcutAction(List<String> shortcutKeys, String description) {
        this.shortcutKeys = shortcutKeys;
        this.description = description;
    }

    public String getShortcutText() {
        return StringUtils.join(shortcutKeys, "");
    }

    public String getDescription() {
        return description;
    }

    public boolean matchesShortcut(ShortcutAction otherAction) {
        return otherAction != null
                && this.shortcutKeys != null
                && otherAction.shortcutKeys != null
                && this.shortcutKeys.size() == otherAction.shortcutKeys.size()
                && this.shortcutKeys.containsAll(otherAction.shortcutKeys);

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShortcutAction that = (ShortcutAction) o;
        return Objects.equals(shortcutKeys, that.shortcutKeys) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shortcutKeys, description);
    }
}
