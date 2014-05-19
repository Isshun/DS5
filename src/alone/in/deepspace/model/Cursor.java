package alone.in.deepspace.model;

import alone.in.deepspace.Utils.Constant;

public class Cursor {

	public void		setPos(int x, int y) { _x = x; _y = y; }
	public void		setMousePos(int x, int y) { _x = x / Constant.TILE_SIZE; _y = y / Constant.TILE_SIZE; }
	public int		getX() { return _x; }
	public int		getY() { return _y; }

	private int		_x;
	private int		_y;

}
