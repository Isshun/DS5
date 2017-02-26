package org.smallbox.faraway.client.controller;

import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.client.ui.engine.views.widgets.*;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.item.UsableItem;
import org.smallbox.faraway.modules.item.factory.CraftJob;
import org.smallbox.faraway.modules.item.factory.ItemFactoryModel;
import org.smallbox.faraway.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 26/04/2016.
 */
public class ItemInfoController extends AbsInfoLuaController<UsableItem> {

    @BindLua private UILabel        lbName;

    @BindLua private View           frameContent;
    @BindLua private View           frameBuild;
    @BindLua private View           frameWorkers;
    @BindLua private View           frameComponents;
    @BindLua private UILabel        lbBuildCost;
    @BindLua private UILabel        lbBuildProgress;
    @BindLua private UIImage        imgBuildProgress;

    @BindLua private UILabel        lbFactoryMessage;
    @BindLua private UILabel        lbFactoryJob;

    @BindLua private UILabel        currentAction;
    @BindLua private UIList         listActions;
    @BindLua private UIList         listWorkers;
    @BindLua private UIList         listComponents;
    @BindLua private UIList         listFactoryInventory;

    @BindLuaController
    private ItemInfoReceiptController itemInfoReceiptController;

    @BindModule
    private ItemModule itemModule;

    @Override
    protected void onDisplayUnique(UsableItem item) {
        getRootView().setVisible(true);

        lbName.setText(item.getLabel());
        refreshActions(item);
        refreshBuilding(item);
        refreshWorkers(item.getJobs());
        refreshInventory(item);

        if (item.getFactory() != null) {
            refreshFactory(item.getFactory());
        }
    }

    private void refreshFactory(ItemFactoryModel factory) {
        lbFactoryMessage.setText(factory.getMessage());

        if (factory.getCraftJob() != null) {
            if (factory.getCraftJob().getCharacter() != null) {
                lbFactoryJob.setText("Crafting: " + factory.getCraftJob().getCharacter().getName());
            }
        }

        else if (CollectionUtils.isNotEmpty(factory.getHaulJobs())) {
            lbFactoryJob.setText(StringUtils.join(factory.getHaulJobs().stream()
                    .map(job -> {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Hauling ").append(job.getConsumableInfo().label);

                        if (job.getCharacter() != null) {
                            sb.append(" (").append(job.getCharacter().getName()).append(")");
                        }

                        return sb.toString();
                    })
                    .collect(Collectors.toList()), ", "));
        }

        else {
            lbFactoryJob.setText("No jobs");
        }
    }

    private void refreshInventory(UsableItem item) {
        listFactoryInventory.clear();
        item.getInventory().forEach(consumable -> {
            UILabel label = new UILabel(null);
            label.setText(consumable.getInfo().label + " x " + consumable.getQuantity());
            listFactoryInventory.addView(label);
        });
    }

    @Override
    protected void onDisplayMultiple(List<UsableItem> list) {
    }

    @Override
    protected UsableItem getObjectOnParcel(ParcelModel parcel) {
        return itemModule.getItem(parcel);
    }

    private void refreshActions(UsableItem item) {
        listActions.clear();

        if (item.getFactory() != null) {
            item.getFactory().getReceiptGroups().forEach(receiptGroup -> {
                UIFrame lineReceiptGroup = new UIFrame(null);
                lineReceiptGroup.setSize(100, 20);

                UICheckBox lbLabel = UICheckBox.create(null);
                lbLabel.setText(receiptGroup.receiptGroupInfo.label);
                lbLabel.setChecked(UICheckBox.Value.PARTIAL);
                lbLabel.setSize(220, 20);
                lineReceiptGroup.addView(lbLabel);

                UILabel btInfo = UILabel.create(null);
                btInfo.setText("[info]");
                btInfo.setOnClickListener(event -> itemInfoReceiptController.display(receiptGroup));
                btInfo.setPosition(220, 0);
                btInfo.setSize(50, 20);
                btInfo.setBackgroundColor(Color.CYAN);
                lineReceiptGroup.addView(btInfo);

                listActions.addView(lineReceiptGroup);

                if (item.getFactory().hasRunningReceipt()) {
                    currentAction.setText(item.getFactory().getRunningReceipt().receiptInfo.label);
                } else {
                    currentAction.setText("no running receipt");
                }
            });
        }

        //        if (item.getFactory() != null) {
//            item.getFactory().getCraftActions().entrySet().forEach(entry -> {
//                listActions.addView(createActionView(entry.getKey(), entry.getValue()));
//            });
//        }
//
//        listFactoryInventory.clear();
//        if (item.getFactory() != null) {
//            item.getFactory().getInventory().forEach(consumableStack ->
//                    listFactoryInventory.addView(UILabel.create(null)
//                            .setText(consumableStack.getItemInfo().label + " (" + consumableStack.getConsumables().size() + ")")
//                            .setSize(300, 22)));
//        }
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
    private void refreshBuilding(UsableItem item) {
        if (!item.isComplete()) {
            frameBuild.setVisible(true);

            imgBuildProgress.setTextureRect(0, 80, (int) (Math.floor(item.getBuildProgress() * 352 / item.getBuildCost() / 10) * 10), 16);
//            lbBuildProgress.setDashedString("Building", item.getBuildProgress() + "/" + item.getBuildCost(), 30);
            lbBuildProgress.setText("Building");
            lbBuildCost.setText(item.getBuildProgress() + "/" + item.getBuildCost());

            if (CollectionUtils.isNotEmpty(item.getInfo().components)) {
                frameComponents.setVisible(true);
                listComponents.clear();
                item.getInfo().components.forEach(component -> listComponents.addView(UILabel.create(null).setText(component.item.name).setSize(100, 22)));
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
}
