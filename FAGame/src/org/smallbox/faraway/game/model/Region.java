package org.smallbox.faraway.game.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Region {
	public static int	_count;
	public int 			id;
	public int 			fromX;
	public int 			fromY;
	public int 			toX;
	public int 			toY;
	public List<Door>	_doors;
//	private Map<Integer, Vector<Position>>	_links;
	
	public static class Door {

		public Set<Door>	doors;
		public int			id;
		public int			x;
		public int			y;
		
		public Door(int x, int y) {
			this.id = _count++;
			this.x = x;
			this.y = y;
			this.doors = new HashSet<Door>();
		}
		
		public void addBridge(Door door) {
			doors.add(door);
		}
		
	}

	public Region(int id, int fromX, int fromY, int toX, int toY) {
//		Log.info("onCreate region: " + id);
//		
//		this._doors = new ArrayList<Door>();
//		this.id = id;
//		this.fromX = fromX;
//		this.fromY = fromY;
//		this.toX = toX;
//		this.toY = toY;
//		
//		WorldManager worldMap = Game.getWorldManager();
//		for (int x = fromX; x <= toX; x++) {
//			for (int y = fromY; y <= toY; y++) {
//				if (x == fromX || x == toX || y == fromY || y == toY) {
//					StructureItem structure = worldMap.getStructure(x, y);
//					if (structure != null && structure.isFloor()) {
//						Log.debug("Region #" + id + ": door at pos " + x + "x" + y);
//						_doors.add(new Door(x, y));
//					}
//				}
//			}
//		}
//
//		for (Door d1: _doors) {
//			for (Door d2: _doors) {
//				if (d1 != d2) {
//					PathManager.getInstance().getInnerPath(d1, d2);
//				}
//			}
//		}
	}
	
	@Override
	public String toString() {
		return id + "(x:" + fromX + "->" + toX + ", y: " + fromY + "->" + toY + ")";
	}
}
