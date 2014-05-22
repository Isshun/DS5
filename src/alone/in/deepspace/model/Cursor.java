package alone.in.deepspace.model;

import alone.in.deepspace.util.Constant;

public class Cursor {

	public void		setPos(int x, int y) { _x = x; _y = y; }
	public void		setMousePos(int x, int y) { _x = x / Constant.TILE_WIDTH; _y = y / Constant.TILE_HEIGHT; }
	public int		getX() { return _x; }
	public int		getY() { return _y; }

	private int		_x;
	private int		_y;

}
