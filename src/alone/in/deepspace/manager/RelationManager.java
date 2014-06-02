package alone.in.deepspace.manager;

import java.util.ArrayList;
import java.util.List;

import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.character.Character.Gender;
import alone.in.deepspace.model.character.CharacterRelation;
import alone.in.deepspace.model.character.CharacterRelation.Relation;
import alone.in.deepspace.util.Constant;

public class RelationManager {

	public void meet(Character c1, Character c2) {
		if (c1 == null || c2 == null || c1 == c2) {
			return;
		}
		
		//		// Are not friends
		//		if (c1.getFriends().contains(c2) == false) {
		//			c1.addFriend(c2);
		//			c2.addFriend(c1);
		//		}

		date(c1, c2);
	}

	private void date(Character c1, Character c2) {
		// Not single
		if (c1.getMate() != null || c2.getMate() != null) {
			return;
		}

		// Too young
		if (c1.getOld() < Constant.CHARACTER_DATE_MIN_OLD || c2.getOld() < Constant.CHARACTER_DATE_MIN_OLD) {
			return;
		}
		
		// Gender mismatch
		if (c1.isGay() != c2.isGay() || (c1.isGay() && c1.getGender() != c2.getGender()) || (c1.isGay() == false && c1.getGender() == c2.getGender())) {
			return;
		}

		// Same family
		List<CharacterRelation> relations = c1.getRelations();
		for (CharacterRelation relation: relations) {
			if (relation.getSecond() == c2) {
				return;
			}
		}

		c1.setMate(c2);
		c2.setMate(c1);
	}

	public Character createChildren(Character c1, Character c2) {
		int count = ServiceManager.getCharacterManager().getList().size();

		String lastName = c1.getGender() == Gender.MALE ? c1.getLastName() : c2.getLastName();
		Character child = new Character(count + 1, c1.getX(), c2.getY(), null, lastName, 0);
		child.setProfession(CharacterManager.professionsChild);

		// Set child's parents
		child.getRelations().add(new CharacterRelation(child, c1, Relation.PARENT));
		child.getRelations().add(new CharacterRelation(child, c2, Relation.PARENT));

		// Update brothers / sisters
		addExistingChildrensToNewBorn(child, c1, c2);

		// Set parents' child
		c1.getRelations().add(new CharacterRelation(c1, child, Relation.CHILDREN));
		c2.getRelations().add(new CharacterRelation(c2, child, Relation.CHILDREN));
		ServiceManager.getCharacterManager().add(child);

		return child;
	}

	private void addExistingChildrensToNewBorn(Character child, Character c1, Character c2) {
		// Get children from first parent
		List<Character> childrensFirstParent = new ArrayList<Character>();
		for (CharacterRelation relation: c1.getRelations()) {
			if (relation.getRelation() == Relation.CHILDREN) {
				childrensFirstParent.add(relation.getSecond());
			}
		}

		// Get children from second parent
		List<Character> childrensSecondParent = new ArrayList<Character>();
		for (CharacterRelation relation: c1.getRelations()) {
			if (relation.getRelation() == Relation.CHILDREN) {
				childrensSecondParent.add(relation.getSecond());
			}
		}

		for (Character c: childrensFirstParent) {
			// Add real brother
			if (childrensSecondParent.contains(c)) {
				child.getRelations().add(new CharacterRelation(child, c, c.getGender() == Gender.MALE ? Relation.BROTHER : Relation.SISTER));
				c.getRelations().add(new CharacterRelation(c, child, child.getGender() == Gender.MALE ? Relation.BROTHER : Relation.SISTER));
			}
			// Add half-brother
			else {
				child.getRelations().add(new CharacterRelation(child, c, c.getGender() == Gender.MALE ? Relation.HALF_BROTHER : Relation.HALF_SISTER));
				c.getRelations().add(new CharacterRelation(c, child, child.getGender() == Gender.MALE ? Relation.HALF_BROTHER : Relation.HALF_SISTER));
			}
		}

		for (Character c: childrensSecondParent) {
			// Add half-brother for second parent
			if (childrensFirstParent.contains(c) == false) {
				child.getRelations().add(new CharacterRelation(child, c, c.getGender() == Gender.MALE ? Relation.HALF_BROTHER : Relation.HALF_SISTER));
				c.getRelations().add(new CharacterRelation(c, child, child.getGender() == Gender.MALE ? Relation.HALF_BROTHER : Relation.HALF_SISTER));
			}
		}
	}

}
