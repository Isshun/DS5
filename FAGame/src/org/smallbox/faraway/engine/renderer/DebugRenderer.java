package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.RenderEffect;

public class DebugRenderer implements IRenderer {
//	private RenderTexture 	_cache;
//
//	public DebugRenderer() {
//		try {
//			_cache = new RenderTexture();
//			_cache.create(Constant.WORLD_WIDTH * Constant.TILE_WIDTH, Constant.WORLD_HEIGHT * Constant.TILE_HEIGHT);
//			_cache.display();
//		} catch (TextureCreationException e) {
//			e.printStackTrace();
//		}
//	}

	public void onDraw(GFXRenderer app, RenderEffect effect, double animProgress) {
		
//		Color color = new Color(0, 0, 0);
//		_shapeDebug.setSize(ObjectPool.getVector2f(Constant.TILE_SIZE, Constant.TILE_SIZE));
//		_shapeDebug.setFillColor(new Color(250, 200, 200, 100));
//
//		Text text = ObjectPool.getText();
//		text.setFont(SpriteManager.getInstance().getFont());
//		text.setCharacterSize(10);
//		for (int x = 0; x < ServiceManager.getWorldMap().getWidth(); x++) {
//			for (int y = 0; y < ServiceManager.getWorldMap().getHeight(); y++) {
//				WorldArea res = ServiceManager.getWorldMap().getArea(x, y);
//				if (res != null) {
//					text.setString(""+(int)(res.getLightSource()));
//					text.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
//					app.draw(text, _renderEffect);
//				}
//			}
//		}

//		if (area.getLight() > 0) {
//			text.setString(String.valueOf((int)(area.getLight() * 255)));
//			text.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
//			_lightCache.draw(text);
//		}

		
		//text.setColor(color);
//		
//		Map<Integer, Boolean> visited = new HashMap<Integer, Boolean>();
//		
//		List<Door> doors = PathManager.getInstance()._doors;
//		for (Door door : doors) {
//			text.setString(String.valueOf(door.id));
//			boolean flag = visited.containsKey(door.x  << 16 + door.y) && visited.get(door.x  << 16 + door.y);
//			text.setPosition(door.x * Constant.TILE_SIZE, door.y * Constant.TILE_SIZE + (flag ? Constant.TILE_SIZE / 2 : 0));
//			visited.put(door.x  << 16 + door.y, true);
//			_app.draw(text, _renderEffect);
//		}

//		List<Region> regions = PathManager.getInstance().getRegions();
//		for (Region region : regions) {
//			for (int i = region.fromX; i <= region.toX; i++) {
//				for (int j = region.fromY; j <= region.toY; j++) {
//					if (i == region.fromX || i == region.toX || j == region.fromY || j == region.toY) {
//						_shapeDebug.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
//						_app.draw(_shapeDebug, _renderEffect);
//					}
//				}
//			}
//		}
//		
		
//		_lightCache.clear(new Color(0, 0, 0, 100));
		
//		RectangleShape shape = null;
//		RectangleShape fullShape = new RectangleShape(new Vector2f(320, 320));
//		RectangleShape halfShape = new RectangleShape(new Vector2f(32, 16));
//		
//		
//		for (int i = toX-1; i >= fromX; i--) {
//			for (int j = toY-1; j >= fromY; j--) {
//				WorldArea item = ServiceManager.getWorldMap().getArea(i, j);
//				StructureItem structure = ServiceManager.getWorldMap().getStructure(i, j);
//				StructureItem structureBellow = ServiceManager.getWorldMap().getStructure(i, j+1);
//
//				if (structure != null && structure.isFloor() && structureBellow != null && structureBellow.isFloor() == false) {
//					shape = halfShape;
//					shape.setFillColor(new Color(0, 200, 0, 100));
//					shape.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE + 16);
//					_lightCache.draw(shape);
//				} else {
//					shape = fullShape;
//				}
//
//				//				//
////				if (item == null) {
////					item = ServiceManager.getWorldMap().getArea(i, j);
////				}
////
////				if (item != null) {
////					_shapeDebug.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
////					_app.draw(_shapeDebug, _renderEffect);
////				}
////				//
////				text.setStyle(Text.REGULAR);
//				if (item.getLight() != 0) {
//					text.setString(String.valueOf(item.getLight()));
//					text.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
//					_lightCache.draw(text);
//				}
//
//				if (structure == null || structure.isFloor()) {
//					if (k == 0 || k == 1) {
////						shape.setFillColor(new Color(200, 0, 0, 155 * (12 - item.getLight()) / 12));
////						shape.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
////						_lightCache.draw(shape);
//					}
//				}
//				else {
//					if (k == 0 || k == 2) {
//						shape.setFillColor(new Color(0, 0, 200, 100));
//						shape.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
//						_lightCache.draw(shape);
//					}
//				}
//
//			}
//		}
//		ObjectPool.release(text);
//
//		_app.draw((new Sprite(_lightCache.getTexture())), _renderEffect);
	}


	@Override
	public void onRefresh(int frame) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void invalidate(int x, int y) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void invalidate() {
		// TODO Auto-generated method stub
		
	}

}