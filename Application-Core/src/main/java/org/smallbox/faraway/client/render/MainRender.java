package org.smallbox.faraway.client.render;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;

@ApplicationObject
public class MainRender {
    @Inject private MinimalRender minimalRender;
    @Inject private GameRender gameRender;
    @Inject private MenuRender menuRender;
    @Inject private Game game;

    public void render() {
        if (game != null && game.getState() == Game.GameStatus.STARTED) {
            gameRender.render();
        } else if (Application.isLoaded) {
            menuRender.render();
        } else if (minimalRender != null) {
            minimalRender.render();
        }
    }

}
