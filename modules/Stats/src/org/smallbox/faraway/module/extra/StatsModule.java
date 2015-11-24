package org.smallbox.faraway.module.extra;

import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;

import java.util.ArrayList;
import java.util.List;

public class StatsModule extends ModuleBase {
    public static class StatsData {
        public List<Integer>     values;
        public String             label;

        public StatsData(String label) {
            this.label = label;
            this.values = new ArrayList<>();
        }

        public void add(int value) {
            values.add(value);
        }
    }

    private static final int     UPDATE_INTERVAL = 10;

    public StatsData             nbCharacter;
    public StatsData             nbSingle;
    public StatsData             nbCouple;
    public StatsData             nbChild;
    public StatsData             nbStudent;

    @Override
    protected void onLoaded(Game game) {
        nbCharacter = new StatsData("Character");
        nbSingle = new StatsData("Single");
        nbCouple = new StatsData("Couple");
        nbChild = new StatsData("Child");
        nbStudent = new StatsData("Student");
    }

    @Override
    protected void onUpdate(int tick) {
        if (tick % UPDATE_INTERVAL == 0) {
            int nbCharacterValue = 0;
            int nbCoupleValue = 0;
            int nbSingleValue = 0;
            int nbChildValue = 0;
            int nbStudentValue = 0;
            for (CharacterModel character: ModuleHelper.getCharacterModule().getCharacters()) {

//                // In relation or single
//                if (model.getRelations().getMate() != null) {
//                    nbCoupleValue++;
//                } else {
//                    nbSingleValue++;
//                }

//                // Is child
//                if (characters.getProfessionId() == Type.CHILD) {
//                    nbChildValue++;
//                }
//
//                if (characters.getProfessionId() == Type.STUDENT) {
//                    nbStudentValue++;
//                }

                nbCharacterValue++;
            }

            nbCharacter.add(nbCharacterValue);
            nbChild.add(nbChildValue);
            nbSingle.add(nbSingleValue);
            nbCouple.add(nbCoupleValue);
            nbStudent.add(nbStudentValue);
        }
    }

}
