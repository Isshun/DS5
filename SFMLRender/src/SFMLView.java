import org.jsfml.graphics.RectangleShape;
import org.jsfml.system.Vector2f;
import org.smallbox.faraway.Color;
import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.engine.ui.*;

import java.awt.*;


public class SFMLView extends View {
	private RectangleShape		_background;
	private RectangleShape		_borders[];
	private org.jsfml.graphics.Color _borderColor;
	protected Color				_backgroundColor;
	private Object 				_data;
    private org.jsfml.graphics.Color        _backgroundColorJSFML;

    public SFMLView(int width, int height) {
		super(width, height);
	}

    @Override
	protected void onDraw(GFXRenderer renderer, RenderEffect effect) {
		if (_backgroundColor != null) {
			if (_background == null) {
				_background = new RectangleShape(new Vector2f(_width, _height));
			}
            ((SFMLRenderer)renderer).draw(_background, effect);
		}
	}

    @Override
	public void draw(GFXRenderer renderer, RenderEffect effect) {
		if (_isVisible == false) {
			return;
		}

		if (_invalid) {
			refresh();
		}
		
		if (_background != null) {
            ((SFMLRenderer)renderer).draw(_background, effect);
		}

		onDraw(renderer, effect);

		// Borders
		if (_borders != null) {
            ((SFMLRenderer)renderer).draw(_borders[0], effect);
            ((SFMLRenderer)renderer).draw(_borders[2], effect);
            ((SFMLRenderer)renderer).draw(_borders[1], effect);
            ((SFMLRenderer)renderer).draw(_borders[3], effect);
		}
	}

    @Override
	public void refresh() {
		// Background
		if (_backgroundColor != null) {
			_background = new RectangleShape();
			_background.setSize(new Vector2f(_width, _height));
			_background.setPosition(new Vector2f(_x, _y));
			_background.setFillColor(_backgroundColorJSFML);
		} else {
			_background = null;
		}
	
		// Border
		if (_borderColor != null) {
			_borders = new RectangleShape[4];
			_borders[0] = new RectangleShape();
			_borders[0].setPosition(new Vector2f(_x, _y));
			_borders[0].setSize(new Vector2f(_width, _borderSize));
			_borders[0].setFillColor(_borderColor);

			_borders[1] = new RectangleShape();
			_borders[1].setPosition(_x, _y + _height);
			_borders[1].setSize(new Vector2f(_width, _borderSize));
			_borders[1].setFillColor(_borderColor);

			_borders[2] = new RectangleShape();
			_borders[2].setPosition(new Vector2f(_x, _y));
			_borders[2].setSize(new Vector2f(_borderSize, _height));
			_borders[2].setFillColor(_borderColor);

			_borders[3] = new RectangleShape();
			_borders[3].setPosition(_x + _width - _borderSize, _y);
			_borders[3].setSize(new Vector2f(_borderSize, _height));
			_borders[3].setFillColor(_borderColor);
		} else {
			_borders = null;
		}

		_invalid = false;
	}

    @Override
	public void setBackgroundColor(Color color) {
		_backgroundColorJSFML = new org.jsfml.graphics.Color(color.r, color.g, color.b);
		_backgroundColor = color;
		_invalid = true;
	}


    @Override
    public void setBorderColor(Color color) {
        _borderColor = color != null ? new org.jsfml.graphics.Color(color.r, color.g, color.b) : null;
		_invalid = true;
	}

	@Override
	public int getContentWidth() {
		return _width;
	}

	@Override
	public int getContentHeight() {
		return _height;
	}
}
