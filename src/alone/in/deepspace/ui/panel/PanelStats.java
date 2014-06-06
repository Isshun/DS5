package alone.in.deepspace.ui.panel;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Image;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.StatsData;
import alone.in.deepspace.engine.ui.ImageView;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.manager.StatsManager;
import alone.in.deepspace.ui.UserInterface.Mode;
import alone.in.deepspace.util.Constant;

public class PanelStats extends BasePanel {
	private static final int 	FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int 	FRAME_HEIGHT = Constant.WINDOW_HEIGHT;
	private static final Color COLOR_BORDER = new Color(22, 50, 56);
	private static final int WIDTH = 300;
	private static final int HEIGHT = 200;
	private static final int NB_DATA_MAX = 5;

	private ImageView 			_imageView;
	private StatsManager		_stats;
	private Image 				_image;
	private TextView[] 			_labels;

	public PanelStats(Mode mode) {
		super(mode, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 32), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT - 32), true);
	}

	@Override
	protected void onCreate() {
		_image = new Image();
		_image.create(300, 200);
		
		_imageView = new ImageView();
		_imageView.setPosition(20, 42);
		addView(_imageView);
		
		_labels = new TextView[NB_DATA_MAX];
		for (int i = 0; i < NB_DATA_MAX; i++) {
			_labels[i] = new TextView();
			_labels[i].setPosition(20 + i * 80, 252);
			_labels[i].setCharacterSize(FONT_SIZE);
			addView(_labels[i]);
		}
		
		TextView lbCharacter = new TextView();
		lbCharacter.setCharacterSize(FONT_SIZE_TITLE);
		lbCharacter.setString("Stats");
		lbCharacter.setPosition(20, 6);
		addView(lbCharacter);
	}

	@Override
	public void onRefresh(int frame) {
		_stats = _ui.getGame().getStatsManager();
//		_image.
		
		addData(0, _stats.nbCharacter, Color.RED);
		addData(1, _stats.nbCouple, Color.GREEN);
		addData(2, _stats.nbChild, Color.BLUE);
		
		for (int i = 0; i < WIDTH; i++) {
			_image.setPixel(i, 0, COLOR_BORDER);
			_image.setPixel(i, 1, COLOR_BORDER);
			_image.setPixel(i, HEIGHT-1, COLOR_BORDER);
			_image.setPixel(i, HEIGHT-2, COLOR_BORDER);
		}
		for (int i = 0; i < HEIGHT; i++) {
			_image.setPixel(0, i, COLOR_BORDER);
			_image.setPixel(1, i, COLOR_BORDER);
			_image.setPixel(WIDTH-1, i, COLOR_BORDER);
			_image.setPixel(WIDTH-2, i, COLOR_BORDER);
		}
		
		Texture texture = new Texture();
		try {
			texture.loadFromImage(_image);
		} catch (TextureCreationException e) {
			e.printStackTrace();
		}
		
		_imageView.setSprite(new Sprite(texture));
	}

	private void addData(int index, StatsData data, Color color) {
		_labels[index].setString(data.label);
		_labels[index].setColor(color);
		
		Color colorLight = new Color(color.r + 50, color.g + 50, color.b + 50, 180);
		
		int i = 0;
		int zoom = 2;
		for (Integer value: data.values) {
			int x = 2 + i++;
			int y = 200 - value * zoom;
			
			setPixel(x+0, y+0, color);
			setPixel(x+1, y+0, colorLight);
			setPixel(x+0, y+1, colorLight);
			setPixel(x-1, y+0, colorLight);
			setPixel(x+0, y-1, colorLight);
		}
	}

	private void setPixel(int x, int y, Color color) {
		if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
			_image.setPixel(x, y, color);
		}
	}
}
