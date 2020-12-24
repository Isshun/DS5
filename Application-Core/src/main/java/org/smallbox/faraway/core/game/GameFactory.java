package org.smallbox.faraway.core.game;

import com.google.gson.Gson;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.GameScenario;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.plant.PlantModule;
import org.smallbox.faraway.modules.world.WorldModule;

import java.io.FileNotFoundException;
import java.io.FileReader;

@ApplicationObject
public class GameFactory {

    @Inject
    private GameManager gameManager;

    @Inject
    private CharacterModule characterModule;

    @Inject
    private ConsumableModule consumableModule;

    @Inject
    private ItemModule itemModule;

    @Inject
    private PlantModule plantModule;

    @Inject
    private WorldModule worldModule;

    @Inject
    private Data data;

    private GameScenario scenario;
    private String planet = "base.planet.corrin";
    private String region = "mountain";
    private int width = 20;
    private int height = 20;
    private int level = 2;

    public void setScenario(GameScenario scenario) {
        this.scenario = scenario;
        this.planet = scenario.planet != null ? scenario.planet : this.planet;
        this.region = scenario.region != null ? scenario.region : this.region;
        this.width = scenario.width != 0 ? scenario.width : this.width;
        this.height = scenario.height != 0 ? scenario.height : this.height;
        this.level = scenario.level != 0 ? scenario.level : this.level;
    }

    private GameScenario loadScenario(String scenarioPath) {
        if (scenarioPath != null) {
            try {
                return new Gson().fromJson(new FileReader(scenarioPath), GameScenario.class);
            } catch (FileNotFoundException ignored) {
            }
        }
        throw new GameException(GameFactory.class, "Unable to load scenario");
    }

    public void create(String scenarioPath) {
        setScenario(loadScenario(scenarioPath));

        gameManager.createGame(GameInfo.create(this.planet, this.region, this.width, this.height, this.level), new GameManager.GameListener() {
            @Override
            public void onGameCreate(Game game) {

                if (GameFactory.this.scenario != null) {
                    if (GameFactory.this.scenario.characters != null) {
                        GameFactory.this.scenario.characters.forEach(characterEntity -> characterModule.addRandom());
                    }

                    if (GameFactory.this.scenario.consumables != null) {
                        GameFactory.this.scenario.consumables.forEach(c -> consumableModule.addConsumable(c.name, c.quantity, c.x, c.y, c.z));
                    }

                    if (GameFactory.this.scenario.items != null) {
                        GameFactory.this.scenario.items.forEach(i -> itemModule.addItem(i.name, true, i.x, i.y, i.z));
                    }

                    if (GameFactory.this.scenario.plants != null) {
                        GameFactory.this.scenario.plants.forEach(i -> plantModule.addPlant(i.name, i.x, i.y, i.z));
                    }

                    if (GameFactory.this.scenario.resources != null) {
                        GameFactory.this.scenario.resources.forEach(i -> worldModule.getParcel(i.x, i.y, i.z).setRockInfo(data.getItemInfo("base.granite")));
                    }

                }

            }

            @Override
            public void onGameUpdate(Game game) {

            }
        });
    }
}
