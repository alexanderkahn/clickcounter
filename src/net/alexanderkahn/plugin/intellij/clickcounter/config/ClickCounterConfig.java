package net.alexanderkahn.plugin.intellij.clickcounter.config;

import java.util.concurrent.atomic.AtomicBoolean;

public class ClickCounterConfig {
    private static ClickCounterConfig instance = new ClickCounterConfig();
    private AtomicBoolean clickCounter = new AtomicBoolean(true);

    private ClickCounterConfig() {
        //don't allow singleton construction
    }

    public boolean getClickCounter() {
        return clickCounter.get();
    }

    public void setClickCounter(boolean clickCounter) {
        this.clickCounter.set(clickCounter);
    }

    public static ClickCounterConfig getInstance() {
        return instance;
    }
}
