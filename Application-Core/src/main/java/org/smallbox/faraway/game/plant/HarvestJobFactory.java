package org.smallbox.faraway.game.plant;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.path.PathManager;
import org.smallbox.faraway.game.character.model.CharacterSkillExtra;
import org.smallbox.faraway.game.consumable.ConsumableModule;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.job.task.TechnicalTask;
import org.smallbox.faraway.game.plant.model.PlantItem;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.SurroundedPattern;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.util.GameException;

@GameObject
public class HarvestJobFactory {
    @Inject private ConsumableModule consumableModule;
    @Inject private PathManager pathManager;

    public JobModel createJob(PlantItem plant) {
        Parcel consumableDropParcel = WorldHelper.searchAround(plant.getParcel(), 1, WorldHelper.SearchStrategy.FREE);

        if (consumableDropParcel != null) {
            JobModel job = new JobModel(plant.getParcel());

            job.setMainLabel("Harvest");
            job.setSkillType(CharacterSkillExtra.SkillType.GATHER);
            job.setIcon("data/graphics/jobs/ic_gather.png");
            job.setColor(Color.CHARTREUSE);
            WorldHelper.getParcelAround(plant.getParcel(), SurroundedPattern.SQUARE, job::addAcceptedParcel);

            // Déplace le personnage à l'emplacement des composants
            job.addAcceptedParcel(consumableDropParcel);

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
