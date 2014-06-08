package alone.in.deepspace.engine.serializer;

import hoten.voronoi.Center;
import hoten.voronoi.VoronoiGraph;
import hoten.voronoi.nodename.as3delaunay.Voronoi;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import alone.in.deepspace.Game;
import alone.in.deepspace.manager.WorldManager;
import alone.in.deepspace.model.item.ItemInfo;
import alone.in.deepspace.model.item.WorldResource;
import alone.in.deepspace.util.Constant;

public class WorldFactory {

	private static final int RES_INTERVAL_HEAVY = 6;

	public static void create(WorldManager world, LoadListener loadListener) {
//		loadListener.onUpdate("Create grass 1");
//		addGrass(world);
//		loadListener.onUpdate("Create grass 2");
//		addGrass(world);
//		loadListener.onUpdate("Create grass 3");
//		addGrass(world);
//		loadListener.onUpdate("Create grass 4");
//		addGrass(world);
//		loadListener.onUpdate("Create grass 5");
//		addGrass(world);
//		loadListener.onUpdate("Create grass 6");
//		addGrass(world);
//		loadListener.onUpdate("Create grass 7");
//		addGrass(world);
//		loadListener.onUpdate("Create grass 8");
////		addGrass(world);
//////		loadListener.onUpdate("Create grass 9");
//////		addGrass(world);
//////		loadListener.onUpdate("Create grass 10");
//////		addGrass(world);
//////		loadListener.onUpdate("Create grass 11");
//////		addGrass(world);
//////		loadListener.onUpdate("Create grass 12");
//////		addGrass(world);
//////		loadListener.onUpdate("Create grass 13");
//////		addGrass(world);
//////		loadListener.onUpdate("Create grass 14");
//////		addGrass(world);
//		loadListener.onUpdate("Create mountain");
//		addMountain(world);
		
		loadListener.onUpdate("Add random ressources");
		addRandomRessources(world);
	}

	private static void addRandomRessources(WorldManager world) {
		int resInterval = RES_INTERVAL_HEAVY;
		List<ItemInfo> resourceItemsInfo = new ArrayList<ItemInfo>();
		for (ItemInfo info: Game.getData().items) {
			if (info.onGather != null) {
				resourceItemsInfo.add(info);
			}
		}
		int nbResource = Constant.WORLD_WIDTH * Constant.WORLD_HEIGHT / resInterval;
		for (int i = 0; i < nbResource; i++) {
			int x = (int)(Math.random() * Constant.WORLD_WIDTH);
			int y = (int)(Math.random() * Constant.WORLD_HEIGHT);
			ItemInfo info = resourceItemsInfo.get((int)(Math.random() * resourceItemsInfo.size()));
			world.putResource(info, 0, x, y, 10);
		}
	}

	private static void addMountain(WorldManager worldMap) {
		int[][] map = new int[Constant.WORLD_HEIGHT][Constant.WORLD_WIDTH];
		for (int i = 0; i < Constant.WORLD_WIDTH; i++) {
			for (int j = 0; j < Constant.WORLD_HEIGHT; j++) {
				map[i][j] = 10;
			}
		}
		
        final int width = Constant.WORLD_WIDTH;
        final int height = Constant.WORLD_HEIGHT;
        final int numSites = 10000;
        final long seed = 42;//System.nanoTime();
        final Random r = new Random(seed);
        System.out.println("seed: " + seed);

        //make the intial underlying voronoi structure
        final Voronoi v = new Voronoi(numSites, width, height, r, null);

        //assemble the voronoi strucutre into a usable graph object representing a map
        final VoronoiGraph graph = new VoronoiGraph(v, 20, r) {
			@Override
			protected Enum getBiome(Center p) {
				return null;
			}

			@Override
			protected Color getColor(Enum biome) {
				return null;
			}
        };
        
        for (Center c : graph.centers) {
        	if (c.elevation >= 0.5) {
        		for (Center n: c.neighbors) {
        			if (n.elevation >= 0.5) {
        				setElevation(map, c, n, 0.5);
        			}
        			//n.elevation = 1.5;
        		}
        	}

//        	// clean
//        	if (x+1 >= 0 && y >= 0 && x+1 < width && y < height && map[x+1][y] > e) { map[x+1][y] = e; }
//        	if (x-1 >= 0 && y >= 0 && x-1 < width && y < height && map[x-1][y] > e) { map[x-1][y] = e; }
//        	if (x >= 0 && y+1 >= 0 && x < width && y+1 < height && map[x][y+1] > e) { map[x][y+1] = e; }
//        	if (x >= 0 && y-1 >= 0 && x < width && y-1 < height && map[x][y-1] > e) { map[x][y-1] = e; }
        }
        
//        clean(map);
//        clean(map);
//        clean(map);

		ItemInfo info = Game.getData().getItemInfo("base.rock");
		for (int i = 0; i < Constant.WORLD_WIDTH; i++) {
			for (int j = 0; j < Constant.WORLD_HEIGHT; j++) {
				if (map[i][j] > 15) {
					worldMap.putItem(info, 0, i, j, 999);
				}
			}
		}

		worldMap.cleanRock();
	}

	private static void addGrass(WorldManager worldMap) {
		int DIV = 5;
		
		int[][] map = new int[Constant.WORLD_HEIGHT][Constant.WORLD_WIDTH];
		for (int i = 0; i < Constant.WORLD_WIDTH; i++) {
			for (int j = 0; j < Constant.WORLD_HEIGHT; j++) {
				map[i][j] = 10;
			}
		}
		
        final int width = Constant.WORLD_WIDTH / DIV;
        final int height = Constant.WORLD_HEIGHT / DIV;
        final int numSites = 5000;
        final long seed = System.nanoTime();
        final Random r = new Random();
        System.out.println("seed: " + seed);

        //make the intial underlying voronoi structure
        final Voronoi v = new Voronoi(numSites, width, height, r, null);

        //assemble the voronoi strucutre into a usable graph object representing a map
        final VoronoiGraph graph = new VoronoiGraph(v, 20, r) {
			@Override
			protected Enum getBiome(Center p) {
				return null;
			}

			@Override
			protected Color getColor(Enum biome) {
				return null;
			}
        };
        
        for (Center c : graph.centers) {
        	if (c.elevation >= 0.5) {
        		for (Center n: c.neighbors) {
        			if (n.elevation >= 0.5) {
        				setElevation(map, c, n, 0.5);
        			}
        			//n.elevation = 1.5;
        		}
        	}

//        	// clean
//        	if (x+1 >= 0 && y >= 0 && x+1 < width && y < height && map[x+1][y] > e) { map[x+1][y] = e; }
//        	if (x-1 >= 0 && y >= 0 && x-1 < width && y < height && map[x-1][y] > e) { map[x-1][y] = e; }
//        	if (x >= 0 && y+1 >= 0 && x < width && y+1 < height && map[x][y+1] > e) { map[x][y+1] = e; }
//        	if (x >= 0 && y-1 >= 0 && x < width && y-1 < height && map[x][y-1] > e) { map[x][y-1] = e; }
        }
        
//        clean(map);
//        clean(map);
//        clean(map);

		ItemInfo info = Game.getData().getItemInfo("base.grass");
		int offsetX = (int)(Math.random() * Constant.WINDOW_WIDTH / DIV);
		int offsetY = (int)(Math.random() * Constant.WINDOW_HEIGHT / DIV);
		for (int i = 0; i < Constant.WORLD_WIDTH; i++) {
			for (int j = 0; j < Constant.WORLD_HEIGHT; j++) {
				if (map[i][j] >= 15) {
					worldMap.putItem(info, 0, offsetX + i, offsetY + j, 999);

					worldMap.putItem(info, 0, offsetX + i + 1, offsetY + j, 999);
					worldMap.putItem(info, 0, offsetX + i - 1, offsetY + j, 999);
					worldMap.putItem(info, 0, offsetX + i, offsetY + j + 1, 999);
					worldMap.putItem(info, 0, offsetX + i, offsetY + j - 1, 999);

					worldMap.putItem(info, 0, offsetX + i + 1, offsetY + j + 1, 999);
					worldMap.putItem(info, 0, offsetX + i + 1, offsetY + j - 1, 999);
					worldMap.putItem(info, 0, offsetX + i - 1, offsetY + j + 1, 999);
					worldMap.putItem(info, 0, offsetX + i - 1, offsetY + j - 1, 999);
				}
			}
		}

		
//		for (int i = 0; i < Constant.WORLD_WIDTH; i++) {
//			for (int j = 0; j < Constant.WORLD_HEIGHT; j++) {
//				WorldResource res = worldMap.getRessource(i, j);
//				if (res != null && res.isRock()) {
//					res.setTile(22);
//				}
//			}
//		}
//		
		worldMap.cleanGrass();
	}

	private static void setElevation(int[][] map, Center c, Center n, double e) {
		int fromX = (int) Math.min(c.loc.x, n.loc.x);
		int fromY = (int) Math.min(c.loc.y, n.loc.y);
		int toX = (int) Math.max(c.loc.x, n.loc.x);
		int toY = (int) Math.max(c.loc.y, n.loc.y);
		double offsetX = toX - fromX;
		double offsetY = toY - fromY;
		double maxOffset = Math.max(offsetX, offsetY);

		for (double x = fromX; x < toX; x += offsetX / maxOffset) {
			for (double y = fromY; y < toY; y += offsetY / maxOffset) {
		    	map[(int)x][(int)y] = (int)(e * 100);
		    	System.out.println("x: " + x + ", y: " + y);
			}
		}
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
