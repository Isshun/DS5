//package org.smallbox.faraway.test.unit;
//
//import org.junit.Assert;
//import org.junit.Test;
//import org.smallbox.faraway.test.technique.HeadlessTestBase;
//
//public class ConsumableModuleTest extends HeadlessTestBase {
//
//    @Test
//    public void AddSeveralConsumableToSamePosition() throws InterruptedException {
//
//        GameTestHelper.create(this)
//                .runOnGameCreate(() -> {
//                    consumableModule.addConsumable("base.consumable.vegetable.rice", 10, 5, 5, 1);
//                    consumableModule.addConsumable("base.consumable.vegetable.rice", 10, 5, 5, 1);
//
//                    consumableModule.addConsumable("base.consumable.vegetable.corn", 10, 5, 5, 1);
//                    consumableModule.addConsumable("base.consumable.vegetable.corn", 20, 5, 5, 1);
//
//                    consumableModule.addConsumable("base.consumable.vegetable.carrot", 2, 5, 5, 1);
//                    consumableModule.addConsumable("base.consumable.vegetable.carrot", 2, 5, 5, 1);
//                })
//                .run();
//
//        Assert.assertEquals("base.consumable.vegetable.rice", consumableModule.getConsumable(5, 5, 1).getInfo().name);
//        Assert.assertEquals(20, consumableModule.getConsumable(5, 5, 1).getFreeQuantity());
//
//        Assert.assertEquals("base.consumable.vegetable.corn", consumableModule.getConsumable(6, 5, 1).getInfo().name);
//        Assert.assertEquals(30, consumableModule.getConsumable(6, 5, 1).getFreeQuantity());
//
//        Assert.assertEquals("base.consumable.vegetable.carrot", consumableModule.getConsumable(6, 6, 1).getInfo().name);
//        Assert.assertEquals(4, consumableModule.getConsumable(6, 6, 1).getFreeQuantity());
//    }
//
//}
