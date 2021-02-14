package org.smallbox.faraway.core.game;

import com.google.gson.Gson;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.util.GameException;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.save.GameInfoFactory;
import org.smallbox.faraway.game.character.CharacterModule;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.consumable.ConsumableModule;
import org.smallbox.faraway.game.item.ItemModule;
import org.smallbox.faraway.game.plant.PlantModule;
import org.smallbox.faraway.game.world.WorldModule;

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

    private GameScenario loadScenario(String scenarioPath) {
        try {
            return new Gson().fromJson(new FileReader(scenarioPath), GameScenario.class);
        } catch (Exception e) {
            throw new GameException(GameFactory.class, e, "Unable to load scenario");
        }
    }

    public void create(String scenarioPath) {
        GameScenario scenario = loadScenario(scenarioPath);

        gameManager.newGame(gameInfoFactory.create(scenario), () -> {
            Optional.ofNullable(scenario.characters).ifPresent(characters -> characters.forEach(characterEntity -> {
                CharacterModel character = characterModule.addRandom();
                character.setParcel(worldModule.getParcel(characterEntity.x, characterEntity.y, characterEntity.z));
                viewport.centerOnMap(characterEntity.x, characterEntity.y);
            }));
            Optional.ofNullable(scenario.consumables).ifPresent(consumables -> consumables.forEach(c -> consumableModule.addConsumable(c.name, c.quantity, c.x, c.y, c.z, c.stack)));
            Optional.ofNullable(scenario.items).ifPresent(items -> items.forEach(i -> itemModule.addItem(i.name, true, i.x, i.y, i.z)));
            Optional.ofNullable(scenario.plants).ifPresent(plants -> plants.forEach(i -> plantModule.addPlant(i.name, i.x, i.y, i.z)));
            Optional.ofNullable(scenario.resources).ifPresent(resources -> resources.forEach(i -> worldModule.getParcel(i.x, i.y, i.z).setRockInfo(dataManager.getItemInfo("base.granite"))));
            if (scenario.centerOnMap != null && scenario.centerOnMap.length == 2) {
                viewport.centerOnMap(scenario.centerOnMap[0], scenario.centerOnMap[1]);
            }
        });
    }
}
