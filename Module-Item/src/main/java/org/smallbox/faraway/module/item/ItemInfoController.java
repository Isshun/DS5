package org.smallbox.faraway.module.item;

import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.LuaPanelController;
import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.job.model.BuildJob;
import org.smallbox.faraway.module.item.item.ItemModel;
import org.smallbox.faraway.ui.engine.views.widgets.UIFrame;
import org.smallbox.faraway.ui.engine.views.widgets.UIImage;
import org.smallbox.faraway.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.ui.engine.views.widgets.View;

/**
 * Created by Alex on 26/04/2016.
 */
public class ItemInfoController extends LuaPanelController {
    @BindLua private UILabel        lbName;

    @BindLua private View           frameBuild;
    @BindLua private UILabel        lbBuildCost;
    @BindLua private UILabel        lbBuildProgress;
    @BindLua private UILabel        lbBuildJob;
    @BindLua private UILabel        lbBuildCharacter;
    @BindLua private UIImage        imgBuildProgress;

    @BindModule("")
    private ItemModule _module;

    public ItemModel _item;

    @Override
    public void gameStart(Game game) {
        _module.addObserver(new ItemModuleObserver() {
            @Override
            public void onDeselectItem(ItemModel item) {
                _item = null;
                setVisible(false);
            }

            @Override
            public void onSelectItem(ItemModel item) {
                _item = item;
                setVisible(true);
                refreshItem();
            }
        });
    }

    @Override
    public void gameUpdate(Game game) {
        refreshItem();
    }

    private void refreshItem() {
        if (_item != null) {
            lbName.setText(_item.getLabel());

            if (!_item.isComplete()) {
                frameBuild.setVisible(true);
                imgBuildProgress.setTextureRect(0, 80, (int) (Math.floor(_item.getBuildProgress() * 352 / _item.getBuildCost() / 10) * 10), 16);
                lbBuildProgress.setText(String.format("Progress: %d/%d", _item.getBuildProgress(), _item.getBuildCost()));
                lbBuildJob.setText(String.format("Job: %s", _item.getBuildJob() != null ? _item.getBuildJob().getLabel() : "No job"));
                lbBuildCharacter.setText(_item.getBuildJob() != null && _item.getBuildJob().getCharacter() != null ? _item.getBuildJob().getCharacter().getName() : "Waiting for character");
            } else {
                frameBuild.setVisible(false);
            }
        }
    }
}
