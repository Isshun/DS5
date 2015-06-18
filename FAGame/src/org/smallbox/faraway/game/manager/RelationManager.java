package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.util.Utils;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.game.model.character.HumanModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel.Gender;
import org.smallbox.faraway.game.model.character.base.CharacterRelation;
import org.smallbox.faraway.game.model.character.base.CharacterRelation.Relation;

import java.util.ArrayList;
import java.util.List;

public class RelationManager extends BaseManager {

	public void meet(CharacterModel c1, CharacterModel c2) {
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

	private void date(CharacterModel c1, CharacterModel c2) {
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

		c1.addMateRelation(c2);
		c2.addMateRelation(c1);
	}

	public CharacterModel createChildren(CharacterModel c1, CharacterModel c2) {
		String lastName = c1.getGender() == Gender.MALE ? c1.getLastName() : c2.getLastName();
		CharacterModel child = new HumanModel(Utils.getUUID(), c1.getX(), c2.getY(), null, lastName, 0);
		child.setProfession(CharacterManager.professionsChild);

		// Set child's parents
		child.getRelations().add(new CharacterRelation(child, c1, Relation.PARENT));
		child.getRelations().add(new CharacterRelation(child, c2, Relation.PARENT));

		// Update brothers / sisters
		addExistingChildrensToNewBorn(child, c1, c2);

		// Set parents' child
		c1.getRelations().add(new CharacterRelation(c1, child, Relation.CHILDREN));
		c2.getRelations().add(new CharacterRelation(c2, child, Relation.CHILDREN));
		Game.getCharacterManager().add(child);

		if ("potter".equals(child.getLastName().toLowerCase()) && child.getRelations().size() == 2) {
			child.setFirstName("Harry");
		}
		
		return child;
	}

	private void addExistingChildrensToNewBorn(CharacterModel child, CharacterModel c1, CharacterModel c2) {
		// Get children from first parent
		List<CharacterModel> childrensFirstParent = new ArrayList<CharacterModel>();
		for (CharacterRelation relation: c1.getRelations()) {
			if (relation.getRelation() == Relation.CHILDREN) {
				childrensFirstParent.add(relation.getSecond());
			}
		}

		// Get children from second parent
		List<CharacterModel> childrensSecondParent = new ArrayList<CharacterModel>();
		for (CharacterRelation relation: c1.getRelations()) {
			if (relation.getRelation() == Relation.CHILDREN) {
				childrensSecondParent.add(relation.getSecond());
			}
		}

		for (CharacterModel c: childrensFirstParent) {
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

		for (CharacterModel c: childrensSecondParent) {
			// Add half-brother for second parent
			if (childrensFirstParent.contains(c) == false) {
				child.getRelations().add(new CharacterRelation(child, c, c.getGender() == Gender.MALE ? Relation.HALF_BROTHER : Relation.HALF_SISTER));
				c.getRelations().add(new CharacterRelation(c, child, child.getGender() == Gender.MALE ? Relation.HALF_BROTHER : Relation.HALF_SISTER));
			}
		}
	}

	@Override
	protected void onUpdate(int tick) {
	}
}
