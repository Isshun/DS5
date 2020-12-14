package org.smallbox.faraway.core.game;

import com.google.gson.Gson;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameScenario;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.plant.PlantModule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by Alex on 14/07/2017.
 */
public class GameFactory {
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

    public void setScenario(String scenarioPath) {
        if (scenarioPath != null) {
            try {
                setScenario(new Gson().fromJson(new FileReader(new File(scenarioPath)), GameScenario.class));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void create() {
        Application.gameManager.createGame(this.planet, this.region, this.width, this.height, this.level, new GameManager.GameListener() {
            @Override
            public void onGameCreate(Game game) {

                if (scenario != null) {

                    if (scenario.characters != null) {
                        scenario.characters.forEach(characterEntity -> Application.moduleManager.getModule(CharacterModule.class).addRandom());
                    }

                    if (scenario.consumables != null) {
                        scenario.consumables.forEach(c -> Application.moduleManager.getModule(ConsumableModule.class).addConsumable(c.name, c.quantity, c.x, c.y, c.z));
                    }

                    if (scenario.items != null) {
                        scenario.items.forEach(i -> Application.moduleManager.getModule(ItemModule.class).addItem(i.name, true, i.x, i.y, i.z));
                    }

                    Application.moduleManager.getModule(PlantModule.class).addPlant("base.plant.carrot", 10, 10, 1);
                    Application.moduleManager.getModule(PlantModule.class).addPlant("base.plant.carrot", 11, 10, 1);
                    Application.moduleManager.getModule(PlantModule.class).addPlant("base.plant.carrot", 12, 10, 1);

                }

            }

            @Override
            public void onGameUpdate(Game game) {

            }
        });
    }
}
