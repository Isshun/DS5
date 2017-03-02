package org.smallbox.faraway.modules;

import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.modules.character.model.CharacterTalentExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.job.JobTaskReturn;
import org.smallbox.faraway.modules.world.WorldModule;

/**
 * Created by Alex on 28/02/2017.
 */
public class BasicDigJob extends JobModel {

    private long _startTick;
    private long _endTick;

    public static BasicDigJob create(ConsumableModule consumableModule, WorldModule worldModule, ParcelModel parcel) {
        BasicDigJob job = new BasicDigJob(parcel);

        ParcelModel targetParcel = WorldHelper.searchAround(parcel, 1, WorldHelper.SearchStrategy.FREE);
        ItemInfo rockInfo = parcel.getRockInfo();

        if (targetParcel != null && rockInfo != null) {

            // Déplace le personnage à l'emplacement des composants
            job.addTask("Move to rock", character -> character.moveTo(targetParcel) ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE);

            // Retire les rochers de la carte
            job.addTechnicalTask("Remove rock", character -> parcel.setRockInfo(null));

            // Crée les gravats
            job.addTechnicalTask("Create components", character ->
                    rockInfo.actions.stream()
                            .filter(action -> action.type == ItemInfo.ItemInfoAction.ActionType.MINE)
                            .flatMap(action -> action.products.stream())
                            .forEach(product -> consumableModule.addConsumable(product.item, product.quantity, parcel)));

            return job;
        }

        return null;
    }

    public BasicDigJob(ParcelModel targetParcel) {
        _mainLabel = "Dig";
        _targetParcel = targetParcel;
    }

    public long getStartTick() { return _startTick; }
    public long getEndTick() { return _endTick; }

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
    public String toString() { return "Dig"; }

}
