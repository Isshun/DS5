package org.smallbox.faraway.module.character.controller;

import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.game.module.character.controller.LuaController;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.ui.engine.views.widgets.UIImage;
import org.smallbox.faraway.ui.engine.views.widgets.UILabel;

/**
 * Created by Alex on 26/04/2016.
 */
public class CharacterStatusController extends LuaController {
    @BindLua private UILabel        lbJob;
    @BindLua private UILabel        lbJobDetail;

    @BindLua private UILabel        lbNeedFood;
    @BindLua private UIImage        gaugeFood;

    @BindLua private UILabel        lbNeedDrink;
    @BindLua private UIImage        gaugeDrink;

    @BindLua private UILabel        lbNeedEnergy;
    @BindLua private UIImage        gaugeEnergy;

    @BindLua private UILabel        lbNeedHappiness;
    @BindLua private UIImage        gaugeHappiness;

    @BindLua private UILabel        lbNeedHealth;
    @BindLua private UIImage        gaugeHealth;

    @BindLua private UILabel        lbNeedJoy;
    @BindLua private UIImage        gaugeJoy;

    @BindLua private UILabel        lbNeedRelation;
    @BindLua private UIImage        gaugeRelation;

    @BindLua private UILabel        lbNeedOxygen;
    @BindLua private UIImage        gaugeOxygen;

    @Override
    protected void onCreate() {
    }

    public void selectCharacter(CharacterModel character) {
        if (character.getJob() != null) {
            lbJob.setText(character.getJob().getLabel());
            lbJobDetail.setText(character.getJob().getMessage());
        }

        displayNeed(lbNeedFood, gaugeFood, "Food", character.getNeeds().get("food"));
        displayNeed(lbNeedDrink, gaugeDrink, "Drink", character.getNeeds().get("drink"));
        displayNeed(lbNeedEnergy, gaugeEnergy, "Energy", character.getNeeds().get("energy"));
        displayNeed(lbNeedHappiness, gaugeHappiness, "Happiness", character.getNeeds().get("happiness"));
        displayNeed(lbNeedHealth, gaugeHealth, "Health", character.getNeeds().get("health"));
        displayNeed(lbNeedJoy, gaugeJoy, "Joy", character.getNeeds().get("entertainment"));
        displayNeed(lbNeedRelation, gaugeRelation, "Relation", character.getNeeds().get("relation"));
        displayNeed(lbNeedOxygen, gaugeOxygen, "Oxygen", character.getNeeds().get("oxygen"));
    }

    private void displayNeed(UILabel label, UIImage gauge, String text, double value) {
        label.setDashedString(text, String.valueOf((int)Math.floor(value)), 21);
        gauge.setTextureRect(0, 80, (int)(Math.floor(value * 170 / 100 / 10) * 10), 16);
    }
}
