package alone.in.deepspace.World;

import alone.in.deepspace.World.BaseItem.Type;


public class WorldRessource extends BaseItem {
	private int		_type;
	private int		_value;

	public WorldRessource(Type type, int id) {
		super(type, id);
	}

	public WorldRessource(Type type) {
		super(type);
	}

	public void addValue(int value) {_value += value;}

	public void	setType(int type) {_type = type;}
	public void	setValue(int value) {_value = value;}

	public int	getValue(int max) {return Math.min(_value, max);}
	public int	getValue() { return _value; }
}
