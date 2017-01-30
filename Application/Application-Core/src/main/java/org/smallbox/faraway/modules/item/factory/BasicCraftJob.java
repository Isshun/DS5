package org.smallbox.faraway.modules.item.factory;

import org.smallbox.faraway.core.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.job.JobTaskReturn;

/**
 * Created by Alex on 09/12/2016.
 */
public abstract class BasicCraftJob extends JobModel {

    public BasicCraftJob(ParcelModel targetParcel) {
        _targetParcel = targetParcel;

        // Apporte les composants à la fabrique
        addTask("Go to factory", character -> character.moveTo(targetParcel) ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE);
        addTask("Craft item", character -> onCraft() ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE);
    }

    public abstract boolean onCraft();

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
