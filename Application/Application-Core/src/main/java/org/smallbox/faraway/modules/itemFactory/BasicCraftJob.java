package org.smallbox.faraway.modules.itemFactory;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.ReceiptGroupInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.job.JobTaskReturn;

/**
 * Created by Alex on 09/12/2016.
 */
public class BasicCraftJob extends JobModel {

    private ReceiptGroupInfo.ReceiptInfo _receiptInfo;
    private long _startTick;
    private long _endTick;
    private ItemFactoryModel factory;

    public BasicCraftJob(ItemInfo.ItemInfoAction actionInfo, ParcelModel targetParcel) {
        super(actionInfo, targetParcel);
    }

//    public abstract boolean onCraft();
//    public abstract int getCost();
//    public abstract int getCostRemaining();

    public long getStartTick() { return _startTick; }
    public long getEndTick() { return _endTick; }
    public ReceiptGroupInfo.ReceiptInfo getReceiptInfo() { return _receiptInfo; }

    //    @Override
    public int getCost() {
        return factory.getRunningReceipt().getCost();
    }

    //    @Override
    public int getCostRemaining() {
        return factory.getRunningReceipt().getCostRemaining();
    }

    @Override
    protected JobCheckReturn onCheck(CharacterModel character) {
        return JobCheckReturn.OK;
    }

    @Override
    public CharacterSkillExtra.SkillType getSkillNeeded() {
        return CharacterSkillExtra.SkillType.BUILD;
    }

    @Override
    public String toString() { return "Craft"; }

    public static BasicCraftJob create(JobModule jobModule, ParcelModel targetParcel, ReceiptGroupInfo.ReceiptInfo receiptInfo, ItemFactoryModel factory) {
        return jobModule.createJob(BasicCraftJob.class, null, targetParcel, job -> {

            job.factory = factory;
            factory.setCraftJob(job);

            job._receiptInfo = receiptInfo;

            job._mainLabel = "Craft";
            job._targetParcel = targetParcel;

            // Apporte les composants à la fabrique
            job.addTask("Go to factory", (character, hourInterval) -> character.moveTo(targetParcel) ? JobTaskReturn.TASK_COMPLETE : JobTaskReturn.TASK_CONTINUE);

            // Craft action
            job.addTask("Craft item", (character, hourInterval) -> {
                if (job._startTick == 0) {
                    job._startTick = Application.gameManager.getGame().getTick();
                    job._endTick = Application.gameManager.getGame().getTick() + job.getCostRemaining();
                }

                // Incrémente la variable count de la recette (état d'avancement) et return TASK_CONTINUE si la valeur retournée n'est pas 0
                if (job.factory.getRunningReceipt().craft(1 / Application.config.game.craftTime * hourInterval) > 0) {
                    return JobTaskReturn.TASK_CONTINUE;
                }

                // Sinon retourn TASK_COMPLETE
                return JobTaskReturn.TASK_COMPLETE;
            });

            return true;
        });
    }
}
