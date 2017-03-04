package org.smallbox.faraway.test.unit;

import org.junit.Assert;
import org.junit.Test;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.test.HeadlessTestBase;

public class CharacterModuleTest extends HeadlessTestBase {

    @Test
    public void test1() throws InterruptedException {

        Application.gameManager.createGame("base.planet.corrin", "mountain", 12, 16, 2, new GameManager.GameListener() {

            @Override
            public void onGameCreate(Game game) {
                characterModule.addRandom();
            }

            @Override
            public void onGameUpdate(Game game) {
                if (game.getTick() == 100) {
                    Assert.assertEquals(1, characterModule.getCharacters().size());
                    complete();
                }
            }

        });

    }

}
