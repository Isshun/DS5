package org.smallbox.faraway.modules.consumable;

import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.modules.character.model.CharacterTalentExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobTaskReturn;

public class ConsumeJob extends JobModel {

    private final ConsumableModule _consumableModule;
    private final ConsumableItem _consumable;
    private final ConsumableModule.ConsumableJobLock _lock;

    public int _duration;

    public interface OnConsumeCallback {
        /**
         * Methode appelée à chaque tick tant que l'action n'est pas terminée
         * @param consumable le consomable
         * @param durationLeft la durée restante
         */
        void onConsume(ConsumableItem consumable, int durationLeft);
    }

    public ConsumeJob(ConsumableModule consumableModule, ConsumableItem consumable, int totalDuration, OnConsumeCallback callback) {
        _consumableModule = consumableModule;
        _consumable = consumable;
        _lock = consumableModule.lock(this, consumable, 1);

        setMainLabel("Consume " + consumable.getInfo().label);

        addTask("Move", character -> character.moveTo(consumable.getParcel()) ? JobTaskReturn.TASK_COMPLETE : JobTaskReturn.TASK_CONTINUE);
        addTask("Consume", character -> {
            if (_lock.available) {
                int durationLeft = totalDuration - ++_duration;
                callback.onConsume(consumable, durationLeft);
                setProgress(_duration, totalDuration);

                if (durationLeft > 0) {
                    return JobTaskReturn.TASK_CONTINUE;
                }

                // Retire le lock si l'action est terminée
                consumableModule.takeConsumable(_lock);
                return JobTaskReturn.TASK_COMPLETE;
            }
            return JobTaskReturn.TASK_ERROR;
        });
    }

    @Override
    public JobCheckReturn onCheck(CharacterModel character) {
        return JobCheckReturn.OK;
    }

    @Override
    protected void onClose() {
        if (_lock.available) {
            _consumableModule.cancelLock(_lock);
        }

        if (_consumable != null) {
            _consumable.removeJob(this);
        }
    }

    @Override
    public String getLabel() {
        return _mainLabel;
    }

    @Override
    public CharacterTalentExtra.TalentType getTalentNeeded() {
        return null;
    }

}
