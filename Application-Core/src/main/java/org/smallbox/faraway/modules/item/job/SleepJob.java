//package org.smallbox.faraway.modules.item.job;
//
//import org.smallbox.faraway.core.Application;
//import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
//import org.smallbox.faraway.modules.job.JobModel;
//import org.smallbox.faraway.core.module.world.model.ParcelModel;
//import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
//import org.smallbox.faraway.modules.character.model.base.CharacterModel;
//import org.smallbox.faraway.modules.item.ItemSlot;
//import org.smallbox.faraway.modules.item.UsableItem;
//
///**
// * Created by Alex
// */
//public class SleepJob extends JobModel {
//    private UsableItem _sleepItem;
//    private long            _startTime;
//    private ItemSlot        _slot;
//
//    public SleepJob(ParcelModel parcel) {
//        _label = "Sleep on ground";
//        _message = "Sleep on ground";
//        _targetParcel = parcel;
//    }
//
//    public SleepJob(ParcelModel parcel, UsableItem item) {
//        _label = _message = Application.data.getString("Sleep in") + " " + Application.data.getString(item.getInfo().label);
//        _sleepItem = item;
//        _jobParcel = _targetParcel = parcel;
//    }
//
//    public UsableItem getItem() { return _sleepItem; }
//
//    @Override
//    protected JobCheckReturn onCheck(CharacterModel character) {
//        if (_sleepItem != null && !Application.pathManager.hasPath(character.getParcel(), _sleepItem.getParcel())) {
//            return JobCheckReturn.ABORT;
//        }
//        return JobCheckReturn.OK;
//    }
//
//    @Override
//    protected void onStart(CharacterModel character) {
//        _startTime = Application.gameManager.getGame().getTick();
//        _endTime = computeWakeTime(character);
//
//        if (_sleepItem != null) {
//            _slot = _sleepItem.takeSlot(this);
//            _sleepItem.addJob(this);
//            _targetParcel = _slot != null ? _slot.getParcel() : _sleepItem.getParcel();
//        }
//
//        character.moveTo(_targetParcel, null);
//    }
//
//    private long computeWakeTime(CharacterModel character) {
//        double change = 0;
//
//        // Add character change property
//        change += character.getType().needs.energy.change.sleep;
//
//        // Add item change property
//        if (_sleepItem != null) {
//            for (ItemInfo.ItemInfoAction action: _sleepItem.getInfo().actions) {
//                change += action.effects.energy;
//            }
//        }
//
//        assert change > 0;
//
////        // Get next alarm
////        int hoursByDay = Application.gameManager.getGame().getInfo().planet.dayDuration;
////        int bedHour = Application.gameManager.getGame().getHour();
////        int wakeTime = -1;
////        for (int i = 0; i < hoursByDay; i++) {
////            if (character.getTimetable().get((bedHour + i) % hoursByDay) != 1) {
////                wakeTime = i;
////            }
////        }
//
//        return Application.gameManager.getGame().getTick() + (int)((100 - character.getNeeds().get("energy")) / change);
//    }
//
//    @Override
//    public JobReturn onAction(CharacterModel character) {
//        if (Application.gameManager.getGame().getTick() < _endTime) {
//            character.setSleeping(true);
//            character.getNeeds().isSleeping = true;
//            _progress = (double)(Application.gameManager.getGame().getTick() - _startTime) / (_endTime - _startTime);
//            return JobReturn.CONTINUE;
//        }
//
//        character.setSleeping(false);
//        return JobReturn.COMPLETE;
//    }
//
//    @Override
//    protected void onClose() {
//        if (_sleepItem != null && _slot != null) {
//            _sleepItem.releaseSlot(_slot);
//            _sleepItem.removeJob(this);
//        }
//    }
//
//    @Override
//    protected void onQuit(CharacterModel character) {
//        character.getNeeds().isSleeping = false;
//    }
//
//    @Override
//    public CharacterSkillExtra.SkillType getSkillNeeded() {
//        return null;
//    }
//
////    public void setWakeTime(int time) {
////        _wakeTime = _startTime + (Application.config.game.tickPerHour * time);
////    }
//}
