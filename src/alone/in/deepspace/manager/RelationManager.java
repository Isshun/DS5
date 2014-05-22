package alone.in.deepspace.manager;

import java.util.List;

import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.Character.Gender;
import alone.in.deepspace.model.CharacterRelation;
import alone.in.deepspace.model.CharacterRelation.Relation;

public class RelationManager {

	public void date(Character c1, Character c2) {
		if (c1 == null || c2 == null || c1 == c2) {
			return;
		}
		
		// Have no mate
		if (c1.getMate() == null && c2.getMate() == null) {
			if (c1.isGay() && c2.isGay()) {
				if (c1.getGender() == c2.getGender()) {
					c1.setMate(c2);
					c2.setMate(c1);
					return;
				}
			} else {
				if (c1.getGender() != c2.getGender()) {
					c1.setMate(c2);
					c2.setMate(c1);
					addChildren(c1, c2);
					return;
				}
			}
		}

//		// Are not friends
//		if (c1.getFriends().contains(c2) == false) {
//			c1.addFriend(c2);
//			c2.addFriend(c1);
//		}
	}

	public void addChildren(Character c1, Character c2) {
		int count = ServiceManager.getCharacterManager().getList().size();
		
//		removeGrandParent(c1);
//		removeGrandParent(c2);
		
		String lastName = c1.getGender() == Gender.GENDER_MALE ? c1.getLastName() : c2.getLastName();
		Character child = new Character(count + 1, c1.getPosX(), c2.getPosY(), null, lastName);
		child.setProfession(CharacterManager.professionsChild);
		child.setParent(c1, c2);
		c1.addChildren(child);
		c2.addChildren(child);
		ServiceManager.getCharacterManager().add(child);
	}

	private void removeGrandParent(Character character) {
		List<CharacterRelation> relations = character.getRelations();
		for (CharacterRelation relation: relations) {
			if (relation.getRelation() == Relation.PARENT) {
				removeParent(relation.getSecond());
			}
		}
	}

	private void removeParent(Character character) {
		List<CharacterRelation> relations = character.getRelations();
		for (CharacterRelation relation: relations) {
			if (relation.getRelation() == Relation.PARENT) {
				ServiceManager.getCharacterManager().remove(relation.getSecond());
			}
		}
	}

}
