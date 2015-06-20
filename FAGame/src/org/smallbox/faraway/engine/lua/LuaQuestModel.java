package org.smallbox.faraway.engine.lua;

import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.QuestManager;
import org.smallbox.faraway.game.manager.ResourceManager;

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
        }

        public void addResource(String resource, int quantity) {
            switch (resource) {
                case "science":
                    ((ResourceManager)Game.getInstance().getManager(ResourceManager.class)).addScience(quantity);
                    break;
            }
        }
    }

    public final QuestManager.QuestModel    quest;
    public final LuaQuestRewardsModel       rewards;
    public int                              option;
    public String                           closeMessage;
    public String                           openMessage;
    public String[]                         openOptions;

    public LuaQuestModel(QuestManager.QuestModel quest) {
        this.quest = quest;
        this.rewards = new LuaQuestRewardsModel();
        this.option = quest.optionIndex;
    }
}
