package org.smallbox.faraway.modules.consumable;

import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobTaskReturn;

public class ConsumeJob extends JobModel {

    private final ConsumableModule _consumableModule;
    private final ConsumableItem _consumable;
    private final ConsumableModule.ConsumableJobLock _lock;

    public double _duration;

    public interface OnConsumeCallback {
        /**
         * Methode appelée à chaque tick tant que l'action n'est pas terminée
         * @param consumable le consomable
         * @param durationLeft la durée restante
         */
        void onConsume(ConsumableItem consumable, double durationLeft);
    }

    public ConsumeJob(ConsumableModule consumableModule, ConsumableItem consumable, double totalDuration, OnConsumeCallback callback) {
        _consumableModule = consumableModule;
        _consumable = consumable;
        _lock = consumableModule.lock(this, consumable, 1);

        _startParcel = _targetParcel = consumable.getParcel();


        setMainLabel("Consume " + consumable.getInfo().label);

        addMoveTask("Move", consumable.getParcel());
        addTask("Consume", (character, hourInterval) -> {
            if (_lock.available) {
                _duration += 1 / DependencyInjector.getInstance().getDependency(GameManager.class).getGame().getTickPerHour();
                double durationLeft = totalDuration - _duration;
                callback.onConsume(consumable, durationLeft);
                setProgress(_duration, totalDuration);

                if (durationLeft > 0) {
                    return JobTaskReturn.TASK_CONTINUE;
                }

                // Retire le lock si l'action est terminée
                consumableModule.createConsumableFromLock(_lock);
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
    public boolean checkCharacterAccepted(CharacterModel character) {
        return true;
    }

    @Override
    public CharacterSkillExtra.SkillType getSkillType() {
        return null;
    }

}
