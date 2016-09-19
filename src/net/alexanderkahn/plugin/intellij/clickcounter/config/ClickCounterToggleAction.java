package net.alexanderkahn.plugin.intellij.clickcounter.config;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;

public class ClickCounterToggleAction extends ToggleAction {
    private ClickCounterConfig config = ClickCounterConfig.getInstance();

    @Override
    public boolean isSelected(AnActionEvent anActionEvent) {
        return config.getEnabled();
    }

    @Override
    public void setSelected(AnActionEvent anActionEvent, boolean isSelected) {
        config.setEnabled(isSelected);
    }
}