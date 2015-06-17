package org.smallbox.faraway.model;

/**
 * Created by Alex on 17/06/2015.
 */
public class PlanetModel {
    private final PlanetAreaModel[][]   _areas;
    private final PlanetInfo            _info;

    public PlanetModel() {
        _info = new PlanetInfo();
        _areas = new PlanetAreaModel[100][80];
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 80; y++) {
                _areas[x][y] = new PlanetAreaModel();
            }
        }
    }

    public PlanetInfo getInfo() {
        return _info;
    }

    public PlanetAreaModel getAreas(int x, int y) {
        return _areas[x][y];
    }
}
