package alone.in.deepspace.World;


public class WorldRessource extends BaseItem {
	private int		_type;
	private int		_value;

	public WorldRessource(Type type, int id) {
		super(type, id);
	}

	public void addValue(int value) {_value += value;}

	public void	setType(int type) {_type = type;}
	public void	setValue(int value) {_value = value;}

	public int	getValue(int max) {return Math.min(_value, max);}
	public int	getValue() { return _value; }
}
