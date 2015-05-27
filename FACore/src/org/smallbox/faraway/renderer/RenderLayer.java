package org.smallbox.faraway.renderer;

import org.jsfml.graphics.*;

import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.Renderer;
import org.smallbox.faraway.SpriteModel;
import org.smallbox.faraway.engine.Viewport;
import org.smallbox.faraway.engine.util.Constant;

public class RenderLayer {
	private static final Color	COLOR_CLEAR = new Color(0, 0, 0, 0);
	
	private Sprite 				_spriteCache;
	private RenderTexture 		_textureCache;
	private boolean 			_invalide;

	public RenderLayer(int width, int height) {
		_spriteCache = new Sprite();

		try {
			_textureCache = new RenderTexture();
			_textureCache.create(Constant.WORLD_WIDTH * Constant.TILE_WIDTH, Constant.WORLD_HEIGHT * Constant.TILE_HEIGHT);
			_textureCache.setSmooth(true);
			_textureCache.display();
		} catch (TextureCreationException e) {
			e.printStackTrace();
		}
	}

	public void clear() {
		_textureCache.clear(COLOR_CLEAR);
		_invalide = true;
	}

	public void onDraw(Renderer renderer, RenderEffect renderEffect) {
		if (_invalide) {
			_invalide = false;
			_spriteCache.setTexture(_textureCache.getTexture());
		}

		renderer.draw(_spriteCache, renderEffect);
	}

	public void draw(SpriteModel sprite) {
		_textureCache.draw(sprite.getData());
	}

	public void draw(Text text) {
		_textureCache.draw(text);
	}
}
