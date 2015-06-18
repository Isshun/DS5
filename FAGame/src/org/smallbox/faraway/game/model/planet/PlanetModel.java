package org.smallbox.faraway.game.model.planet;

/**
 * Created by Alex on 17/06/2015.
 */
public class PlanetModel {
    private final PlanetAreaModel[][]   _areas;
    private final PlanetInfo            _info;
    private double                      _oxygen;

    public PlanetModel(PlanetInfo info) {
        _info = info;
        switch (_info.stats.atmosphere) {
            case -2: _oxygen = 0; break;
            case -1: _oxygen = 0.5; break;
        }
        _areas = new PlanetAreaModel[100][80];
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 80; y++) {
                _areas[x][y] = new PlanetAreaModel();
            }
        }
    }

    public PlanetInfo       getInfo() { return _info; }
    public double           getOxygen() { return _oxygen; }
    public PlanetAreaModel  getAreas(int x, int y) { return _areas[x][y]; }
}
