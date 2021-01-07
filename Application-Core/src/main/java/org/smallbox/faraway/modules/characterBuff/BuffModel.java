package org.smallbox.faraway.modules.characterBuff;

import org.smallbox.faraway.common.ObjectModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class BuffModel extends ObjectModel {
    private final List<BuffLevel> levels = new ArrayList<>();
    private String name;
    private BuffType buffType;
    private CharacterModel character;
    private BuffLevel level;
    private LocalDateTime endTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BuffType getBuffType() {
        return buffType;
    }

    public void setBuffType(BuffType buffType) {
        this.buffType = buffType;
    }

    public List<BuffLevel> getLevels() {
        return levels;
    }

    public void addLevel(int index, String label, int mood, Predicate<CharacterModel> predicate) {
        levels.add(new BuffLevel(this, index, label, mood, predicate));
    }

    public CharacterModel getCharacter() {
        return character;
    }

    public void setCharacter(CharacterModel character) {
        this.character = character;
    }

    public void update() {
        level = levels.stream().filter(BuffLevel::check).max(Comparator.comparingInt(BuffLevel::getIndex)).orElse(null);
    }

    public BuffLevel getLevel() {
        return level;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setLevel(BuffLevel level) {
        this.level = level;
    }
}
