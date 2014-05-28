package alone.in.deepspace.engine.loader;

import hoten.voronoi.nodename.as3delaunay.Voronoi;

import java.util.Random;

import test.TestGraphImpl;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.manager.WorldManager;
import alone.in.deepspace.model.ItemInfo;
import alone.in.deepspace.util.Constant;

public class WorldFactory {

	public static void create(WorldManager worldMap) {
		int[][] map = new int[Constant.WORLD_HEIGHT][Constant.WORLD_WIDTH];
		for (int i = 0; i < Constant.WORLD_WIDTH; i++) {
			for (int j = 0; j < Constant.WORLD_HEIGHT; j++) {
				map[i][j] = 10;
			}
		}
		
        final int width = Constant.WORLD_WIDTH * 2;
        final int height = Constant.WORLD_HEIGHT * 2;
        final int numSites = 5000;
        final long seed = 42;//System.nanoTime();
        final Random r = new Random(seed);
        System.out.println("seed: " + seed);

        //make the intial underlying voronoi structure
        final Voronoi v = new Voronoi(numSites, width, height, r, null);

        //assemble the voronoi strucutre into a usable graph object representing a map
        final TestGraphImpl graph = new TestGraphImpl(v, 2, r);
        
        graph.paint(map);
        
        clean(map);
        clean(map);
        clean(map);
//
//		
//		for (int i = 0; i < 20; i++) {
//			addShape(map,
//					(int)(Math.random() * Constant.WORLD_WIDTH),
//					(int)(Math.random() * Constant.WORLD_HEIGHT),
//					(int)(Math.random() * 20),
//					(int)(Math.random() * 10));
//		}
//		
		ItemInfo info = ServiceManager.getData().getItemInfo("base.rock");
		ItemInfo infoGround = ServiceManager.getData().getItemInfo("base.ground");
		
		for (int i = 0; i < Constant.WORLD_WIDTH; i++) {
			for (int j = 0; j < Constant.WORLD_HEIGHT; j++) {
				if (map[i][j] <= 0) {
					worldMap.putItem(infoGround, 0, i, j, 999);
				} else {
					for (int f = 0; f < map[i][j]; f++) {
						worldMap.putItem(info, f, i, j, 999);
					}
				}
				System.out.print(map[i][j]);
			}
			System.out.print('\n');
		}

		worldMap.cleanRock();
		
//		worldMap.cleanRock2();
		
		
	}

	private static void clean(int[][] map) {
		for (int i = 0; i < Constant.WORLD_WIDTH; i++) {
			for (int j = 0; j < Constant.WORLD_HEIGHT; j++) {
				int maxNeighboorElev = 0;
				int faces = 0;
				if (i+1 >= 0 && i+1 < Constant.WORLD_WIDTH && j >= 0 && j < Constant.WORLD_HEIGHT) {
					if (map[i+1][j] >= map[i][j]) { faces++; }
					else if (map[i+1][j] > maxNeighboorElev) { maxNeighboorElev = map[i+1][j]; }
				}
				if (i-1 >= 0 && i-1 < Constant.WORLD_WIDTH && j >= 0 && j < Constant.WORLD_HEIGHT) {
					if (map[i-1][j] >= map[i][j]) { faces++; }
					else if (map[i-1][j] > maxNeighboorElev) { maxNeighboorElev = map[i-1][j]; }
				}
				if (i >= 0 && i < Constant.WORLD_WIDTH && j+1 >= 0 && j+1 < Constant.WORLD_HEIGHT) {
					if (map[i][j+1] >= map[i][j]) { faces++; }
					else if (map[i][j+1] > maxNeighboorElev) { maxNeighboorElev = map[i][j+1]; }
				}
				if (i >= 0 && i < Constant.WORLD_WIDTH && j-1 >= 0 && j-1 < Constant.WORLD_HEIGHT) {
					if (map[i][j-1] >= map[i][j]) { faces++; }
					else if (map[i][j-1] > maxNeighboorElev) { maxNeighboorElev = map[i][j-1]; }
				}
				if (faces < 2) {
					map[i][j] = maxNeighboorElev;
				}
			}
		}
	}

	private static void addShape(int[][] map, int x, int y, int size, int floor) {
		for (int j = 0; j < size; j++) {
			for (double i = 0; i < Math.PI * 2; i += 0.01) {
				double offsetX = (int)Math.round(Math.cos(i) * j);
				double offsetY = (int)Math.round(Math.sin(i) * j);
				int x2 = x+(int)offsetX;
				int y2 = y+(int)offsetY;
				double radius = Math.sqrt(Math.pow(Math.abs(offsetX), 2) + Math.pow(Math.abs(offsetY), 2));
				if (x2 >= 0 && y2 >= 0 && x2 < Constant.WORLD_WIDTH && y2 < Constant.WORLD_HEIGHT && map[x2][y2] > j / 2) {
					map[x2][y2] = Math.max(j * 10 / size, floor);
				}
			}
		}
	}

}
