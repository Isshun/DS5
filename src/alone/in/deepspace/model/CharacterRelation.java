package alone.in.deepspace.model;

import alone.in.deepspace.Strings;
import alone.in.deepspace.model.CharacterRelation.Relation;

public class CharacterRelation {
	public enum Relation {
		MATE,
		FRIEND,
		CHILDREN,
		PARENT
	}

	private Relation 	_relation;
	private Character 	_c1;
	private Character 	_c2;

	public CharacterRelation(Character c1, Character c2, Relation relation) {
		_relation = relation;
		_c1 = c1;
		_c2 = c2;
	}

	public Character getFirst() {
		return _c1;
	}

	public Character getSecond() {
		return _c2;
	}

	public String getRelationLabel() {
		switch (_relation) {
		case CHILDREN: return Strings.LB_RELATION_CHILDREN;
		case FRIEND: return Strings.LB_RELATION_FRIEND;
		case MATE: return Strings.LB_RELATION_MATE;
		case PARENT: return Strings.LB_RELATION_PARENT;
		}
		return null;
	}

	public Relation getRelation() {
		return _relation;
	}
}
