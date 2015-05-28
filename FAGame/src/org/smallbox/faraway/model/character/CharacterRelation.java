package org.smallbox.faraway.model.character;

import org.smallbox.faraway.Strings;

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
		switch (_relation) {
		case CHILDREN: return Strings.LB_RELATION_CHILDREN;
		case FRIEND: return Strings.LB_RELATION_FRIEND;
		case MATE: return Strings.LB_RELATION_MATE;
		case PARENT: return Strings.LB_RELATION_PARENT;
		case BROTHER: return Strings.LB_RELATION_BROTHER;
		case HALF_BROTHER: return Strings.LB_RELATION_HALF_BROTHER;
		case SISTER: return Strings.LB_RELATION_SISTER;
		case HALF_SISTER: return Strings.LB_RELATION_HALF_SISTER;
		}
		return null;
	}

	public Relation getRelation() {
		return _relation;
	}
}
