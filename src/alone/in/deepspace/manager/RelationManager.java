package alone.in.deepspace.manager;

import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.Character.Gender;

public class RelationManager {

	public void date(Character c1, Character c2) {
		if (c1 == null || c2 == null || c1 == c2) {
			return;
		}

		// Have no mate
		if (c1.getOld() > Constant.CHARACTER_DATE_MIN_OLD && c1.getMate() == null &&
				c2.getOld() > Constant.CHARACTER_DATE_MIN_OLD && c2.getMate() == null) {
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

	public Character addChildren(Character c1, Character c2) {
		int count = ServiceManager.getCharacterManager().getList().size();

		String lastName = c1.getGender() == Gender.GENDER_MALE ? c1.getLastName() : c2.getLastName();
		Character child = new Character(count + 1, c1.getPosX(), c2.getPosY(), null, lastName, 0);
		child.setProfession(CharacterManager.professionsChild);
		child.setParent(c1, c2);
		c1.addChildren(child);
		c2.addChildren(child);
		ServiceManager.getCharacterManager().add(child);

		return child;
	}

}
