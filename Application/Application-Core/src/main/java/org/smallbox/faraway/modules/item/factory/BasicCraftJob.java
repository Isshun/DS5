package org.smallbox.faraway.modules.item.factory;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.modelInfo.ReceiptGroupInfo;
import org.smallbox.faraway.core.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.job.JobTaskReturn;

/**
 * Created by Alex on 09/12/2016.
 */
public abstract class BasicCraftJob extends JobModel {

    private final ReceiptGroupInfo.ReceiptInfo _receiptInfo;
    private long _startTick;
    private long _endTick;

    public BasicCraftJob(ParcelModel targetParcel, ReceiptGroupInfo.ReceiptInfo receiptInfo) {
        _mainLabel = "Craft";
        _targetParcel = targetParcel;
        _receiptInfo = receiptInfo;

        // Apporte les composants à la fabrique
        addTask("Go to factory", character -> character.moveTo(targetParcel) ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE);

        // Craft action
        addTask("Craft item", character -> {
            if (_startTick == 0) {
                _startTick = Application.gameManager.getGame().getTick();
                _endTick = Application.gameManager.getGame().getTick() + getCostRemaining();
            }
            return onCraft() ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE;
        });
    }

    public abstract boolean onCraft();
    public abstract int getCost();
    public abstract int getCostRemaining();

    public long getStartTick() { return _startTick; }
    public long getEndTick() { return _endTick; }
    public ReceiptGroupInfo.ReceiptInfo getReceiptInfo() { return _receiptInfo; }

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
}
