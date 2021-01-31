package org.smallbox.faraway.client.controller.character;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.asset.SpriteManager;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.client.ui.widgets.*;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterGameLayerInit;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameLayerInit;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.character.model.base.CharacterNeedsExtra;
import org.smallbox.faraway.game.character.model.base.NeedEntry;
import org.smallbox.faraway.game.characterBuff.CharacterBuffModule;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.storage.StoreJob;
import org.smallbox.faraway.util.Utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.smallbox.faraway.game.character.model.base.CharacterNeedsExtra.*;

@GameObject
public class CharacterInfoStatusController extends LuaController {
    private static final int GAUGE_WIDTH = 180;

    @Inject private SpriteManager spriteManager;
    @Inject private CharacterBuffModule buffModule;

    @BindLua private UILabel lbJob;
    @BindLua private UIImage imgJob;
    @BindLua private UIGrid gridNeeds;
    @BindLua private UIList listBuffs;
    @BindLua private View gaugeProgress;

    private CharacterModel _selected;
    private List<UIFrame> needs;

    @OnGameLayerInit
    public void init() {
        needs = Stream
                .of(TAG_ENERGY, TAG_OXYGEN, TAG_FOOD, TAG_DRINK, TAG_RELATION, TAG_ENTERTAINMENT, TAG_HAPPINESS)
                .map(key -> {
                    UIFrame frame = gridNeeds.createFromTemplate(UIFrame.class);
                    frame.setId(key);
                    return frame;
                })
                .collect(Collectors.toList());
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
        displayBuffs(character);
    }

    private void displayBuffs(CharacterModel character) {
        buffModule.getBuffs(character).forEach(buff -> {
            UILabel label = listBuffs.createFromTemplate(UILabel.class);
            label.setText(buff.getName());
            listBuffs.addNextView(label);
        });
        listBuffs.switchViews();
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
            lbJob.setText(job.getMainLabel());

            if (job.getProgress() > 0) {
                gaugeProgress.setSize((int) (367 * job.getProgress()), 24);
            }

//            if (job instanceof BasicHaulJob) {
//                ((BasicHaulJob) job).getConsumables().forEach((consumable, quantity) -> {
//                    if (CollectionUtils.isNotEmpty(consumable.getInfo().graphics)) {
//                        imgJob.setVisible(true);
//                        imgJob.setImage(spriteManager.getNewSprite(consumable.getInfo().graphics.get(0)));
//                    }
//                });
//            }

            if (job instanceof StoreJob) {
                if (CollectionUtils.isNotEmpty(((StoreJob) job).sourceConsumable.getInfo().graphics)) {
                    imgJob.setVisible(true);
                    imgJob.setImage(spriteManager.getNewSprite(((StoreJob) job).sourceConsumable.getInfo().graphics.get(0)));
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
            lbJob.setText("Idle");
//            frameJob.setVisible(false);
//            imgJob.setVisible(false);
        }
    }

    private void displayNeed(UIFrame frame, NeedEntry entry) {
        UILabel lbName = (UILabel) frame.find("lb_name");
        UILabel lbValue = (UILabel) frame.find("lb_value");
        View gauge = frame.find("img_gauge");

        lbName.setText(frame.getId());
        lbValue.setText(StringUtils.leftPad(String.valueOf((int) Math.round(entry.value() * 100)), 3) + "%");
        gauge.setSize(Utils.round(entry.value() * GAUGE_WIDTH, 10), 12);

        // Display optimal
        if (entry.value() > entry.warning) {
//            gauge.setTextureRect(0, 96, Utils.round(entry.value() * GAUGE_WIDTH, 10), 8);
            lbName.setTextColor(0xffffffdd);
            lbValue.setTextColor(0xffffffdd);
        }

        // Display warning
        else if (entry.value() > entry.critical) {
//            gauge.setTextureRect(0, 32, Utils.round(entry.value() * GAUGE_WIDTH, 10), 8);
            lbName.setTextColor(0xbbfeb6ff);
            lbValue.setTextColor(0xbbfeb6ff);
        }

        // Display critical
        else {
//            gauge.setTextureRect(0, 48, Math.max(10, Utils.round(entry.value() * GAUGE_WIDTH, 10)), 8);
            lbName.setTextColor(0xff3131ff);
            lbValue.setTextColor(0xff3131ff);
        }
    }

}
