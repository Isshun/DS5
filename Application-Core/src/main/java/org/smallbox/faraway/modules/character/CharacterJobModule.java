package org.smallbox.faraway.modules.character;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.core.game.helper.SurroundedPattern;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.util.Constant;

import java.util.concurrent.TimeUnit;

@GameObject
public class CharacterJobModule extends GameModule<CharacterModuleObserver> {

    @Inject
    private CharacterModule characterModule;

    @Inject
    private CharacterMoveModule characterMoveModule;

    @Inject
    private GameTime gameTime;

    @Override
    public int getModulePriority() {
        return Constant.MODULE_CHARACTER_PRIORITY;
    }

    @Override
    public void onModuleUpdate(Game game) {
        double hourInterval = getTickInterval() / game.getTickPerHour();

        characterModule.getCharacters().stream()
                .filter(character -> character.getJob() != null)
                .forEach(character -> {

                    // Character is on job parcel or next to them, do job action
                    if (WorldHelper.isSurrounded(SurroundedPattern.SQUARE, character.getJob().getTargetParcel(), character.getParcel())) {
                        character.getJob().action(character, hourInterval);
                    }

                    // Character is far away from job parcel, move to position
                    else {
                        CharacterMoveStatus status = characterMoveModule.move(character, character.getJob().getTargetParcel(), true);

                        if (status == CharacterMoveStatus.BLOCKED) {
                            character.getJob().block(character, gameTime.plus(5, TimeUnit.MINUTES));
                            character.clearJob(character.getJob());
                        }
                    }
                });
    }

}
