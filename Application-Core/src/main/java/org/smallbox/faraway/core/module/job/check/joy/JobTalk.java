//package org.smallbox.faraway.core.module.job.check.joy;
//
//import org.smallbox.faraway.core.module.character.model.CharacterSkillExtra;
//import org.smallbox.faraway.modules.character.model.PathModel;
//import org.smallbox.faraway.modules.character.model.base.CharacterModel;
//import org.smallbox.faraway.modules.job.JobModel;
//import org.smallbox.faraway.core.module.path.PathManager;
//import org.smallbox.faraway.core.module.world.model.ParcelModel;
//import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
//
///**
// * Created by Alex
// */
//public class JobTalk extends JobModel {
//    private CharacterModel  _friend;
//
//    public static JobTalk createModules(CharacterModel character, CharacterModel friend) {
//
//        if (friend.getJob() != null && friend.getJob().isEntertainment()) {
//            ModuleHelper.getJobModule().closeJob(friend.getJob());
//        }
//
//        PathModel path = Application.pathManager.getPath(character.getParcel(), friend.getParcel(), true, false);
//        if (path == null) {
//            return null;
//        }
//
//        JobTalk job = new JobTalk();
//        job._friend = friend;
//        job._character = character;
//        job._cost = 32;
//        job._message = "Talk job";
//
//        friend.setJob(job);
//        character.setJob(job);
//
//        character.moveTo(path.getNodes().get(path.getLength() / 2), null);
//        friend.moveTo(path.getNodes().get(Math.min(path.getLength() / 2 + 1, path.getLength() - 1)), null);
//
//        return job;
//    }
//
//    @Override
//    public boolean hasCharacter(CharacterModel character) {
//        return _character != null && _friend != null && (_character == character || _friend == character);
//    }
//
//    @Override
//    public ParcelModel getTargetParcel() {
//        return null;
//    }
//
//    @Override
//    protected void onStart(CharacterModel character) {
//    }
//
//    @Override
//    public CharacterSkillExtra.SkillType getSkillNeeded() {
//        return null;
//    }
//
//    @Override
//    public void onQuit(CharacterModel character) {
//
//    }
//
//    @Override
//    public JobCheckReturn onCheck(CharacterModel character) {
//        return JobCheckReturn.ABORT;
//    }
//
//    @Override
//    protected void onClose() {
//    }
//
//    @Override
//    public JobReturn onAction(CharacterModel character) {
//        if (_progress++ < _cost) {
//            return JobReturn.CONTINUE;
//        }
//        return JobReturn.COMPLETE;
//    }
//}
