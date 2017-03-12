package org.smallbox.faraway.modules.consumable;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.CharacterTalentExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.JobModel;

public class ConsumeJob extends JobModel {

    public int _duration;

    public ConsumeJob(ItemInfo.ItemInfoAction itemInfoAction, ParcelModel parcelModel) {
        super(itemInfoAction, parcelModel);
        setMainLabel("Consume");
    }

    @Override
    public JobCheckReturn onCheck(CharacterModel character) {
        return JobCheckReturn.OK;
    }

    @Override
    public void onQuit(CharacterModel character) {
//        if (_consumable != null && _consumable.getJob() == this) {
//            _consumable.setJob(null);
//        }
    }

    @Override
    protected void onClose() {
//        if (_consumable != null && _consumable.getJob() == this) {
//            _consumable.setJob(null);
//        }
    }

    @Override
    public String getLabel() {
//        if (_actionInfo != null && _actionInfo.label != null) {
//            return _actionInfo.label;
//        }
//        return "use " + _consumable.getLabel();
        return _mainLabel;
    }

    @Override
    public CharacterTalentExtra.TalentType getTalentNeeded() {
        return null;
    }

}
