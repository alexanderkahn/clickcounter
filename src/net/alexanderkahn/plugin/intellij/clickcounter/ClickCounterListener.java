package net.alexanderkahn.plugin.intellij.clickcounter;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.ActionManagerEx;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.editor.impl.EditorComponentImpl;
import net.alexanderkahn.plugin.intellij.clickcounter.config.ClickActionInfo;
import net.alexanderkahn.plugin.intellij.clickcounter.config.ClickCounterConfig;
import net.alexanderkahn.plugin.intellij.clickcounter.event.EventType;
import net.alexanderkahn.plugin.intellij.clickcounter.event.ShortcutActionFactory;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Optional;

public class ClickCounterListener implements ApplicationComponent, AWTEventListener, AnActionListener {
    private ClickCounterConfig config = ClickCounterConfig.getInstance();
    private ClickCounter counter = ClickCounter.getInstance();

    @Override
    public void eventDispatched(AWTEvent event) {
        if (config.getEnabled() && isLeftMouseClick(event)) {
            handleMouseEvent((MouseEvent) event);
        }
    }

    @Override
    public void beforeActionPerformed(AnAction anAction, DataContext dataContext, AnActionEvent anActionEvent) {
        if (anActionEvent.getInputEvent() instanceof KeyEvent) {
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

        shortcutAction.ifPresent(action -> evaluateClickValidity(action, event));
    }

    private void handleKeyEvent(KeyEvent event) {
        ShortcutAction shortcutAction = ShortcutActionFactory.fromKeyEvent(event);
        counter.registerCompleted(shortcutAction, EventType.KEY_PRESS);
        PopUpNotifier.dismissMatchingEvents(shortcutAction);
    }

    private boolean isNotComponent(Object source) {
        return !Component.class.isAssignableFrom(source.getClass());
    }

    private boolean isEditorComponent(Object source) {
        return source.getClass() == EditorComponentImpl.class;
    }

    private void evaluateClickValidity(ShortcutAction shortcutAction, MouseEvent event) {
        ClickActionInfo clickActionInfo = counter.getClickActionInfo(shortcutAction);
        if (clickActionInfo.shouldConsume()) {
            event.consume();
            PopUpNotifier.firePopUp(clickActionInfo);
        } else {
            counter.registerCompleted(clickActionInfo.getShortcutAction(), EventType.MOUSE_CLICK);
            PopUpNotifier.dismissExistingPopUps();
        }
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
