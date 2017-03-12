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
        public final boolean        available;
        public int                  index;
        public double               level;
        public double               learnCoef;

        public TalentEntry(TalentType type, String name, double level, boolean available) {
            this.type = type;
            this.name = name;
            this.level = level;
            this.learnCoef = 1;
            this.available = available;
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

    private HashMap<TalentType, TalentEntry>    _talentsMap;
    private Queue<TalentEntry> _talents;

    public CharacterTalentExtra() {
        _talentsMap = new HashMap<>();
        _talentsMap.put(TalentType.HEAL, new TalentEntry(TalentType.HEAL,    "Heal",     1,  true));
        _talentsMap.put(TalentType.CRAFT, new TalentEntry(TalentType.CRAFT,   "Craft",    3,  true));
        _talentsMap.put(TalentType.COOK, new TalentEntry(TalentType.COOK,    "Cook",     5,  true));
        _talentsMap.put(TalentType.GATHER, new TalentEntry(TalentType.GATHER,  "Gather",   2,  true));
        _talentsMap.put(TalentType.CUT, new TalentEntry(TalentType.CUT,     "Cut",      1,  true));
        _talentsMap.put(TalentType.MINE, new TalentEntry(TalentType.MINE,    "Mine",     0,  false));
        _talentsMap.put(TalentType.CLEAN, new TalentEntry(TalentType.CLEAN,   "Clean",    0,  false));
        _talentsMap.put(TalentType.BUILD, new TalentEntry(TalentType.BUILD,   "Build",    2,  true));
        _talentsMap.put(TalentType.STORE, new TalentEntry(TalentType.STORE,   "Store",    3,  true));

        _talents = new ConcurrentLinkedQueue<>();
        _talentsMap.values().stream().filter(talent -> talent.available).forEach(talent -> {
            talent.index = _talents.size();
            _talents.add(talent);
        });
        _talentsMap.values().stream().filter(talent -> !talent.available).forEach(talent -> {
            talent.index = _talents.size();
            _talents.add(talent);
        });
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
