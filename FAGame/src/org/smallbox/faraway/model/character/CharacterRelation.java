package org.smallbox.faraway.model.character;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.Strings;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.model.GameData;

public class CharacterRelation {
	public enum Relation {
		MATE,
		FRIEND,
		CHILDREN,
		PARENT,
		BROTHER,
		SISTER,
		HALF_BROTHER,
		HALF_SISTER
	}

	private Relation 	_relation;
	private CharacterModel _c1;
	private CharacterModel _c2;

	public CharacterRelation(CharacterModel c1, CharacterModel c2, Relation relation) {
		_relation = relation;
		_c1 = c1;
		_c2 = c2;
	}

	public CharacterModel getFirst() {
		return _c1;
	}

	public CharacterModel getSecond() {
		return _c2;
	}

	public String getRelationLabel() {
		String string = null;

		switch (_relation) {
		case CHILDREN: string = Strings.LB_RELATION_CHILDREN; break;
		case FRIEND: string = Strings.LB_RELATION_FRIEND; break;
		case MATE: string = Strings.LB_RELATION_MATE; break;
		case PARENT: string = Strings.LB_RELATION_PARENT; break;
		case BROTHER: string = Strings.LB_RELATION_BROTHER; break;
		case HALF_BROTHER: string = Strings.LB_RELATION_HALF_BROTHER; break;
		case SISTER: string = Strings.LB_RELATION_SISTER; break;
		case HALF_SISTER: string = Strings.LB_RELATION_HALF_SISTER; break;
		}

		return GameData.getData().getString(string);
	}

	public Relation getRelation() {
		return _relation;
	}
}