package org.smallbox.faraway.client.controller.character;

import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.*;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterGameLayerInit;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.character.model.base.CharacterNeedsExtra;
import org.smallbox.faraway.modules.character.model.base.NeedEntry;
import org.smallbox.faraway.modules.characterBuff.BuffType;
import org.smallbox.faraway.modules.characterBuff.CharacterBuffModule;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.storage.StoreJob;
import org.smallbox.faraway.util.CollectionUtils;
import org.smallbox.faraway.util.Utils;

import java.util.List;

@GameObject
public class CharacterInfoStatusController extends LuaController {

    @Inject
    private SpriteManager spriteManager;

    @BindLua private UILabel lbJob;
    @BindLua private UILabel lbJobProgress;
    @BindLua private UIFrame frameJob;
    @BindLua private UIImage imgJob;
    @BindLua private UIGrid gridNeeds;

    private CharacterModel _selected;
    private List<UIFrame> needs;

    @OnInit
    public void init() {
        needs = List.of(
                createNeedFrame(CharacterNeedsExtra.TAG_ENERGY),
                createNeedFrame(CharacterNeedsExtra.TAG_OXYGEN),
                createNeedFrame(CharacterNeedsExtra.TAG_FOOD),
                createNeedFrame(CharacterNeedsExtra.TAG_DRINK),
                createNeedFrame(CharacterNeedsExtra.TAG_RELATION),
                createNeedFrame(CharacterNeedsExtra.TAG_ENTERTAINMENT),
                createNeedFrame(CharacterNeedsExtra.TAG_HAPPINESS)
        );
    }

    private UIFrame createNeedFrame(String key) {
        UIFrame frame = new UIFrame(null);
        frame.setId(key);

        UILabel lbName = new UILabel(null);
        lbName.setId("lb_name");
        lbName.setText(key);
        lbName.setTextSize(14);
        frame.addView(lbName);

        UILabel lbValue = new UILabel(null);
        lbValue.setId("lb_value");
        lbValue.setTextSize(14);
        lbValue.setPositionX(114);
        frame.addView(lbValue);

        UIImage imageGauge = new UIImage(null);
        imageGauge.setId("img_gauge");
        imageGauge.setPositionY(16);
        imageGauge.setImage("[base]/graphics/needbar.png");
        frame.addView(imageGauge);

        return frame;
    }

    @AfterGameLayerInit
    public void initUI() {
        needs.forEach(frame -> gridNeeds.addView(frame));
    }

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
    }

    private void displayNeeds(CharacterModel character) {
        if (character.hasExtra(CharacterNeedsExtra.class)) {
            CharacterNeedsExtra needsExtra = character.getExtra(CharacterNeedsExtra.class);
            needs.forEach(frame -> displayNeed(frame, needsExtra.get(frame.getId())));
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

//            if (job instanceof BasicHaulJob) {
//                ((BasicHaulJob) job).getConsumables().forEach((consumable, quantity) -> {
//                    if (CollectionUtils.isNotEmpty(consumable.getInfo().graphics)) {
//                        imgJob.setVisible(true);
//                        imgJob.setImage(spriteManager.getNewSprite(consumable.getInfo().graphics.get(0)));
//                    }
//                });
//            }

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

    private void displayNeed(UIFrame frame, NeedEntry entry) {
        UILabel lbName = (UILabel) frame.findById("lb_name");
        UILabel lbValue = (UILabel) frame.findById("lb_value");
        UIImage gauge = (UIImage) frame.findById("img_gauge");

        lbName.setText(frame.getId());
        lbValue.setText(StringUtils.leftPad(String.valueOf((int)Math.round(entry.value() * 100)), 3) + "%");

        // Display optimal
        if (entry.value() > entry.warning) {
            gauge.setTextureRect(0, 80, Utils.round(entry.value() * 160, 10), 8);
            lbName.setTextColor(0xbbff31ff);
            lbValue.setTextColor(0xbbff31ff);
        }

        // Display warning
        else if (entry.value() > entry.critical) {
            gauge.setTextureRect(0, 32, Utils.round(entry.value() * 160, 10), 8);
            lbName.setTextColor(0xbbfeb6ff);
            lbValue.setTextColor(0xbbfeb6ff);
        }

        // Display critical
        else {
            gauge.setTextureRect(0, 48, Math.max(10, Utils.round(entry.value() * 160, 10)), 8);
            lbName.setTextColor(0xff3131ff);
            lbValue.setTextColor(0xff3131ff);
        }
    }

}
