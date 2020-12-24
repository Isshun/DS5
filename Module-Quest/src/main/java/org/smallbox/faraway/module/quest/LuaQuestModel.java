//package org.smallbox.faraway.module.quest;
//
//import org.smallbox.faraway.core.engine.lua.LuaCharacterModel;
//import org.smallbox.faraway.core.engine.lua.LuaConsumableModel;
//import org.smallbox.faraway.core.game.helper.WorldHelper;
//import org.smallbox.faraway.core.module.world.model.ParcelModel;
//
///**
// * Created by Alex
// */
//public class LuaQuestModel {
//    public static class LuaQuestRewardsModel {
//
//        public void addCrew(LuaCharacterModel luaCharacter) {
//            ModuleHelper.getCharacterModule().add(luaCharacter.character);
//        }
//
//        public void addConsumable(LuaConsumableModel luaConsumable) {
//            ModuleHelper.getWorldModule().putConsumable(WorldHelper.getParcel(5, 5, 5), luaConsumable.itemInfo, luaConsumable.quantity);
//            throw new RuntimeException("not allowed");
//        }
//
//        public void addConsumable(LuaConsumableModel luaConsumable, int x, int y, int z) {
//            ParcelModel parcel = WorldHelper.getNearestFreeParcel(x + (int) (Math.random() * 6) - 3, y + (int) (Math.random() * 6) - 3, z, false, true);
//            if (parcel != null) {
//                ModuleHelper.getWorldModule().putConsumable(parcel, luaConsumable.itemInfo, luaConsumable.quantity);
//            }
//        }
//
//        public void addResource(String resource, int quantity) {
//            switch (resource) {
//                case "science":
////                    ((ResourceModule)Application.moduleManager.getModule(ResourceModule.class)).addScience(quantity);
//                    break;
//            }
//        }
//    }
//
//    public final QuestModel                 quest;
//    public final LuaQuestRewardsModel       rewards;
//    public int                              option;
//    public String                           closeMessage;
//    public String                           openMessage;
//    public String[]                         openOptions;
//
//    public LuaQuestModel(QuestModel quest) {
//        this.quest = quest;
//        this.rewards = new LuaQuestRewardsModel();
//        this.option = quest.optionIndex;
//    }
//}
