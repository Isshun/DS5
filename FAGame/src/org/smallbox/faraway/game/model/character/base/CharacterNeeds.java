package org.smallbox.faraway.game.model.character.base;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.RoomManager;
import org.smallbox.faraway.game.manager.TemperatureManager;
import org.smallbox.faraway.game.model.CharacterTypeInfo;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.ItemInfo.ItemInfoAction;
import org.smallbox.faraway.game.model.item.MapObjectModel;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;

public class CharacterNeeds {
    private final CharacterModel 	_character;
    private final GameData      	_data;

    // Actions
    public boolean isSleeping;
    private int	_eating;

    // Stats
    public double 	socialize;
    public double 	drinking;
    public double 	food;
    public double 	happiness;
    public double   happinessChange;
    public double 	relation;
    public double 	security;
    public double 	oxygen;
    public double 	energy;
    public double 	health;
    public double 	sickness;
    public double 	injuries;
    public double 	satiety;
    public double 	joy;
    public double 	heat;
    public int 		environment;
    public int 		light;
    public int 		pain;

    private MapObjectModel 	_sleepItem;
    private CharacterStats	_stats;
    private boolean isFainting;

    public CharacterNeeds(CharacterModel character, CharacterStats stats) {
        _data = GameData.getData();
        _character = character;
        _stats = stats;
        _sleepItem = null;
        food = (int) (Constant.CHARACTER_INIT_FOOD + (Math.random() * 100) % 40 - 20);
        oxygen = (int) (Constant.CHARACTER_INIT_OXYGEN + (Math.random() * 100) % 20 - 10);
        happiness = (Constant.CHARACTER_INIT_HAPPINESS + (Math.random() * 100) % 20 - 10);
        health = (float) (Constant.CHARACTER_INIT_HEALTH + (Math.random() * 100) % 20 - 10);
        energy = (int) (Constant.CHARACTER_INIT_ENERGY + (Math.random() * 100) % 100);
        heat = character.getType().needs.heat.optimal;
        energy = 100;
        relation = 0;
        security = 0;
        injuries = 0;
        sickness = 0;
        satiety = 0;
        joy = 0;
    }

    public int	getFood() { return (int)Math.ceil(food); }
    public int	getEnergy() { return (int)Math.ceil(energy); }

    public boolean	isSleeping() { return isSleeping; }

    public void	update() {
        updateNeeds(_character.getType().needs);

        // Check peoples on proximity
        if (Game.getCharacterManager().havePeopleOnProximity(_character)) {
            this.relation += 1;
        } else {
            this.relation -= 0.25;
        }

        happiness += happinessChange / 100;

        if (energy >= 100) {
            isSleeping = false;
        }

        // Set needs bounds
        food = Math.max(0, Math.min(100, food));
        energy = Math.max(0, Math.min(100, energy));
        oxygen = Math.max(0, Math.min(100, oxygen));
        happiness = Math.max(0, Math.min(100, happiness));
        relation = Math.max(0, Math.min(100, relation));
        security = Math.max(0, Math.min(100, security));
        joy = Math.max(0, Math.min(100, joy));
    }

    public void updateNeeds(CharacterTypeInfo.Needs needs) {
        if (needs.energy != null) {
            energy += isSleeping ? needs.energy.change.sleep : needs.energy.change.wake;
        }

        if (needs.food != null) {
            food += isSleeping ? needs.food.change.sleep : needs.food.change.wake;
        }

        if (needs.joy != null) {
            joy += isSleeping ? isSleeping && _sleepItem == null ? needs.joy.change.sleepOnFloor : needs.joy.change.sleep : needs.joy.change.wake;
        }

        // Oxygen
        if (_character.getParcel() != null) {
            int oxygenLevel = (int)(_character.getParcel().getOxygen() * 100);
            if (oxygen < oxygenLevel || oxygen > oxygenLevel + 1) {
                // Increase oxygen
                if (oxygen < oxygenLevel) {
                    oxygen += 1;
                }
                // Decrease oxygen, use resist
                else {
                    oxygen -= 1 * (1 - _stats.resist.oxygen / 100f);
                }
            }
        }

        // Body heat
        double temperature = _character.getParcel().getRoom() != null ? _character.getParcel().getRoom().getTemperatureInfo().temperature :
                ((TemperatureManager)Game.getInstance().getManager(TemperatureManager.class)).getTemperature();
        double heatDifference = temperature - (this.heat - _character.getType().thermolysis);
        System.out.println("heatDifference BB: " + heatDifference);

        if (heatDifference < 0) {
            heatDifference = Math.min(0, heatDifference + _stats.buff.heat);
        } else if (heatDifference > 0) {
            heatDifference = Math.max(0, heatDifference - _stats.buff.cold);
        }

        System.out.println("heatDifference AB: " + heatDifference);

        if (heatDifference < 0) {
            this.heat += heatDifference * (1 - _stats.resist.cold / 100f) / 100f;
        } else if (heatDifference > 0) {
            this.heat += heatDifference * (1 - _stats.resist.heat / 100f) / 100f;
        } else {
            if (this.heat > _character.getType().needs.heat.optimal + 1) {
                this.heat -= 1 / 100f;
            } else if (this.heat < _character.getType().needs.heat.optimal - 1) {
                this.heat += 1 / 100f;
            } else {
                this.heat = _character.getType().needs.heat.optimal;
            }
        }

        System.out.println("bodyHeat: " + this.heat);
    }

    private void updateNeeds(ItemInfoAction action) {
        food += action.effects.food / (double)action.cost;
        energy += action.effects.energy / (double)action.cost;
        oxygen += action.effects.oxygen / (double)action.cost;
        happiness += action.effects.happiness / (double)action.cost;
        relation += action.effects.relation / (double)action.cost;
        security += action.effects.security / (double)action.cost;
    }

    public void addRelation(int i) {
        relation = Math.min(relation + 1, 100);
    }

    public void use(MapObjectModel item, ItemInfoAction action) {
        if (item.isSleepingItem()) {
            _sleepItem = item;
            isSleeping = true;
        } else {
            _sleepItem = null;
        }

        if (action != null && action.effects != null) {
            energy = Math.min(energy + (double)action.effects.energy / action.cost, 100);
            food = Math.min(food + (double)action.effects.food / action.cost, 100);
            happiness = Math.min(happiness + (double)action.effects.happiness / action.cost, 100);
            health = Math.min(health + (double)action.effects.health / action.cost, 100);
            relation = Math.min(relation + (double)action.effects.relation / action.cost, 100);
            joy = Math.min(joy + (double)action.effects.joy / action.cost, 100);
        }
    }

    public void setSleeping(boolean isSleeping) {
        this.isSleeping = isSleeping;
    }
}
