package org.smallbox.faraway.modules.character.model;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Alex on 31/10/2015.
 */
public class CharacterSkillExtra {
    public static class SkillEntry {
        public final String         name;
        public final SkillType     type;
        public final boolean        available;
        public int                  index;
        public double               level;
        public double               learnCoef;

        public SkillEntry(SkillType type, String name, double level, boolean available) {
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

    public enum SkillType {
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

    private HashMap<SkillType, SkillEntry>    _skillsMap;
    private Queue<SkillEntry> _skills;

    public CharacterSkillExtra() {
        _skillsMap = new HashMap<>();
        _skillsMap.put(SkillType.HEAL, new SkillEntry(SkillType.HEAL,    "Heal",     1,  true));
        _skillsMap.put(SkillType.CRAFT, new SkillEntry(SkillType.CRAFT,   "Craft",    3,  true));
        _skillsMap.put(SkillType.COOK, new SkillEntry(SkillType.COOK,    "Cook",     5,  true));
        _skillsMap.put(SkillType.GATHER, new SkillEntry(SkillType.GATHER,  "Gather",   2,  true));
        _skillsMap.put(SkillType.CUT, new SkillEntry(SkillType.CUT,     "Cut",      1,  true));
        _skillsMap.put(SkillType.MINE, new SkillEntry(SkillType.MINE,    "Mine",     0,  false));
        _skillsMap.put(SkillType.CLEAN, new SkillEntry(SkillType.CLEAN,   "Clean",    0,  false));
        _skillsMap.put(SkillType.BUILD, new SkillEntry(SkillType.BUILD,   "Build",    2,  true));
        _skillsMap.put(SkillType.STORE, new SkillEntry(SkillType.STORE,   "Store",    3,  true));

        _skills = new ConcurrentLinkedQueue<>();
        _skillsMap.values().stream().filter(skill -> skill.available).forEach(skill -> {
            skill.index = _skills.size();
            _skills.add(skill);
        });
        _skillsMap.values().stream().filter(skill -> !skill.available).forEach(skill -> {
            skill.index = _skills.size();
            _skills.add(skill);
        });
    }

    public Collection<SkillEntry>      getAll() { return _skills; }
    public SkillEntry                  get(SkillType type) { return _skillsMap.get(type); }

    public void moveSkill(SkillEntry skillToMove, int index) {
        if (skillToMove.index != index) {
            List<SkillEntry> tmpList = new ArrayList<>(_skills);
            tmpList.remove(skillToMove);
            tmpList.add(index, skillToMove);
            tmpList.forEach(skill -> skill.index = tmpList.indexOf(skill));

            _skills.clear();
            _skills.addAll(tmpList);
        }
    }

}
