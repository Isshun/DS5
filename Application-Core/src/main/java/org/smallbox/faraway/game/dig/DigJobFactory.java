package org.smallbox.faraway.game.dig;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.path.PathManager;
import org.smallbox.faraway.game.character.model.CharacterSkillExtra;
import org.smallbox.faraway.game.consumable.ConsumableModule;
import org.smallbox.faraway.game.job.task.ActionTask;
import org.smallbox.faraway.game.job.task.TechnicalTask;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.SurroundedPattern;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.game.world.WorldModule;
import org.smallbox.faraway.util.GameException;

import java.util.Optional;

import static org.smallbox.faraway.game.job.JobTaskReturn.TASK_COMPLETED;
import static org.smallbox.faraway.game.job.JobTaskReturn.TASK_CONTINUE;

@GameObject
public class DigJobFactory {
    @Inject private ApplicationConfig applicationConfig;
    @Inject private ConsumableModule consumableModule;
    @Inject private WorldModule worldModule;
    @Inject private PathManager pathManager;

    public DigJob createJob(Parcel digParcel, DigType digType) {
        if (digParcel.getRockInfo() != null) {
            DigJob job = new DigJob();

            job._targetParcel = digParcel;

            WorldHelper.getParcelAround(digParcel, SurroundedPattern.X_CROSS, job::addAcceptedParcel);
            WorldHelper.getParcelAround(WorldHelper.getParcelOffset(digParcel, 0, 0, 1), SurroundedPattern.X_CROSS, job::addAcceptedParcel);

            job.setMainLabel("Dig");
            job.setSkillType(CharacterSkillExtra.SkillType.DIG);
            job.setIcon("[base]/graphics/jobs/ic_mining.png");
            job.setColor(new Color(0x80391eff));

            // Dig action
            job.addTask(new ActionTask("Dig", (character, hourInterval, localDateTime) -> {
                if (digParcel.getRockInfo() != null) {
                    job._time += hourInterval;
                    job.setProgress(job._time, applicationConfig.game.digTime);
                }
            }, () -> !digParcel.hasRock() || job._time >= applicationConfig.game.digTime ? TASK_COMPLETED : TASK_CONTINUE));

            // - Create output products
            // - Remove rock from parcel
            // - Refresh GraphNode connections
            job.addTask(new TechnicalTask(j -> {

                switch (digType) {

                    case ROCK -> {
                        if (digParcel.getRockInfo() != null) {
                            digParcel.getRockInfo().actions.stream()
                                    .filter(action -> action.type == ItemInfo.ItemInfoAction.ActionType.MINE)
                                    .flatMap(action -> action.products.stream())
                                    .forEach(product -> consumableModule.addConsumable(product.item, product.quantity, digParcel));
                            digParcel.setRockInfo(null);
                            pathManager.refreshConnections(digParcel);

                            // Remove ground for upper parcel
                            Optional.ofNullable(WorldHelper.getParcelOffset(digParcel, 0, 0, 1)).ifPresent(parcel -> {
                                parcel.setGroundInfo(null);
                                pathManager.refreshConnections(parcel);
                            });

                            Application.notify(gameObserver -> gameObserver.onRemoveRock(digParcel));
                        }
                    }
                    case RAMP -> {
                        digParcel.setRamp(MovableModel.Direction.LEFT);
                        pathManager.refreshConnections(digParcel);

                        // Remove ground for upper parcel
                        Optional.ofNullable(WorldHelper.getParcelOffset(digParcel, 0, 0, 1)).ifPresent(parcel -> {
                            parcel.setGroundInfo(null);
                            pathManager.refreshConnections(parcel);
                        });
                    }
                    case FLOOR -> {
                        digParcel.setGroundInfo(null);
                        pathManager.refreshConnections(digParcel);
                    }
                }

            }));

            job.onNewInit();

            return job;
        }

        throw new GameException(DigJobFactory.class, "Unable to create dig job");
    }
}
