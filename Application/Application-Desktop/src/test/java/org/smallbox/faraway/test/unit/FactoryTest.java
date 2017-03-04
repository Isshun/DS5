package org.smallbox.faraway.test.unit;

import org.junit.Assert;
import org.junit.Test;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.test.HeadlessTestBase;

public class FactoryTest extends HeadlessTestBase {

    @Test
    public void test1() throws InterruptedException {

        Application.gameManager.createGame("base.planet.corrin", "mountain", 12, 16, 2, new GameManager.GameListener() {

            @Override
            public void onGameCreate(Game game) {
                characterModule.addRandom().addInventory("base.vegetable_rice", 10);
                itemModule.addItem("base.cooker", true, 4, 4, 1);
                consumableModule.addConsumable("base.vegetable_rice", 10, 2, 10, 1);
                consumableModule.addConsumable("base.vegetable_carrot", 10, 4, 10, 1);
            }

            @Override
            public void onGameUpdate(Game game) {
                if (game.getTick() == 100) {
                    Assert.assertEquals(0, consumableModule.getTotal("base.vegetable_rice"));
                    Assert.assertEquals(0, consumableModule.getTotal("base.vegetable_carrot"));
                    Assert.assertEquals(20, consumableModule.getTotal("base.easy_meal"));
                    complete();
                }
            }

        });

    }

}
