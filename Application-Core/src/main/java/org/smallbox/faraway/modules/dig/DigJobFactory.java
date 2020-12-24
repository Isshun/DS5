package org.smallbox.faraway.modules.dig;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.core.Application;
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
import org.smallbox.faraway.modules.job.JobTaskReturn;

@GameObject
public class DigJobFactory {

    @Inject
    private ApplicationConfigService applicationConfigService;

    @Inject
    private ConsumableModule consumableModule;

    @Inject
    private PathManager pathManager;

    public JobModel createJob(ParcelModel digParcel) {
        ParcelModel targetParcel = WorldHelper.searchAround(digParcel, 1, WorldHelper.SearchStrategy.FREE);

        if (digParcel.getRockInfo() != null) {
            JobModel job = new DigJob();

            job._targetParcel = digParcel;
            job._startParcel = digParcel;
            job._jobParcel = digParcel;
            job.setMainLabel("Dig");
            job.setSkillType(CharacterSkillExtra.SkillType.DIG);
            job.setIcon("[base]/graphics/jobs/ic_mining.png");
            job.setColor(new Color(0x80391eff));

            // Move character to rock
            job.addMoveTask("Move to rock", targetParcel);

            // Dig action
            job.addTask("Dig", (character, hourInterval) -> {
                job._time += hourInterval;
                job.setProgress(job._time, applicationConfigService.getGameInfo().digTime);
                return job._time >= applicationConfigService.getGameInfo().digTime ? JobTaskReturn.TASK_COMPLETE : JobTaskReturn.TASK_CONTINUE;
            });

            // - Create output products
            // - Remove rock from parcel
            // - Refresh GraphNode connections
            job.addTechnicalTask(() -> {
                digParcel.getRockInfo().actions.stream()
                        .filter(action -> action.type == ItemInfo.ItemInfoAction.ActionType.MINE)
                        .flatMap(action -> action.products.stream())
                        .forEach(product -> consumableModule.addConsumable(product.item, product.quantity, digParcel));
                digParcel.setRockInfo(null);
                pathManager.refreshConnections(digParcel);
                Application.notify(gameObserver -> gameObserver.onRemoveRock(digParcel));
            });

            job.onNewInit();

            return job;
        }

        throw new GameException(DigJobFactory.class, "Unable to create dig job");
    }
}
