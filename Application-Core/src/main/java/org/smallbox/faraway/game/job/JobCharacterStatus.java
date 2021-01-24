package org.smallbox.faraway.game.job;

import org.smallbox.faraway.game.character.model.base.CharacterModel;

public class JobCharacterStatus implements Comparable<JobCharacterStatus> {
    public CharacterModel character;
    public String label;
    public int approxDistance;
    public int skillLevel;
    public boolean available;
    public int index;

    @Override
    public int compareTo(JobCharacterStatus other) {
        return 0;
    }
}
