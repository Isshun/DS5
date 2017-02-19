package org.smallbox.faraway.test;

import org.junit.Assert;
import org.junit.Test;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.farpoint.desktop.GdxTestApplication;

public class LoadLastGameTest extends TestBase {

    @Test
    public void test1() throws InterruptedException {
        launchGame(new GdxTestApplication.GameTestCallback() {
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
