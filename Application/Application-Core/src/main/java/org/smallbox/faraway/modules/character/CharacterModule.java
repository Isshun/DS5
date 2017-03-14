package org.smallbox.faraway.modules.character;

import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.module.ModuleSerializer;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.HumanModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.character.model.base.CharacterName;
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

@ModuleSerializer(CharacterModuleSerializer.class)
//@ModuleRenderer(CharacterRenderer.class)
public class CharacterModule extends GameModule<CharacterModuleObserver> {

//    @BindModule
//    private WorldInteractionModule worldInteractionModule;

    @BindComponent
    private Data data;

    @BindModule
    private JobModule jobModule;

    @BindModule
    private ConsumableModule consumableModule;

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
    public void onGameStart(Game game) {
//        jobModule.addPriorityCheck(new CheckCharacterEnergyCritical());
//        jobModule.addPriorityCheck(new CheckCharacterWaterWarning());
//        jobModule.addPriorityCheck(new CheckCharacterFoodWarning());
//        jobModule.addPriorityCheck(new CheckCharacterEnergyWarning());
//        jobModule.addSleepCheck(new CheckCharacterTimetableSleep());
//        jobModule.addSleepCheck(new CheckJoySleep(itemModule));
    }

    public void addVisitor() {
        _visitors.add(new HumanModel(Utils.getUUID(), WorldHelper.getRandomFreeSpace(WorldHelper.getGroundFloor(), true, true), "plop", "plop", 0));
    }

    @Override
    public void onModuleUpdate(Game game) {

        fixCharacterPosition();

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
        _characters.forEach(character -> {
            character.setDirection(MovableModel.Direction.NONE);
            character.action();
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

        if (CollectionUtils.isEmpty(character.getBuffs())) {
            character.getBuffs().clear();
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
        c.getExtra(CharacterPersonalsExtra.class).setName(Strings.LB_DECEADED);
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
        CharacterModel character = new HumanModel(Utils.getUUID(), parcel, "rand", "rand", 16);
        add(character);
        return character;
    }

    public CharacterModel addRandom() {
        CharacterModel character = new HumanModel(Utils.getUUID(), WorldHelper.getRandomFreeSpace(WorldHelper.getGroundFloor(), true, true), "rand", "rand", 16);
        add(character);
        return character;
    }

    public CharacterModel addRandom(Class<? extends CharacterModel> cls) {
        try {
            Constructor<? extends CharacterModel> constructor = cls.getConstructor(int.class, ParcelModel.class, String.class, String.class, double.class);
            CharacterModel character = constructor.newInstance(
                    Utils.getUUID(),
                    WorldHelper.getRandomFreeSpace(WorldHelper.getGroundFloor(), true, true),
                    CharacterName.getFirstname(CharacterPersonalsExtra.Gender.MALE),
                    CharacterName.getLastName(),
                    16);
            add(character);
            return character;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
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

}
