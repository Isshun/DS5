package org.smallbox.faraway.ui;

import org.smallbox.faraway.PathManager;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.BaseManager;
import org.smallbox.faraway.util.Utils;
import org.smallbox.faraway.game.manager.WorldManager;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.StorageModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 13/06/2015.
 */
public class AreaManager extends BaseManager {
    private List<AreaModel> _areas = new ArrayList<>();

    public void createArea(AreaType type, int fromX, int fromY, int toX, int toY) {

        // Search existing area for current position
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                for (AreaModel area: _areas) {
                    if (area.contains(x, y)) {
                        addParcelToArea(area, fromX, fromY, toX, toY);
                        return;
                    }
                }
            }
        }

        // Create new area
        AreaModel area = createArea(type);
        _areas.add(area);
        addParcelToArea(area, fromX, fromY, toX, toY);
    }

    public static AreaModel createArea(AreaType type) {
        switch (type) {
            case STORAGE: return new StorageModel(type);
            default: return new AreaModel(type);
        }
    }

    private void addParcelToArea(AreaModel area, int fromX, int fromY, int toX, int toY) {
        WorldManager worldManager = Game.getWorldManager();

        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                area.addParcel(worldManager.getParcel(x, y));
            }
        }
    }

    public List<AreaModel> getAreas() {
        return _areas;
    }

    public AreaModel getArea(int x, int y) {
        for (AreaModel area: _areas) {
            if (area.contains(x, y)) {
                return area;
            }
        }
        return null;
    }

    public StorageModel getNearestFreeStorage(ConsumableModel consumable, ParcelModel fromParcel) {
        int x = fromParcel.getX();
        int y = fromParcel.getY();
        int bestDistance = Integer.MAX_VALUE;
        AreaModel bestArea = null;
        for (AreaModel area: _areas) {
            ParcelModel parcel = ((StorageModel)area).getNearestFreeParcel(consumable, x, y);
            if (area.isStorage() && parcel != null && Utils.getDistance(parcel, x, y) < bestDistance && PathManager.getInstance().getPath(fromParcel, parcel) != null) {
                bestArea = area;
                bestDistance = Utils.getDistance(parcel, x, y);
            }
        }
        return (StorageModel)bestArea;
    }

    public void addArea(AreaModel area) {
        _areas.add(area);
    }

    @Override
    protected void onUpdate(int tick) {
    }

    public void remove(AreaModel area) {
        if (area != null) {
            area.getParcels().forEach(parcel -> parcel.setArea(null));
            _areas.remove(area);
        }
    }
}
