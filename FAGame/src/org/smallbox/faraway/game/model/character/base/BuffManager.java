package org.smallbox.faraway.game.model.character.base;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.smallbox.faraway.engine.lua.LuaCharacterModel;
import org.smallbox.faraway.engine.lua.LuaGameModel;
import org.smallbox.faraway.engine.lua.LuaQuestModel;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.BaseManager;
import org.smallbox.faraway.game.manager.RoomManager;
import org.smallbox.faraway.game.model.BuffModel;
import org.smallbox.faraway.game.model.CharacterBuffModel;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 16/06/2015.
 */
public class BuffManager extends BaseManager {

    public static class BuffScriptModel {
        public final LuaValue globals;
        public final String     fileName;
        public boolean          isOpen;
        public String           message;
        public String[]         options;
        public int              optionIndex;
        public int              level;
        public int              mood;

        public BuffScriptModel(String fileName) {
            this.globals = JsePlatform.standardGlobals();
            this.fileName = fileName;
            this.isOpen = true;
        }
    }

    public static void checkBuffs(CharacterModel character) {
        boolean needSort = false;
        for (CharacterBuffModel characterBuff: character.getBuffs()) {
            for (BuffModel.BuffLevelModel level: characterBuff.buff.levels) {
                // Level checked successfully
                if (checkBuff(character, level)) {
                    // Is current level and not the last
                    if (characterBuff.levelIndex < characterBuff.maxLevelIndex && (!characterBuff.buff.levels.get(characterBuff.levelIndex + 1).conditions.previous || characterBuff.levelIndex + 1 == level.index)) {
                        // Upgrade progress to current level if too low
                        if (characterBuff.progress < level.index - 1) {
                            characterBuff.progress = level.index - 1;
                        }
                        if (characterBuff.buff.levels.get(characterBuff.levelIndex + 1).delay > 0) {
                            characterBuff.progress += 1f / characterBuff.buff.levels.get(characterBuff.levelIndex + 1).delay / GameData.config.tickPerHour;
                        } else {
                            characterBuff.progress++;
                        }
                        if (characterBuff.progress >= characterBuff.levelIndex) {
                            characterBuff.levelIndex = (int)characterBuff.progress - 1;
                            characterBuff.level = characterBuff.levelIndex < 0 ? null : characterBuff.buff.levels.get(characterBuff.levelIndex);
                            needSort = true;
                        }
                    }
                }

                // Level onCheck failed
                else {
                    if (characterBuff.progress >= level.index) {
                        characterBuff.progress = level.index - 1;
                        characterBuff.levelIndex = level.index - 1;
                        characterBuff.level = characterBuff.levelIndex == -1 ? null : characterBuff.buff.levels.get(characterBuff.levelIndex);
                    }
                }
            }
        }

        if (needSort) {
            character.getBuffs().sort((b1, b2) -> b2.getMood() - b1.getMood());
        }
    }

    private static boolean checkBuff(CharacterModel character, BuffModel.BuffLevelModel level) {
        if (level.conditions.character != null && !level.conditions.character.contains(character.getTypeName())) return false;

        // Character
        if (!checkBuffValue(level.conditions.minFood, level.conditions.maxFood, character.getNeeds().food))     return false;
        if (!checkBuffValue(level.conditions.minSocial, level.conditions.maxSocial, character.getNeeds().relation)) return false;
        if (!checkBuffValue(level.conditions.minOxygen, level.conditions.maxOxygen, character.getNeeds().oxygen)) return false;
        if (!checkBuffValue(level.conditions.minCharacterTemperature, level.conditions.maxCharacterTemperature, character.getBodyHeat())) return false;

        // World
        if (!checkBuffValue(level.conditions.minDay, level.conditions.maxDay, Game.getInstance().getDay())) return false;
        if (!checkBuffValue(level.conditions.minLight, level.conditions.maxLight, ((RoomManager)Game.getInstance().getManager(RoomManager.class)).getLight(character.getX(), character.getY()))) return false;
        if (!checkBuffValue(level.conditions.minEnvironment, level.conditions.maxEnvironment, Game.getWorldManager().getEnvironmentValue(character.getX(), character.getY(), GameData.config.environmentDistance))) return false;

        return true;
    }

    private static boolean checkBuffValue(int minValue, double maxValue, double currentValue) {
        if (minValue != Integer.MIN_VALUE || maxValue != Integer.MIN_VALUE) {
            if (minValue != Integer.MIN_VALUE && currentValue <= minValue) {
                return true;
            }
            if (maxValue != Integer.MIN_VALUE && currentValue >= maxValue) {
                return true;
            }
            return false;
        }
        return true;
    }

    public static void applyBuffs(CharacterModel character) {
        character.getBuffs().stream()
                .filter(CharacterBuffModel::isActive)
                .forEach(characterBuff -> applyBuff(character, characterBuff.level));
    }

    private static void applyBuff(CharacterModel character, BuffModel.BuffLevelModel level) {
        if (level != null) {
            if (level.effects != null) {
                if (level.effects.fainting != 0) {
                    Log.warning("about to faint");
                    if (Math.random() < level.effects.fainting) {
                        character.setIsFaint();
                    }
                }
                if (level.effects.death != 0) {
                    Log.warning("about to death");
                    if (Math.random() < level.effects.death) {
                        character.setIsDead();
                    }
                }
                if (level.effects.mood != 0) {
                    character.getNeeds().updateHappiness(level.effects.mood * 0.1);
                }
            }
        }
    }

    @Override
    protected void onUpdate(int tick) {
        LuaValue luaGame = CoerceJavaToLua.coerce(new LuaGameModel(Game.getInstance()));

        for (CharacterModel character: Game.getCharacterManager().getCharacters()) {
            Log.debug("Check buff for " + character.getName());

            LuaValue luaCharacter = CoerceJavaToLua.coerce(new LuaCharacterModel(character));
            for (BuffScriptModel buff: character._buffsScript) {
                LuaValue ret = buff.globals.get("OnUpdate").call(luaGame, luaCharacter);
                buff.message = ret.isnil() ? null : ret.get(1).toString();
                buff.level = ret.isnil() ? 0 : ret.get(2).toint();
                buff.mood = ret.isnil() ? 0 : ret.get(3).toint();

                LuaValue effects = ret.isnil() || ret.get(4).isnil() ? null : ret.get(4);
                if (effects != null) {
                    Log.notice("apply effects");
                    for (int i = 0; i < effects.length(); i++) {
                        Log.notice("effect: " + effects.get(i+1).get(1).toString() + " rand: " + effects.get(i+1).get(2).todouble());
                    }
                }
            }
            Collections.sort(character._buffsScript, (b1, b2) -> b2.mood - b1.mood);
        }
//        Game.getInstance().notify(observer -> observer.onCloseQuest(quest));
    }

    @Override
    public void onAddCharacter(CharacterModel character) {
        try {
            for (File file : new File("data/buffs/scripted/").listFiles()) {
                if (file.getName().endsWith(".lua")) {
                    // Load lua script
                    BuffScriptModel buff = new BuffScriptModel(file.getName());
                    buff.globals.get("dofile").call(LuaValue.valueOf("data/buffs/scripted/" + file.getName()));
                    character.addBuff(buff);
                }
            }
        } catch (LuaError error) {
            error.printStackTrace();
            Log.error(error.getMessage());
        }
    }
}
