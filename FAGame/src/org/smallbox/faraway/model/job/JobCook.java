package org.smallbox.faraway.model.job;

import org.smallbox.faraway.model.ReceiptModel;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ItemInfo;
import org.smallbox.faraway.model.item.MapObjectModel;

public class JobCook extends JobCraft {

    @Override
    public CharacterModel.TalentType getTalentNeeded() {
        return CharacterModel.TalentType.COOK;
    }

    private JobCook(ItemInfo.ItemInfoAction action, int x, int y) {
        super(action, x, y);
    }

    public static JobCook create(ItemInfo.ItemInfoAction action, MapObjectModel item) {
        if (item == null) {
            throw new RuntimeException("Cannot add cook job (item is null)");
        }

        if (action == null) {
            throw new RuntimeException("Cannot add cook job (action is null)");
        }

        JobCook job = new JobCook(action, item.getX(), item.getY());
        job.setItem(item);
        for (ItemInfo.ItemInfoReceipt receiptInfo: action.receipts) {
            job._receipts.add(new ReceiptModel(receiptInfo));
        }

        item.addJob(job);

        return job;
    }

    @Override
    public String getType() {
        return "cook";
    }

    @Override
    public String getLabel() {
        return _actionInfo.label;
    }

    @Override
    public String getShortLabel() {
        return "Cook";
    }

}
