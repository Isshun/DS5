package alone.in.deepspace.engine.renderer;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.TextureCreationException;

import alone.in.deepspace.util.Constant;

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

	public void onDraw(RenderWindow app, RenderStates render) {
		if (_invalide) {
			_invalide = false;
			_spriteCache.setTexture(_textureCache.getTexture());
		}

		app.draw(_spriteCache, render);
	}

	public void draw(Drawable drawable) {
		_textureCache.draw(drawable);
	}

}
