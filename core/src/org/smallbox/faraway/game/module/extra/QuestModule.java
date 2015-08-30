package org.smallbox.faraway.game.module.extra;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.smallbox.faraway.engine.lua.LuaGameModel;
import org.smallbox.faraway.engine.lua.LuaQuestModel;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.module.GameModule;
import org.smallbox.faraway.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 19/06/2015.
 */
public class QuestModule extends GameModule {

    public static class QuestModel {
        public final LuaValue   globals;
        public final String     fileName;
        public boolean          isOpen;
        public String           message;
        public String[]         options;
        public int              optionIndex;

        public QuestModel(String fileName) {
            this.globals = JsePlatform.standardGlobals();
            this.fileName = fileName;
            this.isOpen = true;
        }
    }

    private List<QuestModel>    _quests;

    public QuestModule() {
        _quests = new ArrayList<>();
    }

    @Override
    protected boolean loadOnStart() {
        return GameData.config.manager.quest;
    }

    @Override
    protected void onUpdate(int tick) {
        // Check status of current running quests
        if (tick % 100 == 0) {
            checkOpenedQuests();
        }

        // Launch random quest if no one running
        if (tick % 1000 == 0) {
            if (_quests.isEmpty()) {
                launchRandomQuest();
            }
        }
    }

    private void checkOpenedQuests() {
        printInfo("Check quests (open: " + _quests.size() + ")");

        LuaValue luaGame = CoerceJavaToLua.coerce(new LuaGameModel(Game.getInstance()));
        _quests.stream().filter(quest -> quest.isOpen && !quest.globals.get("OnUpdate").call(luaGame).toboolean()).forEach(quest -> {
            LuaValue luaQuest = CoerceJavaToLua.coerce(new LuaQuestModel(quest));

            if (quest.globals.get("OnClose").call(luaGame, luaQuest, LuaValue.valueOf(quest.optionIndex)).toboolean()) {
                printWarning("quest success !");
            } else {
                printWarning("quest fail !");
            }
            quest.isOpen = false;
            quest.message = luaQuest.get("closeMessage").isnil() ? null : String.valueOf(luaQuest.get("closeMessage"));
            Game.getInstance().notify(observer -> observer.onCloseQuest(quest));
        });

        _quests.removeAll(_quests.stream().filter(quest -> !quest.isOpen).collect(Collectors.toList()));
    }

    public void launchRandomQuest() {
        try {
            LuaValue luaGame = CoerceJavaToLua.coerce(new LuaGameModel(Game.getInstance()));
            List<File> files = Arrays.asList(new File("data/quests/").listFiles());
            Collections.shuffle(files);
            for (File file: files) {
                if (!isRunning(file.getName()) && file.getName().endsWith(".lua")) {
                    // Load lua script
                    QuestModel quest = new QuestModel(file.getName());
                    quest.globals.get("dofile").call(LuaValue.valueOf("data/quests/" + file.getName()));

                    // Check if quest can be launched
                    LuaValue luaQuest = CoerceJavaToLua.coerce(new LuaQuestModel(quest));
                    if (quest.globals.get("CanBeLaunched").call(luaGame, luaQuest).toboolean()) {
                        printWarning("Open new quest !");

                        // Get quest message
                        quest.message = String.valueOf(luaQuest.get("openMessage"));

                        // Get quest options
                        LuaValue opts = luaQuest.get("openOptions");
                        int len = opts.length();
                        quest.options = new String[len];
                        for (int i = 0; i < len; i++) {
                            quest.options[i] = String.valueOf(opts.get(i+1));
                        }

                        Game.getInstance().notify(observer -> observer.onOpenQuest(quest));
                    }
                }
            }
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
        quest.globals.get("OnLaunch").call(luaGame, luaQuest);
        _quests.add(quest);

        checkOpenedQuests();
    }

    private boolean isRunning(String fileName) {
        for (QuestModel quest: _quests) {
            if (quest.fileName.equals(fileName)) {
                return true;
            }
        }
        return false;
    }
}
