package net.alexanderkahn.plugin.intellij.clickcounter.config;

import java.util.concurrent.atomic.AtomicBoolean;

public class ClickCounterConfig {
    private static ClickCounterConfig instance = new ClickCounterConfig();

    private final AtomicBoolean enabled = new AtomicBoolean(true);

    private ClickCounterConfig() {
        //don't allow singleton construction
    }

    public static ClickCounterConfig getInstance() {
        return instance;
    }

    public boolean getEnabled() {
        return enabled.get();
    }

    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }


}
