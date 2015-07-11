package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.PathManager;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.area.*;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 13/06/2015.
 */
public class AreaManager extends BaseManager {
    private List<AreaModel> _areas = new ArrayList<>();

    public AreaManager() {
        _updateInterval = 10;
    }

    public void createArea(AreaType type, int fromX, int fromY, int toX, int toY) {
        // Search existing area for current position
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                for (AreaModel area: _areas) {
                    if (area.getType() == type && area.contains(x, y)) {
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

    public void removeArea(AreaType type, int fromX, int fromY, int toX, int toY) {
        // Search existing area for current position
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                for (AreaModel area: _areas) {
                    if (area.getType() == type && area.contains(x, y)) {
                        area.removeParcel(Game.getWorldManager().getParcel(x, y));
                    }
                }
            }
        }

        // Delete empty areas
        _areas.removeAll(_areas.stream().filter(area -> area.getParcels().isEmpty()).collect(Collectors.toList()));
    }

    public static AreaModel createArea(AreaType type) {
        switch (type) {
            case STORAGE: return new StorageAreaModel();
            case GARDEN: return new GardenAreaModel();
            case HOME: return new HomeAreaModel();
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

    public StorageAreaModel getNearestFreeStorage(ConsumableModel consumable, ParcelModel fromParcel) {
        int x = fromParcel.x;
        int y = fromParcel.y;
        int bestDistance = Integer.MAX_VALUE;
        AreaModel bestArea = null;
        for (AreaModel area: _areas) {
            if (area.isStorage() && area.accept(consumable.getInfo()) && PathManager.getInstance().getPath(area, fromParcel) != null) {
                ParcelModel parcel = ((StorageAreaModel)area).getNearestFreeParcel(consumable, x, y);
                if (parcel != null && Utils.getDistance(parcel, x, y) < bestDistance) {
                    bestArea = area;
                    bestDistance = Utils.getDistance(parcel, x, y);
                }
            }
        }
        return (StorageAreaModel)bestArea;
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
