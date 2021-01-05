package org.smallbox.faraway.modules.item.job;

import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.item.UsableItem;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobTaskReturn;

public class UseJob extends JobModel {

    private final ItemModule _itemModule;
    private final UsableItem _item;

    public double _duration;

    public interface OnUseCallback {
        /**
         * Methode appelée à chaque tick tant que l'action n'est pas terminée
         * @param item l'item
         * @param durationLeft la durée restante
         */
        void onUse(UsableItem item, double durationLeft);
    }

    public UseJob(ItemModule itemModule, UsableItem item, double totalDuration, OnUseCallback callback) {
        _itemModule = itemModule;
        _item = item;
        _targetParcel = item.getParcel();

        setMainLabel("Use " + item.getInfo().label);

        addMoveTask("Move", item::getParcel);
        addTask("Use", (character, hourInterval) -> {
            // TODO
//            _duration += 1 / DependencyInjector.getInstance().getDependency(GameManager.class).getGame().getTickPerHour();
            double durationLeft = totalDuration - _duration;
            callback.onUse(item, durationLeft);
            setProgress(_duration, totalDuration);

            if (durationLeft > 0) {
                return JobTaskReturn.TASK_CONTINUE;
            }

            return JobTaskReturn.TASK_COMPLETED;
        });

    }

    @Override
    public JobCheckReturn onCheck(CharacterModel character) {
        // TODO
//        // Item is no longer exists
//        if (_item != _item.getParcel().getItem()) {
//            _reason = JobAbortReason.TASK_ERROR;
//            return JobCheckReturn.ABORT;
//        }

//        if (!DependencyInjector.getInstance().getDependency(PathManager.class).hasPath(character.getParcel(), _item.getParcel())) {
//            return JobCheckReturn.STAND_BY;
//        }

        return JobCheckReturn.OK;
    }

    @Override
    protected void onQuit(CharacterModel character) {
//        if (_item != null && _slot != null) {
//            _item.releaseSlot(_slot);
//        }
    }

    @Override
    public boolean checkCharacterAccepted(CharacterModel character) {
        return true;
    }

    @Override
    public CharacterSkillExtra.SkillType getSkillType() {
        return null;
    }

}
