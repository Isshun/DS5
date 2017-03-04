package org.smallbox.faraway.modules.flora;

import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.CharacterTalentExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.flora.model.PlantItem;
import org.smallbox.faraway.modules.job.JobTaskReturn;

/**
 * Created by Alex on 03/03/2017.
 */
public class BasicHarvestJob extends JobModel {

    public static BasicHarvestJob create(ConsumableModule consumableModule, PlantItem plant) {
        ParcelModel targetParcel = WorldHelper.searchAround(plant.getParcel(), 1, WorldHelper.SearchStrategy.FREE);

        if (targetParcel != null) {
            BasicHarvestJob job = new BasicHarvestJob(plant.getParcel());

            // Déplace le personnage à l'emplacement des composants
            job.addTask("Move to plant", character -> character.moveTo(targetParcel) ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE);

            // Crée les composants
            job.addTechnicalTask("Create components", character ->
                    plant.getInfo().actions.stream()
                            .filter(action -> action.type == ItemInfo.ItemInfoAction.ActionType.GATHER)
                            .flatMap(action -> action.products.stream())
                            .forEach(product -> consumableModule.addConsumable(product.item, product.quantity, targetParcel)));

            // Harvest
            job.addTechnicalTask("Harvest", character -> {
                plant.setJob(null);
                plant.setMaturity(0);
                plant.setSeed(false);
            });

            return job;
        }

        return null;
    }

    public BasicHarvestJob(ParcelModel targetParcel) {
        _mainLabel = "Harvest";
        _targetParcel = targetParcel;
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
        return CharacterTalentExtra.TalentType.GATHER;
    }

    @Override
    public String toString() { return "Harvest"; }

}
