package alone.in.deepspace;

public class WorldArea extends BaseItem {

	private BaseItem 		_item;
	private WorldRessource 	_ressource;
	private int				_oxygen;

	public WorldArea(BaseItem.Type type, int id) {
		super(type, id);
		_item = null;
		_ressource = null;
		_oxygen = (int) (Math.random() % 100);
	  }
	
	public void				setItem(BaseItem item) { _item = item; }
	public void				setRessource(WorldRessource ressource) { _ressource = ressource; }
	public void				setOxygen(int oxygen) { _oxygen = oxygen; }

	public BaseItem			getItem() { return _item; }
	public WorldRessource	getRessource() { return _ressource; }
	public int				getOxygen() { return _oxygen; }
}
