package org.smallbox.faraway.modules.character;

import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.game.modelInfo.CharacterInfo;
import org.smallbox.faraway.core.module.ModuleSerializer;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.CharacterInfoAnnotation;
import org.smallbox.faraway.modules.character.model.CharacterInventoryExtra;
import org.smallbox.faraway.modules.character.model.HumanModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.character.model.base.CharacterPersonalsExtra;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.util.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@GameObject
@ModuleSerializer(CharacterModuleSerializer.class)
//@ModuleLayer(CharacterLayer.class)
public class CharacterModule extends GameModule<CharacterModuleObserver> {

//    @BindComponent
//    private WorldInteractionModule worldInteractionModule;

    @BindComponent
    private Data data;

    @BindComponent
    private JobModule jobModule;

    @BindComponent
    private ConsumableModule consumableModule;

    @BindComponent
    private ItemModule itemModule;

    private BlockingQueue<CharacterModel>       _characters = new LinkedBlockingQueue<>();
    private List<CharacterModel>                _addOnUpdate = new ArrayList<>();
    private List<CharacterModel>                _visitors = new ArrayList<>();
    private int                                 _count;

    public void addCharacter(CharacterModel character) { _characters.add(character); }
    public Collection<CharacterModel>     getCharacters() { return _characters; }
    public Collection<CharacterModel>     getVisitors() { return _visitors; }
    public int                            getCount() { return _count; }

    @Override
    public void onModuleUpdate(Game game) {

        fixCharacterPosition();
        fixCharacterInventory();

        // Add new born
        if (CollectionUtils.isNotEmpty(_addOnUpdate)) {
            Log.info("Add new character");
            _characters.addAll(_addOnUpdate);
            _addOnUpdate.clear();
        }

        // Remove dead characters
        _characters.stream().filter(CharacterModel::isDead).forEach(this::updateDeadCharacter);
        _characters.removeIf(CharacterModel::isDead);

        // Execute action
        double hourInterval = getTickInterval() / game.getTickPerHour();
        _characters.forEach(character -> {
            character.setDirection(MovableModel.Direction.NONE);
            character.action(hourInterval);
            character.move();
        });
    }

    private void fixCharacterPosition() {
        _characters.stream()
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

    /**
     * Cancel all actions for dead character
     *
     * @param character CharacterModel
     */
    private void updateDeadCharacter(CharacterModel character) {
        // Cancel job
        if (character.getJob() != null) {
            jobModule.quitJob(character.getJob(), JobModel.JobAbortReason.DIED);
        }
    }

    public CharacterModel add(CharacterModel character) {
        _count++;
        _addOnUpdate.add(character);

        notifyObservers(observer -> observer.onAddCharacter(character));

        return character;
    }

    public void remove(CharacterModel c) {
        c.setIsDead();
        if (c.hasExtra(CharacterPersonalsExtra.class)) {
            c.getExtra(CharacterPersonalsExtra.class).setName(Strings.LB_DECEADED);
        }
    }

    public CharacterModel getCharacter(int characterId) {
        for (CharacterModel character: _characters) {
            if (character.getId() == characterId) {
                return character;
            }
        }
        return null;
    }

    public CharacterModel getCharacter(ParcelModel parcel) {
        for (CharacterModel character: _characters) {
            if (character.getParcel() == parcel) {
                return character;
            }
        }
        return null;
    }

    public CharacterModel addRandom() {
        return addRandom(HumanModel.class);
    }

    public CharacterModel addRandom(Class<? extends CharacterModel> cls) {
        try {
            Constructor<? extends CharacterModel> constructor = cls.getConstructor(int.class, CharacterInfo.class, ParcelModel.class);
            CharacterInfo characterInfo = data.characters.get(cls.getAnnotation(CharacterInfoAnnotation.class).value());
            CharacterModel character = constructor.newInstance(
                    Utils.getUUID(),
                    characterInfo,
                    WorldHelper.getRandomFreeSpace(WorldHelper.getGroundFloor(), true, true));
            add(character);
            return character;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new GameException(CharacterModule.class, "Unable to create character with class: " + cls.getSimpleName());
        }
    }

    public boolean havePeopleOnProximity(CharacterModel character) {
        for (CharacterModel c: _characters) {
            if (c != character && WorldHelper.getApproxDistance(character.getParcel(), c.getParcel()) < 4) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getModulePriority() {
        return Constant.MODULE_CHARACTER_PRIORITY;
    }

    public boolean hasCharacterOnParcel(ParcelModel parcel) {
        for (CharacterModel character: _characters) {
            if (character.getParcel() == parcel) {
                return true;
            }
        }
        return false;
    }

    private void fixCharacterInventory() {
        _characters.stream()
                .filter(character -> character.hasExtra(CharacterInventoryExtra.class))
                .filter(character -> character.getJob() == null && !character.getExtra(CharacterInventoryExtra.class).getAll().isEmpty())
                .forEach(character -> {
                    Log.warning(getName() + " have item in inventory without job");
                    character.getExtra(CharacterInventoryExtra.class).getAll().forEach((itemInfo, quantity) ->
                            consumableModule.addConsumable(itemInfo, quantity, character.getParcel()));
                    character.getExtra(CharacterInventoryExtra.class).getAll().clear();
                });
    }

}
