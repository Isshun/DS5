package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.util.log.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ApplicationObject
public class ThreadManager {
    @Inject private DependencyManager dependencyManager;
    @Inject private GameTime gameTime;
    @Inject private Game game;

    private final ScheduledExecutorService _moduleScheduler = Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService  _moduleScheduler4 = Executors.newScheduledThreadPool(1);

    public ScheduledExecutorService getScheduler() {
        return _moduleScheduler;
    }

    public void launchBackgroundThread() {

        _moduleScheduler.scheduleAtFixedRate(() -> {
            try {
                if (game.isRunning()) {
                    game.addTick();

                    game.update();

//                    _modules.forEach(module -> {
//                        try {
//                            module.updateGame(Game.this);
//                        } catch (Exception e) {
//                            Log.error(e);
//                        }
//                    });

                    Application.notify(gameObserver -> gameObserver.onGameUpdate());
                }
            } catch (Exception e) {
                Log.error(e);
            }
        }, 0, 40, TimeUnit.MILLISECONDS);

        _moduleScheduler4.scheduleAtFixedRate(() -> {
            try {
                if (game.isRunning()) {
                    Application.notify(gameObserver -> gameObserver.onGameLongUpdate(game));
                }
            } catch (Error e) {
                Log.error(e);
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);

    }

}
