package org.smallbox.faraway.modules.dig;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;
import org.smallbox.faraway.core.module.path.PathManager;
import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.job.JobTaskReturn;

@GameObject
public class DigJobFactory {

    @Inject
    private ApplicationConfig applicationConfig;

    @Inject
    private ConsumableModule consumableModule;

    @Inject
    private PathManager pathManager;

    public DigJob createJob(Parcel digParcel) {
        if (digParcel.getRockInfo() != null) {
            DigJob job = new DigJob();

            job._targetParcel = digParcel;
            job.setMainLabel("Dig");
            job.setSkillType(CharacterSkillExtra.SkillType.DIG);
            job.setIcon("[base]/graphics/jobs/ic_mining.png");
            job.setColor(new Color(0x80391eff));

            job.addMoveTask("Move to parcel", () -> digParcel);

            // Dig action
            job.addTask("Dig", (character, hourInterval) -> {
                if (digParcel.getRockInfo() != null) {
                    job._time += hourInterval;
                    job.setProgress(job._time, applicationConfig.game.digTime);
                    return job._time >= applicationConfig.game.digTime ? JobTaskReturn.TASK_COMPLETED : JobTaskReturn.TASK_CONTINUE;
                }
                return JobTaskReturn.TASK_COMPLETED;
            });

            // - Create output products
            // - Remove rock from parcel
            // - Refresh GraphNode connections
            job.addTechnicalTask(() -> {
                if (digParcel.getRockInfo() != null) {
                    digParcel.getRockInfo().actions.stream()
                            .filter(action -> action.type == ItemInfo.ItemInfoAction.ActionType.MINE)
                            .flatMap(action -> action.products.stream())
                            .forEach(product -> consumableModule.addConsumable(product.item, product.quantity, digParcel));
                    digParcel.setRockInfo(null);
                    pathManager.refreshConnections(digParcel);
                    Application.notify(gameObserver -> gameObserver.onRemoveRock(digParcel));
                }
            });

            job.onNewInit();

            return job;
        }

        throw new GameException(DigJobFactory.class, "Unable to create dig job");
    }
}
