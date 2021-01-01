package org.smallbox.faraway.modules.character;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.PathModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.MoveListener;

@GameObject
public class CharacterMoveModule extends GameModule<CharacterModuleObserver> {

    @Inject
    private CharacterModule characterModule;

    @Inject
    private ApplicationConfig applicationConfig;

    @Inject
    private GameManager gameManager;

//    private Map<CharacterModel, PathModel> paths = new ConcurrentHashMap<>();

    @Override
    public int getModulePriority() {
        return Constant.MODULE_CHARACTER_PRIORITY;
    }

    @Override
    public void onModuleUpdate(Game game) {
//        fixCharacterPosition();

        characterModule.getCharacters().forEach(character -> {
            character.setDirection(MovableModel.Direction.NONE);
            move(character);
        });
    }

    @Override
    public void onGameRender(Game game) {
//        characterModule.getAll().forEach(this::move);
    }

    public void move(CharacterModel character) {
        PathModel _path = character._path;

        if (_path != null) {
            // Character is sleeping
            if (character.isSleeping()) {
                Log.debug("Character #" + character.getId() + ": sleeping . move canceled");
                return;
            }

            // Increase move progress
            character.setMoveStep(applicationConfig.game.characterSpeed / gameManager.getGame().getTickPerHour());
//            _moveStep = 1 * getExtra(CharacterStatsExtra.class).speed * (_job != null ? _job.getSpeedModifier() : 1);

            // Character has reach next parcel
            if (character._moveProgress >= 1) {
                moveToNextParcel(character, _path);
            }

            character._moveProgress += character._moveStep;
            character._moveProgress2 += character._moveStep;

            character.position._moveProgress = character._moveProgress;
            character.position._moveProgress2 = character._moveProgress2;

            character.position.parcelX = character.getParcel().x;
            character.position.parcelY = character.getParcel().y;
            character.position.parcelZ = character.getParcel().z;

            Log.debug(getName() + " Move progress = " + character._moveProgress + ", " + character._moveProgress2);
        }
    }

    private void moveToNextParcel(CharacterModel character, PathModel _path) {

        character.setMoveProgress(0);

        // If path.next() return true, the move is continuing
        if (_path.next()) {

            // Set direction to the next parcel
            ParcelModel fromParcel = character._parcel;
            ParcelModel toParcel = _path.getCurrentParcel();
            character._direction = getDirection(fromParcel.x, fromParcel.y, toParcel.x, toParcel.y);

            // Set logic position of the character to the next parcel
            character.setParcel(_path.getCurrentParcel());

            // TODO: no idea of the purpose of pathLength
            character.position.pathLength = _path.getLength();
        }

        // When path.next() return false, the move is completed
        else {
            character._path = null;

            // TODO: is moveListener useful ?
            if (character._moveListener != null) {
                Log.info(getName() + " Move state: call onReach");
                MoveListener listener = character._moveListener;
                character._moveListener = null;
                listener.onReach(character);
            }

            // TODO: no idea of the purpose of pathLength
            character.position.pathLength = 0;
        }
    }

//    private void fixCharacterPosition() {
//        characterModule.getCharacters().stream()
//                .filter(character -> character.getParcel() != null && !character.getParcel().isWalkable())
//                .forEach(character -> {
//                    Log.warning(getName() + " is stuck !");
//                    character.setParcel(WorldHelper.getNearestWalkable(character.getParcel(), 1, 20));
//                    if (character.getJob() != null) {
//                        character.getJob().quit(character);
//                        character.clearJob(character.getJob());
//                    }
//                });
//    }

    public boolean havePeopleOnProximity(CharacterModel character) {
        return characterModule.getCharacters().stream()
                .anyMatch(c -> c != character
                        && WorldHelper.getApproxDistance(character.getParcel(), c.getParcel()) < 4);
    }

    public boolean hasCharacterOnParcel(ParcelModel parcel) {
        return characterModule.getCharacters().stream().anyMatch(c -> c.getParcel() == parcel);
    }

    private MovableModel.Direction getDirection(int fromX, int fromY, int toX, int toY) {
        if (toX > fromX && toY > fromY) return MovableModel.Direction.BOTTOM_RIGHT;
        if (toX < fromX && toY > fromY) return MovableModel.Direction.BOTTOM_LEFT;
        if (toX > fromX && toY < fromY) return MovableModel.Direction.TOP_RIGHT;
        if (toX < fromX && toY < fromY) return MovableModel.Direction.TOP_LEFT;
        if (toX > fromX) return MovableModel.Direction.RIGHT;
        if (toX < fromX) return MovableModel.Direction.LEFT;
        if (toY > fromY) return MovableModel.Direction.BOTTOM;
        if (toY < fromY) return MovableModel.Direction.TOP;
        return MovableModel.Direction.NONE;
    }

}
