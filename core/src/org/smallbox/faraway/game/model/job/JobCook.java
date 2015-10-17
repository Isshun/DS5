package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.game.model.OldReceiptModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.model.item.ParcelModel;

import java.util.ArrayList;

public class JobCook extends JobCraft {

    @Override
    public CharacterModel.TalentType getTalentNeeded() {
        return CharacterModel.TalentType.COOK;
    }

    private JobCook(ItemInfo.ItemInfoAction action, ParcelModel jobParcel) {
        super(action, jobParcel);
    }

    public static JobCook create(ItemModel item) {
        if (item == null) {
            throw new RuntimeException("Cannot add cook job (item is null)");
        }

        JobCook job = new JobCook(null, item.getParcel());
        job.setItem(item);
        item.getFactory().setJob(job);
        job._mainItem = item;
        job._receipts = new ArrayList<>();
        item.getFactory().getReceipts()
                .forEach(receiptEntry -> receiptEntry.receiptGroupInfo.receipts
                        .forEach(product -> job._receipts.add(OldReceiptModel.createFromReceiptInfo(item, product))));
        job.setStrategy(j -> {
            if (j.getCharacter().getType().needs.joy != null) {
                j.getCharacter().getNeeds().joy += j.getCharacter().getType().needs.joy.change.work;
            }
        });
        job.onCheck(null);

        item.addJob(job);

        return job;
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
