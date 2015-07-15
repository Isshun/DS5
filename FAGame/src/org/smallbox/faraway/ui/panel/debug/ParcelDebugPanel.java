package org.smallbox.faraway.ui.panel.debug;

import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.ui.panel.BasePanel;

/**
 * Created by Alex on 14/07/2015.
 */
public class ParcelDebugPanel extends BaseDebugPanel {
    private ParcelModel         _parcel;

    @Override
    protected String getTitle() {
        return "Parcel";
    }

    @Override
    protected void onAddDebug() {
        if (_parcel != null) {
            addDebugView("Item: " + (_parcel.getItem() != null ? _parcel.getItem().getLabel() : ""));
            addDebugView("Structure: " + (_parcel.getStructure() != null ? _parcel.getStructure().getLabel() : ""));
            addDebugView("Resource: " + (_parcel.getResource() != null ? _parcel.getResource().getLabel() : ""));
            addDebugView("Consumable: " + (_parcel.getConsumable() != null ? _parcel.getConsumable().getLabel() : ""));
        }
    }

    @Override
    public void onSelectParcel(ParcelModel parcel) {
        _parcel = parcel;
    }
}

