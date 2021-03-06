//package org.smallbox.faraway.modules.building;
//
//import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
//import org.smallbox.faraway.core.world.model.BuildableMapObject;
//import org.smallbox.faraway.game.consumable.ConsumableItem;
//import org.smallbox.faraway.core.world.model.MapObjectModel;
//import org.smallbox.faraway.core.world.model.ParcelModel;
//import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
//import org.smallbox.faraway.modules.character.model.base.CharacterModel;
//import org.smallbox.faraway.modules.job.JobModel;
//import org.smallbox.faraway.modules.job.JobModule;
//import org.smallbox.faraway.modules.job.JobTaskReturn;
//
//import java.util.Map;
//
///**
// * Job déplacant les consomables vers les zones de stockage
// */
//public class BasicRepairJob extends JobModel {
//
//    protected Map<ConsumableItem, Integer> _targetConsumables;
//    private MapObjectModel _mapObject;
//
//    public Map<ConsumableItem, Integer> getConsumables() { return _targetConsumables; }
//
//    public static void repairStructure(JobModule jobModule, BuildableMapObject mapObject) {
//
//        jobModule.createJob(BasicRepairJob.class, null, mapObject.getParcel(), job -> {
//            job.setMainLabel("Repair " + mapObject.getInfo().label);
//
//            job._mapObject = mapObject;
//            job._targetParcel = mapObject.getParcel();
//
//            job.addMoveTask("Move to object", mapObject::getParcel);
//            job.addTask("Repair", (character, hourInterval) -> {
//                mapObject.addHealth(1 / jobModule.getGameConfig().repairTime * hourInterval);
//                job.setProgress(mapObject.getHealth(), mapObject.getMaxHealth());
//                return mapObject.getHealth() >= mapObject.getMaxHealth() ? JobTaskReturn.TASK_COMPLETE : JobTaskReturn.TASK_CONTINUE;
//            });
//
////            job.addTaskAsync(character -> {
////                mapObject.setHealth(mapObject.getMaxHealth());
////                job.setProgress(mapObject.getHealth(), mapObject.getMaxHealth());
////                return JobTaskReturn.TASK_COMPLETE;
////            });
//
//            return true;
//        });
//
//    }
//
//    public BasicRepairJob(ItemInfo.ItemInfoAction itemInfoAction, ParcelModel parcelModel) {
//        super(itemInfoAction, parcelModel);
//    }
//
//    @Override
//    protected JobCheckReturn onCheck(CharacterModel character) {
//        return JobCheckReturn.OK;
//    }
//
//    @Override
//    public boolean checkCharacterAccepted(CharacterModel character) {
//
//        // Character have no skill
//        if (!character.hasExtra(CharacterSkillExtra.class) || !character.getExtra(CharacterSkillExtra.class).hasSkill(CharacterSkillExtra.SkillType.BUILD)) {
//            return false;
//        }
//
//        // Character is qualified for job
//        return true;
//
//    }
//
//    public MapObjectModel getObject() {
//        return _mapObject;
//    }
//
//    @Override
//    public CharacterSkillExtra.SkillType getSkillType() {
//        return CharacterSkillExtra.SkillType.BUILD;
//    }
//
//}
