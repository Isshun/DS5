//package org.smallbox.faraway.ui.panel;
//
//import org.jsfml.graphics.Image;
//import org.jsfml.graphics.Texture;
//import org.jsfml.graphics.TextureCreationException;
//import org.smallbox.faraway.Color;
//import org.smallbox.faraway.Game;
//import org.smallbox.faraway.GameEventListener;
//import org.smallbox.faraway.StatsData;
//import org.smallbox.faraway.engine.ui.ImageView;
//import org.smallbox.faraway.engine.ui.TextView;
//import org.smallbox.faraway.manager.StatsManager;
//import org.smallbox.faraway.ui.UserInterface.Mode;
//
//public class PanelStats extends BaseRightPanel {
//	private static final org.jsfml.graphics.Color COLOR_BORDER = new org.jsfml.graphics.Color(22, 50, 56);
//	private static final int CHART_WIDTH = 300;
//	private static final int CHART_HEIGHT = 200;
//	private static final int NB_DATA_MAX = 5;
//
//	private ImageView 			_imageView;
//	private StatsManager		_stats;
//	private Image 				_image;
//	private TextView[] 			_labels;
//
//	public PanelStats(Mode mode, GameEventListener.Key shortcut) {
//		super(mode, shortcut);
//	}
//
//	@Override
//	protected void onCreate() {
//		_image = new Image();
//		_image.create(300, 200);
//
//		_imageView = new ImageView();
//		_imageView.setPosition(20, 42);
//		addView(_imageView);
//
//		_labels = new TextView[NB_DATA_MAX];
//		for (int i = 0; i < NB_DATA_MAX; i++) {
//			_labels[i] = new TextView();
//			_labels[i].setPosition(20 + i * 80, 252);
//			_labels[i].setCharacterSize(FONT_SIZE);
//			addView(_labels[i]);
//		}
//
//		TextView lbCharacter = new TextView();
//		lbCharacter.setCharacterSize(FONT_SIZE_TITLE);
//		lbCharacter.setString("Stats");
//		lbCharacter.setPosition(20, 6);
//		addView(lbCharacter);
//	}
//
//	@Override
//	public void onDraw(int frame) {
//		_stats = Game.getStatsManager();
////		_image.
//
//		addData(0, _stats.nbCharacter, Color.RED);
//		addData(1, _stats.nbCouple, Color.GREEN);
//		addData(2, _stats.nbChild, Color.BLUE);
//
//		for (int i = 0; i < CHART_WIDTH; i++) {
//			_image.setPixel(i, 0, COLOR_BORDER);
//			_image.setPixel(i, 1, COLOR_BORDER);
//			_image.setPixel(i, CHART_HEIGHT-1, COLOR_BORDER);
//			_image.setPixel(i, CHART_HEIGHT-2, COLOR_BORDER);
//		}
//		for (int i = 0; i < CHART_HEIGHT; i++) {
//			_image.setPixel(0, i, COLOR_BORDER);
//			_image.setPixel(1, i, COLOR_BORDER);
//			_image.setPixel(CHART_WIDTH-1, i, COLOR_BORDER);
//			_image.setPixel(CHART_WIDTH-2, i, COLOR_BORDER);
//		}
//
//		Texture texture = new Texture();
//		try {
//			texture.loadFromImage(_image);
//		} catch (TextureCreationException e) {
//			e.printStackTrace();
//		}
//
//		// TODO
////		SpriteModel sprite = new SpriteModel();
////		sprite.getData().setTexture(texture);
////		_imageView.setSprite(sprite);
//	}
//
//	private void addData(int index, StatsData data, Color color) {
//		_labels[index].setString(data.label);
//		_labels[index].setColor(color);
//
//        org.jsfml.graphics.Color colorLight = new org.jsfml.graphics.Color(color.r + 50, color.g + 50, color.b + 50, 180);
//
//		int i = 0;
//		int zoom = 2;
//		for (Integer value: data.values) {
//			int x = 2 + i++;
//			int y = 200 - value * zoom;
//
//			setPixel(x+0, y+0, new org.jsfml.graphics.Color(color.r, color.g, color.b));
//			setPixel(x+1, y+0, colorLight);
//			setPixel(x+0, y+1, colorLight);
//			setPixel(x-1, y+0, colorLight);
//			setPixel(x+0, y-1, colorLight);
//		}
//	}
//
//	private void setPixel(int x, int y, org.jsfml.graphics.Color color) {
//		if (x >= 0 && x < CHART_WIDTH && y >= 0 && y < CHART_HEIGHT) {
//			_image.setPixel(x, y, color);
//		}
//	}
//}
