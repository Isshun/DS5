package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex on 26/04/2016.
 */
public class RockInfoController extends AbsInfoLuaController<ParcelModel> {

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
    protected void onDisplayUnique(ParcelModel parcel) {
        if (parcel.getRockInfo() != null) {
            lbLabel.setText(parcel.getRockInfo().label);

            StringBuilder sb = new StringBuilder();
            parcel.getRockInfo().actions.get(0).products.forEach(
                    productInfo -> sb.append(productInfo.item.label).append(" ").append(productInfo.quantity[0]).append(" - ").append(productInfo.quantity[1])
            );
            lbProduct.setText(sb.toString());
        }
    }

    @Override
    protected void onDisplayMultiple(List<ParcelModel> list) {
        Map<ItemInfo, Integer> items = new HashMap<>();
        list.stream()
                .filter(parcel -> parcel.getRockInfo() != null)
                .forEach(parcel -> items.put(parcel.getRockInfo(), items.getOrDefault(parcel.getRockInfo(), 0) + 1));

        StringBuilder sb = new StringBuilder();
        items.forEach((item, quantity) -> sb.append(item.label).append(" x").append(quantity));
        lbLabel.setText(sb.toString());

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
    protected ParcelModel getObjectOnParcel(ParcelModel parcel) {
        return parcel.getRockInfo() != null ? parcel : null;
    }
}
