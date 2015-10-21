package org.smallbox.faraway.core.game.module.world.model;

import org.smallbox.faraway.core.PotentialConsumable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 17/10/2015.
 */
public class ItemFactoryReceiptModel {
    public final ItemFactoryModel.OrderEntry    order;
    public final ReceiptGroupInfo.ReceiptInfo   receiptInfo;
    public final ReceiptGroupInfo               receiptGroupInfo;
    public int                                  totalDistance;
    public boolean                              enoughComponents;

    public ItemFactoryReceiptModel(ItemFactoryModel.OrderEntry order, ReceiptGroupInfo.ReceiptInfo receiptInfo) {
        this.order = order;
        this.receiptGroupInfo = order.receiptGroupInfo;
        this.receiptInfo = receiptInfo;
    }

    public void setPotentialComponents(List<PotentialConsumable> potentialComponents) {
        this.totalDistance = 0;
        this.enoughComponents = true;

        for (ReceiptGroupInfo.ReceiptInputInfo inputInfo: this.receiptInfo.inputs) {
            List<PotentialConsumable> potentials = potentialComponents.stream()
                    .filter(potentialConsumable -> potentialConsumable.itemInfo.instanceOf(inputInfo.item))
                    .sorted((o1, o2) -> o1.distance - o2.distance)
                    .collect(Collectors.toList());
            int quantity = 0;
            for (PotentialConsumable potential: potentials) {
                if (quantity < inputInfo.quantity) {
                    this.totalDistance += potential.distance;
                    quantity += potential.consumable.getQuantity();
                }
            }
            if (quantity < inputInfo.quantity) {
                this.enoughComponents = false;
            }
        }

        System.out.println("Set potential components for receipt entry: " + this.receiptInfo.label + " (distance: " + this.totalDistance + ", enough: " + this.enoughComponents + ")");
    }
}
