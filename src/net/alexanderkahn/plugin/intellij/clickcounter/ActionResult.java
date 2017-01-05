package net.alexanderkahn.plugin.intellij.clickcounter;

public class ActionResult {

    final boolean blocked;

    public ActionResult(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isBlocked() {
        return blocked;
    }
}
