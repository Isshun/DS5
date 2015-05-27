package org.smallbox.faraway.engine.ui;

import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.Renderer;
import org.smallbox.faraway.SpriteModel;

public class ImageView extends View {

	private SpriteModel _image;

	public ImageView() {
		super(0, 0);
	}
	
	public ImageView(SpriteModel icon) {
		super(icon.getWidth(), icon.getHeight());
		
		_image = icon;
	}
	
	@Override
	public void onDraw(Renderer renderer, RenderEffect effect) {
		if (_image != null) {
			_image.setPosition((int)(_pos.x + _paddingLeft), (int)(_pos.y + _paddingTop));
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
}

