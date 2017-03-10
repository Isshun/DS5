package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIImage;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ReceiptGroupInfo;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.consumable.BasicHaulJob;
import org.smallbox.faraway.modules.consumable.BasicStoreJob;
import org.smallbox.faraway.modules.itemFactory.BasicCraftJob;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.util.CollectionUtils;

/**
 * Created by Alex on 26/04/2016.
 */
public class CharacterInfoStatusController extends LuaController {

    @BindLua private UILabel lbJob;
    @BindLua private UIImage imgJob;

    @BindLua private UILabel lbNeedFood;
    @BindLua private UIImage gaugeFood;

    @BindLua private UILabel lbNeedDrink;
    @BindLua private UIImage gaugeDrink;

    @BindLua private UILabel lbNeedEnergy;
    @BindLua private UIImage gaugeEnergy;

    @BindLua private UILabel lbNeedJoy;
    @BindLua private UIImage gaugeJoy;

    @BindLua private UILabel lbNeedRelation;
    @BindLua private UIImage gaugeRelation;

    @BindLua private UILabel lbNeedOxygen;
    @BindLua private UIImage gaugeOxygen;

    @BindLua private UIList listBuffs;

    private CharacterModel _selected;

    @Override
    public void onNewGameUpdate(Game game) {
        if (isVisible() && _selected != null) {
            selectCharacter(_selected);
        }
    }

    public void selectCharacter(CharacterModel character) {
        _selected = character;

        displayJob(character);

        displayNeed(lbNeedFood,     gaugeFood,      "Food",     character.getNeeds().get("food"));
        displayNeed(lbNeedDrink,    gaugeDrink,     "Drink",    character.getNeeds().get("drink"));
        displayNeed(lbNeedEnergy,   gaugeEnergy,    "Energy",   character.getNeeds().get("energy"));
        displayNeed(lbNeedJoy,      gaugeJoy,       "Joy",      character.getNeeds().get("entertainment"));
        displayNeed(lbNeedRelation, gaugeRelation,  "Relation", character.getNeeds().get("relation"));
        displayNeed(lbNeedOxygen,   gaugeOxygen,    "Oxygen",   character.getNeeds().get("oxygen"));

        displayBuffs(character);
    }

    private void displayJob(CharacterModel character) {
        JobModel job = character.getJob();
        if (job != null) {
            lbJob.setText(job.getMainLabel());

            if (job instanceof BasicHaulJob) {
                ((BasicHaulJob) job).getConsumables().forEach((consumable, quantity) -> {
                    if (CollectionUtils.isNotEmpty(consumable.getInfo().graphics)) {
                        imgJob.setImage(ApplicationClient.spriteManager.getNewSprite(consumable.getInfo().graphics.get(0)));
                    }
                });
            }

            if (job instanceof BasicStoreJob) {
                ((BasicStoreJob) job).getConsumables().forEach((consumable, quantity) -> {
                    if (CollectionUtils.isNotEmpty(consumable.getInfo().graphics)) {
                        imgJob.setImage(ApplicationClient.spriteManager.getNewSprite(consumable.getInfo().graphics.get(0)));
                    }
                });
            }

            if (job instanceof BasicCraftJob) {
                ReceiptGroupInfo.ReceiptInfo receiptInfo = ((BasicCraftJob) job).getReceiptInfo();
                if (receiptInfo != null) {
                    if (CollectionUtils.isNotEmpty(receiptInfo.inputs)) {
                        imgJob.setImage(ApplicationClient.spriteManager.getNewSprite(receiptInfo.inputs.get(0).item.graphics.get(0)));
                    }
                    if (CollectionUtils.isNotEmpty(receiptInfo.outputs)) {
//                        imgJobOut.setImage(ApplicationClient.spriteManager.getNewSprite(receiptInfo.outputs.get(0).item.graphics.get(0)));
                    }
                }
            }
        }
    }

    private void displayNeed(UILabel label, UIImage gauge, String text, double value) {
        if (value > 80) {
            label.setTextColor(0xb3d035).setDashedString(text, String.valueOf((int) Math.floor(value)), 21);
            gauge.setTextureRect(0, 80, (int) (Math.floor(value * 170 / 100 / 10) * 10), 8);
        } else if (value > 50) {
            label.setTextColor(0xfff54f).setDashedString(text, String.valueOf((int) Math.floor(value)), 21);
            gauge.setTextureRect(0, 32, (int) (Math.floor(value * 170 / 100 / 10) * 10), 8);
        } else {
            label.setTextColor(0xf73939).setDashedString(text, String.valueOf((int) Math.floor(value)), 21);
            gauge.setTextureRect(0, 48, Math.max(10, (int) (Math.floor(value * 170 / 100 / 10) * 10)), 8);
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
