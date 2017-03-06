package org.smallbox.faraway.modules.consumable;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.item.UsableItem;
import org.smallbox.faraway.modules.item.factory.ItemFactoryModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.job.JobTaskReturn;

import java.util.Map;

/**
 * Created by Alex on 09/12/2016.
 */
public class BasicHaulJobToFactory extends BasicHaulJob {

    private ItemFactoryModel _factory;

    /**
     * Apporte les composants à la factory
     *
     * @param consumableModule ConsumableModule
     * @param itemInfo ItemInfo
     * @param targetConsumables Map<ConsumableItem, Integer>
     * @param item UsableItem
     * @param haulingQuantity int
     * @return BasicHaulJob
     */
    public static BasicHaulJobToFactory toFactory(ConsumableModule consumableModule, JobModule jobModule, ItemInfo itemInfo, Map<ConsumableItem, Integer> targetConsumables, UsableItem item, int haulingQuantity) {

        BasicHaulJobToFactory job = jobModule.createJob(BasicHaulJobToFactory.class, null, item.getParcel());
        job.init(itemInfo, haulingQuantity, item.getParcel(), targetConsumables);
        job.setMainLabel("Haul " + itemInfo.label + " to factory");

        if (job.create(consumableModule, jobModule, itemInfo, targetConsumables, haulingQuantity, item.getParcel())) {

            job._factory = item.getFactory();

            // Apporte les composants à la fabrique
            job.addTask("Bring back to factory", character -> character.moveTo(item.getParcel()) ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE);

            // Charge les comnposants dans la fabrique
            job.addTechnicalTask("Load factory", character -> {

                targetConsumables.forEach((initialConsumable, quantity) -> {
                    ConsumableItem consumable = character.takeInventory(initialConsumable.getInfo(), quantity);
                    item.addInventory(consumable);
                });

            });

            job.ready();
        }

        else {
            job.abort();
        }

        return job;
    }

    public BasicHaulJobToFactory(ItemInfo.ItemInfoAction itemInfoAction, ParcelModel parcelModel) {
        super(itemInfoAction, parcelModel);
    }

    public ItemFactoryModel getFactory() {
        return _factory;
    }
}
