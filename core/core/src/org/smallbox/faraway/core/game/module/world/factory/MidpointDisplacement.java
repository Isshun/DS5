package org.smallbox.faraway.core.game.module.world.factory;

import com.badlogic.gdx.math.MathUtils;
import org.smallbox.faraway.core.game.GameInfo;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;

public class MidpointDisplacement {
    public interface MapGenListener {
        void onCreate(ParcelModel parcel);
    }

    public float deepWaterThreshold,
            shallowWaterThreshold,
            desertThreshold,
            plainsThreshold,
            grasslandThreshold,
            forestThreshold,
            hillsThreshold,
            mountainsThreshold;

    public int n;
    public int wmult, hmult;

    public float smoothness;

    public MidpointDisplacement(WorldFactoryConfig config) {

        // the thresholds which determine cutoffs for different condition types
        mountainsThreshold = config.threshold;
//        deepWaterThreshold = 0.5f;
//        shallowWaterThreshold = 0.55f;
//        desertThreshold = 0.58f;
//        plainsThreshold = 0.62f;
//        grasslandThreshold = 0.7f;
//        forestThreshold = 0.8f;
//        hillsThreshold = 0.88f;
//        mountainsThreshold = 0.95f;

        // n partly controls the size of the old, but mostly controls the level of detail available
        n = config.n;

        // wmult and hmult are the width and height multipliers.  They set how separate regions there are
        wmult = config.w;
        hmult = config.h;

        // Smoothness controls how smooth the resultant terain is.  Higher = more smooth
        smoothness = config.smooth;
    }

    public void create(GameInfo info, ParcelModel[][][] parcels, int floor, MapGenListener listener) {
        int[][] map = getMap();

        for (int col = 0; col < map.length; col++) {
            for(int row = 0; row < map[col].length; row++) {
                if (map[col][row] == 2 && row < info.worldHeight && col < info.worldWidth) {
                    if (parcels[col][row][floor] != null) {
                        listener.onCreate(parcels[col][row][floor]);
                    }
                }
            }
        }
    }

    public int[][] getMap() {
        // get the dimensions of the old
        int power = (int)Math.pow(2,n);
        int width = wmult*power + 1;
        int height = hmult*power + 1;

        // initialize arrays to hold values
        float[][] map = new float[width][height];
        int[][] returnMap = new int[width][height];


        int step = power/2;
        float sum;
        int count;

        // h determines the fineness of the scale it is working on.  After every step, h
        // is decreased by a factor of "smoothness"
        float h = 1;

        // Initialize the grid points
        for (int i=0; i<width; i+=2*step) {
            for (int j=0; j<height; j+=2*step) {
                map[i][j] = MathUtils.random(2*h);
            }
        }

        // Do the rest of the magic
        while (step > 0) {
            // Diamond step
            for (int x = step; x < width; x+=2*step) {
                for (int y = step; y < height; y+=2*step) {
                    sum = map[x-step][y-step] + //down-left
                            map[x-step][y+step] + //up-left
                            map[x+step][y-step] + //down-right
                            map[x+step][y+step];  //up-right
                    map[x][y] = sum/4 + MathUtils.random(-h,h);
                }
            }

            // Square step
            for (int x = 0; x < width; x+=step) {
                for (int y = step*(1-(x/step)%2); y<height; y+=2*step) {
                    sum = 0;
                    count = 0;
                    if (x-step >= 0) {
                        sum+=map[x-step][y];
                        count++;
                    }
                    if (x+step < width) {
                        sum+=map[x+step][y];
                        count++;
                    }
                    if (y-step >= 0) {
                        sum+=map[x][y-step];
                        count++;
                    }
                    if (y+step < height) {
                        sum+=map[x][y+step];
                        count++;
                    }
                    if (count > 0) map[x][y] = sum/count + MathUtils.random(-h,h);
                    else map[x][y] = 0;
                }

            }
            h /= smoothness;
            step /= 2;
        }

        // Normalize the old
        float max = Float.MIN_VALUE;
        float min = Float.MAX_VALUE;
        for (float[] row : map) {
            for (float d : row) {
                if (d > max) max = d;
                if (d < min) min = d;
            }
        }

        // Use the thresholds to fill in the return old
        for(int col = 0; col < map.length; col++){
            for(int row = 0; row < map[col].length; row++){
                map[col][row] = (map[col][row]-min)/(max-min);
                if (map[col][row] > mountainsThreshold) returnMap[col][row] = 2;
                else returnMap[col][row] = 1;
//                if (old[col][row] < deepWaterThreshold) returnMap[col][row] = 0;
//                else if (old[col][row] < shallowWaterThreshold) returnMap[col][row] = 1;
//                else if (old[col][row] < desertThreshold) returnMap[col][row] = 2;
//                else if (old[col][row] < plainsThreshold) returnMap[col][row] = 3;
//                else if (old[col][row] < grasslandThreshold) returnMap[col][row] = 4;
//                else if (old[col][row] < forestThreshold) returnMap[col][row] = 5;
//                else if (old[col][row] < hillsThreshold) returnMap[col][row] = 6;
//                else if (old[col][row] < mountainsThreshold) returnMap[col][row] = 7;
//                else returnMap[col][row] = 8;
            }
        }

        return returnMap;
    }
}