package org.smallbox.faraway.modules.world.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.world.FastNoise;

import java.util.Random;

@ApplicationObject
public class CaveGenerator {

    @Inject private WorldFactoryDebug worldFactoryDebug;
    @Inject private WorldFactory worldFactory;

    private Pixmap caveMask;

    @OnInit
    private void init() {
        Texture texture = new Texture(Gdx.files.internal("data/worldFactory/cave_mask.png"), Pixmap.Format.RGBA8888, false);
        texture.getTextureData().prepare();
        caveMask = texture.getTextureData().consumePixmap();
        texture.dispose();
    }

    public void addCave(ParcelModel[][][] _parcels, int width, int height, int floors, int offsetX, int offsetY) {
        FastNoise noise = new FastNoise();
        noise.SetSeed(new Random().nextInt());
        noise.SetNoiseType(FastNoise.NoiseType.PerlinFractal);
        noise.SetFrequency(0.04f);
        noise.SetFractalType(FastNoise.FractalType.FBM);
        noise.SetFractalOctaves(7);
        noise.SetFractalLacunarity(2);
        noise.SetFractalGain(0.5f);

        for (int z = 0; z < floors; z++) {
            for (int x = 0; x < 40; x++) {
                for (int y = 0; y < 40; y++) {
//                    float value = noise.GetNoise(x, y, z) * getIslandMask(width, height, x, y);
                    float noiseValue = noise.GetNoise(x, y) + 0.5f;
                    float islandValue = getIslandMask(40, 40, floors, x, y, z);
                    float value = noiseValue * islandValue;
//                    pixmap.drawPixel(x, y, 0xffffff00 + (int)(getIslandMask(40, 40, x, y) * 255));
                    if (value > 0.4) {
                        worldFactoryDebug.drawPixel(x, y + (z * height), 0x00ffffff);
                    } else if (value > 0.3) {
                        worldFactoryDebug.drawPixel(x, y + (z * height), 0x00ff00ff);
                    } else if (value > 0.2) {
                        worldFactoryDebug.drawPixel(x, y + (z * height), 0x0000ffff);
                    } else if (value > 0.1) {
                        worldFactoryDebug.drawPixel(x, y + (z * height), 0xff0000ff);
                    } else {
                        worldFactoryDebug.drawPixel(x, y + (z * height), 0x000000ff);
                    }

                    if (value > 0.1) {
                        ParcelModel parcel = worldFactory.safeParcel(_parcels, offsetX + x, offsetY + y, z);
                        if (parcel != null) {
                            parcel.setRockInfo(null);
                        }
                    }

                }
            }
        }

        worldFactoryDebug.next();
    }

    private float getIslandMask(int width, int height, int floors, int x, int y, int z) {
//        return (1 - ((Math.abs((width / 2f) - x) / (width / 2f)) * (Math.abs((height / 2f) - y) / (height / 2f))));
        float colorX = (caveMask.getPixel(x * caveMask.getWidth() / width, caveMask.getHeight() / 2) & 0x000000ff) / 255f;
        float colorY = (caveMask.getPixel(y * caveMask.getWidth() / height, caveMask.getHeight() / 2) & 0x000000ff) / 255f;
        float colorZ = (caveMask.getPixel(caveMask.getWidth() / 2, z * caveMask.getHeight() / floors) & 0x000000ff) / 255f;
        return colorX * colorY * colorZ;
    }

}
