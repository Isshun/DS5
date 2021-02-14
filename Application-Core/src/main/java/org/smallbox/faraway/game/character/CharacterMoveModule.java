package org.smallbox.faraway.game.character;

import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameUpdate;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.module.SuperGameModule2;
import org.smallbox.faraway.core.path.PathManager;
import org.smallbox.faraway.game.character.model.PathModel;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.GameException;
import org.smallbox.faraway.util.MoveListener;
import org.smallbox.faraway.util.log.Log;

import java.util.Optional;

@GameObject
public class CharacterMoveModule extends SuperGameModule2<CharacterModuleObserver> {
    @Inject private CharacterModule characterModule;
    @Inject private ApplicationConfig applicationConfig;
    @Inject private PathManager pathManager;
    @Inject private GameManager gameManager;
    @Inject private Game game;

    @OnGameUpdate
    public void onGameUpdate() {
//        fixCharacterPosition();

        characterModule.getAll().forEach(character -> {
            character.setDirection(MovableModel.Direction.NONE);
            doMove(character);
        });
    }

    public CharacterMoveStatus move(CharacterModel character, JobModel job) {
        PathModel path = Optional.ofNullable(job.getStatusForCharacter(character)).map(jobStatus -> jobStatus.path).orElse(null);

        // Check that path going to job's accepted parcels
        if (path != null && !job.getAcceptedParcels().contains(path.getLastParcel())) {
            Log.warning("Job containing outdated path");
            path = pathManager.getPath(character.getParcel(), job.getAcceptedParcels());
            job.getStatusForCharacter(character).path = path;
        }

        if (path != null) {

            // Character is already moving on this path
            if (character.getPath() == path) {
                return CharacterMoveStatus.CONTINUE;
            }

            // Last move on this path
            if (job.getAcceptedParcels().contains(character.getParcel())) {
                return CharacterMoveStatus.COMPLETED;
            }

            // First move on this path
            character.setPath(path);
            path.setMoveSpeed(job.getMoveSpeed());
            return CharacterMoveStatus.CONTINUE;
        }

        throw new GameException(CharacterMoveModule.class, "Try to move character for job but path didn't exists in JobCharacterStatus");
    }

    private void doMove(CharacterModel character) {

        Optional.ofNullable(character.getPath()).ifPresent(path -> {

            // Character is sleeping
            if (character.isSleeping()) {
                Log.debug("Character #" + character.getId() + ": sleeping . move canceled");
                return;
            }

            // Increase move progress
            character.setMoveStep(applicationConfig.game.characterSpeed / game.getTickPerHour());
//            _moveStep = 1 * getExtra(CharacterStatsExtra.class).speed * (_job != null ? _job.getSpeedModifier() : 1);

            // Character has reach next parcel
            if (character._moveProgress >= 1) {
                moveToNextParcel(character, path);
            }

            character._moveProgress += character._moveStep * path.getMoveSpeed();
            character._moveProgress2 += character._moveStep * path.getMoveSpeed();

            character.position._moveProgress = character._moveProgress;
            character.position._moveProgress2 = character._moveProgress2;

            character.position.parcelX = character.getParcel().x;
            character.position.parcelY = character.getParcel().y;
            character.position.parcelZ = character.getParcel().z;

            Log.debug(getName() + " Move progress = " + character._moveProgress + ", " + character._moveProgress2);
        });
    }

    private void moveToNextParcel(CharacterModel character, PathModel _path) {

        character.setMoveProgress(0);

        // If path.next() return true, the move is continuing
        if (_path.next()) {

            // Set direction to the next parcel
            Parcel fromParcel = character._parcel;
            Parcel toParcel = _path.getCurrentParcel();
            character._direction = getDirection(fromParcel.x, fromParcel.y, toParcel.x, toParcel.y);

            // Set logic position of the character to the next parcel
            character.setParcel(_path.getCurrentParcel());

            // TODO: no idea of the purpose of pathLength
            character.position.pathLength = _path.getLength();
        }

        // When path.next() return false, the move is completed
        else {
            character._path = null;
            character._parcel = _path.getLastParcel();

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
        return characterModule.getAll().stream()
                .anyMatch(c -> c != character
                        && WorldHelper.getApproxDistance(character.getParcel(), c.getParcel()) < 4);
    }

    public boolean hasCharacterOnParcel(Parcel parcel) {
        return characterModule.getAll().stream().anyMatch(c -> c.getParcel() == parcel);
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
