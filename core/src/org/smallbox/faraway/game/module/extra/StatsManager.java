package org.smallbox.faraway.game.module.extra;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.module.GameModule;
import org.smallbox.faraway.game.model.character.base.CharacterModel;

import java.util.ArrayList;
import java.util.List;

public class StatsManager extends GameModule {
	public static class StatsData {

		public List<Integer> 	values;
		public String 			label;

		public StatsData(String label) {
			this.label = label;
			this.values = new ArrayList<>();
		}

		public void add(int value) {
			values.add(value);
		}

	}

	private static final int 	UPDATE_INTERVAL = 10;

	public StatsData 			nbCharacter;
	public StatsData 			nbSingle;
	public StatsData 			nbCouple;
	public StatsData 			nbChild;
	public StatsData 			nbStudent;

	public StatsManager() {
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
			for (CharacterModel character : Game.getCharacterManager().getCharacters()) {

				// In relation or single
				if (character.getRelations().getMate() != null) {
					nbCoupleValue++;
				} else {
					nbSingleValue++;
				}

//				// Is child
//				if (characters.getProfessionId() == Type.CHILD) {
//					nbChildValue++;
//				}
//
//				if (characters.getProfessionId() == Type.STUDENT) {
//					nbStudentValue++;
//				}

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
