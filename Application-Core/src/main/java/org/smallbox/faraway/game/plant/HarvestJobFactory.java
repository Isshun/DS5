package org.smallbox.faraway.game.plant;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.util.GameException;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.path.PathManager;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.character.model.CharacterSkillExtra;
import org.smallbox.faraway.game.consumable.ConsumableModule;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.job.task.MoveTask;
import org.smallbox.faraway.game.job.task.TechnicalTask;
import org.smallbox.faraway.game.plant.model.PlantItem;

@GameObject
public class HarvestJobFactory {
    @Inject private ConsumableModule consumableModule;
    @Inject private PathManager pathManager;

    public JobModel create(PlantItem plant) {
        Parcel consumableDropParcel = WorldHelper.searchAround(plant.getParcel(), 1, WorldHelper.SearchStrategy.FREE);

        if (consumableDropParcel != null) {
            JobModel job = new JobModel();

            job._targetParcel = plant.getParcel();
            job.setMainLabel("Harvest");
            job.setSkillType(CharacterSkillExtra.SkillType.GATHER);
            job.setIcon("[base]/graphics/jobs/ic_gather.png");
            job.setColor(Color.CHARTREUSE);

            // Déplace le personnage à l'emplacement des composants
            job.addTask(new MoveTask("Move to plant", () -> consumableDropParcel));

            // - Create output products
            // - Set plant maturity to 0
            // - Remove seed from plant
            // - Remove job from plant
            job.addTask(new TechnicalTask(j -> {
                plant.getInfo().actions.stream()
                        .filter(action -> action.type == ItemInfo.ItemInfoAction.ActionType.GATHER)
                        .flatMap(action -> action.products.stream())
                        .forEach(product -> consumableModule.addConsumable(product.item, product.quantity, consumableDropParcel));
                plant.setMaturity(0);
                plant.setSeed(false);
                plant.setJob(null);
            }));

            plant.setJob(job);

            return job;
        }

        throw new GameException(HarvestJobFactory.class, "Unable to create harvest job");
    }
}
