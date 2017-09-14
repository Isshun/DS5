package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.SelectionManager;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.common.ParcelCommon;
import org.smallbox.faraway.common.dependencyInjector.BindComponent;
import org.smallbox.faraway.common.dependencyInjector.GameObject;

import java.util.Queue;

/**
 * Created by Alex on 26/04/2016.
 */
@GameObject
public class RockInfoController extends AbsInfoLuaController<ParcelCommon> {

    @BindComponent
    protected SelectionManager selectionManager;

    @BindComponent
    private UIEventManager uiEventManager;

    @BindLua
    private UILabel lbLabel;

    @BindLua
    private UILabel lbQuantity;

    @BindLua
    private UILabel lbJob;

    @BindLua
    private UILabel lbName;

    @BindLua
    private UILabel lbProduct;

    @Override
    public void onReloadUI() {
        selectionManager.registerSelection(this);
    }

    @Override
    protected void onDisplayUnique(ParcelCommon parcel) {
//        if (parcel.getRockInfo() != null) {
//            lbLabel.setText(parcel.getRockInfo().label);
//
//            StringBuilder sb = new StringBuilder();
//            parcel.getRockInfo().actions.get(0).products.forEach(
//                    productInfo -> sb.append(productInfo.item.label).append(" ").append(productInfo.quantity[0]).append(" - ").append(productInfo.quantity[1])
//            );
//            lbProduct.setText(sb.toString());
//        }
    }

    @Override
    protected void onDisplayMultiple(Queue<ParcelCommon> objects) {
//        Map<ItemInfo, Integer> items = new HashMap<>();
//        objects.stream()
//                .filter(parcel -> parcel.getRockInfo() != null)
//                .forEach(parcel -> items.put(parcel.getRockInfo(), items.getOrDefault(parcel.getRockInfo(), 0) + 1));
//
//        StringBuilder sb = new StringBuilder();
//        items.forEach((item, quantity) -> sb.append(item.label).append(" x").append(quantity));
//        lbLabel.setText(sb.toString());
//
//        Map<ItemInfo.ItemProductInfo, Integer> products = new HashMap<>();
//        items.forEach((item, quantity) -> item.actions.get(0).products.forEach(productInfo -> products.put(productInfo, products.getOrDefault(productInfo, 0) + quantity)));
//
//        StringBuilder sb2 = new StringBuilder();
//        products.forEach((productInfo, quantity) -> sb2
//                .append(productInfo.item.label)
//                .append(" ")
//                .append(productInfo.quantity[0] * quantity)
//                .append(" - ")
//                .append(productInfo.quantity[1] * quantity));
//        lbProduct.setText(sb2.toString());
    }

    @Override
    public ParcelCommon getObjectOnParcel(ParcelCommon parcel) {
//        return parcel.getRockInfo() != null ? parcel : null;
        return null;
    }
}
