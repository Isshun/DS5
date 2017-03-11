package org.smallbox.faraway.modules.character.model;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Alex on 31/10/2015.
 */
public class CharacterTalentExtra {
    public static class TalentEntry {
        public final String         name;
        public final TalentType     type;
        public int                  index;
        public double               level;
        public double               learnCoef;

        public TalentEntry(TalentType type, String name, double level) {
            this.type = type;
            this.name = name;
            this.level = level;
            this.learnCoef = 1;
        }

        public double work() {
            this.level = Math.min(10, this.level + 0.5 * this.learnCoef);
            return this.level / 2;
        }

        @Override
        public String toString() { return name; }
    }

    public enum TalentType {
        HEAL,
        CRAFT,
        COOK,
        GATHER,
        MINE,
        STORE,
        BUILD,
        CUT,
        CLEAN
    }

    private static final TalentEntry[] TALENTS = new TalentEntry[] {
            new TalentEntry(TalentType.HEAL,    "Heal",     1),
            new TalentEntry(TalentType.CRAFT,   "Craft",    3),
            new TalentEntry(TalentType.COOK,    "Cook",     5),
            new TalentEntry(TalentType.GATHER,  "Gather",   2),
            new TalentEntry(TalentType.CUT,     "Cut",      1),
            new TalentEntry(TalentType.MINE,    "Mine",     1),
            new TalentEntry(TalentType.CLEAN,   "Clean",    1),
            new TalentEntry(TalentType.BUILD,   "Build",    2),
            new TalentEntry(TalentType.STORE,   "Store",    3),
    };

    private HashMap<TalentType, TalentEntry>    _talentsMap;
    private Queue<TalentEntry> _talents;

    public CharacterTalentExtra() {
        _talentsMap = new HashMap<>();
        _talents = new ConcurrentLinkedQueue<>();
        for (TalentEntry talent: TALENTS) {
            talent.index = _talents.size();
            _talents.add(talent);
            _talentsMap.put(talent.type, talent);
        }
    }

    public Collection<TalentEntry>      getAll() { return _talents; }
    public TalentEntry                  get(TalentType type) { return _talentsMap.get(type); }

    public void moveTalent(TalentEntry talentToMove, int index) {
        if (talentToMove.index != index) {
            List<TalentEntry> tmpList = new ArrayList<>(_talents);
            tmpList.remove(talentToMove);
            tmpList.add(index, talentToMove);
            tmpList.forEach(talent -> talent.index = tmpList.indexOf(talent));

            _talents.clear();
            _talents.addAll(tmpList);
        }
    }

}
