package net.alexanderkahn.plugin.intellij.clickcounter.event;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.openapi.actionSystem.impl.ActionMenuItem;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.util.text.StringUtil;
import net.alexanderkahn.plugin.intellij.clickcounter.ShortcutAction;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ShortcutActionFactory {

    private ShortcutActionFactory() {
        //static
    }

    private static Pattern pattern = Pattern.compile("F?."); //This, uh, won't work with the literal F key in some cases. Just realized that. Jeez.

    public static ShortcutAction fromKeyEvent(KeyEvent event) {
        return new ShortcutAction(getKeyPresses(event), null);
    }

    private static List<String> getKeyPresses(KeyEvent event) {
        Map<String, Boolean> eventChars = new HashMap<>();
        eventChars.put(KeyEvent.getKeyText(KeyEvent.VK_META), event.isMetaDown());
        eventChars.put(KeyEvent.getKeyText(KeyEvent.VK_CONTROL), event.isControlDown());
        eventChars.put(KeyEvent.getKeyText(KeyEvent.VK_ALT), event.isAltDown());
        eventChars.put(KeyEvent.getKeyText(KeyEvent.VK_SHIFT), event.isShiftDown());
        eventChars.put(KeyEvent.getKeyText(event.getKeyCode()), true);

        return eventChars.entrySet().stream()
                .filter(entry -> Boolean.TRUE.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static Optional<ShortcutAction> fromComponent(Component component) {
        ShortcutAction shortcut = null;
        if (isActionButton(component)) {
            shortcut = fromActionButton((ActionButton) component);
        } else if (isActionMenuItem(component)) {
            shortcut = fromActionMenuItem((ActionMenuItem) component);
        }

        if (shortcut == null || StringUtil.isEmptyOrSpaces(shortcut.getShortcutText())) {
            return Optional.empty();
        }

        return Optional.of(shortcut);
    }

    private static ShortcutAction fromActionButton(ActionButton actionButton) {
        AnAction anAction = actionButton.getAction();
        if (anAction == null) {
            return null;
        }

        String shortcutText = KeymapUtil.getFirstKeyboardShortcutText(anAction);
        String description = anAction.getTemplatePresentation().getText();

        return new ShortcutAction(getShortcutKeys(shortcutText), description);
    }

    private static ShortcutAction fromActionMenuItem(ActionMenuItem actionMenuItem) {
        String shortcutText = actionMenuItem.getFirstShortcutText();
        String description = actionMenuItem.getText();

        return new ShortcutAction(getShortcutKeys(shortcutText), description);
    }

    private static List<String> getShortcutKeys(String shortcutText) {
        List<String> shortcutKeys = new ArrayList<>(shortcutText.length());
        Matcher m = pattern.matcher(shortcutText);
        while (m.find()) {
            shortcutKeys.add(m.group());
        }
        return shortcutKeys;
    }

    private static boolean isActionButton(Component component) {
        return ActionButton.class.isAssignableFrom(component.getClass());
    }

    private static boolean isActionMenuItem(Component component) {
        return ActionMenuItem.class.isAssignableFrom(component.getClass());
    }
}