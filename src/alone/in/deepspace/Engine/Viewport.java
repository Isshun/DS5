package alone.in.deepspace.Engine;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Transform;

import alone.in.deepspace.Utils.Constant;

public class Viewport {
	private static final int ANIM_FRAME = 10;

	private int _posX;
	private int _posY;
	private int _lastPosX;
	private int _lastPosY;
	private int _width;
	private int _toScale;
	private int _height;
	private int _fromScale;
	private int _scaleAnim;

	public Viewport(RenderWindow app) {
		_posX = 0;
		_posY = 0;
		_lastPosX = 0;
		_lastPosY = 0;
		_width = Constant.WINDOW_WIDTH - Constant.UI_WIDTH;
		_height = Constant.WINDOW_HEIGHT - Constant.UI_HEIGHT;
		_toScale = 0;
	}

	public int   getPosX() { return _posX; }
	public int   getPosY() { return _posY; }
	public int   getWidth() { return _width; }
	public int   getHeight() { return _height; }

	public void  setScale(int delta) {
		_scaleAnim = 0;
		_fromScale = _toScale;
		_toScale = Math.min(Math.max(_toScale + delta, -4), 4);
	}

	public void    startMove(int x, int y) {
		_lastPosX = x;
		_lastPosY = y;
	}

	public void    update(int x, int y) {
		_posX -= (_lastPosX - x);
		_posY -= (_lastPosY - y);
		_lastPosX = x;
		_lastPosY = y;
	}

	public Transform  getViewTransform(Transform transform) {
		float fromValue = getScale(_fromScale);
		float toValue = getScale(_toScale);
		if (_scaleAnim < ANIM_FRAME) {
			_scaleAnim++;
		}
		float scale = fromValue + ((toValue - fromValue) / ANIM_FRAME * _scaleAnim);

		transform = Transform.translate(transform, Constant.UI_WIDTH + _posX, Constant.UI_HEIGHT + _posY);
		transform = Transform.scale(transform, scale, scale);

		return transform;
	}

	public Transform  getViewTransformBackground(Transform transform) {
		float scale = getScale(_toScale);
		transform = Transform.translate(transform, _posX / 10 - 250, _posY / 10 - 50);
		transform = Transform.scale(transform, 1+(scale/20), 1+(scale/20));
		return transform;
	}

	public float  getScale() {
		return getScale(_toScale);
	}

	public float  getMinScale() {
		return getScale(Math.min(_toScale, _fromScale));
	}

	public float  getMaxScale() {
		return getScale(Math.max(_toScale, _fromScale));
	}

	public float  getScale(int scale) {
		switch (scale) {
		case -4: return 0.5f;
		case -3: return 0.625f;
		case -2: return 0.75f;
		case -1: return 0.875f;
		default: return 1.0f;
		case 1: return 1.125f;
		case 2: return 1.25f;
		case 3: return 1.375f;
		case 4: return 1.5f;
		}
	}

	public int getRealPosX(int x) {
		//x *= _toScale;
		int posX = _posX + x * Constant.TILE_SIZE;
		return Constant.UI_WIDTH + (int) (posX * getScale(_toScale));
	}

	public int getRealPosY(int y) {
		int posY = _posY + y * Constant.TILE_SIZE;
		return Constant.UI_HEIGHT + (int) (posY * getScale(_toScale));
	}

}
