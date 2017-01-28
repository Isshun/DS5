package org.smallbox.faraway.module.mainPanel.controller;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIImage;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;

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

    private CharacterModel _selected;

    @Override
    public void onGameUpdate(Game game) {
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
        }
    }
}
