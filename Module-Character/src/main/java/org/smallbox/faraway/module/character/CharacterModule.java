package org.smallbox.faraway.module.character;

import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.CollectionUtils;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.BindLuaController;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.game.module.character.model.HumanModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Strings;
import org.smallbox.faraway.core.util.Utils;
import org.smallbox.faraway.module.character.controller.CharacterController;
import org.smallbox.faraway.module.character.job.*;
import org.smallbox.faraway.module.item.ItemModule;
import org.smallbox.faraway.module.job.JobModule;
import org.smallbox.faraway.module.world.WorldInteractionModule;
import org.smallbox.faraway.module.world.WorldInteractionModuleObserver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CharacterModule extends GameModule<CharacterModuleObserver> {
    @BindLuaController
    private CharacterController _controller;

    @BindModule("")
    private WorldInteractionModule _worldInteraction;

    @BindModule("")
    private JobModule _jobs;

    @BindModule("")
    private ItemModule _items;

    private BlockingQueue<CharacterModel>       _characters = new LinkedBlockingQueue<>();
    private List<CharacterModel>                _addOnUpdate = new ArrayList<>();
    private int                                 _count;
    private List<CharacterModel>                _visitors = new ArrayList<>();

    @Override
    public boolean isModuleMandatory() {
        return true;
    }

    public Collection<CharacterModel>     getCharacters() { return _characters; }
    public Collection<CharacterModel>     getVisitors() { return _visitors; }
    public int                            getCount() { return _count; }

    @Override
    protected void onGameCreate(Game game) {
        game.addRender(new CharacterRenderer(this));
        game.addSerializer(new CharacterModuleSerializer(this));

        _worldInteraction.addObserver(new WorldInteractionModuleObserver() {
            @Override
            public void onSelect(Collection<ParcelModel> parcels) {
                _characters.stream()
                        .filter(character -> parcels.contains(character.getParcel()))
                        .forEach(character -> notifyObservers(obs -> obs.onSelectCharacter(character)));
            }
        });
    }

    @Override
    protected void onGameStart(Game game) {
        _jobs.addPriorityCheck(new CheckCharacterEnergyCritical());
        _jobs.addPriorityCheck(new CheckCharacterWaterWarning());
        _jobs.addPriorityCheck(new CheckCharacterFoodWarning());
        _jobs.addPriorityCheck(new CheckCharacterEnergyWarning());
        _jobs.addSleepCheck(new CheckCharacterTimetableSleep());
        _jobs.addSleepCheck(new CheckJoySleep(_items));
    }

    @Override
    public boolean onKey(GameEventListener.Key key) {
        if (key == GameEventListener.Key.TAB) {
            select(getNext(_controller.getSelected()));
            return true;
        }
        return false;
    }

    private CharacterModel getNext(CharacterModel currentCharacter) {
        if (CollectionUtils.isNotEmpty(_characters)) {
            Iterator<CharacterModel> iterator = _characters.iterator();
            while (iterator.hasNext()) {
                if (iterator.next() == currentCharacter) {
                    break;
                }
            }
            if (iterator.hasNext()) {
                return iterator.next();
            } else {
                return _characters.iterator().next();
            }
        }
        return null;
    }

    public void addVisitor() {
        _visitors.add(new HumanModel(Utils.getUUID(), WorldHelper.getRandomFreeSpace(WorldHelper.getGroundFloor(), true, true), "plop", "plop", 0));
    }

    @Override
    public void onGameUpdate(Game game, int tick) {
        // TODO
//        // Add new born
//        _characters.addAll(_addOnUpdate);
//        _addOnUpdate.clear();
//

        // Remove dead characters
        _characters.stream()
                .filter(character -> !character.isAlive())
                .forEach(character -> {
                    // Cancel job
                    if (character.getJob() != null) {
                        _jobs.quitJob(character.getJob(), JobModel.JobAbortReason.DIED);
                    }

                    if (CollectionUtils.isEmpty(character.getBuffs())) {
                        character.getBuffs().clear();
                    }
                });

        _characters.stream()
                .filter(CharacterModel::isAlive)
                .forEach(character -> {
                    if (tick % 10 == character.getLag()) {
                        // Assign job
                        if (character.getJob() == null && !character.isSleeping()) {
                            _jobs.assign(character);
                        }

                        // Update characters (buffs, stats)
                        character.update();
                    }

                    // Update needs
                    character.getNeeds().update();

                    character.setDirection(MovableModel.Direction.NONE);
                    character.action();
                    character.move();
                    character.fixPosition();
                });

        if (tick % 10 == 0) {
            _characters.forEach(CharacterModel::longUpdate);
        }
    }

    // TODO: heavy
    public CharacterModel getCharacterAtPos(int x, int y, int z) {
        printDebug("getCharacterAtPos: " + x + "x" + y);

        for (CharacterModel c: _characters) {
            if (c.getParcel().equals(x, y, z)) {
                printDebug("getCharacterAtPos: found");
                return c;
            }
        }

        return null;
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
        for (CharacterModel c: _characters) {
            if (c.getId() == characterId) {
                return c;
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

    public int countCharacterAtPos(int posX, int posY) {
        int count = 0;
        for (CharacterModel character: _characters) {
            if (character.getParcel().x == posX && character.getParcel().y == posY) {
                count++;
            }
        }
        return count;
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

    public void select(CharacterModel character) {
        notifyObservers(obs -> obs.onSelectCharacter(character));
    }
}
