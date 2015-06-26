package org.smallbox.faraway.game.model.character;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.RelationManager;
import org.smallbox.faraway.game.model.character.base.CharacterInfoModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.character.base.CharacterRelation;
import org.smallbox.faraway.util.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 24/06/2015.
 */
public class CharacterRelationModel {
    protected List<CharacterRelation> _relations;
    protected CharacterModel _mate;
    protected double _nextChildAtOld;
    protected int _nbChild;

    public CharacterRelationModel() {
        _relations = new ArrayList<>();
        _nextChildAtOld = -1;
    }

    public CharacterModel getMate() {
        return _mate;
    }

    public List<CharacterRelation> getRelations() {
        return _relations;
    }

    public double getNextChildAtOld() {
        return _nextChildAtOld;
    }

    public int getNbChild() {
        return _nbChild;
    }

    public void setNextChildAtOld(double nextChildAtOld) {
        _nextChildAtOld = nextChildAtOld;
    }

    public void setNbChild(int nbChild) {
        _nbChild = nbChild;
    }

    public void addMateRelation(CharacterModel character, CharacterModel mate) {
        if (_mate == mate) {
            return;
        }

        // Update lastName
        if (character.getInfo().getGender() == CharacterInfoModel.Gender.FEMALE && mate.getInfo().getGender() == CharacterInfoModel.Gender.MALE) {
            character.getInfo().setLastName(mate.getInfo().getLastName());
        }

//        // Break up
//        if (_mate != null) {
//            // Remove quarter
//            if (_quarter != null && _quarter.getOwner() == _mate) {
//                _quarter.removeOccupant(this);
//                _quarter = null;
//            }
//
//            // Remove relation
//            CharacterRelation r = null;
//            for (CharacterRelation relation: _relations) {
//                if (relation.getRelation() == CharacterRelation.Relation.MATE) {
//                    r = relation;
//                }
//            }
//            _relations.remove(r);
//
//            // Restore birtName
//            _lastName = _birthName;
//
//            // Cancel next child
//            _nextChildAtOld = -1;
//
//            _mate.addMateRelation(null);
//            _mate = null;
//        }
//
//        // New mate
//        if (mate != null) {
//            _mate = mate;
//
//            // Add relation
//            _relations.add(new CharacterRelation(this, mate, CharacterRelation.Relation.MATE));
//
//            // Schedule next child
//            if (_gender == Gender.FEMALE) {
//                _nextChildAtOld = _old + Constant.CHARACTER_DELAY_BEFORE_FIRST_CHILD;
//            }
//
//            // Add quarter
//            if (mate.getQuarter() != null && mate.getQuarter().getOwner() == mate) {
//                if (_quarter != null) {
//                    _quarter.removeOccupant(this);
//                }
//
//                mate.getQuarter().addOccupant(this);
//                _quarter = mate.getQuarter();
//            }
//        }
//    }
    }

    public void longUpdate(CharacterModel character) {
        if (_nbChild < Constant.CHARACTER_MAX_CHILD && _mate != null && character.getOld() > Constant.CHARACTER_CHILD_MIN_OLD && character.getOld() < Constant.CHARACTER_CHILD_MAX_OLD && character.getOld() > _nextChildAtOld && _nextChildAtOld > 0) {
            _nextChildAtOld = character.getOld() + Constant.CHARACTER_DELAY_BETWEEN_CHILDS;
            if (((RelationManager)Game.getInstance().getManager(RelationManager.class)).createChildren(character, _mate) != null) {
                _nbChild++;
            }
        }
    }
}
