package alone.in.DeepSpace.World;

import alone.in.DeepSpace.Models.BaseItem;

public class WorldArea extends BaseItem {

	private UserItem 		_item;
	private StructureItem 	_structure;
	private WorldRessource 	_ressource;
	private int				_oxygen;

	public WorldArea(BaseItem.Type type, int id) {
		super(type, id);
		
		_oxygen = (int) (Math.random() % 100);
	  }
	
	public void				setItem(UserItem item) { _item = item; }
	public void				setStructure(StructureItem item) { _structure = item; }
	public void				setRessource(WorldRessource ressource) { _ressource = ressource; }
	public void				setOxygen(int oxygen) { _oxygen = oxygen; }

	public UserItem			getItem() { return _item; }
	public StructureItem	getStructure() { return _structure; }
	public WorldRessource	getRessource() { return _ressource; }
	public int				getOxygen() { return _oxygen; }
}
