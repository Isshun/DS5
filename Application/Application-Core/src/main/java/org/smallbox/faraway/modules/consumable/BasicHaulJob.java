package org.smallbox.faraway.modules.consumable;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Alex on 09/12/2016.
 */
public abstract class BasicHaulJob extends JobModel {

    protected ItemInfo _consumableInfo;
    protected Collection<ConsumableModule.ConsumableJobLock> _locks = new ConcurrentLinkedQueue<>();

    public BasicHaulJob(ItemInfo.ItemInfoAction itemInfoAction, ParcelModel parcelModel) {
        super(itemInfoAction, parcelModel);
    }

    /**
     * Déplace le personnage vers chaque consomable et l'ajoute à son inventaire
     *
     * @param consumableModule ConsumableModule
     * @param lock
     */
    public void addMoveToConsumableTasks(ConsumableModule consumableModule, ConsumableModule.ConsumableJobLock lock) {

    }

    public ItemInfo getConsumableInfo() {
        return _consumableInfo;
    }
}
