package org.smallbox.faraway.module.character;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.client.ModuleRenderer;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.module.ModuleSerializer;
import org.smallbox.faraway.core.module.character.model.HumanModel;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.module.character.job.*;
import org.smallbox.faraway.module.item.ItemModule;
import org.smallbox.faraway.module.job.JobModule;
import org.smallbox.faraway.module.world.WorldInteractionModule;
import org.smallbox.faraway.module.world.WorldInteractionModuleObserver;
import org.smallbox.faraway.util.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@ModuleSerializer(CharacterModuleSerializer.class)
@ModuleRenderer(CharacterRenderer.class)
public class CharacterModule extends GameModule<CharacterModuleObserver> {

    @BindModule
    private WorldInteractionModule worldInteractionModule;

    @BindModule
    private JobModule jobModule;

    @BindModule
    private ItemModule itemModule;

    private BlockingQueue<CharacterModel>       _characters = new LinkedBlockingQueue<>();
    private List<CharacterModel>                _addOnUpdate = new ArrayList<>();
    private List<CharacterModel>                _visitors = new ArrayList<>();
    private int                                 _count;

    @Override
    public boolean isModuleMandatory() {
        return true;
    }

    public void addCharacter(CharacterModel character) { _characters.add(character); }
    public Collection<CharacterModel>     getCharacters() { return _characters; }
    public Collection<CharacterModel>     getVisitors() { return _visitors; }
    public int                            getCount() { return _count; }

    @Override
    public void onGameCreate(Game game) {
        worldInteractionModule.addObserver(new WorldInteractionModuleObserver() {
            @Override
            public void onSelect(GameEvent event, Collection<ParcelModel> parcels) {
                _characters.stream()
                        .filter(character -> parcels.contains(character.getParcel()))
                        .forEach(character -> notifyObservers(obs -> obs.onSelectCharacter(event, character)));
            }
        });
    }

    @Override
    public void onGameStart(Game game) {
        jobModule.addPriorityCheck(new CheckCharacterEnergyCritical());
        jobModule.addPriorityCheck(new CheckCharacterWaterWarning());
        jobModule.addPriorityCheck(new CheckCharacterFoodWarning());
        jobModule.addPriorityCheck(new CheckCharacterEnergyWarning());
        jobModule.addSleepCheck(new CheckCharacterTimetableSleep());
        jobModule.addSleepCheck(new CheckJoySleep(itemModule));
    }

    public void addVisitor() {
        _visitors.add(new HumanModel(Utils.getUUID(), WorldHelper.getRandomFreeSpace(WorldHelper.getGroundFloor(), true, true), "plop", "plop", 0));
    }

    @Override
    public void onGameUpdate(Game game, int tick) {
        // Add new born
        if (CollectionUtils.isNotEmpty(_addOnUpdate)) {
            Log.info("Add new character");
            _characters.addAll(_addOnUpdate);
            _addOnUpdate.clear();
        }

        // Remove dead characters
        _characters.stream().filter(CharacterModel::isDead).forEach(this::updateDeadCharacter);
        _characters.removeIf(CharacterModel::isDead);

//            _characters.forEach(this::updateNeeds);
        _characters.forEach(this::updateJobs);
        _characters.forEach(this::updateBuffs);
        _characters.forEach(this::updatePosition);
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

        if (CollectionUtils.isEmpty(character.getBuffs())) {
            character.getBuffs().clear();
        }
    }

    /**
     *
     * @param character CharacterModel
     */
    private void updateJobs(CharacterModel character) {
        // Assign job
        if (character.getJob() == null) {
            jobModule.assign(character);
        }
    }

    /**
     *
     * @param character CharacterModel
     */
    private void updateNeeds(CharacterModel character) {
        // Update needs
        character.getNeeds().update();
    }

    /**
     *
     * @param character CharacterModel
     */
    private void updateBuffs(CharacterModel character) {
        // Update characters (buffs, stats)
        character.update();
    }

    /**
     *
     * @param character CharacterModel
     */
    private void updatePosition(CharacterModel character) {
        character.setDirection(MovableModel.Direction.NONE);
        character.action();
        character.move();
        character.fixPosition();
    }

    public CharacterModel add(CharacterModel character) {
        _count++;
        _addOnUpdate.add(character);

        notifyObservers(observer -> observer.onAddCharacter(character));

        return character;
    }

    public void remove(CharacterModel c) {
        c.setIsDead();
        c.getPersonals().setName(Strings.LB_DECEADED);
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

    public CharacterModel addRandom(int x, int y, int z) {
        CharacterModel character = new HumanModel(Utils.getUUID(), WorldHelper.getParcel(x, y, z), null, null, 16);
        add(character);
        return character;
    }

    public CharacterModel addRandom(ParcelModel parcel) {
        CharacterModel character = new HumanModel(Utils.getUUID(), parcel, null, null, 16);
        add(character);
        return character;
    }

    public void addRandom(Class<? extends CharacterModel> cls) {
        try {
            Constructor<? extends CharacterModel> constructor = cls.getConstructor(int.class, int.class, int.class, String.class, String.class, double.class);
            CharacterModel character = constructor.newInstance(Utils.getUUID(), 10, 10, null, null, 16);
            add(character);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
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

    public void select(GameEvent event, CharacterModel character) {
        notifyObservers(obs -> obs.onSelectCharacter(event, character));
    }
}
