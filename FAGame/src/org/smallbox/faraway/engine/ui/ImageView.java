package org.smallbox.faraway.engine.ui;

import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.SpriteModel;

public abstract class ImageView extends View {

	private SpriteModel _image;
	protected String 	_path;
	protected double 		_scaleX = 1;
	protected double 		_scaleY = 1;

	public ImageView() {
		super(0, 0);
	}
	
	public ImageView(SpriteModel icon) {
		super(icon.getWidth(), icon.getHeight());
		
		_image = icon;
	}
	
	@Override
	public void onDraw(GFXRenderer renderer, RenderEffect effect) {
		if (_image != null) {
			_image.setPosition((int)(_x + _paddingLeft), (int)(_y + _paddingTop));
			renderer.draw(_image, effect);
		}
	}

	public void setPosition(int x, int y) {
		super.setPosition(x, y);
		if (_image != null) {
			_image.setPosition(x, y);
		}
	}

	public void setImage(SpriteModel icon) {
		if (icon != null) {
			setSize(icon.getWidth(), icon.getHeight());
		}
		_image = icon;
	}

	public void setSprite(SpriteModel sprite) {
		_image = sprite;
	}

	public void setImagePath(String path) {
		_path = path;
	}

	public void setScale(double scaleX, double scaleY) {
		_scaleX = scaleX;
		_scaleY = scaleY;
	}
}

