package org.smallbox.faraway.model.character;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.model.BuffModel;
import org.smallbox.faraway.model.CharacterBuffModel;
import org.smallbox.faraway.model.GameData;
import org.smallbox.faraway.model.room.RoomModel;

/**
 * Created by Alex on 16/06/2015.
 */
public class CharacterBuffManager {
    public static void checkBuffs(CharacterModel character) {
        boolean needSort = false;
        for (CharacterBuffModel characterBuff: character.getBuffs()) {
            for (BuffModel.BuffLevelModel level: characterBuff.buff.levels) {
                // Level checked successfully
                if (checkBuff(character, level)) {
                    // Is current level and not the last
                    if (characterBuff.levelIndex + 1 == level.index && characterBuff.maxLevelIndex > characterBuff.levelIndex) {
                        if (characterBuff.buff.levels.get(characterBuff.levelIndex).delay > 0) {
                            characterBuff.progress += 1f / characterBuff.buff.levels.get(characterBuff.levelIndex).delay / GameData.config.tickPerHour;
                        } else {
                            characterBuff.progress++;
                        }
                        if (characterBuff.progress >= characterBuff.levelIndex + 1) {
                            characterBuff.level = characterBuff.buff.levels.get(characterBuff.levelIndex);
                            characterBuff.levelIndex = characterBuff.level.index;
                            needSort = true;
                        }
                    }
                }

                // Level check failed
                else {
                    if (characterBuff.progress >= level.index) {
                        characterBuff.progress = level.index - 1;
                        characterBuff.levelIndex = level.index - 1;
                        characterBuff.level = characterBuff.levelIndex == 0 ? null : characterBuff.buff.levels.get(characterBuff.levelIndex);
                    }
                }
            }
        }

        if (needSort) {
            character.getBuffs().sort((b1, b2) -> b2.getMood() - b1.getMood());
        }
    }

    private static boolean checkBuff(CharacterModel character, BuffModel.BuffLevelModel level) {
        if (level.conditions.minFood != Integer.MIN_VALUE && character.getNeeds().food >= level.conditions.minFood) {
            return false;
        }
        if (level.conditions.maxFood != Integer.MIN_VALUE && character.getNeeds().food <= level.conditions.maxFood) {
            return false;
        }
        if (level.conditions.minSocial != Integer.MIN_VALUE && character.getNeeds().relation >= level.conditions.minSocial) {
            return false;
        }
        if (level.conditions.maxSocial != Integer.MIN_VALUE && character.getNeeds().relation <= level.conditions.maxSocial) {
            return false;
        }

        // Check light
        if (level.conditions.minLight != Integer.MIN_VALUE || level.conditions.maxLight != Integer.MIN_VALUE) {
            RoomModel room = Game.getRoomManager().getRoom(character.getX(), character.getY());
            if (level.conditions.minLight != Integer.MIN_VALUE && room != null && room.getLight() <= level.conditions.minLight) {
                return false;
            }
            if (level.conditions.maxLight != Integer.MIN_VALUE && room != null && room.getLight() >= level.conditions.maxLight) {
                return false;
            }
        }

        // Check environment
        if (level.conditions.minEnvironment != Integer.MIN_VALUE || level.conditions.maxEnvironment != Integer.MIN_VALUE) {
            int value = Game.getWorldManager().getEnvironmentValue(character.getX(), character.getY(), GameData.config.environmentDistance);
            if (level.conditions.minEnvironment != Integer.MIN_VALUE && value <= level.conditions.minEnvironment) {
                return false;
            }
            if (level.conditions.maxEnvironment != Integer.MIN_VALUE && value >= level.conditions.maxEnvironment) {
                return false;
            }
        }
        if (level.conditions.minCharacterTemperature != Integer.MIN_VALUE && character.getBodyHeat() >= level.conditions.minCharacterTemperature) {
            return false;
        }
        if (level.conditions.maxCharacterTemperature != Integer.MIN_VALUE && character.getBodyHeat() <= level.conditions.maxCharacterTemperature) {
            return false;
        }
        if (level.conditions.minDay != Integer.MIN_VALUE && Game.getInstance().getDay() <= level.conditions.minDay) {
            return false;
        }
        if (level.conditions.maxDay != Integer.MIN_VALUE && Game.getInstance().getDay() >= level.conditions.maxDay) {
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
            if (level.effects.fainting != 0 && Math.random() < level.effects.fainting) {
                Log.warning("fainting");
            }
            if (level.effects.mood != 0) {
                character.getNeeds().updateHappiness(level.effects.mood * 0.1);
            }
        }
    }
}
