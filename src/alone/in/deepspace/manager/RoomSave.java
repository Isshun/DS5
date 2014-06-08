package alone.in.deepspace.manager;

import java.util.ArrayList;
import java.util.List;

public class RoomSave {

	public static class RoomSaveArea {
		public int 				x;
		public int 				y;
		
		public RoomSaveArea(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	public int 					type;
	public String				culture;
	public ArrayList<Integer> 	occupants;
	public List<RoomSaveArea>		areas;
	
	public RoomSave() {
		this.areas = new ArrayList<RoomSaveArea>();
	}
}
