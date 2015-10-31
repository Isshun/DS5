package org.smallbox.faraway.core.game.module.character.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

        public TalentEntry(TalentType type, String name) {
            this.type = type;
            this.name = name;
            this.level = 1;
            this.learnCoef = 1;
        }

        public double work() {
            this.level = Math.min(10, this.level + 0.5 * this.learnCoef);
            return this.level / 2;
        }
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
            new TalentEntry(TalentType.HEAL,    "Heal"),
            new TalentEntry(TalentType.CRAFT,   "Craft"),
            new TalentEntry(TalentType.COOK,    "Cook"),
            new TalentEntry(TalentType.GATHER,  "Gather"),
            new TalentEntry(TalentType.CUT,     "Cut"),
            new TalentEntry(TalentType.MINE,    "Mine"),
            new TalentEntry(TalentType.CLEAN,   "Clean"),
            new TalentEntry(TalentType.BUILD,   "Build"),
            new TalentEntry(TalentType.STORE,   "Store"),
    };

    private HashMap<TalentType, TalentEntry>    _talentsMap;
    private List<TalentEntry>                   _talents;

    public CharacterTalentExtra() {
        _talentsMap = new HashMap<>();
        _talents = new ArrayList<>();
        for (TalentEntry talent: TALENTS) {
            _talents.add(talent);
            _talentsMap.put(talent.type, talent);
            talent.index = _talents.indexOf(talent);
        }
    }

    public List<TalentEntry>            getAll() { return _talents; }
    public TalentEntry                  get(TalentType type) { return _talentsMap.get(type); }

    //    public void moveTalent(TalentEntry talent, int offset) {
//        Optional<TalentEntry> optionalEntry = _talents.stream().filter(entry -> entry == talent).findFirst();
//        if (optionalEntry.isPresent()) {
//            int position = _talents.indexOf(optionalEntry.get()) + offset;
//            _talents.remove(optionalEntry.get());
//            _talents.add(Math.min(Math.max(position, 0), _talents.size()), optionalEntry.get());
//        }
//    }

}
