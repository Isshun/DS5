package alone.in.deepspace.World;


public class WorldArea {

	private UserItem 		_item;
	private StructureItem 	_structure;
	private WorldRessource 	_ressource;
	private int				_oxygen;
	private int 			_zoneId;
	private int 			_roomId;
	private int				_x;
	private int				_y;
	private String			_name;

	public WorldArea(BaseItem.Type type, int id) {
		_oxygen = (int) (Math.random() % 100);
		_name = "area";
	  }
	
	public void				setItem(UserItem item) { _item = item; }
	public void				setStructure(StructureItem structure) { _structure = structure; }
	public void				setRessource(WorldRessource ressource) { _ressource = ressource; }
	public void				setOxygen(int oxygen) { _oxygen = oxygen; }
	public void				setRoomId(int roomId) { _roomId = roomId; }
	public void				setZoneId(int zoneId) { _zoneId = zoneId; }

	public UserItem			getItem() { return _item; }
	public StructureItem	getStructure() { return _structure; }
	public WorldRessource	getRessource() { return _ressource; }
	public int				getOxygen() { return _oxygen; }
//	  public int		getZoneId() { return _zoneId; }
	public int				getRoomId() { return _roomId; }
	public int				getX() { return _x; }
	public int				getY() { return _y; }
	public String 			getName() { return _name; }

}
