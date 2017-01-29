package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.ui.engine.views.widgets.UIImage;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;

/**
 * Created by Alex on 11/12/2016.
 */
public class CharacterHealthController extends LuaController {

    @BindLua private UILabel lbNeedFood;
    @BindLua private UIImage gaugeFood;

    @BindLua private UILabel lbNeedDrink;
    @BindLua private UIImage gaugeDrink;

    @BindLua private UILabel lbNeedEnergy;
    @BindLua private UIImage gaugeEnergy;

    @BindLua private UILabel lbNeedHappiness;
    @BindLua private UIImage gaugeHappiness;

    @BindLua private UILabel lbNeedHealth;
    @BindLua private UIImage gaugeHealth;

    @BindLua private UILabel lbNeedJoy;
    @BindLua private UIImage gaugeJoy;

    @BindLua private UILabel lbNeedRelation;
    @BindLua private UIImage gaugeRelation;

    @BindLua private UILabel lbNeedOxygen;
    @BindLua private UIImage gaugeOxygen;

    @BindLua private UIList listBuffs;

    private CharacterModel _selected;

    public void selectCharacter(CharacterModel character) {
        _selected = character;

        displayNeed(lbNeedFood, gaugeFood, "Food", character.getNeeds().get("food"));
        displayNeed(lbNeedDrink, gaugeDrink, "Drink", character.getNeeds().get("drink"));
        displayNeed(lbNeedEnergy, gaugeEnergy, "Energy", character.getNeeds().get("energy"));
        displayNeed(lbNeedHappiness, gaugeHappiness, "Happiness", character.getNeeds().get("happiness"));
        displayNeed(lbNeedHealth, gaugeHealth, "Health", character.getNeeds().get("health"));
        displayNeed(lbNeedJoy, gaugeJoy, "Joy", character.getNeeds().get("entertainment"));
        displayNeed(lbNeedRelation, gaugeRelation, "Relation", character.getNeeds().get("relation"));
        displayNeed(lbNeedOxygen, gaugeOxygen, "Oxygen", character.getNeeds().get("oxygen"));

        displayBuffs(character);
    }

    private void displayNeed(UILabel label, UIImage gauge, String text, double value) {
        if (value > 80) {
            label.setTextColor(Color.GREEN).setDashedString(text, String.valueOf((int) Math.floor(value)), 21);
            gauge.setTextureRect(0, 80, (int) (Math.floor(value * 170 / 100 / 10) * 10), 16);
        } else if (value > 50) {
            label.setTextColor(Color.YELLOW).setDashedString(text, String.valueOf((int) Math.floor(value)), 21);
            gauge.setTextureRect(0, 32, (int) (Math.floor(value * 170 / 100 / 10) * 10), 16);
        } else {
            label.setTextColor(Color.RED).setDashedString(text, String.valueOf((int) Math.floor(value)), 21);
            gauge.setTextureRect(0, 48, (int) (Math.floor(value * 170 / 100 / 10) * 10), 16);
        }
    }

    private void displayBuffs(CharacterModel character) {
        listBuffs.clear();
        character.getChecks().forEach(check -> listBuffs.addView(UILabel
                .create(null)
                .setText(check.getLabel())
                .setSize(300, 22)));
    }
}