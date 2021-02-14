package org.smallbox.faraway.client.controller;

import org.apache.commons.collections4.CollectionUtils;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.shortcut.GameShortcut;
import org.smallbox.faraway.client.ui.event.UIEventManager;
import org.smallbox.faraway.client.ui.widgets.*;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameLongUpdate;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameAction.OnGameSelectAction;
import org.smallbox.faraway.game.item.ItemModule;
import org.smallbox.faraway.game.item.UsableItem;
import org.smallbox.faraway.game.itemFactory.ItemFactoryModel;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.world.Parcel;

import java.util.Collection;
import java.util.Optional;
import java.util.Queue;

@GameObject
public class ItemInfoController extends AbsInfoLuaController<UsableItem> {
    @Inject protected GameSelectionManager gameSelectionManager;
    @Inject private UIEventManager uiEventManager;
    @Inject private MainPanelController mainPanelController;

    @BindLua private UILabel lbName;
    @BindLua private UILabel lbHealth;
    @BindLua private View progressHealth;
    @BindLua private View viewProgress;

    @BindLua private View frameDurability;
    @BindLua private View frameContent;
    @BindLua private View frameWorkers;
    @BindLua private View frameComponents;
    @BindLua private UILabel lbBuildCost;
    @BindLua private UILabel lbBuildProgress;
    @BindLua private UIImage imgBuildProgress;

    @BindLua private UILabel lbFactoryMessage;
    @BindLua private UILabel lbFactoryJob;

    @BindLua private UILabel currentAction;
    @BindLua private UIList listWorkers;
    @BindLua private UIList listComponents;
    @BindLua private UIList listFactoryInventory;
    @BindLua private UIList listInventory;

    @BindLua private View frameBuild;
    @BindLua private UILabel lbBuild;
    @BindLua private UIImage image;
    @Inject private ItemInfoReceiptController itemInfoReceiptController;
    @Inject private ItemInfoFactoryComponentsController itemInfoFactoryComponentsController;
    @Inject private ItemModule itemModule;

    private UsableItem item;

    @OnGameLongUpdate
    public void onGameLongUpdate() {

        // Refresh inventory
        if (item != null) {
            refreshItem(item);
        }

    }

    private void refreshItem(UsableItem item) {
        refreshInventory(item);
        refreshBuildProgress(item);
    }

    private void refreshBuildProgress(UsableItem item) {
        frameBuild.setVisible(false);
        frameDurability.setVisible(false);

        if (item.isComplete()) {
            frameDurability.setVisible(true);
            lbHealth.setText(item.getHealth() + " / " + item.getMaxHealth());
            progressHealth.getGeometry().setWidth(progressHealth.getGeometry().getFixedWidth() * item.getHealth() / item.getMaxHealth());
        } else {
            frameBuild.setVisible(true);
            lbBuild.setText((int) (item.getBuildProgress() * 100) + "%");
            viewProgress.getGeometry().setWidth((int) (viewProgress.getGeometry().getFixedWidth() * item.getBuildProgress()));
        }
    }

    @OnGameSelectAction(UsableItem.class)
    protected void onSelectItem(UsableItem item) {
        setVisible(true);

        this.item = item;

        lbName.setText(item.getLabel());
        lbHealth.setText(item.getHealth() + " / " + item.getMaxHealth());
        image.setImage(item.getGraphic());
        progressHealth.getGeometry().setWidth(80 * item.getHealth() / item.getMaxHealth());

        if (!item.isComplete()) {
            frameBuild.setVisible(true);
            displayBuildPane(item);
            return;
        } else {
            frameBuild.setVisible(false);
        }
//
//        refreshActions(item);
//        refreshBuilding(item);
//        refreshJobs(item.getJobs());
//        refreshInventory(item);
//
//        if (item.getFactory() != null) {
//            itemInfoFactoryComponentsController.setItem(item);
//            refreshFactory(item.getFactory());
//        }

    }

    private void displayBuildPane(UsableItem item) {

    }

    private void refreshFactory(ItemFactoryModel factory) {
        lbFactoryMessage.setText(factory.getMessage());

        if (factory.getCraftJob() != null) {
            if (factory.getCraftJob().getCharacter() != null) {
                lbFactoryJob.setText("Crafting: " + factory.getCraftJob().getCharacter().getName());
            }
        }

        // TODO
//        else if (CollectionUtils.isNotEmpty(factory.getHaulJobs())) {
//            lbFactoryJob.setText(StringUtils.join(factory.getHaulJobs().stream()
//                    .map(job -> {
//                        StringBuilder sb = new StringBuilder();
//                        sb.append("Hauling ").append(job.getConsumableInfo().label);
//
//                        if (job.getCharacter() != null) {
//                            sb.append(" (").append(job.getCharacter().getName()).append(")");
//                        }
//
//                        return sb.toString();
//                    })
//                    .collect(Collectors.toList()), ", "));
//        }

        else {
            lbFactoryJob.setText("No jobs");
        }
    }

    private void refreshInventory(UsableItem item) {
        Optional.ofNullable(item.getInventory()).ifPresent(consumables -> {
            consumables.forEach(consumable -> {
                CompositeView entryItem = listInventory.createFromTemplate(CompositeView.class);
                entryItem.findLabel("lb_item").setText(consumable.getInfo().label + " x" + consumable.getTotalQuantity());
                listInventory.addNextView(entryItem);
            });
            listInventory.switchViews();
        });
    }

    @Override
    protected void onDisplayUnique(UsableItem usableItem) {
    }

    @Override
    protected void onDisplayMultiple(Queue<UsableItem> objects) {
    }

    @Override
    public UsableItem getObjectOnParcel(Parcel parcel) {
        return itemModule.getItem(parcel);
    }

    private void refreshActions(UsableItem item) {
//        listActions.removeAllViews();
//
//        if (item.getFactory() != null) {
//            item.getFactory().getReceiptGroups().forEach(receiptGroup -> {
//                UIFrame lineReceiptGroup = new UIFrame(null);
//                lineReceiptGroup.setSize(100, 20);
//
//                UIMultiCheckBox lbLabel = UIMultiCheckBox.create(null);
//                lbLabel.setText(receiptGroup.receiptGroupInfo.label);
//                lbLabel.setChecked(UIMultiCheckBox.Value.PARTIAL);
//                lbLabel.setSize(220, 20);
//                lineReceiptGroup.addView(lbLabel);
//
//                UILabel btInfo = UILabel.create(null);
//                btInfo.setText("[info]");
//                btInfo.getEvents().setOnClickListener(() -> itemInfoReceiptController.display(receiptGroup));
//                btInfo.setPosition(220, 0);
//                btInfo.setSize(50, 20);
//                btInfo.getStyle().setBackgroundColor(Color.CYAN);
//                lineReceiptGroup.addView(btInfo);
//
//                listActions.addView(lineReceiptGroup);
//
//                if (item.getFactory().hasRunningReceipt()) {
//                    currentAction.setText(item.getFactory().getRunningReceipt().receiptInfo.label);
//                } else {
//                    currentAction.setText("no running receipt");
//                }
//            });
//        }

        //        if (item.getFactory() != null) {
//            item.getFactory().getCraftActions().entrySet().forEach(entry -> {
//                listActions.addView(createActionView(entry.getKey(), entry.getValue()));
//            });
//        }
//
//        listFactoryInventory.removeAllViews();
//        if (item.getFactory() != null) {
//            item.getFactory().getInventory().forEach(consumableStack ->
//                    listFactoryInventory.addView(UILabel.create(null)
//                            .setText(consumableStack.getItemInfo().label + " (" + consumableStack.getConsumables().size() + ")")
//                            .setSize(300, 22)));
//        }
    }

    // Refresh building frame
    private void refreshBuilding(UsableItem item) {
        if (!item.isComplete()) {
            frameBuild.setVisible(true);

            imgBuildProgress.setTextureRect(0, 80, (int) (Math.floor(item.getBuildValue() * 352 / item.getBuildCost() / 10) * 10), 16);
//            lbBuildProgress.setDashedString("Building", item.getBuildProgress() + "/" + item.getBuildCost(), 30);
            lbBuildProgress.setText("Building");
            lbBuildCost.setText(item.getBuildValue() + "/" + item.getBuildCost());

            if (CollectionUtils.isNotEmpty(item.getInfo().components)) {
                frameComponents.setVisible(true);
                listComponents.removeAllViews();
                item.getInfo().components.forEach(component -> listComponents.addView(UILabel.create(null).setText(component.item.name).setSize(100, 22)));
            } else {
                frameComponents.setVisible(false);
            }
        } else {
            frameBuild.setVisible(false);
        }
    }

    private void refreshJobs(Collection<JobModel> jobs) {
        if (CollectionUtils.isNotEmpty(jobs)) {
            frameWorkers.setVisible(true);

            listWorkers.removeAllViews();
            jobs.forEach(job -> listWorkers.addView(UILabel.create(null).setText(job.getCharacter().getName()).setSize(300, 22)));
        } else {
            frameWorkers.setVisible(false);
        }
    }

    @BindLuaAction
    private void onOpenComponents(View view) {
        itemInfoFactoryComponentsController.setVisible(true);
    }

//    @BindLuaAction
//    public void onDumpItem(View view) {
//        if (listSelected.size() == 1) {
//            itemModule.dumpItem(listSelected.peek());
//        }
//    }

    @BindLuaAction
    public void onCancelBuild(View view) {
    }

    @BindLuaAction
    private void onClose(View view) {
        mainPanelController.setVisible(true);
    }

    @GameShortcut("escape")
    private void onClose() {
        mainPanelController.setVisible(true);
    }

}
