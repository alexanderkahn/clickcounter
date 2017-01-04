package net.alexanderkahn.plugin.intellij.clickcounter.event;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.openapi.actionSystem.impl.ActionMenuItem;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.util.text.StringUtil;
import net.alexanderkahn.plugin.intellij.clickcounter.ShortcutAction;

import java.awt.*;
import java.util.Optional;

public class ShortcutActionFactory {

    private ShortcutActionFactory() {
        //static
    }

    public static Optional<ShortcutAction> fromComponent(Component component) {
        ShortcutAction shortcut = null;
        if (isActionButton(component)) {
            shortcut = buildShortcut((ActionButton) component);
        } else if (isActionMenuItem(component)) {
            shortcut = buildShortcut((ActionMenuItem) component);
        }

        if (shortcut == null || StringUtil.isEmptyOrSpaces(shortcut.getShortcutText())) {
            return Optional.empty();
        }

        return Optional.of(shortcut);
    }

    private static ShortcutAction buildShortcut(ActionButton actionButton) {
        AnAction anAction = actionButton.getAction();
        if (anAction == null) {
            return null;
        }

        String shortcutText = KeymapUtil.getFirstKeyboardShortcutText(anAction);
        String description = anAction.getTemplatePresentation().getText();

        return new ShortcutAction(shortcutText, description);
    }

    private static ShortcutAction buildShortcut(ActionMenuItem actionMenuItem) {
        String shortcutText = actionMenuItem.getFirstShortcutText();
        String description = actionMenuItem.getText();

        return new ShortcutAction(shortcutText, description);
    }

    private static boolean isActionButton(Component component) {
        return ActionButton.class.isAssignableFrom(component.getClass());
    }

    private static boolean isActionMenuItem(Component component) {
        return ActionMenuItem.class.isAssignableFrom(component.getClass());
    }
}