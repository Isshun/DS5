package alone.in.deepspace.World;


public class WorldRessource extends BaseItem {
	public WorldRessource(Type type, int id) {
		super(type, id);
		// TODO Auto-generated constructor stub
	}

	public void addValue(int value) {_value += value;}

	public void	setType(int type) {_type = type;}
	public void	setValue(int value) {_value = value;}

	public int	getValue(int max) {return Math.min(_value, max);}
	public int getValue() { return _value; }

	int	_type;
	int	_value;

}
