package org.smallbox.faraway.game.character.model.base;

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

    private final Relation     _relation;
    private final CharacterModel _c1;
    private final CharacterModel _c2;

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

//        switch (_relation) {
//        case CHILDREN: string = Strings.LB_RELATION_CHILDREN; break;
//        case FRIEND: string = Strings.LB_RELATION_FRIEND; break;
//        case MATE: string = Strings.LB_RELATION_MATE; break;
//        case PARENT: string = Strings.LB_RELATION_PARENT; break;
//        case BROTHER: string = Strings.LB_RELATION_BROTHER; break;
//        case HALF_BROTHER: string = Strings.LB_RELATION_HALF_BROTHER; break;
//        case SISTER: string = Strings.LB_RELATION_SISTER; break;
//        case HALF_SISTER: string = Strings.LB_RELATION_HALF_SISTER; break;
//        }

        return string;
    }

    public Relation getRelation() {
        return _relation;
    }

    //    public void                     addFriend(CharacterModel friend) { _relations.add(new CharacterRelation(this, friend, CharacterRelation.Relation.FRIEND)); }
}
