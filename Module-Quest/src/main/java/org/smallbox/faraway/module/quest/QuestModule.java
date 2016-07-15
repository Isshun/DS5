package org.smallbox.faraway.module.quest;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.smallbox.faraway.core.engine.lua.LuaGameModel;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.game.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alex on 19/06/2015.
 */
public class QuestModule extends GameModule {
    private final LuaQuestExtend    _extend;
    private List<QuestModel>        _quests;

    public QuestModule() {
        _extend = new LuaQuestExtend();
        LuaModuleManager.getInstance().getExtends().add(_extend);
        _quests = new ArrayList<>();
    }

    @Override
    protected void onGameStart(Game game) {
    }

//    @Override
//    public void onOpenQuest(org.smallbox.faraway.module.quest.QuestModel quest) {
//        if (_ui != null) {
//            _ui.onOpenQuest(quest);
//        }
//    }
//
//    @Override
//    public void onCloseQuest(org.smallbox.faraway.module.quest.QuestModel quest) {
//        if (_ui != null) {
//            _ui.onCloseQuest(quest);
//        }
//    }

    @Override
    protected void onGameUpdate(Game game, int tick) {
        // Check status of current running quests
        if (tick % 100 == 0) {
            checkOpenedQuests();
        }

        // Launch random quest if no one running
        if (tick % 5000 == 0) {
            if (_quests.isEmpty()) {
                launchRandomQuest();
            }
        }
    }

    private void checkOpenedQuests() {
        printInfo("Check quests (open: " + _quests.size() + ")");

        LuaValue luaGame = CoerceJavaToLua.coerce(new LuaGameModel(Game.getInstance()));
//        _quests.stream().filter(quest -> quest.isOpen && !quest.globals.get("OnUpdate").call(luaGame).toboolean()).forEach(quest -> {
//            LuaValue luaQuest = CoerceJavaToLua.coerce(new org.smallbox.faraway.module.quest.LuaQuestModel(quest));
////
////            if (quest.globals.get("OnClose").call(luaGame, luaQuest, LuaValue.valueOf(quest.optionIndex)).toboolean()) {
////                printWarning("quest success !");
////            } else {
////                printWarning("quest fail !");
////            }
////            quest.isOpen = false;
////            quest.message = luaQuest.get("closeMessage").isnil() ? null : String.valueOf(luaQuest.get("closeMessage"));
//////            Application.getInstance().notify(observer -> observer.onCloseQuest(quest));
////            if (_ui != null) {
////                _ui.onCloseQuest(quest);
////            }
//        });

        _quests.removeIf(quest -> !quest.isOpen);
    }

    public void launchRandomQuest() {
        try {
            LuaValue luaGame = CoerceJavaToLua.coerce(new LuaGameModel(Game.getInstance()));
            Collections.shuffle(_extend.getQuestInfos());

            QuestModel quest = new QuestModel(_extend.getQuestInfos().get(0));

            if (quest.info.onQuestCheckListener.onQuestCheck(quest)) {
                printWarning("Open new quest !");
            }

            _quests.add(quest);

            Game.getInstance().setSpeed(0);
//            Application.getInstance().notify(observer -> observer.onOpenQuest(quest));
        } catch (LuaError error) {
            error.printStackTrace();
            printError(error.getMessage());
        }
    }

    public void setChoice(QuestModel quest, int optionIndex) {
        printWarning("Set quest choice: " + optionIndex);

        quest.optionIndex = optionIndex;
        if (quest.info.onQuestStartListener != null) {
            quest.info.onQuestStartListener.onQuestStart(quest, optionIndex);
        }

        checkOpenedQuests();

        Game.getInstance().setSpeed(Game.getInstance().getLastSpeed());
    }
}
