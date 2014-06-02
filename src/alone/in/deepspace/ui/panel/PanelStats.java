package alone.in.deepspace.ui.panel;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Image;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.StatsData;
import alone.in.deepspace.engine.ui.ImageView;
import alone.in.deepspace.ui.UserInterface.Mode;
import alone.in.deepspace.util.Constant;

public class PanelStats extends BasePanel {
	private static final int 	FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int 	FRAME_HEIGHT = Constant.WINDOW_HEIGHT;
	private static final Color COLOR_BORDER = new Color(22, 50, 56);

	private ImageView 			_image;
	private StatsData			_stats;

	public PanelStats(Mode mode) {
		super(mode, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 32), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT - 32));
		
		_image = new ImageView();
		_image.setPosition(20, 20);
		addView(_image);
	}

	@Override
	public void onRefresh(int frame) {
		_stats = _ui.getGame().getStats();
		
		int width = 300;
		int height = 200;
		
		Image image = new Image();
		image.create(300, 200);

		{
			int i = 0;
			int zoom = 2;
			for (Integer value: _stats.nbCharacter) {
				int x = i++ * zoom;
				int y = 200 - value * zoom;
	
				image.setPixel(x, y, Color.RED);
				image.setPixel(x+1, y, Color.RED);
				image.setPixel(x, y+1, Color.RED);
				image.setPixel(x+1, y+1, Color.RED);
			}
		}
		
		for (int i = 0; i < width; i++) {
			image.setPixel(i, 0, COLOR_BORDER);
			image.setPixel(i, 1, COLOR_BORDER);
			image.setPixel(i, height-1, COLOR_BORDER);
			image.setPixel(i, height-2, COLOR_BORDER);
		}
		for (int i = 0; i < height; i++) {
			image.setPixel(0, i, COLOR_BORDER);
			image.setPixel(1, i, COLOR_BORDER);
			image.setPixel(width-1, i, COLOR_BORDER);
			image.setPixel(width-2, i, COLOR_BORDER);
		}
		
		Texture texture = new Texture();
		try {
			texture.loadFromImage(image);
		} catch (TextureCreationException e) {
			e.printStackTrace();
		}
		
		_image.setSprite(new Sprite(texture));
	}

	@Override
	protected void onCreate() {
		// TODO Auto-generated method stub
		
	}
}
