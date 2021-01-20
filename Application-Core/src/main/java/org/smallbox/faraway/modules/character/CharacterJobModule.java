package org.smallbox.faraway.modules.character;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.engine.module.SuperGameModule2;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.core.game.helper.SurroundedPattern;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.modules.job.JobStatus;
import org.smallbox.faraway.util.Constant;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@GameObject
public class CharacterJobModule extends SuperGameModule2<CharacterModuleObserver> {
    @Inject private CharacterMoveModule characterMoveModule;
    @Inject private CharacterModule characterModule;
    @Inject private GameTime gameTime;

    @Override
    public int getModulePriority() {
        return Constant.MODULE_CHARACTER_PRIORITY;
    }

    @Override
    public void onModuleUpdate(Game game) {
        double hourInterval = getTickInterval() / game.getTickPerHour();

        characterModule.getAll().forEach(character -> {
            Optional.ofNullable(character.getJob()).ifPresent(job -> {

                // No target parcel
                if (job.getTargetParcel() == null) {
                    job.action(character, hourInterval, gameTime.now());
                }

                // Character is on job parcel or next to them, do job action
                else if (WorldHelper.isSurrounded(SurroundedPattern.SQUARE, job.getTargetParcel(), character.getParcel())) {
                    job.action(character, hourInterval, gameTime.now());
                }

                // Character is far away from job parcel, move to position
                else {
                    CharacterMoveStatus status = characterMoveModule.move(character, job.getTargetParcel(), !job.isExactParcel(), job.getMoveSpeed());

                    if (status == CharacterMoveStatus.BLOCKED) {
                        job.clearCharacter(character, gameTime.now());
                        job.setStatus(JobStatus.JOB_BLOCKED);
                        job.setBlockedUntil(gameTime.plus(5, TimeUnit.MINUTES));
                    }
                }

            });
        });
    }

}
