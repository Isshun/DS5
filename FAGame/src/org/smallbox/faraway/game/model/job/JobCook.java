package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.game.model.ReceiptModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.model.item.MapObjectModel;

import java.util.stream.Collectors;

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
            throw new RuntimeException("Cannot add cook job (onAction is null)");
        }

        JobCook job = new JobCook(action, item.getX(), item.getY());
        job.setItem(item);
        job._factory = (ItemModel)item;
        job._receipts = action.receipts.stream().map(receiptInfo -> new ReceiptModel((ItemModel) item, receiptInfo)).collect(Collectors.toList());
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
