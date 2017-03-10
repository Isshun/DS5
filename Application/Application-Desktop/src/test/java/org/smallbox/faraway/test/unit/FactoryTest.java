package org.smallbox.faraway.test.unit;

import org.junit.Assert;
import org.junit.Test;
import org.smallbox.faraway.test.technique.GameTestHelper;
import org.smallbox.faraway.test.technique.HeadlessTestBase;

public class FactoryTest extends HeadlessTestBase {

    @Test
    public void createEasyMealFromRiceAndCarrot() throws InterruptedException {

        GameTestHelper.create(this)
                .runOnGameCreate(() -> {
                    itemModule.addItem("base.cooker", true, 4, 4, 1);
                    consumableModule.addConsumable("base.consumable.vegetable.rice", 10, 2, 10, 1);
                    consumableModule.addConsumable("base.consumable.vegetable.carrot", 10, 4, 10, 1);
                    characterModule.addRandom();
                })
                .runUntil(200);

        Assert.assertEquals(0, consumableModule.getTotal("base.consumable.vegetable.rice"));
        Assert.assertEquals(0, consumableModule.getTotal("base.consumable.vegetable.carrot"));
        Assert.assertEquals(20, consumableModule.getTotal("base.consumable.easy_meal"));
    }

}
