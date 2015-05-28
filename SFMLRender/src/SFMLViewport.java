import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.Transform;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.Viewport;
import org.smallbox.faraway.engine.util.Constant;

public class SFMLViewport extends Viewport {
	private static final int ANIM_FRAME = 10;

	private Transform	_transform;
	private int 		_posX;
	private int 		_posY;
	private int 		_lastPosX;
	private int 		_lastPosY;
	private int 		_width;
	private int 		_toScale;
	private int 		_height;
	private int 		_fromScale;
	private int 		_scaleAnim;

	private RenderStates _render;

	public SFMLViewport(int x, int y) {
		_posX = x;
		_posY = y;
		_lastPosX = 0;
		_lastPosY = 0;
		_width = Constant.WINDOW_WIDTH - Constant.PANEL_WIDTH;
		_height = Constant.WINDOW_HEIGHT;
		_toScale = 0;
		_transform = new Transform();
		_transform = Transform.translate(_transform, x, y);
	}

    @Override
	public int   getPosX() { return _posX; }

    @Override
	public int   getPosY() { return _posY; }

	public int   getWidth() { return _width; }
	public int   getHeight() { return _height; }

    @Override
	public void  setScale(int delta, int centerX, int centerY) {
		_fromScale = _toScale;
		_toScale = Math.min(Math.max(_toScale + delta, -4), 4);
		if (_fromScale == _toScale) {
			return;
		}
		
		_scaleAnim = 0;
		
		// Update transform
		float fromValue = getScale(_fromScale);
		float toValue = getScale(_toScale);
		if (_scaleAnim < ANIM_FRAME) {
			_scaleAnim++;
		}
		
		centerX = _width / 2;
		centerY = _height / 2;
		
		int offsetX = _posX - centerX;
		int offsetXAfter = (int)(offsetX * (toValue / fromValue));
		_posX = centerX + offsetXAfter;

		int offsetY = _posY - centerY;
		int offsetYAfter = (int)(offsetY * (toValue / fromValue));
		_posY = centerY + offsetYAfter;
		
		_transform = new Transform();
		_transform = Transform.translate(_transform, _posX, _posY);
		_transform = Transform.scale(_transform, toValue, toValue);

		_lastPosX = _posX;
		_lastPosY = _posY;

		_render = null;
	}

	public void    startMove(int x, int y) {
		_lastPosX = x;
		_lastPosY = y;
	}

	public void    update(int x, int y) {
		if (x != 0 || y != 0) {
			// Update transform
			_transform = Transform.translate(_transform, x-_lastPosX, y-_lastPosY);

			_posX -= (_lastPosX - x);
			_posY -= (_lastPosY - y);
			_lastPosX = x;
			_lastPosY = y;

			_render = null;
		}
	}

	public Transform  getViewTransformBackground(Transform transform) {
		float scale = getScale(_toScale);
		transform = Transform.translate(transform, _posX / 10 - 250, _posY / 10 - 50);
		transform = Transform.scale(transform, 1+(scale/20), 1+(scale/20));
		return transform;
	}

	@Override
	public float  getScale() {
		return getScale(_toScale);
	}

    @Override
	public float  getMinScale() {
		return getScale(Math.min(_toScale, _fromScale));
	}

    @Override
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
		int posX = _posX + x * Constant.TILE_WIDTH;
		return (int) (posX * getScale(_toScale));
	}

	public int getRealPosY(int y) {
		int posY = _posY + y * Constant.TILE_HEIGHT;
		return (int) (posY * getScale(_toScale));
	}

	public RenderStates getRender() {
		if (_render == null) {
			_render = new RenderStates(_transform);
		}
		return _render;
	}

	public RenderEffect getRenderEffect() {
		SFMLRenderEffect effect = new SFMLRenderEffect();
		effect.setTransform(_transform);
		return effect;
	}
}
