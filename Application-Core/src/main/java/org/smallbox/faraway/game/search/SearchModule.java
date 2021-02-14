package org.smallbox.faraway.game.search;

import org.smallbox.faraway.client.notification.NotificationManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameLongUpdate;
import org.smallbox.faraway.core.module.ModuleBase;

public class SearchModule extends ModuleBase {
    @Inject private NotificationManager notificationManager;

    private int goal = 500;
    private int current;

    @OnGameLongUpdate
    public void onGameLongUpdate() {
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
