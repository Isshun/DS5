package org.smallbox.faraway.module.item;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.CollectionUtils;
import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.game.BindLuaAction;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.character.controller.LuaController;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.module.item.item.ItemModel;
import org.smallbox.faraway.ui.engine.views.widgets.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 26/04/2016.
 */
public class ItemInfoController extends LuaController {
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
    @BindLua private UIList         listFactoryInventory;

    @BindModule
    private ItemModule _module;

    public ItemModel _item;

    @Override
    public void onGameStart(Game game) {
        _module.addObserver(new ItemModuleObserver() {
            @Override
            public void onDeselectItem(ItemModel item) {
                _item = null;
                setVisible(false);
            }

            @Override
            public void onSelectItem(GameEvent event, ItemModel item) {
                setVisible(true);
                refreshItem(item);
                event.consume();

                System.out.println(ReflectionToStringBuilder.toString(item).replace(",", "\n"));
            }
        });
    }

    @Override
    public void onGameUpdate(Game game) {
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
        if (item.getFactory() != null) {
            item.getFactory().getCraftActions().entrySet().forEach(entry -> {
                listActions.addView(createActionView(entry.getKey(), entry.getValue()));
            });
        }

        listFactoryInventory.clear();
        if (item.getFactory() != null) {
            item.getFactory().getInventory().forEach(consumableStack ->
                    listFactoryInventory.addView(UILabel.create(null)
                            .setText(consumableStack.getItemInfo().label + " (" + consumableStack.getConsumables().size() + ")")
                            .setSize(300, 22)));
        }
    }

    private View createActionView(ItemInfo.ItemInfoAction action, CraftJob job) {
        UIFrame frame = new UIFrame(null);
        frame.setSize(330, 100);
        frame.setBackgroundColor(0x22ffff00);
        frame.setMargin(5, 5, 5, 5);

        frame.addView(UILabel.create(null).setText(action.label).setPosition(10, 10));

        if (job != null) {
            UILabel lbCharacter = new UILabel(null);
            lbCharacter.setText(job.getCharacter() != null ? job.getCharacter().getName() : "auto");
            lbCharacter.setPosition(10, 30);
            frame.addView(lbCharacter);

            UILabel lbProgress = new UILabel(null);
            lbProgress.setDashedString("Progress", job.getProgressPercent() + "%", 48);
            lbProgress.setPosition(10, 48);
            lbProgress.setTextSize(12);
            frame.addView(lbProgress);

            UILabel lbInputs = new UILabel(null);
            lbInputs.setText(job.getInputs().entrySet().stream().map(entry -> entry.getKey().item.label + "-" + entry.getValue()).collect(Collectors.joining()));
            lbInputs.setPosition(10, 60);
            lbInputs.setTextSize(12);
            frame.addView(lbInputs);

            UIImage gauge = new UIImage(null);
            gauge.setPosition(10, 82);
            gauge.setImage("[base]/graphics/needbar.png");
            gauge.setTextureRect(0, 0, (int) (Math.floor(job.getProgress() * 300 / 10) * 10), 16);
            gauge.setSize(300, 20);
            frame.addView(gauge);
        }

//        if (job != null) {
//            listActions.addRootView(UILabel.createGame(null).setDashedString(job.getLabel(), job.getProgressPercent() + "%", 42).setSize(300, 22));
//        } else {
//            listActions.addRootView();
//        }

        return frame;
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
