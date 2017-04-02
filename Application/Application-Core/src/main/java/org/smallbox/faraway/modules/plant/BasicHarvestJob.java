package org.smallbox.faraway.modules.plant;

import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.plant.model.PlantItem;

/**
 * Created by Alex on 03/03/2017.
 */
public class BasicHarvestJob extends JobModel {

    public static BasicHarvestJob create(ConsumableModule consumableModule, JobModule jobModule, PlantItem plant) {
        ParcelModel consumableDropParcel = WorldHelper.searchAround(plant.getParcel(), 1, WorldHelper.SearchStrategy.FREE);

        if (consumableDropParcel != null) {
            return jobModule.createJob(BasicHarvestJob.class, null, plant.getParcel(), job -> {

                job._startParcel = job._targetParcel = plant.getParcel();

                // Déplace le personnage à l'emplacement des composants
                job.addMoveTask("Move to plant", consumableDropParcel);

                // Crée les composants
                job.addTechnicalTask("Create components", character ->
                        plant.getInfo().actions.stream()
                                .filter(action -> action.type == ItemInfo.ItemInfoAction.ActionType.GATHER)
                                .flatMap(action -> action.products.stream())
                                .forEach(product -> consumableModule.addConsumable(product.item, product.quantity, consumableDropParcel)));

                // Harvest
                job.addTechnicalTask("Harvest", character -> {
                    plant.setJob(null);
                    plant.setMaturity(0);
                    plant.setSeed(false);
                });

                plant.setJob(job);

                return true;
            });
        }

        return null;
    }

    public BasicHarvestJob(ItemInfo.ItemInfoAction actionInfo, ParcelModel targetParcel) {
        _mainLabel = "Harvest";
        _targetParcel = targetParcel;
    }

    @Override
    protected JobCheckReturn onCheck(CharacterModel character) {
        return JobCheckReturn.OK;
    }

    @Override
    public boolean checkCharacterAccepted(CharacterModel character) {

        // Character have no skill
        if (!character.hasExtra(CharacterSkillExtra.class) || !character.getExtra(CharacterSkillExtra.class).hasSkill(CharacterSkillExtra.SkillType.GATHER)) {
            return false;
        }

        // Character is qualified for job
        return true;

    }

    @Override
    public String toString() { return "Harvest"; }

}
