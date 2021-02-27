package org.smallbox.faraway.game.world.factory;

import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.world.FastNoise;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.factory.old.MapFactoryConfig;

import java.util.List;

@ApplicationObject
public class MountainsGenerator {
    @Inject private CaveGenerator caveGenerator;
    @Inject private DataManager dataManager;

    public void computeGroundFloorMountains(List<Parcel> parcels, int width, int height, int floors) {
        ItemInfo defaultRockInfo = dataManager.getItemInfo("base.granite");

        MapFactoryConfig config = MapFactoryConfig.createMountains();

        // Create and configure FastNoise object
        FastNoise noise = new FastNoise();
        noise.SetSeed(1337);
        noise.SetNoiseType(FastNoise.NoiseType.PerlinFractal);
        noise.SetFrequency(0.004f);
        noise.SetFractalType(FastNoise.FractalType.FBM);
        noise.SetFractalOctaves(7);
        noise.SetFractalLacunarity(2);
        noise.SetFractalGain(0.5f);
//        noise.SetNoiseType(FastNoise.NoiseType.Cellular);
//        noise.SetFrequency(0.35f);
//        noise.SetFractalOctaves(1);
//        noise.SetFractalType(FastNoise.FractalType.RigidMulti);
//
//        float[][] image = PerlingGenerator.GenerateWhiteNoise(width * 10, height * 10);
//        float[][] perlinNoise = PerlingGenerator.GeneratePerlinNoise(image, config.perlinOctave);
//        for (MapFactoryConfig.AdjustmentValue adjustment : config.adjustments) {
//            perlinNoise = PerlingGenerator.AdjustLevels(perlinNoise, adjustment.min, adjustment.max);
//        }

        parcels.stream().filter(parcel -> parcel.z == floors - 1).forEach(parcel ->
                parcel.setRockInfo(noise.GetNoise(parcel.x, parcel.y) + 0.5f > 0.45 ? defaultRockInfo : null));

        caveGenerator.addCave(parcels, 20, floors, 10, 10, 0);
        caveGenerator.addCave(parcels, 35, floors, 30, 30, 4);
    }

}
