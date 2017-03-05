package org.smallbox.faraway.modules.item.factory;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.ReceiptGroupInfo;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.CharacterTalentExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
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
    public boolean onCraft() {
        // Incrémente la variable count de la recette (état d'avancement)
        return factory.getRunningReceipt().decreaseCostRemaining() == 0;
    }

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
    protected JobActionReturn onAction(CharacterModel character) {
        return null;
    }

    @Override
    public CharacterTalentExtra.TalentType getTalentNeeded() {
        return CharacterTalentExtra.TalentType.BUILD;
    }

    @Override
    public String toString() { return "Craft"; }

    public static BasicCraftJob create(JobModule jobModule, ParcelModel targetParcel, ReceiptGroupInfo.ReceiptInfo receiptInfo, ItemFactoryModel factory) {
        BasicCraftJob job = jobModule.createJob(BasicCraftJob.class, null, targetParcel);

        job.factory = factory;
        factory.setCraftJob(job);

        job._receiptInfo = receiptInfo;

        job._mainLabel = "Craft";
        job._targetParcel = targetParcel;

        // Apporte les composants à la fabrique
        job.addTask("Go to factory", character -> character.moveTo(targetParcel) ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE);

        // Craft action
        job.addTask("Craft item", character -> {
            if (job._startTick == 0) {
                job._startTick = Application.gameManager.getGame().getTick();
                job._endTick = Application.gameManager.getGame().getTick() + job.getCostRemaining();
            }
            return job.onCraft() ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE;
        });

        job.ready();
        return job;
    }
}
