package org.smallbox.faraway.game.character;

import org.apache.commons.collections4.CollectionUtils;
import org.smallbox.faraway.GameTaskManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.SuperGameModule;
import org.smallbox.faraway.game.character.model.CharacterInfoAnnotation;
import org.smallbox.faraway.game.character.model.CharacterInventoryExtra;
import org.smallbox.faraway.game.character.model.HumanModel;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.character.model.base.CharacterPersonalsExtra;
import org.smallbox.faraway.game.consumable.ConsumableModule;
import org.smallbox.faraway.game.item.ItemModule;
import org.smallbox.faraway.game.job.JobModule;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.game.world.WorldModule;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.GameException;
import org.smallbox.faraway.util.UUIDUtils;
import org.smallbox.faraway.util.log.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

@GameObject
public class CharacterModule extends SuperGameModule<CharacterModel, CharacterModuleObserver> {
    @Inject private GameTaskManager gameTaskManager;
    @Inject private JobModule jobModule;
    @Inject private ConsumableModule consumableModule;
    @Inject private ItemModule itemModule;
    @Inject private WorldModule worldModule;
    @Inject private DataManager dataManager;

    private final List<CharacterModel> addOnUpdate = new ArrayList<>();

    @OnInit
    private void init() {
        modelList = new LinkedBlockingQueue<>();
    }

    @Override
    public void onModuleUpdate(Game game) {

//        modelList.stream()
//                .filter(CharacterModel::isFree)
//                .forEach(character -> gameTaskManager.startTask(new RandomMoveTask(character)));

        fixCharacterInventory();

        // Add new born
        if (CollectionUtils.isNotEmpty(addOnUpdate)) {
            Log.info("Add new character");
            modelList.addAll(addOnUpdate);
            addOnUpdate.clear();
        }

        // Remove dead characters
        modelList.stream().filter(CharacterModel::isDead).forEach(this::updateDeadCharacter);
        modelList.removeIf(CharacterModel::isDead);

    }

    /**
     * Cancel all actions for dead character
     *
     * @param character CharacterModel
     */
    private void updateDeadCharacter(CharacterModel character) {
        if (character.getJob() != null) {
            jobModule.quitJob(character.getJob());
        }
    }

    @Override
    public void add(CharacterModel character) {
        addOnUpdate.add(character);

        notifyObservers(observer -> observer.onAddCharacter(character));
    }

    public void remove(CharacterModel c) {
        c.setIsDead();
        if (c.hasExtra(CharacterPersonalsExtra.class)) {
            c.getExtra(CharacterPersonalsExtra.class).setName("dead");
        }
    }

    public CharacterModel getCharacter(Parcel parcel) {
        for (CharacterModel character : modelList) {
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
            Constructor<? extends CharacterModel> constructor = cls.getConstructor(int.class, CharacterInfo.class, Parcel.class);
            CharacterInfo characterInfo = dataManager.characters.get(cls.getAnnotation(CharacterInfoAnnotation.class).value());
            CharacterModel character = constructor.newInstance(
                    UUIDUtils.getUUID(),
                    characterInfo,
                    WorldHelper.getRandomFreeSpace(WorldHelper.getGroundFloor(), true, true));
            add(character);
            return character;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new GameException(CharacterModule.class, "Unable to create character with class: " + cls.getSimpleName());
        }
    }

    @Override
    public int getModulePriority() {
        return Constant.MODULE_CHARACTER_PRIORITY;
    }

    private void fixCharacterInventory() {
        modelList.stream()
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
