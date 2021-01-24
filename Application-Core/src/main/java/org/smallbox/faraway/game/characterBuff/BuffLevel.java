package org.smallbox.faraway.game.characterBuff;

import org.smallbox.faraway.game.character.model.base.CharacterModel;

import java.util.function.Predicate;

public class BuffLevel {
    private final BuffModel buff;
    private final Predicate<CharacterModel> predicate;
    private final String label;
    private final int index;
    private final int mood;

    public BuffLevel(BuffModel buff, int index, String label, int mood, Predicate<CharacterModel> predicate) {
        this.buff = buff;
        this.label = label;
        this.index = index;
        this.mood = mood;
        this.predicate = predicate;
    }

    public String getLabel() {
        return label;
    }

    public int getIndex() {
        return index;
    }

    public int getMood() {
        return mood;
    }

    public boolean check() {
        return predicate.test(buff.getCharacter());
    }

}
