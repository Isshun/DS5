package org.smallbox.faraway.game.model.character.base;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.game.model.BuffModel;
import org.smallbox.faraway.game.model.CharacterBuffModel;
import org.smallbox.faraway.game.model.GameData;

/**
 * Created by Alex on 16/06/2015.
 */
public class BuffManager {
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

                // Level check failed
                else {
                    if (characterBuff.progress >= level.index) {
                        characterBuff.progress = level.index - 1;
                        characterBuff.levelIndex = level.index - 1;
                        characterBuff.level = characterBuff.levelIndex == -1 ? null : characterBuff.buff.levels.get(characterBuff.levelIndex - 1);
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
        if (!checkBuffValue(level.conditions.minLight, level.conditions.maxLight, Game.getRoomManager().getLight(character.getX(), character.getY()))) return false;
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
                .filter(characterBuff -> characterBuff.isActive())
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
}
