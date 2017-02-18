package org.smallbox.faraway.test;

import org.junit.Assert;
import org.junit.Test;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.modules.character.CharacterModule;

public class LoadLastGameTest extends TestBase {

    @Test
    public void test1() throws InterruptedException {
        launchGame(new GameTestCallback() {
            @Override
            public void onApplicationReady() {
                Application.gameManager.loadLastGame();
            }

            @Override
            public void onGameUpdate(long tick) {
                Assert.assertEquals(3, Application.moduleManager.getModule(CharacterModule.class).getCharacters().size());
                quit();
            }
        });
    }
}
