package org.smallbox.faraway.engine.lua;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.helper.WorldHelper;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.module.quest.QuestModel;
import org.smallbox.faraway.game.module.quest.QuestModule;
import org.smallbox.faraway.game.module.extra.ResourceModule;

/**
 * Created by Alex on 20/06/2015.
 */
public class LuaQuestModel {
    public static class LuaQuestRewardsModel {

        public void addCrew(LuaCharacterModel luaCharacter) {
            Game.getCharacterManager().add(luaCharacter.character);
        }

        public void addConsumable(LuaConsumableModel luaConsumable) {
            Game.getWorldManager().putConsumable(luaConsumable.itemInfo, luaConsumable.quantity, 5, 5, 0);
            throw new RuntimeException("not allowed");
        }

        public void addConsumable(LuaConsumableModel luaConsumable, int x, int y) {
            ParcelModel parcel = WorldHelper.getNearestFreeParcel(x + (int) (Math.random() * 6) - 3, y + (int) (Math.random() * 6) - 3, false, true);
            if (parcel != null) {
                Game.getWorldManager().putConsumable(luaConsumable.itemInfo, luaConsumable.quantity, parcel.x, parcel.y, 0);
            }
        }

        public void addResource(String resource, int quantity) {
            switch (resource) {
                case "science":
                    ((ResourceModule)Game.getInstance().getModule(ResourceModule.class)).addScience(quantity);
                    break;
            }
        }
    }

    public final QuestModel                 quest;
    public final LuaQuestRewardsModel       rewards;
    public int                              option;
    public String                           closeMessage;
    public String                           openMessage;
    public String[]                         openOptions;

    public LuaQuestModel(QuestModel quest) {
        this.quest = quest;
        this.rewards = new LuaQuestRewardsModel();
        this.option = quest.optionIndex;
    }
}
