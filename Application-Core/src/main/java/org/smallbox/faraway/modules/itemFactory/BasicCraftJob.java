package org.smallbox.faraway.modules.itemFactory;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.ReceiptGroupInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.job.JobTaskReturn;

public class BasicCraftJob extends JobModel {

    private ReceiptGroupInfo.ReceiptInfo _receiptInfo;
    private ItemFactoryModel factory;

    public BasicCraftJob(ItemInfo.ItemInfoAction actionInfo, ParcelModel targetParcel) {
        super(actionInfo, targetParcel);
    }

    public ReceiptGroupInfo.ReceiptInfo getReceiptInfo() { return _receiptInfo; }

    //    @Override
    public int getCost() {
        return factory.getRunningReceipt().getCost();
    }

    //    @Override
    public double getCostRemaining() {
        return factory.getRunningReceipt().getCostRemaining();
    }

    @Override
    protected JobCheckReturn onCheck(CharacterModel character) {
        return JobCheckReturn.OK;
    }

    @Override
    public boolean checkCharacterAccepted(CharacterModel character) {

        // Character have no skill
        if (!character.hasExtra(CharacterSkillExtra.class) || !character.getExtra(CharacterSkillExtra.class).hasSkill(CharacterSkillExtra.SkillType.CRAFT)) {
            return false;
        }

        // Character is qualified for job
        return true;

    }

    @Override
    public String toString() { return "Craft"; }

    public static BasicCraftJob create(JobModule jobModule, ParcelModel factoryParcel, ReceiptGroupInfo.ReceiptInfo receiptInfo, ItemFactoryModel factory) {
        return jobModule.createJob(BasicCraftJob.class, null, factoryParcel, job -> {

            job.factory = factory;
            factory.setCraftJob(job);

            job._receiptInfo = receiptInfo;

            job._mainLabel = "Craft";
            job._targetParcel = factoryParcel;
            job._startParcel = factoryParcel;

            // Apporte les composants à la fabrique
            job.addTask("Go to factory", (character, hourInterval) -> character.moveTo(factoryParcel) ? JobTaskReturn.TASK_COMPLETE : JobTaskReturn.TASK_CONTINUE);

            // Craft action
            job.addTask("Craft item", (character, hourInterval) -> {
                FactoryReceiptModel receipt = job.factory.getRunningReceipt();
                job.setProgress(receipt.getCost() - receipt.getCostRemaining(), receipt.getCost());

                // Incrémente la variable count de la recette (état d'avancement) et return TASK_CONTINUE si la valeur retournée n'est pas 0
                if (receipt.craft(1 / jobModule.getGameConfig().craftTime * hourInterval) > 0) {
                    return JobTaskReturn.TASK_CONTINUE;
                }

                // Sinon crée les consomables et retourne TASK_COMPLETE
                // TODO: fait par la factory
//                job.factory.getRunningReceipt().receiptInfo.outputs.forEach(receiptOutputInfo -> receiptOutputInfo.item);
                return JobTaskReturn.TASK_COMPLETE;
            });

            return true;
        });
    }

    @Override
    public CharacterSkillExtra.SkillType getSkillType() {
        return CharacterSkillExtra.SkillType.CRAFT;
    }

}
