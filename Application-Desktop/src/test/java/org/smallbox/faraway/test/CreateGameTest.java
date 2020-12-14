//package org.smallbox.faraway.test;
//
//import org.junit.Assert;
//import org.junit.Test;
//import org.smallbox.faraway.core.Application;
//import org.smallbox.faraway.core.game.Game;
//import org.smallbox.faraway.core.game.GameManager;
//import org.smallbox.faraway.core.game.helper.WorldHelper;
//import org.smallbox.faraway.modules.character.CharacterModule;
//import org.smallbox.faraway.modules.consumable.ConsumableModule;
//import org.smallbox.faraway.modules.item.ItemModule;
//import org.smallbox.faraway.modules.structure.StructureModule;
//import org.smallbox.faraway.modules.world.WorldModule;
//import org.smallbox.faraway.test.base.TestBase;
//
//public class CreateGameTest extends TestBase {
//
//    @Test
//    public void test1() throws InterruptedException {
//        launchApplication(() ->
//
//                Application.gameManager.createGame("base.planet.corrin", "mountain", 12, 16, 2, new GameManager.GameListener() {
//
//                    @Override
//                    public void onGameCreate(Game game) {
//
//                        // Check world map size
//                        Assert.assertEquals(12, Application.moduleManager.getModule(WorldModule.class).getWidth());
//                        Assert.assertEquals(16, Application.moduleManager.getModule(WorldModule.class).getHeight());
//                        Assert.assertEquals(2, Application.moduleManager.getModule(WorldModule.class).getFloors());
//
//                        // Check world map parcels
//                        for (int x = 0; x < 12; x++) {
//                            for (int y = 0; y < 16; y++) {
//                                for (int z = 0; z < 2; z++) {
//                                    Assert.assertNotNull(WorldHelper.getParcel(x, y, z));
//                                    Assert.assertNotNull(Application.moduleManager.getModule(WorldModule.class).getParcel(x, y, z));
//                                    Assert.assertEquals(WorldHelper.getParcel(x, y, z), Application.moduleManager.getModule(WorldModule.class).getParcel(x, y, z));
//                                }
//                            }
//                        }
//
//                        // Check consumables / items / structures modules are empty
//                        Assert.assertEquals(0, Application.moduleManager.getModule(ConsumableModule.class).getConsumables().size());
//                        Assert.assertEquals(0, Application.moduleManager.getModule(StructureModule.class).getStructures().size());
//                        Assert.assertEquals(0, Application.moduleManager.getModule(ItemModule.class).getItems().size());
//
//                        // Check character module is empty
//                        Assert.assertEquals(0, Application.moduleManager.getModule(CharacterModule.class).getCharacters().size());
//
//                        complete();
//                    }
//
//                    @Override
//                    public void onGameUpdate(Game game) {
//
//                    }
//
//                }));
//    }
//}
