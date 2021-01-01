package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIFrame;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIImage;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.character.model.base.CharacterNeedsExtra;
import org.smallbox.faraway.modules.character.model.base.NeedEntry;
import org.smallbox.faraway.modules.characterBuff.CharacterBuffModule;
import org.smallbox.faraway.modules.consumable.BasicHaulJob;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.storage.StoreJob;
import org.smallbox.faraway.util.CollectionUtils;
import org.smallbox.faraway.util.Utils;

@GameObject
public class CharacterInfoStatusController extends LuaController {

    @Inject
    private CharacterBuffModule buffModule;

    @Inject
    private SpriteManager spriteManager;

    @BindLua private UILabel lbJob;
    @BindLua private UILabel lbJobProgress;
    @BindLua private UIFrame frameJob;
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

    @BindLua private UIList listBuffs;

    private CharacterModel _selected;

    @Override
    public void onControllerUpdate() {
        if (isVisible() && _selected != null) {
            selectCharacter(_selected);
        }
    }

    public void selectCharacter(CharacterModel character) {
        _selected = character;

        displayJob(character);
        displayNeeds(character);
        displayBuffs(character);
    }

    private void displayNeeds(CharacterModel character) {
        if (character.hasExtra(CharacterNeedsExtra.class)) {
            CharacterNeedsExtra needs = character.getExtra(CharacterNeedsExtra.class);
            displayNeed(lbNeedFood,     gaugeFood,      "Food",         needs.get(CharacterNeedsExtra.TAG_FOOD));
            displayNeed(lbNeedDrink,    gaugeDrink,     "Drink",        needs.get(CharacterNeedsExtra.TAG_DRINK));
            displayNeed(lbNeedEnergy,   gaugeEnergy,    "Energy",       needs.get(CharacterNeedsExtra.TAG_ENERGY));
            displayNeed(lbNeedJoy,      gaugeJoy,       "Entertainment",needs.get(CharacterNeedsExtra.TAG_ENTERTAINMENT));
            displayNeed(lbNeedRelation, gaugeRelation,  "Relation",     needs.get(CharacterNeedsExtra.TAG_RELATION));
        }
    }

    private void displayJob(CharacterModel character) {
        JobModel job = character.getJob();
        if (job != null) {
            lbJob.setVisible(true);
            lbJob.setText(job.getMainLabel());

            if (job.getProgress() > 0) {
                lbJobProgress.setText(String.format("%3d%%", (int) (job.getProgress() * 100)));
            }

            frameJob.setVisible(true);
            frameJob.setWidth((int) (job.getProgress() * 300));

            if (job instanceof BasicHaulJob) {
                ((BasicHaulJob) job).getConsumables().forEach((consumable, quantity) -> {
                    if (CollectionUtils.isNotEmpty(consumable.getInfo().graphics)) {
                        imgJob.setVisible(true);
                        imgJob.setImage(spriteManager.getNewSprite(consumable.getInfo().graphics.get(0)));
                    }
                });
            }

            if (job instanceof StoreJob) {
                if (CollectionUtils.isNotEmpty(((StoreJob) job).targetConsumable.getInfo().graphics)) {
                    imgJob.setVisible(true);
                    imgJob.setImage(spriteManager.getNewSprite(((StoreJob) job).targetConsumable.getInfo().graphics.get(0)));
                }
            }

//            if (job instanceof BasicCraftJob) {
//                ReceiptGroupInfo.ReceiptInfo receiptInfo = ((BasicCraftJob) job).getReceiptInfo();
//                if (receiptInfo != null) {
//                    if (CollectionUtils.isNotEmpty(receiptInfo.inputs)) {
//                        imgJob.setVisible(true);
//                        imgJob.setImage(spriteManager.getNewSprite(receiptInfo.inputs.get(0).item.graphics.get(0)));
//                    }
//                    if (CollectionUtils.isNotEmpty(receiptInfo.outputs)) {
////                        imgJobOut.setImage(ApplicationClient.spriteManager.getNewSprite(receiptInfo.outputs.get(0).item.graphics.get(0)));
//                    }
//                }
//            }
        } else {
            lbJob.setVisible(false);
            frameJob.setVisible(false);
            imgJob.setVisible(false);
        }
    }

    private void displayNeed(UILabel label, UIImage gauge, String text, NeedEntry entry) {

        // Display optimal
        if (entry.value() > entry.warning) {
//            label.setTextColor(0xbbd3ff07).setDashedString(text, String.valueOf((int) Math.floor(entry.value() * 100)), 21);
            gauge.setTextureRect(0, 80, Utils.round(entry.value() * 170, 10), 8);
        }

        // Display warning
        else if (entry.value() > entry.critical) {
//            label.setTextColor(0xbbfeb60b).setDashedString(text, String.valueOf((int) Math.floor(entry.value() * 100)), 21);
            gauge.setTextureRect(0, 32, Utils.round(entry.value() * 170, 10), 8);
        }

        // Display critical
        else {
//            label.setTextColor(0xbbff3131).setDashedString(text, String.valueOf((int) Math.floor(entry.value() * 100)), 21);
            gauge.setTextureRect(0, 48, Math.max(10, Utils.round(entry.value() * 170, 10)), 8);
        }
    }

    private void displayBuffs(CharacterModel character) {
        buffModule.getBuffs(character)
                .stream()
                .sorted((o1, o2) -> o2.mood - o1.mood)
                .forEach(buff ->
                        listBuffs.addNextView(UILabel
                                .create(null)
                                .setText("[" + (buff.mood > 0 ? "+" : "") + buff.mood  + "] " + buff.message)
                                .setTextSize(14)
                                .setTextColor(buff.mood > 0 ? 0x33bb88ff : 0xbb5555ff)
                                .setSize(300, 22)));

        listBuffs.switchViews();
    }

}
