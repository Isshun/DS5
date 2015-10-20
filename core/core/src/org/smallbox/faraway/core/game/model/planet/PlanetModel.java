package org.smallbox.faraway.core.game.model.planet;

import org.smallbox.faraway.core.util.Constant;

import java.util.HashMap;
import java.util.Map;

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

        Map<RegionInfo, Double> regions = new HashMap<>();
        _areas = new PlanetAreaModel[Constant.PLANET_WIDTH][Constant.PLANET_HEIGHT];
        for (int y = 0; y < Constant.PLANET_HEIGHT; y++) {
            regions.clear();

            // Get region for latitude
            int latitude = 90 - y * 180 / Constant.PLANET_HEIGHT;
            double frequencySum = 0;
            double totalFrequency = getTotalFrequency(latitude);
            for (RegionInfo infoRegion: _info.regions) {
                for (RegionInfo.RegionDistribution distribution: infoRegion.distribution) {
                    if (latitude >= distribution.latitude[0] && latitude <= distribution.latitude[1]) {
                        regions.put(infoRegion, frequencySum + distribution.frequency / totalFrequency);
                        frequencySum += distribution.frequency / totalFrequency;
                    }
                }
            }

            // Check regions exists for latitude
            if (regions.isEmpty()) {
                throw new RuntimeException("No regions for latitude: " + latitude);
            }

            // Put regions on old
            for (int x = 0; x < Constant.PLANET_WIDTH; x++) {
                _areas[x][y] = new PlanetAreaModel();
                double r = Math.random();
                for (Map.Entry<RegionInfo, Double> entry: regions.entrySet()) {
                    if (r < entry.getValue()) {
                        _areas[x][y].region = entry.getKey();
                    }
                }

                // Check region exists for area
                if (_areas[x][y].region == null) {
                    throw new RuntimeException("No region for area: " + x + "x" + y);
                }
            }
        }
    }

    private double getTotalFrequency(int latitude) {
        double totalFrequency = 0;
        for (RegionInfo infoRegion: _info.regions) {
            for (RegionInfo.RegionDistribution distribution: infoRegion.distribution) {
                if (latitude >= distribution.latitude[0] && latitude <= distribution.latitude[1]) {
                    totalFrequency += distribution.frequency;
                }
            }
        }
        return totalFrequency;
    }

    public PlanetInfo       getInfo() { return _info; }
    public double           getOxygen() { return _oxygen; }
    public PlanetAreaModel  getAreas(int x, int y) { return _areas[x][y]; }
}
