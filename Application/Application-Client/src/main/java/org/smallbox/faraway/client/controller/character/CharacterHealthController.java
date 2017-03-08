package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIImage;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

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

    @BindLua private UIList listDiseases;

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

        displayDiseases(character);
    }

    private void displayDiseases(CharacterModel character) {
        listDiseases.clear();
        listDiseases.addView(UILabel.create(null).setText("gg").setTextColor(new Color(0xB4D4D3)).setTextSize(14).setSize(0, 20));
        listDiseases.addView(UILabel.create(null).setText("gg").setTextColor(new Color(0xB4D4D3)).setTextSize(14).setSize(0, 20));
        listDiseases.addView(UILabel.create(null).setText("gg").setTextColor(new Color(0xB4D4D3)).setTextSize(14).setSize(0, 20));
    }

    private void displayNeed(UILabel label, UIImage gauge, String text, double value) {
        if (value > 80) {
            label.setTextColor(0xb3d035).setDashedString(text, String.valueOf((int) Math.floor(value)), 21);
            gauge.setTextureRect(0, 80, (int) (Math.floor(value * 170 / 100 / 10) * 10), 16);
        } else if (value > 50) {
            label.setTextColor(0xfff54f).setDashedString(text, String.valueOf((int) Math.floor(value)), 21);
            gauge.setTextureRect(0, 32, (int) (Math.floor(value * 170 / 100 / 10) * 10), 16);
        } else {
            label.setTextColor(0xf73939).setDashedString(text, String.valueOf((int) Math.floor(value)), 21);
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

    @Override
    protected void onNewGameUpdate(Game game) {

    }
}
