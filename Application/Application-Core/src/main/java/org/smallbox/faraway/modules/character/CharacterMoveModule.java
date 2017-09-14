package org.smallbox.faraway.modules.character;

import org.smallbox.faraway.common.GameModule;
import org.smallbox.faraway.common.dependencyInjector.BindComponent;
import org.smallbox.faraway.common.dependencyInjector.GameObject;
import org.smallbox.faraway.common.util.Constant;
import org.smallbox.faraway.common.util.Log;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.game.model.MoveListener;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.PathModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

@GameObject
public class CharacterMoveModule extends GameModule<CharacterModuleObserver> {

    @BindComponent
    private CharacterModule characterModule;

//    private Map<CharacterModel, PathModel> paths = new ConcurrentHashMap<>();

    @Override
    public int getModulePriority() {
        return Constant.MODULE_CHARACTER_PRIORITY;
    }

    @Override
    public void onModuleUpdate() {
        fixCharacterPosition();

        characterModule.getCharacters().forEach(character -> {
            character.setDirection(MovableModel.Direction.NONE);
            move(character);
        });
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
            character.setMoveStep(Application.config.game.characterSpeed / Application.gameManager.getGame().getTickPerHour());
//            _moveStep = 1 * getExtra(CharacterStatsExtra.class).speed * (_job != null ? _job.getSpeedModifier() : 1);

            // Character has reach next parcel
            if (character._moveProgress >= 1 && _path.getCurrentParcel() != null) {
                character.setMoveProgress(0);

                // Move continue, set next parcel + direction
                if (_path.next()) {
                    int fromX = character._parcel.x;
                    int fromY = character._parcel.y;
                    int toX = _path.getCurrentParcel().x;
                    int toY = _path.getCurrentParcel().y;
                    character._direction = getDirection(fromX, fromY, toX, toY);

                    character.setParcel(_path.getCurrentParcel());

//                    character.position.myCatmull = _path.myCatmull;
                    character.position.pathLength = _path.getLength();
//                    character.position._curve = _path._curve;
                }

                // Move state, set path to null and call listener
                else {
                    Log.info(getName() + " Move state (" + _path.getFirstParcel().x + "x" + _path.getFirstParcel().y + "x" + _path.getFirstParcel().z + " to " + _path.getLastParcel().x + "x" + _path.getLastParcel().y + "x" + _path.getLastParcel().z + ")");
                    _path = null;

                    if (character._moveListener != null) {
                        Log.info(getName() + " Move state: call onReach");
                        MoveListener listener = character._moveListener;
                        character._moveListener = null;
                        listener.onReach(character);
                    }

                    character.position.pathLength = 0;
                }
            }

            character._moveProgress += character._moveStep;
            character._moveProgress2 += character._moveStep;

            character.position._moveProgress = character._moveProgress;
            character.position._moveProgress2 = character._moveProgress2;

            character.position.parcelX = character.getParcel().x;
            character.position.parcelY = character.getParcel().y;
            character.position.parcelZ = character.getParcel().z;

            Application.gameServer.write(character.position);
        }
    }

    private void fixCharacterPosition() {
        characterModule.getCharacters().stream()
                .filter(character -> character.getParcel() != null && !character.getParcel().isWalkable())
                .forEach(character -> {
                    Log.warning(getName() + " is stuck !");
                    character.setParcel(WorldHelper.getNearestWalkable(character.getParcel(), 1, 20));
                    if (character.getJob() != null) {
                        character.getJob().quit(character);
                        character.clearJob(character.getJob());
                    }
                });
    }

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
