package org.smallbox.faraway.modules.characterRelation;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.CharacterMoveModule;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

@GameObject
public class CharacterRelationModule extends GameModule {

    private CharacterRelationConfig config = new CharacterRelationConfig();

    @Inject
    private CharacterModule characterModule;

    @Inject
    private CharacterMoveModule characterMoveModule;

    @Override
    public void onModuleUpdate(Game game) {
        characterModule.getAll().forEach(this::updateCharacter);
    }

    private void updateCharacter(CharacterModel character) {
    }

    public double getScore(CharacterModel character) {
        if (characterMoveModule.havePeopleOnProximity(character)) {
            return config.hourlyRelationChangeWithPeopleAtProximity;
        }
        return config.hourlyRelationChange;
    }

//    public void meet(CharacterModel c1, CharacterModel c2) {
//        if (c1 == null || c2 == null || c1 == c2) {
//            return;
//        }
//
//        //        // Are not friends
//        //        if (c1.getFriends().contains(c2) == false) {
//        //            c1.addFriend(c2);
//        //            c2.addFriend(c1);
//        //        }
//
//        date(c1, c2);
//    }
//
//    private void date(CharacterModel c1, CharacterModel c2) {
//        // Not single
//        if (c1.getRelations().getMate() != null || c2.getRelations().getMate() != null) {
//            return;
//        }
//
//        // Too young
//        if (c1.getOld() < Constant.CHARACTER_DATE_MIN_OLD || c2.getOld() < Constant.CHARACTER_DATE_MIN_OLD) {
//            return;
//        }
//
//        // Gender mismatch
//        if (c1.getInfo().isGay() != c2.getInfo().isGay() || (c1.getInfo().isGay() && c1.getInfo().getGender() != c2.getInfo().getGender()) || (!c1.getInfo().isGay() && c1.getInfo().getGender() == c2.getInfo().getGender())) {
//            return;
//        }
//
//        // Same family
//        List<CharacterRelation> relations = c1.getRelations().getRelations();
//        for (CharacterRelation relation: relations) {
//            if (relation.getSecond() == c2) {
//                return;
//            }
//        }
//
//        c1.getRelations().addMateRelation(c1, c2);
//        c2.getRelations().addMateRelation(c2, c1);
//    }
//
//    public CharacterModel createChildren(CharacterModel c1, CharacterModel c2) {
//        String lastName = c1.getInfo().getGender() == CharacterPersonalsExtra.Gender.MALE ? c1.getInfo().getLastName() : c2.getInfo().getLastName();
//        CharacterModel child = new HumanModel(Utils.getUUID(), c1.getX(), c2.getY(), null, lastName, 0);
//
//        // Set child's parents
//        child.getRelations().getRelations().add(new CharacterRelation(child, c1, Relation.PARENT));
//        child.getRelations().getRelations().add(new CharacterRelation(child, c2, Relation.PARENT));
//
//        // Update brothers / sisters
//        addExistingChildrensToNewBorn(child, c1, c2);
//
//        // Set parents' child
//        c1.getRelations().getRelations().add(new CharacterRelation(c1, child, Relation.CHILDREN));
//        c2.getRelations().getRelations().add(new CharacterRelation(c2, child, Relation.CHILDREN));
//        Game.getCharacterManager().add(child);
//
//        if ("potter".equals(child.getInfo().getLastName().toLowerCase()) && child.getRelations().getRelations().size() == 2) {
//            child.getInfo().setFirstName("Harry");
//        }
//
//        return child;
//    }
//
//    private void addExistingChildrensToNewBorn(CharacterModel child, CharacterModel c1, CharacterModel c2) {
//        // Get children from first parent
//        List<CharacterModel> childrensFirstParent = new ArrayList<>();
//        for (CharacterRelation relation: c1.getRelations().getRelations()) {
//            if (relation.getRelation() == Relation.CHILDREN) {
//                childrensFirstParent.add(relation.getSecond());
//            }
//        }
//
//        // Get children from second parent
//        List<CharacterModel> childrensSecondParent = new ArrayList<CharacterModel>();
//        for (CharacterRelation relation: c1.getRelations().getRelations()) {
//            if (relation.getRelation() == Relation.CHILDREN) {
//                childrensSecondParent.add(relation.getSecond());
//            }
//        }
//
//        for (CharacterModel c: childrensFirstParent) {
//            // Add real brother
//            if (childrensSecondParent.contains(c)) {
//                child.getRelations().getRelations().add(new CharacterRelation(child, c, c.getInfo().getGender() == CharacterPersonalsExtra.Gender.MALE ? Relation.BROTHER : Relation.SISTER));
//                c.getRelations().getRelations().add(new CharacterRelation(c, child, child.getInfo().getGender() == CharacterPersonalsExtra.Gender.MALE ? Relation.BROTHER : Relation.SISTER));
//            }
//            // Add half-brother
//            else {
//                child.getRelations().getRelations().add(new CharacterRelation(child, c, c.getInfo().getGender() == CharacterPersonalsExtra.Gender.MALE ? Relation.HALF_BROTHER : Relation.HALF_SISTER));
//                c.getRelations().getRelations().add(new CharacterRelation(c, child, child.getInfo().getGender() == CharacterPersonalsExtra.Gender.MALE ? Relation.HALF_BROTHER : Relation.HALF_SISTER));
//            }
//        }
//
//        for (CharacterModel c: childrensSecondParent) {
//            // Add half-brother for second parent
//            if (!childrensFirstParent.contains(c)) {
//                child.getRelations().getRelations().add(new CharacterRelation(child, c, c.getInfo().getGender() == CharacterPersonalsExtra.Gender.MALE ? Relation.HALF_BROTHER : Relation.HALF_SISTER));
//                c.getRelations().getRelations().add(new CharacterRelation(c, child, child.getInfo().getGender() == CharacterPersonalsExtra.Gender.MALE ? Relation.HALF_BROTHER : Relation.HALF_SISTER));
//            }
//        }
//    }
//
//    @Override
//    protected void onGameUpdate(int tick) {
//        // Leave parent quarters
//        if (_old > Constant.CHARACTER_LEAVE_HOME_OLD && _quarter != null && (_quarter.getOwner() != this || _quarter.getOwner() != _relations.getMate())) {
//            _quarter.removeOccupant(this);
//            _quarter = null;
//        }
//    }
}
