package net.alexanderkahn.plugin.intellij.clickcounter;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.editor.impl.EditorComponentImpl;
import net.alexanderkahn.plugin.intellij.clickcounter.config.ClickCounterConfig;
import net.alexanderkahn.plugin.intellij.clickcounter.config.ClickInfo;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.util.Optional;

public class ClickCounter implements ApplicationComponent, AWTEventListener {
    private ClickCounterConfig config = ClickCounterConfig.getInstance();

    public void eventDispatched(AWTEvent event) {
        if (config.getEnabled() && isLeftMouseClick(event)) {
            handleMouseEvent((MouseEvent) event);
        }
    }

    private void handleMouseEvent(MouseEvent event) {
        final Object source = event.getSource();

        if (isNotComponent(source) || isEditorComponent(source)) {
            return;
        }

        Component sourceComponent = (Component) source;
        Optional<ClickInfo> clickInfo = ClickInfoFactory.buildClickInfoIfAvailable(sourceComponent);

        if (clickInfo.isPresent()) {
            evaluateClickValidity(clickInfo.get(), event);
        }
    }

    private boolean isNotComponent(Object source) {
        return !Component.class.isAssignableFrom(source.getClass());
    }

    private boolean isEditorComponent(Object source) {
        return source.getClass() == EditorComponentImpl.class;
    }

    private void evaluateClickValidity(ClickInfo clickInfo, MouseEvent event) {
        if (clickInfo.shouldConsume()) {
            PopUpNotifier.firePopUp(clickInfo);
            event.consume();
        } else {
            PopUpNotifier.dismissExistingPopUps();
        }
    }

    private boolean isLeftMouseClick(AWTEvent event) {
        return event.getID() == MouseEvent.MOUSE_RELEASED && ((MouseEvent) event).getButton() == MouseEvent.BUTTON1;
    }

    public void initComponent() {
        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK);
    }

    public void disposeComponent() {
        Toolkit.getDefaultToolkit().removeAWTEventListener(this);
    }

    @NotNull
    public String getComponentName() {
        return "Click Counter";
    }
}
