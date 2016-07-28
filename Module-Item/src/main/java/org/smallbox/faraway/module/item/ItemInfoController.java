package org.smallbox.faraway.module.item;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.CollectionUtils;
import org.smallbox.faraway.core.LuaPanelController;
import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.game.BindLuaAction;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.module.item.item.ItemModel;
import org.smallbox.faraway.ui.engine.views.widgets.*;

import java.util.List;

/**
 * Created by Alex on 26/04/2016.
 */
public class ItemInfoController extends LuaPanelController {
    @BindLua private UILabel        lbName;

    @BindLua private View           frameContent;
    @BindLua private View           frameBuild;
    @BindLua private View           frameWorkers;
    @BindLua private View           frameComponents;
    @BindLua private UILabel        lbBuildCost;
    @BindLua private UILabel        lbBuildProgress;
    @BindLua private UIImage        imgBuildProgress;

    @BindLua private UIList         listActions;
    @BindLua private UIList         listWorkers;
    @BindLua private UIList         listComponents;

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
                setVisible(true);
                refreshItem(item);
            }
        });
    }

    @Override
    public void gameUpdate(Game game) {
        if (_item != null) {
            refreshItem(_item);
        }
    }

    private void refreshItem(ItemModel item) {
        _item = item;

        lbName.setText(item.getLabel());

        refreshActions(item);
        refreshBuilding(item);
        refreshWorkers(item.getJobs());
    }

    private void refreshActions(ItemModel item) {
        listActions.clear();
//        if (CollectionUtils.isNotEmpty(item.getFactory().)) {
//            item.getJobs().forEach(job -> listActions.addView(UILabel.create(null).setDashedString(job.getCharacter().getName(), job.getLabel(), 42)));
//        }
    }

    // Refresh building frame
    private void refreshBuilding(ItemModel item) {
        if (!item.isComplete()) {
            frameBuild.setVisible(true);

            imgBuildProgress.setTextureRect(0, 80, (int) (Math.floor(item.getBuildProgress() * 352 / item.getBuildCost() / 10) * 10), 16);
//            lbBuildProgress.setDashedString("Building", item.getBuildProgress() + "/" + item.getBuildCost(), 30);
            lbBuildProgress.setText("Building");
            lbBuildCost.setText(item.getBuildProgress() + "/" + item.getBuildCost());

            if (CollectionUtils.isNotEmpty(item.getInfo().components)) {
                frameComponents.setVisible(true);
                listComponents.clear();
                item.getInfo().components.forEach(component -> listComponents.addView(UILabel.create(null).setText(component.itemName).setSize(100, 22)));
            } else {
                frameComponents.setVisible(false);
            }
        } else {
            frameBuild.setVisible(false);
        }
    }

    private void refreshWorkers(List<JobModel> jobs) {
        if (CollectionUtils.isNotEmpty(jobs)) {
            frameWorkers.setVisible(true);

            listWorkers.clear();
            jobs.forEach(job -> listWorkers.addView(UILabel.create(null).setText(job.getCharacter().getName()).setSize(300, 22)));
        } else {
            frameWorkers.setVisible(false);
        }
    }

    @BindLuaAction
    public void onDump(View view) {
        if (_item != null) {
            Log.dump(_item);
        }
    }
}
