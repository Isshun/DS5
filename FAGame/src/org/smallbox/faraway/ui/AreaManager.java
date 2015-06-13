package org.smallbox.faraway.ui;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.manager.WorldManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 13/06/2015.
 */
public class AreaManager {
    private List<AreaModel> _areas = new ArrayList<>();

    public void createArea(AreaType type, int fromX, int fromY, int toX, int toY) {

        // Search existing area for current position
        for (int x = fromX; x < toX; x++) {
            for (int y = fromY; y < toY; y++) {
                for (AreaModel area: _areas) {
                    if (area.contains(x, y)) {
                        addParcelToArea(area, fromX, fromY, toX, toY);
                        return;
                    }
                }
            }
        }

        // Create new area
        AreaModel area = new AreaModel(type);
        _areas.add(area);
        addParcelToArea(area, fromX, fromY, toX, toY);
    }

    private void addParcelToArea(AreaModel area, int fromX, int fromY, int toX, int toY) {
        WorldManager worldManager = Game.getWorldManager();

        for (int x = fromX; x < toX; x++) {
            for (int y = fromY; y < toY; y++) {
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
}
