package org.smallbox.faraway.game.item.job;

import org.smallbox.faraway.game.character.model.CharacterSkillExtra;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.item.ItemModule;
import org.smallbox.faraway.game.item.UsableItem;
import org.smallbox.faraway.game.job.JobCheckReturn;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.job.task.ActionTask;
import org.smallbox.faraway.game.job.task.MoveTask;

import static org.smallbox.faraway.game.job.JobTaskReturn.TASK_COMPLETED;
import static org.smallbox.faraway.game.job.JobTaskReturn.TASK_CONTINUE;

public class UseJob extends JobModel {

    private final ItemModule _itemModule;
    private final UsableItem _item;

    public double _duration;

    public interface OnUseCallback {
        /**
         * Methode appelée à chaque tick tant que l'action n'est pas terminée
         *
         * @param item         l'item
         * @param durationLeft la durée restante
         */
        void onUse(UsableItem item, double durationLeft);
    }

    public UseJob(ItemModule itemModule, UsableItem item, double totalDuration, OnUseCallback callback) {
        _itemModule = itemModule;
        _item = item;
        _targetParcel = item.getParcel();

        setMainLabel("Use " + item.getInfo().label);

        addTask(new MoveTask("Move", item::getParcel));
        addTask(new ActionTask("Use", (character, hourInterval, localDateTime) -> {
            // TODO
//            _duration += 1 / DependencyInjector.getInstance().getDependency(GameManager.class).getGame().getTickPerHour();
            double durationLeft = totalDuration - _duration;
            callback.onUse(item, durationLeft);
            setProgress(_duration, totalDuration);
        }, () -> (totalDuration - _duration) <= 0 ? TASK_COMPLETED : TASK_CONTINUE));

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
    public boolean checkCharacterAccepted(CharacterModel character) {
        return true;
    }

    @Override
    public CharacterSkillExtra.SkillType getSkillType() {
        return null;
    }

}
