package org.smallbox.faraway.test;

import org.junit.Assert;
import org.junit.Test;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.GameInfo;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.farpoint.desktop.GdxTestApplication;

public class FactoryTest extends TestBase {

    @Test
    public void test1() throws InterruptedException {
        launchGame(new GdxTestApplication.GameTestCallback() {
            @Override
            public void onApplicationReady() {
                Application.gameManager.createGame(GameInfo.create(Application.data.getRegion("base.planet.corrin", "mountain"), 12, 16, 2));

                Application.moduleManager.getModule(CharacterModule.class).addRandom();
                Application.moduleManager.getModule(ItemModule.class).addItem(Application.data.getItemInfo("base.cooker"), 4, 4, 1);
                Application.moduleManager.getModule(ConsumableModule.class).create(Application.data.getItemInfo("base.vegetable"), 100, 0, 0, 1);
            }

            @Override
            public void onGameUpdate(long tick) {
                System.out.println("Game update: " + tick);

                if (tick == 4000) {
                    Assert.assertEquals(0, Application.moduleManager.getModule(ConsumableModule.class).getTotal(Application.data.getItemInfo("base.vegetable")));
                    Assert.assertEquals(1, Application.moduleManager.getModule(ConsumableModule.class).getTotal(Application.data.getItemInfo("base.easy_meal")));
                    quit();
                }
            }
        });

        Thread.sleep(10000);
    }
}
