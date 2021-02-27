package org.smallbox.faraway.core.game;

import com.google.gson.Gson;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.dependencyInjector.DependencyNotifier;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnApplicationNewGame;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameStart;
import org.smallbox.faraway.core.save.GameFileManager;
import org.smallbox.faraway.core.save.GameInfo;
import org.smallbox.faraway.core.save.GameInfoFactory;
import org.smallbox.faraway.game.character.CharacterModule;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.consumable.ConsumableModule;
import org.smallbox.faraway.game.item.ItemModule;
import org.smallbox.faraway.game.plant.PlantModule;
import org.smallbox.faraway.game.world.WorldModule;
import org.smallbox.faraway.game.world.factory.WorldFactory;
import org.smallbox.faraway.util.GameException;
import org.smallbox.faraway.util.log.Log;

import java.io.FileReader;
import java.util.Optional;

@ApplicationObject
public class GameFactory {
    @Inject private GameInfoFactory gameInfoFactory;
    @Inject private GameManager gameManager;
    @Inject private CharacterModule characterModule;
    @Inject private ConsumableModule consumableModule;
    @Inject private ItemModule itemModule;
    @Inject private PlantModule plantModule;
    @Inject private WorldModule worldModule;
    @Inject private Viewport viewport;
    @Inject private DataManager dataManager;
    @Inject private WorldFactory worldFactory;
    @Inject private DependencyNotifier dependencyNotifier;
    @Inject private GameFileManager gameFileManager;

    public void create(String scenarioPath) {
        try {
            GameScenario scenario = new Gson().fromJson(new FileReader(scenarioPath), GameScenario.class);
            GameInfo gameInfo = gameInfoFactory.create(scenario);
            gameFileManager.createSaveDirectory(gameInfo);
            gameManager.newGame(gameInfo, scenario);
        } catch (Exception e) {
            throw new GameException(GameFactory.class, e, "Unable to load scenario");
        }
    }

    @OnApplicationNewGame
    public void onNewGame(GameScenario scenario) {
        long time = System.currentTimeMillis();

        worldFactory.buildMap();
        applyScenario(scenario);

        dependencyNotifier.notify(OnGameStart.class);

        Log.info(GameFactory.class, "New game (" + (System.currentTimeMillis() - time) + "ms)");
    }

    private void applyScenario(GameScenario scenario) {
        Optional.ofNullable(scenario.characters).ifPresent(characters -> characters.forEach(characterEntity -> {
            CharacterModel character = characterModule.addRandom();
            character.setParcel(worldModule.getParcel(characterEntity.x, characterEntity.y, characterEntity.z));
            viewport.centerOnMap(characterEntity.x, characterEntity.y);
        }));

        Optional.ofNullable(scenario.consumables).ifPresent(consumables -> consumables.forEach(c -> consumableModule.addConsumable(c.name, c.quantity, c.x, c.y, c.z, c.stack)));
        Optional.ofNullable(scenario.items).ifPresent(items -> items.forEach(i -> itemModule.addItem(i.name, true, i.x, i.y, i.z)));
        Optional.ofNullable(scenario.plants).ifPresent(plants -> plants.forEach(p -> plantModule.addPlant(p.name, p.x, p.y, p.z)));
        Optional.ofNullable(scenario.resources).ifPresent(resources -> resources.forEach(r -> worldModule.getParcel(r.x, r.y, r.z).setRockInfo(dataManager.getItemInfo("base.granite"))));

        if (scenario.centerOnMap != null && scenario.centerOnMap.length == 2) {
            viewport.centerOnMap(scenario.centerOnMap[0], scenario.centerOnMap[1]);
        }
    }

}
