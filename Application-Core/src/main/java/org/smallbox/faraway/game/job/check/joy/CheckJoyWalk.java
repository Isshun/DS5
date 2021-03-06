//package org.smallbox.faraway.modules.job.check.joy;
//
//import org.smallbox.faraway.util.GameException;
//import org.smallbox.faraway.modules.area.AreaModel;
//import org.smallbox.faraway.modules.job.check.old.CharacterCheck;
//import org.smallbox.faraway.modules.job.model.MoveJob;
//import org.smallbox.faraway.modules.job.JobModel;
//import org.smallbox.faraway.core.world.model.ParcelModel;
//import org.smallbox.faraway.modules.character.model.base.CharacterModel;
//
///**
// * Created by Alex
// */
//public class CheckJoyWalk extends CharacterCheck {
//    private ParcelModel             _parcel;
//    private AreaModel               _area;
//
//    @Override
//    public JobModel onCreateJob(CharacterModel character) {
//        if (_parcel == null) {
//            throw new GameException(CheckJoyWalk.class, "[CheckEntertainmentWalk] Create job with null parcel");
//        }
//
//        MoveJob job = MoveJob.create(character, _parcel);
//        job.start(character);
//        job.setLabel("Move for a walk");
//        job.setOnActionListener(() -> job.getCharacter().getNeeds().addValue("entertainment", 1));
//        job.setSpeedModifier(0.5);
//        job.setLimit(150);
//        job.setEntertainment(true);
//
//        return job;
//    }
//
//    @Override
//    public boolean isJobLaunchable(CharacterModel character) {
//        return false;
////        throw new NotImplementedException("");
//
////        // LF org.smallbox.faraway.core.module.room.model
////        _area = null;
////        int bestDistance = Integer.MAX_VALUE;
////        for (AreaModel area: ((AreaModule) Application.moduleManager.getModule(AreaModule.class)).getAreasByType()) {
////            if (area.isHome()) {
////                int distance = Math.abs(character.getParcel().x - area.getX()) + Math.abs(character.getParcel().y - area.getY());
////                if (bestDistance > distance) {
////                    bestDistance = distance;
////                    _area = area;
////                }
////            }
////        }
////
////        // LF parcel
////        if (_area != null) {
////            _parcel = null;
////            int size = _area.getParcelsByType().size();
////            int r = (int)(Math.random() * size);
////            int i = 0;
////            for (ParcelModel parcel: _area.getParcelsByType()) {
////                if (parcel.isWalkable() && parcel.getItem() == null && (i++ < r || _parcel == null)) {
////                    _parcel = parcel;
////                }
////            }
////            if (_parcel != null) {
////                return true;
////            }
////        }
////        return false;
//    }
//
//    @Override
//    public boolean isJobNeeded(CharacterModel character) {
//        return character.getNeeds().get("entertainment") < character.getType().needs.joy.warning;
//    }
//}
