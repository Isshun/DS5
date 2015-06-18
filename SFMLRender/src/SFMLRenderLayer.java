import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.TextureCreationException;
import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.engine.SpriteModel;
import org.smallbox.faraway.ui.engine.TextView;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.engine.renderer.RenderLayer;

public class SFMLRenderLayer extends RenderLayer {
	private static final Color COLOR_CLEAR = new Color(0, 0, 0, 0);
	
	private Sprite              _spriteCache;
	private RenderTexture       _textureCache;
	private boolean 			_invalide;

	public SFMLRenderLayer(int width, int height) {
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

	public void onDraw(GFXRenderer renderer, RenderEffect renderEffect) {
		if (_invalide) {
			_invalide = false;
			_spriteCache.setTexture(_textureCache.getTexture());
		}

		((SFMLRenderer)renderer).draw(_spriteCache, renderEffect);
	}

	public void draw(SpriteModel sprite) {
		_textureCache.draw(((SFMLSprite) sprite).getData());
	}

	public void draw(TextView text) {
		_textureCache.draw(((SFMLTextView)text).getText());
	}
}
