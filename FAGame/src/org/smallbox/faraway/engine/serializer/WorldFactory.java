package org.smallbox.faraway.engine.serializer;

import hoten.voronoi.Center;
import hoten.voronoi.VoronoiGraph;
import hoten.voronoi.nodename.as3delaunay.Voronoi;
import org.smallbox.faraway.Game;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.manager.WorldManager;
import org.smallbox.faraway.model.item.ItemInfo;
import org.smallbox.faraway.model.item.WorldResource;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class WorldFactory {

    private static final int RES_INTERVAL_HEAVY = 6;

    private static final int[][] TILE_POSITIONS = new int[][] {
            // Top
            new int[] {
                    2, 0, 2,
                    0, 1, 1,
                    2, 1, 1},

            new int[] {
                    2, 0, 2,
                    1, 1, 1,
                    1, 1, 1},

            new int[] {
                    2, 0, 2,
                    1, 1, 0,
                    1, 1, 2},

            // Middle
            new int[] {
                    2, 1, 2,
                    0, 1, 1,
                    2, 1, 2},

            new int[] {
                    2, 1, 2,
                    1, 1, 1,
                    2, 1, 2},

            new int[] {
                    1, 1, 2,
                    1, 1, 0,
                    1, 1, 2},

            // Bottom
            new int[] {
                    2, 1, 1,
                    0, 1, 1,
                    2, 0, 2},

            new int[] {
                    2, 1, 2,
                    1, 1, 1,
                    2, 0, 2},

            new int[] {
                    1, 1, 2,
                    1, 1, 0,
                    2, 0, 2},

            // Center
            new int[] {
                    1, 1, 1,
                    1, 1, 1,
                    1, 1, 0},

            new int[] {
                    1, 1, 1,
                    1, 1, 1,
                    1, 0, 1},

            new int[] {
                    1, 1, 1,
                    1, 1, 1,
                    0, 1, 1},

            new int[] {
                    1, 1, 0,
                    1, 1, 0,
                    1, 1, 0},

            new int[] {
                    0, 0, 0,
                    0, 1, 0,
                    0, 0, 0},

            new int[] {
                    0, 1, 1,
                    0, 1, 1,
                    0, 1, 1},

            new int[] {
                    1, 1, 0,
                    1, 1, 1,
                    1, 1, 1},

            new int[] {
                    1, 0, 1,
                    1, 1, 1,
                    1, 1, 1},

            new int[] {
                    0, 1, 1,
                    1, 1, 1,
                    1, 1, 1},

            // 3/4
            new int[] {
                    2, 0, 2,
                    0, 1, 0,
                    0, 1, 0},

            new int[] {
                    2, 0, 2,
                    1, 1, 0,
                    2, 0, 2},

            new int[] {
                    2, 0, 2,
                    1, 1, 1,
                    2, 0, 2},

            new int[] {
                    2, 1, 2,
                    0, 1, 0,
                    2, 0, 2},

            new int[] {
                    2, 0, 2,
                    0, 1, 1,
                    2, 0, 2},

            new int[] {
                    2, 1, 2,
                    0, 1, 0,
                    2, 1, 2},
    };

    public static void create(WorldManager world, LoadListener loadListener) {
        loadListener.onUpdate("Create grass 1");
        addGrass(world);
        loadListener.onUpdate("Create grass 2");
        addGrass(world);
        loadListener.onUpdate("Create grass 3");
        addGrass(world);
        loadListener.onUpdate("Create grass 4");
        addGrass(world);
        loadListener.onUpdate("Create grass 5");
        addGrass(world);
        loadListener.onUpdate("Create grass 6");
        addGrass(world);
        loadListener.onUpdate("Create grass 7");
        addGrass(world);
        loadListener.onUpdate("Create grass 8");
        addGrass(world);
        loadListener.onUpdate("Create grass 9");
        addGrass(world);
        loadListener.onUpdate("Create grass 10");
        addGrass(world);
        loadListener.onUpdate("Create grass 11");
        addGrass(world);
        loadListener.onUpdate("Create grass 12");
        addGrass(world);
        loadListener.onUpdate("Create grass 13");
        addGrass(world);
        loadListener.onUpdate("Create grass 14");
        addGrass(world);
        loadListener.onUpdate("Create mountain");
        addMountain(world);

        loadListener.onUpdate("Add random resources");
        addRandomResources(world);
    }

    private static void addRandomResources(WorldManager world) {
        int resInterval = RES_INTERVAL_HEAVY;
        List<ItemInfo> resourceItemsInfo = new ArrayList<ItemInfo>();
        Game.getData().items.stream().filter(info -> info.actions != null).forEach(info -> {
            resourceItemsInfo.addAll(info.actions.stream()
                    .filter(action -> "gather".equals(action.type))
                    .map(action -> info)
                    .collect(Collectors.toList()));
        });
        int nbResource = Constant.WORLD_WIDTH * Constant.WORLD_HEIGHT / resInterval;
        for (int i = 0; i < nbResource; i++) {
            int x = (int)(Math.random() * Constant.WORLD_WIDTH);
            int y = (int)(Math.random() * Constant.WORLD_HEIGHT);
            ItemInfo info = resourceItemsInfo.get((int)(Math.random() * resourceItemsInfo.size()));
            world.putItem(info, x, y, 0, 10);
        }
    }

    private static void addMountain(WorldManager worldMap) {
        int[][] map = new int[Constant.WORLD_HEIGHT][Constant.WORLD_WIDTH];
        for (int i = 0; i < Constant.WORLD_WIDTH; i++) {
            for (int j = 0; j < Constant.WORLD_HEIGHT; j++) {
                map[i][j] = 10;
            }
        }

        final int numSites = 10000;
        final long seed = 42;//System.nanoTime();
        final Random r = new Random(seed);
        System.out.println("seed: " + seed);

        //make the intial underlying voronoi structure
        final Voronoi v = new Voronoi(numSites, Constant.WORLD_WIDTH, Constant.WORLD_HEIGHT, r, null);

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

        ItemInfo info = Game.getData().getItemInfo("base.res_rock");
        for (int i = 0; i < Constant.WORLD_WIDTH; i++) {
            for (int j = 0; j < Constant.WORLD_HEIGHT; j++) {
                if (map[i][j] > 15) {
                    worldMap.putItem(info, i, j, 0, 999);
                }
            }
        }

        cleanRock();
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
                    worldMap.putItem(info, offsetX + i, offsetY + j, 0, 999);

                    worldMap.putItem(info, offsetX + i + 1, offsetY + j, 0, 999);
                    worldMap.putItem(info, offsetX + i - 1, offsetY + j, 0, 999);
                    worldMap.putItem(info, offsetX + i, offsetY + j + 1, 0, 999);
                    worldMap.putItem(info, offsetX + i, offsetY + j - 1, 0, 999);

                    worldMap.putItem(info, offsetX + i + 1, offsetY + j + 1, 0, 999);
                    worldMap.putItem(info, offsetX + i + 1, offsetY + j - 1, 0, 999);
                    worldMap.putItem(info, offsetX + i - 1, offsetY + j + 1, 0, 999);
                    worldMap.putItem(info, offsetX + i - 1, offsetY + j - 1, 0, 999);
                }
            }
        }


//		for (int i = 0; i < Constant.WORLD_WIDTH; i++) {
//			for (int j = 0; j < Constant.WORLD_HEIGHT; j++) {
//				WorldResource res = worldMap.getResource(i, j);
//				if (res != null && res.isRock()) {
//					res.setTile(22);
//				}
//			}
//		}
//		
        cleanGrass();
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

    public static void cleanRock() {
        WorldManager worldManager = Game.getWorldManager();

        for (int f = 0; f < 1; f++) {
            for (int x = Constant.WORLD_WIDTH; x >= 0; x--) {
                for (int y = Constant.WORLD_HEIGHT; y >= 0; y--) {
                    WorldResource resource = worldManager.getResource(x, y, f);
                    if (resource != null && resource.isRock()) {
                        resource.setTile(4);
                        for (int i = 0; i < TILE_POSITIONS.length; i++) {
                            if (
                                    (TILE_POSITIONS[i][0] == 2 || TILE_POSITIONS[i][0] == cheackRock(x-1, y-1)) &&
                                            (TILE_POSITIONS[i][1] == 2 || TILE_POSITIONS[i][1] == cheackRock(x,   y-1)) &&
                                            (TILE_POSITIONS[i][2] == 2 || TILE_POSITIONS[i][2] == cheackRock(x+1, y-1)) &&

                                            (TILE_POSITIONS[i][3] == 2 || TILE_POSITIONS[i][3] == cheackRock(x-1, y)) &&
                                            (TILE_POSITIONS[i][4] == 2 || TILE_POSITIONS[i][4] == cheackRock(x,   y)) &&
                                            (TILE_POSITIONS[i][5] == 2 || TILE_POSITIONS[i][5] == cheackRock(x+1, y)) &&

                                            (TILE_POSITIONS[i][6] == 2 || TILE_POSITIONS[i][6] == cheackRock(x-1, y+1)) &&
                                            (TILE_POSITIONS[i][7] == 2 || TILE_POSITIONS[i][7] == cheackRock(x,   y+1)) &&
                                            (TILE_POSITIONS[i][8] == 2 || TILE_POSITIONS[i][8] == cheackRock(x+1, y+1))
                                    ) {
                                resource.setTile(i);
                            }

                        }
//
//                        // top left
//						if (!isRock(x, y-1) && isRock(x, y+1) && !isRock(x-1, y) && isRock(x+1, y)) {
//                            resource.setTile(0);
//                        }
//
//                        // top
//                        if (!isRock(x, y-1) && isRock(x, y+1) && isRock(x-1, y) && isRock(x+1, y)) {
//                            resource.setTile(1);
//                        }
//
//                        // top right
//						if (!isRock(x, y-1) && isRock(x, y+1) && isRock(x-1, y) && !isRock(x+1, y)) {
//                            resource.setTile(2);
//                        }
//
//                        // left
//                        if (isRock(x, y-1) && isRock(x, y+1) && !isRock(x-1, y) && isRock(x+1, y)) {
//                            resource.setTile(3);
//                        }
//
//                        // center
//                        if (isRock(x, y-1) && isRock(x, y+1) && isRock(x-1, y) && isRock(x+1, y)) {
//                            resource.setTile(4);
//                        }
//
//                        // right
//                        if (isRock(x, y-1) && isRock(x, y+1) && isRock(x-1, y) && !isRock(x+1, y)) {
//                            resource.setTile(5);
//                        }
//
//                        // bottom left
//                        if (isRock(x, y-1) && !isRock(x, y+1) && !isRock(x-1, y) && isRock(x+1, y)) {
//                            resource.setTile(6);
//                        }
//
//                        // bottom
//                        if (isRock(x, y-1) && !isRock(x, y+1) && isRock(x-1, y) && isRock(x+1, y)) {
//                            resource.setTile(7);
//                        }
//
//                        // bottom right
//						if (isRock(x, y-1) && !isRock(x, y+1) && isRock(x-1, y) && !isRock(x+1, y)) {
//                            resource.setTile(8);
//                        }
//
//                        /**
//                         * -#-
//                         * -X-
//                         * -#-
//                         */
//                        if (isRock(x, y-1) && isRock(x, y+1) && !isRock(x-1, y) && !isRock(x+1, y)) {
//                            resource.setTile(14);
//                        }
//
//                        /**
//                         * ---
//                         * #X#
//                         * ---
//                         */
//						if (!isRock(x, y-1) && !isRock(x, y+1) && isRock(x-1, y) && isRock(x+1, y)) {
//                            resource.setTile(11);
//                        }
//
//                        /**
//                         * ---
//                         * #X#
//                         * ---
//                         */
//						if (!isRock(x, y-1) && !isRock(x, y+1) && isRock(x-1, y) && isRock(x+1, y)) {
//                            resource.setTile(11);
//                        }


////							if (notRock(x-1, y-1) && notRock(x+1, y+1)) { resource.setTile(6); } // 16
////							else if (notRock(x+1, y+1) && notRock(x-1, y-1)) { resource.setTile(16); } // 6
////
////							else if (notRock(x+1, y-1) && notRock(x-1, y-1)) { resource.setTile(0); } // 7
////							else if (notRock(x+1, y+1) && notRock(x-1, y+1)) { resource.setTile(0); } // 17
////							else if (notRock(x+1, y+1) && notRock(x+1, y-1)) { resource.setTile(0); } // 27
////							else if (notRock(x-1, y+1) && notRock(x-1, y-1)) { resource.setTile(0); } // 37
////
////							else if (notRock(x+1, y-1)) { resource.setTile(14); setTop(f, x, y+1, 24); } // ok
////							else if (notRock(x-1, y-1)) { resource.setTile(11); setTop(f, x, y+1, 21); } // ok
////							else if (notRock(x+1, y+1)) { resource.setTile(59); setTop(f, x, y-1, 49); setTop(f, x, y-2, 39); } // ok + 55 bellow
////							else if (notRock(x-1, y+1)) { resource.setTile(56); setTop(f, x, y-1, 46); setTop(f, x, y-2, 36); setTop(f, x+1, y, 31); } // ok + 50 beloow
//						}
//
//						// 3 faces
//						else if (isRock(x, y-1) && isRock(x, y+1) && isRock(x-1, y) && notRock(x+1, y)) {
//							WorldResource res = worldManager.getResource(x, y + 2, f);
//							// Rock bellow
//							if (res != null && res.isRock()) {
//								resource.setTile(25); setTop(f, x-1, y, 24);
//							} else {
//								resource.setTile(25);
//							}
//
//						} // ok
//						else if (isRock(x, y-1) && isRock(x, y+1) && notRock(x-1, y) && isRock(x+1, y)) {
//							WorldResource res = worldManager.getResource(x, y - 1, f);
//							if (res != null && res.isRock() && (res.getTile() == 66 || res.getTile() == 56 || res.getTile() == 50)) {
//								resource.setTile(25); setTop(f, x+1, y, 31);
//							} else {
//								res = worldManager.getResource(x, y + 1, f);
//								if (res != null && res.isRock() && (res.getTile() == 66 || res.getTile() == 50)) {
//									resource.setTile(20); setTop(f, x+1, y, 31);
//								} else {
//									resource.setTile(20);
//								}
//							}
//						} // ok
//						else if (isRock(x, y-1) && notRock(x, y+1) && isRock(x-1, y) && isRock(x+1, y)) {
//							WorldResource res = worldManager.getResource(x - 1, y, f);
//							if (res != null && res.isRock() && res.getTile() == 68) {
//								resource.setTile(67); setTop(f, x, y-1, 57); setTop(f, x, y-2, 47); setTop(f, x, y-3, 37);
//							} else {
//								resource.setTile(68); setTop(f, x, y-1, 58); setTop(f, x, y-2, 48); setTop(f, x, y-3, 38);
//							}
//						} // ok + 62 bellow
//						else if (notRock(x, y-1) && isRock(x, y+1) && isRock(x-1, y) && isRock(x+1, y)) { resource.setTile(2); setTop(f, x, y+1, 12); setTop(f, x, y+2, 22); } // ok
//
//						// 2 faces
//						else if (isRock(x, y-1) && isRock(x, y+1) && notRock(x-1, y) && notRock(x+1, y)) { resource.setTile(0); } // 36
//						else if (notRock(x, y-1) && notRock(x, y+1) && isRock(x-1, y) && isRock(x+1, y)) { resource.setTile(0); } // 26
//						else if (isRock(x, y-1) && notRock(x, y+1) && notRock(x-1, y) && isRock(x+1, y)) { resource.setTile(66); setTop(f, x, y-1, 50); } // 14
//						else if (isRock(x, y-1) && notRock(x, y+1) && isRock(x-1, y) && notRock(x+1, y)) { resource.setTile(69); } // 143
//						else if (notRock(x, y-1) && isRock(x, y+1) && isRock(x-1, y) && notRock(x+1, y)) { resource.setTile(15); } // ok
//						else if (notRock(x, y-1) && isRock(x, y+1) && notRock(x-1, y) && isRock(x+1, y)) { resource.setTile(10); } // ok
//
//						// 1 face
//						else if (isRock(x, y-1) && notRock(x, y+1) && notRock(x-1, y) && notRock(x+1, y)) { resource.setTile(0); } // 34
//						else if (notRock(x, y-1) && isRock(x, y+1) && notRock(x-1, y) && notRock(x+1, y)) { resource.setTile(0); } // 35
//						else if (notRock(x, y-1) && notRock(x, y+1) && isRock(x-1, y) && notRock(x+1, y)) { resource.setTile(0); } // 25
//						else if (notRock(x, y-1) && notRock(x, y+1) && notRock(x-1, y) && isRock(x+1, y)) { resource.setTile(0); } // 24

                    }
                }
            }
        }
    }

    private static void cleanGrass() {
        WorldManager worldManager = Game.getWorldManager();

        for (int f = 0; f < 1; f++) {
            for (int x = Constant.WORLD_WIDTH; x >= 0; x--) {
                for (int y = Constant.WORLD_HEIGHT; y >= 0; y--) {
                    WorldResource structure = worldManager.getResource(x, y, f);
                    if (structure != null && structure.isGrass()) {
                        // 4 faces
                        if (isGrass(x, y-1) && isGrass(x, y+1) && isGrass(x-1, y) && isGrass(x+1, y)) {
                            // inside corner
                            if (notGrass(x-1, y-1)) { structure.setTile(3); }
                            else if (notGrass(x+1, y-1)) { structure.setTile(4); }
                            else if (notGrass(x-1, y+1)) { structure.setTile(13); }
                            else if (notGrass(x+1, y+1)) { structure.setTile(14); }
                            else { structure.setTile(11); }
                        }

                        // 3 faces
                        if (notGrass(x, y-1) && isGrass(x, y+1) && isGrass(x-1, y) && isGrass(x+1, y)) { structure.setTile(1); }
                        if (isGrass(x, y-1) && notGrass(x, y+1) && isGrass(x-1, y) && isGrass(x+1, y)) { structure.setTile(21); }
                        if (isGrass(x, y-1) && isGrass(x, y+1) && notGrass(x-1, y) && isGrass(x+1, y)) { structure.setTile(10); }
                        if (isGrass(x, y-1) && isGrass(x, y+1) && isGrass(x-1, y) && notGrass(x+1, y)) { structure.setTile(12); }

                        // 2 faces
                        if (notGrass(x, y-1) && isGrass(x, y+1) && notGrass(x-1, y) && isGrass(x+1, y)) { structure.setTile(0); }
                        if (isGrass(x, y-1) && notGrass(x, y+1) && notGrass(x-1, y) && isGrass(x+1, y)) { structure.setTile(20); }
                        if (isGrass(x, y-1) && notGrass(x, y+1) && isGrass(x-1, y) && notGrass(x+1, y)) { structure.setTile(22); }
                        if (notGrass(x, y-1) && isGrass(x, y+1) && isGrass(x-1, y) && notGrass(x+1, y)) { structure.setTile(2); }
                    }
                }
            }
        }
    }

    private static void setTop(int f, int x, int y, int tile) {
        WorldResource topres = Game.getWorldManager().getResource(x, y, f);
        if (topres != null && topres.isRock() && topres.getTile() == 0) {
            topres.setTile(tile);
        }
    }

    private static boolean notRock(int x, int y) {
        if (x < 0 || x >= Constant.WORLD_WIDTH || y < 0 || y >= Constant.WORLD_HEIGHT) {
            return false;
        }
        if (Game.getWorldManager().getResource(x, y) != null && Game.getWorldManager().getResource(x, y).isRock()) {
            return false;
        }
        return true;
    }

    private static boolean notGrass(int x, int y) {
        if (x < 0 || x >= Constant.WORLD_WIDTH || y < 0 || y >= Constant.WORLD_HEIGHT) {
            return false;
        }
        if (Game.getWorldManager().getResource(x, y) != null && Game.getWorldManager().getResource(x, y).isGrass()) {
            return false;
        }
        return true;
    }

    private static boolean isRock(int x, int y) {
        if (x < 0 || x >= Constant.WORLD_WIDTH || y < 0 || y >= Constant.WORLD_HEIGHT) {
            return true;
        }
        if (Game.getWorldManager().getResource(x, y) != null && Game.getWorldManager().getResource(x, y).isRock()) {
            return true;
        }
        return false;
    }

    private static int cheackRock(int x, int y) {
        if (x < 0 || x >= Constant.WORLD_WIDTH || y < 0 || y >= Constant.WORLD_HEIGHT) {
            return 1;
        }
        if (Game.getWorldManager().getResource(x, y) != null && Game.getWorldManager().getResource(x, y).isRock()) {
            return 1;
        }
        return 0;
    }

    private static boolean isGrass(int x, int y) {
        if (x < 0 || x >= Constant.WORLD_WIDTH || y < 0 || y >= Constant.WORLD_HEIGHT) {
            return true;
        }
        if (Game.getWorldManager().getResource(x, y) != null && Game.getWorldManager().getResource(x, y).isGrass()) {
            return true;
        }
        return false;
    }

}
