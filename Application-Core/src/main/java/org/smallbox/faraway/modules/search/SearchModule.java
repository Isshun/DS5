package org.smallbox.faraway.modules.search;

import org.smallbox.faraway.client.manager.NotificationManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.game.Game;

public class SearchModule extends ModuleBase {
    @Inject private NotificationManager notificationManager;

    private int goal = 500;
    private int current;

    @Override
    public void onGameLongUpdate(Game game) {
        current++;
        notificationManager.add("test");
    }

    public int getGoal() {
        return goal;
    }

    public int getCurrent() {
        return current;
    }
}