package org.smallbox.faraway.modules.plant;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfigService;
import org.smallbox.faraway.core.module.path.PathManager;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.plant.model.PlantItem;

@GameObject
public class HarvestJobFactory {

    @Inject
    private ApplicationConfigService applicationConfigService;

    @Inject
    private ConsumableModule consumableModule;

    @Inject
    private PathManager pathManager;

    public JobModel create(PlantItem plant) {
        ParcelModel consumableDropParcel = WorldHelper.searchAround(plant.getParcel(), 1, WorldHelper.SearchStrategy.FREE);

        if (consumableDropParcel != null) {
            JobModel job = new JobModel();

            job._startParcel = job._targetParcel = plant.getParcel();
            job._targetParcel = plant.getParcel();
            job.setMainLabel("Harvest");
            job.setSkillType(CharacterSkillExtra.SkillType.GATHER);
            job.setIcon("[base]/graphics/jobs/ic_gather.png");
            job.setColor(Color.CHARTREUSE);

            // Déplace le personnage à l'emplacement des composants
            job.addMoveTask("Move to plant", () -> consumableDropParcel);

            // - Create output products
            // - Set plant maturity to 0
            // - Remove seed from plant
            // - Remove job from plant
            job.addTechnicalTask(() -> {
                plant.getInfo().actions.stream()
                        .filter(action -> action.type == ItemInfo.ItemInfoAction.ActionType.GATHER)
                        .flatMap(action -> action.products.stream())
                        .forEach(product -> consumableModule.addConsumable(product.item, product.quantity, consumableDropParcel));
                plant.setMaturity(0);
                plant.setSeed(false);
                plant.setJob(null);
            });

            plant.setJob(job);

            return job;
        }

        throw new GameException(HarvestJobFactory.class, "Unable to create harvest job");
    }
}
