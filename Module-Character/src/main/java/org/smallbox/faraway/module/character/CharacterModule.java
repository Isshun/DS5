package org.smallbox.faraway.module.character;

import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.renderer.CharacterRenderer;
import org.smallbox.faraway.core.game.BindLuaController;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.module.character.model.HumanModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Strings;
import org.smallbox.faraway.core.util.Utils;
import org.smallbox.faraway.module.character.controller.CharacterController;
import org.smallbox.faraway.module.world.WorldInteractionModule;
import org.smallbox.faraway.module.world.WorldInteractionModuleObserver;
import org.smallbox.faraway.module.world.WorldModule;
import org.smallbox.faraway.module.world.WorldModuleObserver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CharacterModule extends GameModule<CharacterModuleObserver> {
    @BindLuaController
    private CharacterController _controller;

    @BindModule("")
    private WorldInteractionModule _worldInteraction;

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
    public int                             getCount() { return _count; }

    @Override
    protected void onGameCreate(Game game) {
        game.getRenders().add(new CharacterRenderer());
        getSerializers().add(new CharacterModuleSerializer(this));

        _worldInteraction.addObserver(new WorldInteractionModuleObserver() {
            @Override
            public void onSelect(Collection<ParcelModel> parcels) {
                _characters.stream()
                        .filter(character -> parcels.contains(character.getParcel()))
                        .forEach(character -> notifyObservers(obs -> obs.onSelectCharacter(character)));
            }
        });
    }

    // TODO
    public CharacterModel getNext(CharacterModel character) {
        for (CharacterModel c: _characters) {
            if (c == character) {
                return c;
            }
        }

        return null;
    }

    @Override
    public boolean onSelectParcel(ParcelModel parcel) {
        for (CharacterModel character: _characters) {
            if (character.getParcel() == parcel) {
                _controller.selectCharacter(character);
                return true;
            }
        }
        return false;
    }

    public void addVisitor() {
        _visitors.add(new HumanModel(Utils.getUUID(), WorldHelper.getRandomFreeSpace(WorldHelper.getGroundFloor(), true, true), "plop", "plop", 0));
    }

    @Override
    public void onGameStart(Game game) {
    }

    @Override
    public void onGameUpdate(Game game, int tick) {
        // TODO
//        // Add new born
//        _characters.addAll(_addOnUpdate);
//        _addOnUpdate.clear();
//
//        for (CharacterModel c: _characters) {
//            // Check if characters is dead
//            if (!c.isAlive()) {
//
//                // Cancel job
//                if (c.getJob() != null) {
//                    ModuleHelper.getJobModule().quitJob(c.getJob(), JobAbortReason.DIED);
//                }
//
//                if (!c.getBuffs().isEmpty()) {
//                    c.getBuffs().clear();
//                }
//
////                characterToRemove = c;
//            }
//
//            else {
//                if (tick % 10 == c.getLag()) {
//                    // Assign job
//                    if (c.getJob() == null && !c.isSleeping()) {
//                        ModuleHelper.getJobModule().assign(c);
//                    }
//
//                    // Update characters (buffs, stats)
//                    c.update();
//                }
//
//                // Update needs
//                c.getNeeds().update();
//
//                c.setDirection(Direction.NONE);
//                c.action();
//                c.move();
//                c.fixPosition();
//            }
//        }
//
//        if (tick % 10 == 0) {
//            for (CharacterModel c: _characters) {
//                c.longUpdate();
//            }
//        }
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
}
