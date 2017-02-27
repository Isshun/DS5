package org.smallbox.faraway.test;

import org.junit.Assert;
import org.junit.Test;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.modules.character.CharacterModule;

public class CharacterModuleTest extends TestBase {

    @Test
    public void test1() throws InterruptedException {
        launchApplication(() ->

                Application.gameManager.createGame("base.planet.corrin", "mountain", 12, 16, 2, new GameManager.GameListener() {

                    @Override
                    public void onGameCreate(Game game) {
                        Application.moduleManager.getModule(CharacterModule.class).addRandom();
                    }

                    @Override
                    public void onGameUpdate(Game game) {
                        if (game.getTick() == 100) {
                            Assert.assertEquals(1, Application.moduleManager.getModule(CharacterModule.class).getCharacters().size());
                            complete();
                        }
                    }

                }));
    }
}
