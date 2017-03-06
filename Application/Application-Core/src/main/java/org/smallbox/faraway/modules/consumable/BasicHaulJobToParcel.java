package org.smallbox.faraway.modules.consumable;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.item.factory.ItemFactoryModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.job.JobTaskReturn;

import java.util.Map;

/**
 * Created by Alex on 09/12/2016.
 */
public class BasicHaulJobToParcel extends BasicHaulJob {

    /**
     * Apporte les composants sur la parcel
     *
     * @param consumableModule ConsumableModule
     * @param jobModule
     *@param itemInfo ItemInfo
     * @param targetConsumables Map<ConsumableItem, Integer>
     * @param targetParcel ParcelModel
     * @param haulingQuantity int     @return BasicHaulJob
     */
    public static BasicHaulJobToParcel toParcel(ConsumableModule consumableModule, JobModule jobModule, ItemInfo itemInfo, Map<ConsumableItem, Integer> targetConsumables, ParcelModel targetParcel, int haulingQuantity) {

        BasicHaulJobToParcel job = jobModule.createJob(BasicHaulJobToParcel.class, null, targetParcel);
        job.init(itemInfo, haulingQuantity, targetParcel, targetConsumables);
        job.setMainLabel(String.format("Haul %s (x%d) to storage", itemInfo.label, haulingQuantity));

        if (job.create(consumableModule, jobModule, itemInfo, targetConsumables, haulingQuantity, targetParcel)) {

            // Apporte les composants à la zone de stockage
            job.addTask("Move to storage", character -> character.moveTo(targetParcel) ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE);

            // Ajoute les composants à la zone de stockage
            job.addTechnicalTask("Drop consumable to storage", character -> {

                targetConsumables.forEach((initialConsumable, quantity) -> {
                    ConsumableItem consumable = character.takeInventory(initialConsumable.getInfo(), quantity);
                    consumableModule.addConsumable(consumable.getInfo(), consumable.getFreeQuantity(), targetParcel);
                });

            });

            job.ready();
        }

        else {
            job.abort();
        }

        return job;
    }

    public BasicHaulJobToParcel(ItemInfo.ItemInfoAction itemInfoAction, ParcelModel parcelModel) {
        super(itemInfoAction, parcelModel);
    }

    public ItemFactoryModel getFactory() {
        return _factory;
    }

}
