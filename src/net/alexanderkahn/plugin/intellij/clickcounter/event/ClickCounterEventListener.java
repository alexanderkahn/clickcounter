package net.alexanderkahn.plugin.intellij.clickcounter.event;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.ActionManagerEx;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.editor.impl.EditorComponentImpl;
import net.alexanderkahn.plugin.intellij.clickcounter.ActionResult;
import net.alexanderkahn.plugin.intellij.clickcounter.ClickCounter;
import net.alexanderkahn.plugin.intellij.clickcounter.ShortcutAction;
import net.alexanderkahn.plugin.intellij.clickcounter.config.ClickCounterConfig;
import net.alexanderkahn.plugin.intellij.clickcounter.notification.IntelliJNotificationManager;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Optional;

public class ClickCounterEventListener implements ApplicationComponent, AWTEventListener, AnActionListener {
    private ClickCounter counter = new ClickCounter(ClickCounterConfig.getInstance(), new IntelliJNotificationManager());

    @Override
    public void eventDispatched(AWTEvent event) {
        if (counter.getConfig().getEnabled() && isLeftMouseClick(event)) {
            handleMouseEvent((MouseEvent) event);
        }
    }

    @Override
    public void beforeActionPerformed(AnAction anAction, DataContext dataContext, AnActionEvent anActionEvent) {
        if (counter.getConfig().getEnabled() && anActionEvent.getInputEvent() instanceof KeyEvent) {
            handleKeyEvent((KeyEvent) anActionEvent.getInputEvent());
        }
    }

    @Override
    public void afterActionPerformed(AnAction anAction, DataContext dataContext, AnActionEvent anActionEvent) {

    }

    @Override
    public void beforeEditorTyping(char c, DataContext dataContext) {

    }

    private void handleMouseEvent(MouseEvent event) {
        final Object source = event.getSource();

        if (isNotComponent(source) || isEditorComponent(source)) {
            return;
        }

        Component sourceComponent = (Component) source;
        Optional<ShortcutAction> shortcutAction = ShortcutActionFactory.fromComponent(sourceComponent);

        shortcutAction.ifPresent(shortcutAction1 -> processEvent(event, shortcutAction1, EventType.MOUSE_CLICK));
    }

    private void processEvent(InputEvent event, ShortcutAction shortcutAction, EventType eventType) {
        ActionResult result = counter.processAction(shortcutAction, eventType);
        if (result.isBlocked()) {
            event.consume();
        }
    }

    private void handleKeyEvent(KeyEvent event) {
        ShortcutAction shortcutAction = ShortcutActionFactory.fromKeyEvent(event);
        processEvent(event, shortcutAction, EventType.KEY_PRESS);
    }

    private boolean isNotComponent(Object source) {
        return !Component.class.isAssignableFrom(source.getClass());
    }

    private boolean isEditorComponent(Object source) {
        return source.getClass() == EditorComponentImpl.class;
    }

    private boolean isLeftMouseClick(AWTEvent event) {
        return event.getID() == MouseEvent.MOUSE_RELEASED && ((MouseEvent) event).getButton() == MouseEvent.BUTTON1;
    }

    public void initComponent() {
        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK);
        ActionManagerEx.getInstanceEx().addAnActionListener(this);
    }

    public void disposeComponent() {
        Toolkit.getDefaultToolkit().removeAWTEventListener(this);
        ActionManagerEx.getInstanceEx().removeAnActionListener(this);
    }

    @NotNull
    public String getComponentName() {
        return "Click Counter";
    }
}
