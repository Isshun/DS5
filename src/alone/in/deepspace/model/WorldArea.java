package alone.in.deepspace.model;



public class WorldArea {

	private UserItem 		_item;
	private StructureItem 	_structure;
	private WorldRessource 	_ressource;
	private int				_oxygen;
	private int				_x;
	private int				_y;
	private double 			_light;
	private int 			_lightPass;

	public WorldArea(int x, int y) {
		_oxygen = (int) (Math.random() % 100);
		_light = 0;
		_x = x;
		_y = y;
	}

	public void 			addLight(double value) { _light += value; }

	public void				setItem(UserItem item) { _item = item; }
	public void				setStructure(StructureItem structure) { _structure = structure; }
	public void				setRessource(WorldRessource ressource) { _ressource = ressource; }
	public void				setOxygen(int oxygen) { _oxygen = oxygen; }
	public void 			setLight(double value) { _light = value; }
	public void 			setLightPass(int pass) { _lightPass = pass; }

	public UserItem			getItem() { return _item; }
	public StructureItem	getStructure() { return _structure; }
	public WorldRessource	getRessource() { return _ressource; }
	public int				getOxygen() { return _oxygen; }
	public int				getX() { return _x; }
	public int				getY() { return _y; }
	public double 			getLight() { return _light; }
	public int 				getLightPass() { return _lightPass; }
}
