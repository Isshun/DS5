//package org.smallbox.faraway.engine.renderer;
//
//import org.smallbox.faraway.*;
//import org.smallbox.faraway.util.Constant;
//import org.smallbox.faraway.game.manager.ServiceManager;
//import org.smallbox.faraway.game.manager.WorldManager;
//import org.smallbox.faraway.game.model.character.base.CharacterModel;
//
//import java.util.List;
//
//public class MiniMapRenderer implements IRenderer {
//
//	private static final Color COLOR_BORDER = new Color(22, 50, 56);
//	private Color[]		_colors;
//	private SpriteModel	_sprite;
//	private Image 		_image;
//	private int 		_width;
//	private int 		_height;
//	private Texture 	_texture;
//	private Viewport 	_viewport;
//
//	public MiniMapRenderer(Viewport viewport) {
//		_width = Constant.PANEL_WIDTH - 48;
//		_height = 200;
//		_viewport = viewport;
//
//		_image = new Image();
//		_image.create(_width, _height);
//
//		int i1 = 40;
//		int i2 = 20;
//		_colors = new Color[42];
//		for (int i = 0; i < 42; i++) {
//			_colors[i] = new Color((int)(18 + Math.random() * i1 - i2), (int)(28 + Math.random() * i1 - i2), (int)(30 + Math.random() * i1 - i2));
//		}
//
//		_texture = new Texture();
//	}
//
//	public void onDraw(int frame) {
//		WorldManager worldManager = Game.getWorldManager();
//		for (int x = 0; x < _width; x++) {
//			for (int y = 0; y < _height; y++) {
//				Color color = _colors[(int)(Math.random() * 42)];
//				int viewportX = -_viewport.getPosX() / 32;
//				int viewportY = -_viewport.getPosY() / 32;
//				int viewportWidth = _viewport.getWidth() / 32;
//				int viewportHeight = _viewport.getHeight() / 32;
//
//				if (x <= 4 || x >= _width - 4 || y <= 4 || y >= _height - 4) {
//					color = COLOR_BORDER;
//				}
//				else if ((x == viewportX || x == viewportX + viewportWidth) && y >= viewportY && y <= viewportY + viewportHeight) {
//					color = Color.YELLOW;
//				}
//				else if ((y == viewportY || y == viewportY + viewportHeight) && x >= viewportX && x <= viewportX + viewportWidth) {
//					color = Color.YELLOW;
//				}
//				else if (worldManager.getItem(x, y) != null) {
//					color = Color.GREEN;
//				}
//				else if (worldManager.getStructure(x, y) != null) {
//					if (worldManager.getStructure(x, y).isWall()) {
//						color = Color.RED;
//					}
//				}
//				else if (worldManager.getResource(x, y) != null) {
//					if (worldManager.getResource(x, y).isRock()) {
//					}
//					if (worldManager.getResource(x, y).getInfo().onGather != null) {
//						color = Color.GREEN;
//					}
//				}
//
//
////				image.setPixel(x, y, new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)));
//				_image.setPixel(x, y, color);
//			}
//		}
//
//		List<CharacterModel> list = Game.getCharacterManager().getList();
//		for (CharacterModel character: list) {
//			_image.setPixel(character.getX(), character.getY(), Color.RED);
//		}
//
//		try {
//			_texture.loadFromImage(_image);
//		} catch (TextureCreationException e) {
//			e.printStackTrace();
//		}
//
//		_sprite = new SpriteModel();
//		_sprite.getData().setTexture(_texture);
//		_sprite.setPosition(24, 20);
//	}
//
//	@Override
//	public void onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress) {
////		if (_sprite != null) {
////			app.draw(_sprite, _renderEffect);
////		}
//	}
//
//	public SpriteModel getSprite() {
//		return _sprite;
//	}
//
//	@Override
//	public void invalidate(int x, int y) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void invalidate() {
//		// TODO Auto-generated method stub
//
//	}
//
//}
