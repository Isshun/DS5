package alone.in.deepspace;

public class WorldRessource {
	public void addValue(int value) {_value += value;}

	public void	setType(int type) {_type = type;}
	public void	setValue(int value) {_value = value;}

	int	getType() {return _type;}
	int	getValue(int max) {return Math.min(_value, max);}

	int	_type;
	int	_value;

}
