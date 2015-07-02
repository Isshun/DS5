package org.smallbox.faraway.game.model.character.base;

import org.smallbox.faraway.engine.lua.LuaCharacterModel;
import org.smallbox.faraway.engine.lua.LuaConsumableModel;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.QuestManager;
import org.smallbox.faraway.game.manager.ResourceManager;
import org.smallbox.faraway.game.model.item.ParcelModel;

/**
 * Created by Alex on 01/07/2015.
 */
public class LuaBuffModel {
    public static class LuaQuestRewardsModel {

        public void addCrew(LuaCharacterModel luaCharacter) {
            Game.getCharacterManager().add(luaCharacter.character);
        }

        public void addConsumable(LuaConsumableModel luaConsumable) {
            Game.getWorldManager().putConsumable(luaConsumable.itemInfo, luaConsumable.quantity, 5, 5, 0);
            throw new RuntimeException("not allowed");
        }

        public void addConsumable(LuaConsumableModel luaConsumable, int x, int y) {
            ParcelModel parcel = Game.getWorldManager().getNearestFreeSpace(x + (int) (Math.random() * 6) - 3, y + (int) (Math.random() * 6) - 3, false, true);
            if (parcel != null) {
                Game.getWorldManager().putConsumable(luaConsumable.itemInfo, luaConsumable.quantity, parcel.getX(), parcel.getY(), 0);
            }
        }

        public void addResource(String resource, int quantity) {
            switch (resource) {
                case "science":
                    ((ResourceManager)Game.getInstance().getManager(ResourceManager.class)).addScience(quantity);
                    break;
            }
        }
    }

    public final BuffManager.BuffScriptModel quest;
    public final LuaQuestRewardsModel       rewards;
    public int                              option;
    public String                           closeMessage;
    public String                           openMessage;
    public String[]                         openOptions;

    public LuaBuffModel(BuffManager.BuffScriptModel quest) {
        this.quest = quest;
        this.rewards = new LuaQuestRewardsModel();
        this.option = quest.optionIndex;
    }
}
