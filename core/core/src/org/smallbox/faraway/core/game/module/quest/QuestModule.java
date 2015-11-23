package org.smallbox.faraway.core.game.module.quest;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.lua.LuaGameModel;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 19/06/2015.
 */
public class QuestModule extends GameModule {
    private List<QuestModel>    _quests;

    public QuestModule() {
        _quests = new ArrayList<>();
    }

    @Override
    protected boolean loadOnStart() {
        return true;
    }

    @Override
    protected void onLoaded(Game game) {
    }

//    @Override
//    public void onOpenQuest(QuestModel quest) {
//        if (_ui != null) {
//            _ui.onOpenQuest(quest);
//        }
//    }
//
//    @Override
//    public void onCloseQuest(QuestModel quest) {
//        if (_ui != null) {
//            _ui.onCloseQuest(quest);
//        }
//    }

    @Override
    protected void onUpdate(int tick) {
        // Check status of current running quests
        if (tick % 100 == 0) {
            checkOpenedQuests();
        }

        // Launch random quest if no one running
        if (tick % 100 == 0) {
            if (_quests.isEmpty()) {
                launchRandomQuest();
            }
        }
    }

    private void checkOpenedQuests() {
        printInfo("Check quests (open: " + _quests.size() + ")");

        LuaValue luaGame = CoerceJavaToLua.coerce(new LuaGameModel(Game.getInstance()));
//        _quests.stream().filter(quest -> quest.isOpen && !quest.globals.get("OnUpdate").call(luaGame).toboolean()).forEach(quest -> {
//            LuaValue luaQuest = CoerceJavaToLua.coerce(new LuaQuestModel(quest));
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

        _quests.removeAll(_quests.stream().filter(quest -> !quest.isOpen).collect(Collectors.toList()));
    }

    public void launchRandomQuest() {
        try {
            LuaValue luaGame = CoerceJavaToLua.coerce(new LuaGameModel(Game.getInstance()));
            Collections.shuffle(Data.getData().quests);

            QuestModel quest = new QuestModel(Data.getData().quests.get(0));

            if (quest.info.onQuestCheckListener.onQuestCheck(quest)) {
                printWarning("Open new quest !");

                if (quest.info.onQuestStartListener != null) {
                    quest.info.onQuestStartListener.onQuestStart(quest);
                }
            }

            _quests.add(quest);

            Application.getInstance().notify(observer -> observer.onOpenQuest(quest));
        } catch (LuaError error) {
            error.printStackTrace();
            printError(error.getMessage());
        }
    }

    public void selectQuestionOption(QuestModel quest, int optionIndex) {
        printWarning("Launch new quest: " + optionIndex);

        LuaValue luaGame = CoerceJavaToLua.coerce(new LuaGameModel(Game.getInstance()));
        LuaValue luaQuest = CoerceJavaToLua.coerce(new LuaQuestModel(quest));
        luaQuest.set("option", optionIndex);
        quest.optionIndex = optionIndex;
//        quest.globals.get("OnLaunch").call(luaGame, luaQuest);
        _quests.add(quest);

        checkOpenedQuests();
    }
}
