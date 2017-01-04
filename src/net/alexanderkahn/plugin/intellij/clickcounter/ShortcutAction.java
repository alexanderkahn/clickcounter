package net.alexanderkahn.plugin.intellij.clickcounter;

import java.awt.event.KeyEvent;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    public boolean matchesKeyEvent(KeyEvent event) {
        Collection<String> pressedKeys = getKeyPresses(event);
        for (String keyPress : pressedKeys) {
            if (!shortcutText.contains(keyPress)) {
                return false;
            }
        }

        for (String expected : getExpectedKeys()) {
            if (!pressedKeys.contains(expected)) {
                return false;
            }
        }

        return true;

    }

    private Collection<String> getExpectedKeys() {
        Matcher m = Pattern.compile("F?.").matcher(shortcutText);
        Collection<String> expectedKeys = new HashSet<>(shortcutText.length());
        while (m.find()) {
            expectedKeys.add(m.group());
        }
        return expectedKeys;
    }

    private Collection<String> getKeyPresses(KeyEvent event) {
        Map<String, Boolean> eventChars = new HashMap<>();
        eventChars.put("⌘", event.isMetaDown()); //TODO: this won't work on Windows (or Linux?)
        eventChars.put("⌃", event.isControlDown());
        eventChars.put("⌥", event.isAltDown());
        eventChars.put("⇧", event.isShiftDown());
        eventChars.put(KeyEvent.getKeyText(event.getKeyCode()), true);

        return eventChars.entrySet().stream()
                .filter(entry -> Boolean.TRUE.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
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
