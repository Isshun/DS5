package alone.in.deepspace.World;

import alone.in.deepspace.Models.Room;


public class WorldArea {

	private UserItem 		_item;
	private StructureItem 	_structure;
	private WorldRessource 	_ressource;
	private int				_oxygen;
	private Room.Type		_roomType;
	private int 			_roomId;
	private int				_x;
	private int				_y;
	private String			_name;

	public WorldArea(BaseItem.Type type, int x, int y) {
		_oxygen = (int) (Math.random() % 100);
		_x = x;
		_y = y;
		_name = "area";
	}

	public void				setItem(UserItem item) { _item = item; }
	public void				setStructure(StructureItem structure) { _structure = structure; }
	public void				setRessource(WorldRessource ressource) { _ressource = ressource; }
	public void				setOxygen(int oxygen) { _oxygen = oxygen; }
	public void				setRoomId(int roomId) { _roomId = roomId; }
	public void				setZoneId(Room.Type roomType) { _roomType = roomType; }

	public UserItem			getItem() { return _item; }
	public StructureItem	getStructure() { return _structure; }
	public WorldRessource	getRessource() { return _ressource; }
	public int				getOxygen() { return _oxygen; }
	public int				getRoomId() { return _roomId; }
	public int				getX() { return _x; }
	public int				getY() { return _y; }
	public String 			getName() { return _name; }
	public Room.Type		getRoomType() { return _roomType; }

}
