package org.smallbox.faraway.game.module.dev;

import org.smallbox.faraway.game.module.GameModule;

/**
 * Created by Alex on 30/08/2015.
 */
public class LogModule extends GameModule {
    @Override
    protected void onUpdate(int tick) {
    }

    @Override
    public void onLog(String tag, String message) {
        System.out.print("[");
        System.out.print(tag);
        System.out.print("] ");
        System.out.println(message);
    }
}
