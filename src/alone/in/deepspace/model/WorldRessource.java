package alone.in.deepspace.model;

public class WorldRessource extends UserItem {
	private int		_value;
	private int 	_tile;
	private int _doubleRender;

	public WorldRessource(ItemInfo info, int id) {
		super(info, id);
	}

	public WorldRessource(ItemInfo info) {
		super(info);
	}

	public void addValue(int value) {_value += value;}

	public void	setValue(int value) {_value = value;}
	
	public int	getValue(int max) {return Math.min(_value, max);}
	public int	getValue() { return _value; }
	

	public boolean isRock() {
		return "base.rock".equals(_info.name);
	}

	public void setTile(int tile) {
		_tile = tile;
	}

	public int getTile() {
		return _tile;
	}

	public void setDoubleRender(int b) {
		_doubleRender = b;
	}

	public int getDoubleRender() {
		return _doubleRender;
	}

}
