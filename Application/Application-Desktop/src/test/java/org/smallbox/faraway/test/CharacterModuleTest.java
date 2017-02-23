package org.smallbox.faraway.test;

import org.junit.Assert;
import org.junit.Test;
import org.smallbox.faraway.client.GDXApplication;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.GameInfo;
import org.smallbox.faraway.modules.character.CharacterModule;

public class CharacterModuleTest extends TestBase {

    @Test
    public void test1() throws InterruptedException {
        launchApplication(new GDXApplication.GameTestCallback() {
            @Override
            public void onApplicationReady() {

                Application.gameManager.createGame(GameInfo.create(Application.data.getRegion("base.planet.corrin", "mountain"), 12, 16, 2));

                Application.moduleManager.getModule(CharacterModule.class).addRandom();
            }

            @Override
            public void onGameUpdate(long tick) {
                if (tick == 100) {
                    Assert.assertEquals(1, Application.moduleManager.getModule(CharacterModule.class).getCharacters().size());
                    quit();
                }
            }
        });
    }
}
