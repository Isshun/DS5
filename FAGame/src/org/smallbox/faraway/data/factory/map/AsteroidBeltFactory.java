package org.smallbox.faraway.data.factory.map;

import org.smallbox.faraway.data.serializer.LoadListener;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.item.ResourceModel;

/**
 * Created by Alex on 20/06/2015.
 */
public class AsteroidBeltFactory implements IMapFactory {
    private ParcelModel[][][]   _parcels;
    private int                 _width;
    private int                 _height;
    public static float[][]     sData;

    @Override
    public void create(ParcelModel[][][] parcels, int width, int height, LoadListener loadListener) {
        _parcels = parcels;
        _width = width;
        _height = height;

        ItemInfo itemRock = GameData.getData().getItemInfo("base.rock");

        MapFactoryConfig config = MapFactoryConfig.createMountains();

        float[][] image = PerlingGenerator.GenerateWhiteNoise(1000, 1000);
        float[][] perlinNoise = PerlingGenerator.GeneratePerlinNoise(image, config.perlinOctave);

        for (MapFactoryConfig.AdjustmentValue adjustment: config.adjustments) {
            perlinNoise = PerlingGenerator.AdjustLevels(perlinNoise, adjustment.min, adjustment.max);
        }

        System.out.println("done");

        this.sData = perlinNoise;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (perlinNoise[x*4][y*4] > 0.65f) {
                    ParcelModel parcelModel = getParcel(x, y);
                    if (parcelModel != null) {
                        ResourceModel resourceModel = new ResourceModel(itemRock);
                        resourceModel.setValue(10);
                        parcelModel.setResource(resourceModel);
                    }
                }
            }
        }
    }

    private ParcelModel getParcel(int x, int y) {
        if (x > 0 && x < _width && y > 0 && y < _height) {
            return _parcels[x][y][0];
        }
        return null;
    }

    private void setTypeIfZero(int width, int height, ParcelModel[][][] parcels, int x, int y, double elevation) {
        if (x > 0 && x < width && y > 0 && y < height) {
            if (parcels[x][y][0].getElevation() <= 0.5) {
                parcels[x][y][0].setElevation(elevation);
            }
        }
    }
}
