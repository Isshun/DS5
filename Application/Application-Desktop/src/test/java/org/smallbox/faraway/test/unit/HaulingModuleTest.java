package org.smallbox.faraway.test.unit;

import org.junit.Assert;
import org.junit.Test;
import org.smallbox.faraway.test.technique.GameTestHelper;
import org.smallbox.faraway.test.technique.GuiTestBase;

public class HaulingModuleTest extends GuiTestBase {

    @Test
    public void HaulingConsumablesToStorage() throws InterruptedException {

        GameTestHelper.create(this)
                .runOnGameCreate(() -> {
                    consumableModule.addConsumable("base.consumable.vegetable.rice", 10, 5, 5, 1);
                    consumableModule.addConsumable("base.consumable.vegetable.corn", 10, 6, 5, 1);
                    characterModule.addRandom();
                })
                .runUntil(200);

        Assert.assertEquals("base.consumable.vegetable.carrot", consumableModule.getConsumable(6, 6, 1).getInfo().name);
        Assert.assertEquals(4, consumableModule.getConsumable(6, 6, 1).getFreeQuantity());
    }

}
