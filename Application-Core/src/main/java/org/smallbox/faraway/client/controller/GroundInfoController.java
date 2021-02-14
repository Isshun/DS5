package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.ui.event.UIEventManager;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.client.ui.widgets.UILabel;
import org.smallbox.faraway.client.shortcut.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnInit;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameAction.OnGameSelectAction;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.consumable.ConsumableModule;
import org.smallbox.faraway.game.item.ItemModule;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

@GameObject
public class GroundInfoController extends AbsInfoLuaController<Parcel> {
    @Inject protected MainPanelController mainPanelController;
    @Inject protected GameSelectionManager gameSelectionManager;
    @Inject private UIEventManager uiEventManager;
    @Inject private ItemModule itemModule;
    @Inject private ConsumableModule consumableModule;

    @BindLua private UILabel lbParcel;
    @BindLua private UILabel lbQuantity;
    @BindLua private UILabel lbJob;
    @BindLua private UILabel lbProduct;
    @BindLua private UILabel lbGround;
    @BindLua private UILabel lbRock;
    @BindLua private UILabel lbItem;
    @BindLua private UILabel lbConsumable;

    @OnInit
    public void init() {
//        gameSelectionManager.registerSelectionParcelListener(this);
    }

    @OnGameSelectAction(Parcel.class)
    private void onSelectParcel(Parcel parcel) {
        setVisible(true);

        lbParcel.setText(parcel.x + "x" + parcel.y + "x" + parcel.z);

        displayGround(parcel);
        displayRock(parcel);
        displayItem(parcel);
        displayConsumable(parcel);
    }

    @Override
    protected void onDisplayUnique(Parcel parcel) {
    }

    private void displayGround(Parcel parcel) {
        if (parcel.getGroundInfo() != null) {
            lbGround.setText(parcel.getGroundInfo().label);
        }
    }

    private void displayRock(Parcel parcel) {
        if (parcel.getRockInfo() != null) {
            lbGround.setText(parcel.getRockInfo().label);

            StringBuilder sb = new StringBuilder();
            parcel.getRockInfo().actions.get(0).products.forEach(
                    productInfo -> sb.append(productInfo.item.label).append(" ").append(productInfo.quantity[0]).append(" - ").append(productInfo.quantity[1])
            );
            lbProduct.setText(sb.toString());
        }
    }

    private void displayItem(Parcel parcel) {
        Optional.ofNullable(itemModule.getItem(parcel)).ifPresent(item -> lbItem.setText(item.getLabel()));
    }

    private void displayConsumable(Parcel parcel) {
        Optional.ofNullable(consumableModule.getConsumable(parcel)).ifPresent(consumable -> lbConsumable.setText(consumable.getLabel()));
    }

    @Override
    protected void onDisplayMultiple(Queue<Parcel> objects) {
        Map<ItemInfo, Integer> items = new HashMap<>();
        objects.stream()
                .filter(parcel -> parcel.getRockInfo() != null)
                .forEach(parcel -> items.put(parcel.getRockInfo(), items.getOrDefault(parcel.getRockInfo(), 0) + 1));

        StringBuilder sb = new StringBuilder();
        items.forEach((item, quantity) -> sb.append(item.label).append(" x").append(quantity));
//        lbLabel.setText(sb.toString());

        Map<ItemInfo.ItemProductInfo, Integer> products = new HashMap<>();
        items.forEach((item, quantity) -> item.actions.get(0).products.forEach(productInfo -> products.put(productInfo, products.getOrDefault(productInfo, 0) + quantity)));

        StringBuilder sb2 = new StringBuilder();
        products.forEach((productInfo, quantity) -> sb2
                .append(productInfo.item.label)
                .append(" ")
                .append(productInfo.quantity[0] * quantity)
                .append(" - ")
                .append(productInfo.quantity[1] * quantity));
        lbProduct.setText(sb2.toString());
    }

    @Override
    public Parcel getObjectOnParcel(Parcel parcel) {
        return parcel.getRockInfo() != null ? parcel : null;
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
