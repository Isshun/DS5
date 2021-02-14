package org.smallbox.faraway.game.world.factory;

import com.badlogic.gdx.graphics.Pixmap;
import org.smallbox.faraway.client.asset.AssetManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnInit;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.game.world.FastNoise;
import org.smallbox.faraway.game.world.Parcel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ApplicationObject
public class CaveGenerator {

    @Inject private WorldFactoryDebug worldFactoryDebug;
    @Inject private WorldFactory worldFactory;
    @Inject private AssetManager assetManager;
    @Inject private DataManager dataManager;

    private final List<Pixmap> caveMasks = new ArrayList<>();

    @OnInit
    private void init() {
        caveMasks.add(assetManager.createPixmapFromTexture("data/worldFactory/cave_mask_0.png"));
        caveMasks.add(assetManager.createPixmapFromTexture("data/worldFactory/cave_mask_1.png"));
        caveMasks.add(assetManager.createPixmapFromTexture("data/worldFactory/cave_mask_2.png"));
        caveMasks.add(assetManager.createPixmapFromTexture("data/worldFactory/cave_mask_3.png"));
    }

    public void addCave(List<Parcel> parcels, int size, int floors, int offsetX, int offsetY, int offsetZ) {
        FastNoise noise = new FastNoise();
        noise.SetSeed(new Random().nextInt());
        noise.SetNoiseType(FastNoise.NoiseType.PerlinFractal);
        noise.SetFrequency(0.04f);
        noise.SetFractalType(FastNoise.FractalType.FBM);
        noise.SetFractalOctaves(7);
        noise.SetFractalLacunarity(2);
        noise.SetFractalGain(0.5f);

        Pixmap caveMask = caveMasks.get(new Random().nextInt(caveMasks.size()));

        for (int z = floors; z >= 0; z--) {
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    float noiseValue = noise.GetNoise(x, y) + 0.5f;
                    float islandValue = getIslandMask(caveMask, size, size, floors, x, y, offsetZ + z);
                    float value = noiseValue * islandValue;

                    if (value > 0.4) {
                        worldFactoryDebug.drawPixel(x, y + (z * (size + 10)), 0x00ffffff);
                    } else if (value > 0.3) {
                        worldFactoryDebug.drawPixel(x, y + (z * (size + 10)), 0x00ff00ff);
                    } else if (value > 0.2) {
                        worldFactoryDebug.drawPixel(x, y + (z * (size + 10)), 0x0000ffff);
                    } else if (value > 0.1) {
                        worldFactoryDebug.drawPixel(x, y + (z * (size + 10)), 0xff0000ff);
                    } else {
                        worldFactoryDebug.drawPixel(x, y + (z * (size + 10)), 0x000000ff);
                    }

                    if (value > 0.1) {
                        Parcel parcel = worldFactory.safeParcel(parcels, offsetX + x, offsetY + y, z);
                        if (parcel != null) {
                            parcel.setRockInfo(null);
                            parcel.setGroundInfo(dataManager.getItemInfo("base.ground.rock"));
                        }

                        Parcel topParcel = worldFactory.safeParcel(parcels, offsetX + x, offsetY + y, z + 1);
                        if (topParcel != null && !topParcel.hasRock()) {
                            topParcel.setGroundInfo(null);
                        }
                    }

                }
            }
        }

        worldFactoryDebug.next();
    }

    private float getIslandMask(Pixmap caveMask, int width, int height, int floors, int x, int y, int z) {
        float colorX = (caveMask.getPixel(x * caveMask.getWidth() / width, caveMask.getHeight() / 2) & 0x000000ff) / 255f;
        float colorY = (caveMask.getPixel(y * caveMask.getWidth() / height, caveMask.getHeight() / 2) & 0x000000ff) / 255f;
        float colorZ = (caveMask.getPixel(caveMask.getWidth() / 2, caveMask.getHeight() - (z * caveMask.getHeight() / floors)) & 0x000000ff) / 255f;
        return colorX * colorY * colorZ;
    }

}
