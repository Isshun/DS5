package org.smallbox.faraway.manager;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.StatsData;
import org.smallbox.faraway.model.Profession.Type;
import org.smallbox.faraway.model.character.Character;

import java.util.List;

public class StatsManager {
	public StatsData nbCharacter;
	public StatsData nbSingle;
	public StatsData nbCouple;
	public StatsData nbChild;
	public StatsData nbStudent;

	public StatsManager() {
		nbCharacter = new StatsData("Character");
		nbSingle = new StatsData("Single");
		nbCouple = new StatsData("Couple");
		nbChild = new StatsData("Child");
		nbStudent = new StatsData("Student");
	}
	
	public void update() {
		int nbCharacterValue = 0;
		int nbCoupleValue = 0;
		int nbSingleValue = 0;
		int nbChildValue = 0;
		int nbStudentValue = 0;
		List<Character> characters = Game.getCharacterManager().getList();
		for (Character character: characters) {
			
			// In relation or single
			if (character.getMate() != null) {
				nbCoupleValue++;
			} else {
				nbSingleValue++;
			}
			
			// Is child
			if (character.getProfessionId() == Type.CHILD) {
				nbChildValue++;
			}
			
			if (character.getProfessionId() == Type.STUDENT) {
				nbStudentValue++;
			}
			
			nbCharacterValue++;
		}
		
		nbCharacter.add(nbCharacterValue);
		nbChild.add(nbChildValue);
		nbSingle.add(nbSingleValue);
		nbCouple.add(nbCoupleValue);
		nbStudent.add(nbStudentValue);
	}

}
