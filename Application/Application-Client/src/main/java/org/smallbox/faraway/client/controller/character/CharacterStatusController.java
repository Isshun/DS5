package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIImage;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ReceiptGroupInfo;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.consumable.BasicHaulJob;
import org.smallbox.faraway.modules.consumable.BasicStoreJob;
import org.smallbox.faraway.modules.item.factory.BasicCraftJob;
import org.smallbox.faraway.util.CollectionUtils;

/**
 * Created by Alex on 26/04/2016.
 */
public class CharacterStatusController extends LuaController {
    @BindLua private UILabel        lbJob;
    @BindLua private UILabel        lbJobDetail;
    @BindLua private UILabel        lbJobFrom;
    @BindLua private UILabel        lbJobTo;
    @BindLua private UILabel        lbJobProgress;
    @BindLua private UIImage        imgJobProgress;
    @BindLua private UIImage        imgJob;
    @BindLua private UIImage        imgJobOut;

    private CharacterModel _selected;

    @Override
    public void onNewGameUpdate(Game game) {
        if (isVisible() && _selected != null) {
            selectCharacter(_selected);
        }
    }

    public void selectCharacter(CharacterModel character) {
        _selected = character;

        JobModel job = character.getJob();
        if (job != null) {
            lbJob.setText(job.getLabel());
            lbJobDetail.setText(job.getMessage());
            lbJobFrom.setText(String.valueOf(job.getStartTime()));
            lbJobTo.setText(String.valueOf(job.getEndTime()));
//            lbJobFrom.setText(Utils.getTimeStr(job.getStartTime()));
//            lbJobTo.setText(Utils.getTimeStr(job.getEndTime()));
            lbJobProgress.setText(String.valueOf(job.getProgress()));
            imgJobProgress.setTextureRect(0, 80, (int) (Math.floor(job.getProgress() / 10) * 10), 16);

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
                        imgJobOut.setImage(ApplicationClient.spriteManager.getNewSprite(receiptInfo.outputs.get(0).item.graphics.get(0)));
                    }
                }
            }
        }
    }
}
