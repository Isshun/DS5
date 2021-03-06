//package org.smallbox.faraway.modules.character.job;
//
//import org.smallbox.faraway.modules.job.check.old.CharacterCheck;
//import org.smallbox.faraway.modules.job.JobModel;
//import org.smallbox.faraway.modules.character.model.base.CharacterModel;
//import org.smallbox.faraway.modules.item.ItemModule;
//import org.smallbox.faraway.modules.item.UsableItem;
//import org.smallbox.faraway.modules.item.job.UseJob;
//
///**
// * Created by Alex
// */
//public class CheckJoySleep extends CharacterCheck {
//    private final ItemModule _items;
//
//    public CheckJoySleep(ItemModule items) {
//        _items = items;
//    }
//
//    @Override
//    public JobModel onCreateJob(CharacterModel character) {
//        if (character != null) {
//            UsableItem item = _items.getItems().stream().filter(UsableItem::isBed).findAny().orElse(null);
//            if (item != null) {
//                UseJob job = UseJob.create(item);
//                if (job != null) {
//                    job.setCharacterRequire(character);
//                    return job;
//                }
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public boolean isJobLaunchable(CharacterModel character) {
//        return _items.getItems().stream().filter(UsableItem::isBed).findAny().isPresent();
//    }
//
//    @Override
//    public boolean isJobNeeded(CharacterModel character) {
//        return character.getNeeds().get("energy") <= character.getType().needs.energy.warning;
//    }
//}
