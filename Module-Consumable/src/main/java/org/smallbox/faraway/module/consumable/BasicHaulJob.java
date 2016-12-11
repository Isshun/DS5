package org.smallbox.faraway.module.consumable;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.module.item.UsableItem;
import org.smallbox.faraway.module.job.JobTask;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Alex on 09/12/2016.
 */
public class BasicHaulJob extends JobModel {

    private int _haulingQuantity;
    private ConsumableItem _consumable;
    private Queue<JobTask> _tasks = new ConcurrentLinkedQueue<>();

    public int getHaulingQuantity() { return _haulingQuantity; }
    public ConsumableItem getHaulingConsumable() { return _consumable; }

    public static BasicHaulJob toFactory(ConsumableItem consumable, UsableItem item, int haulingQuantity) {
        BasicHaulJob job = new BasicHaulJob(consumable, haulingQuantity, consumable.getParcel());

        job._tasks.add(character -> character.moveTo(consumable.getParcel()));
        job._tasks.add(character -> { character.addInventory(consumable, haulingQuantity); return true; });
        job._tasks.add(character -> character.moveTo(item.getParcel()));
        job._tasks.add(character -> {
            int needQuantity = item.getFactory().getRunningReceipt().getQuantityNeeded(consumable.getInfo());
            int availableQuantity = character.getInventoryQuantity(consumable.getInfo());
            item.getFactory().getRunningReceipt().addComponent(consumable.getInfo(), Math.min(needQuantity, availableQuantity));
            character.setInventoryQuantity(consumable.getInfo(), availableQuantity - needQuantity);
            return true;
        });

        return job;
    }

    public static BasicHaulJob toParcel(ConsumableItem consumable, ParcelModel targetParcel, int haulingQuantity) {
        BasicHaulJob job = new BasicHaulJob(consumable, haulingQuantity, consumable.getParcel());

        job._tasks.add(character -> character.moveTo(consumable.getParcel()));
        job._tasks.add(character -> { character.addInventory(consumable, haulingQuantity); return true; });
        job._tasks.add(character -> character.moveTo(targetParcel));
        job._tasks.add(character -> {
            int availableQuantity = character.getInventoryQuantity(consumable.getInfo());
            Application.moduleManager.getModule(ConsumableModule.class).putConsumable(targetParcel, consumable.getInfo(), availableQuantity);
            character.setInventoryQuantity(consumable.getInfo(), 0);
            return true;
        });

        return job;
    }

    public BasicHaulJob(ConsumableItem consumable, int haulingQuantity, ParcelModel targetParcel) {
        _targetParcel = targetParcel;
        _consumable = consumable;
        _haulingQuantity = haulingQuantity;
    }

    @Override
    protected JobCheckReturn onCheck(CharacterModel character) {
        return JobCheckReturn.OK;
    }

    @Override
    protected JobActionReturn onAction(CharacterModel character) {

        // Retourne COMPLETE si plus aucune tache n'existe
        if (_tasks.isEmpty()) {
            return JobActionReturn.COMPLETE;
        }

        // Execute la tache en tête de file et la retire si elle est terminée
        if (_tasks.peek().onExecuteTask(character)) {
            _tasks.poll();
        }

        return JobActionReturn.CONTINUE;
    }

    @Override
    public CharacterTalentExtra.TalentType getTalentNeeded() {
        return CharacterTalentExtra.TalentType.BUILD;
    }

    @Override
    public String getLabel() {
        return "Haul " + _consumable + " to " + _targetParcel;
    }
}
