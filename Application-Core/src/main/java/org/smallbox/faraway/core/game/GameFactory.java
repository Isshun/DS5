package org.smallbox.faraway.core.game;

import com.google.gson.Gson;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.GameScenario;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.save.GameInfoFactory;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.plant.PlantModule;
import org.smallbox.faraway.modules.world.WorldModule;

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
    @Inject private Data data;

    private GameScenario loadScenario(String scenarioPath) {
        try {
            return new Gson().fromJson(new FileReader(scenarioPath), GameScenario.class);
        } catch (Exception e) {
            throw new GameException(GameFactory.class, e, "Unable to load scenario");
        }
    }

    public void create(String scenarioPath) {
        GameScenario scenario = loadScenario(scenarioPath);

        gameManager.createGame(gameInfoFactory.create(scenario), new GameManager.GameListener() {
            @Override
            public void onGameCreate(Game game) {
                Optional.ofNullable(scenario.characters).ifPresent(characters -> characters.forEach(characterEntity -> {
                    CharacterModel character = characterModule.addRandom();
                    character.setParcel(worldModule.getParcel(characterEntity.x, characterEntity.y, characterEntity.z));
                    viewport.centerOnMap(characterEntity.x, characterEntity.y);
                }));
                Optional.ofNullable(scenario.consumables).ifPresent(consumables -> consumables.forEach(c -> consumableModule.addConsumable(c.name, c.quantity, c.x, c.y, c.z, c.stack)));
                Optional.ofNullable(scenario.items).ifPresent(items -> items.forEach(i -> itemModule.addItem(i.name, true, i.x, i.y, i.z)));
                Optional.ofNullable(scenario.plants).ifPresent(plants -> plants.forEach(i -> plantModule.addPlant(i.name, i.x, i.y, i.z)));
                Optional.ofNullable(scenario.resources).ifPresent(resources -> resources.forEach(i -> worldModule.getParcel(i.x, i.y, i.z).setRockInfo(data.getItemInfo("base.granite"))));
                viewport.centerOnMap(7, 12);
            }

            @Override
            public void onGameUpdate(Game game) {
            }
        });
    }
}
