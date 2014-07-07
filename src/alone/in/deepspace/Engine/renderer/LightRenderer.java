package alone.in.deepspace.engine.renderer;

import java.io.File;
import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Shader;
import org.jsfml.graphics.ShaderSourceException;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.item.ItemBase;
import alone.in.deepspace.model.item.StructureItem;
import alone.in.deepspace.model.item.WorldArea;
import alone.in.deepspace.util.Constant;

public class LightRenderer implements IRenderer {
	private static final int	LIGHT_DISTANCE = 10;

	private RenderTexture 		_cache;
//	private Sprite 				_sprite;
//	private Sprite 				_sprite2;
	private Shader 				_blur;
	private Sprite 				_spriteCache;
	private boolean 			_invalidate;

	public LightRenderer() {
		try {
			Texture texture = new Texture();
			texture.loadFromFile((new File("res/Tilesets/shadow.png").toPath()));

//			_sprite2 = new Sprite();
//			_sprite2.setTexture(texture);
//			_sprite2.setTextureRect(new IntRect(0, 0, 16, 48));

//			_sprite = new Sprite();
//			_sprite.setTexture(texture);
//			_sprite.setTextureRect(new IntRect(16, 0, 16, 48));

			_cache = new RenderTexture();
			_cache.create(Constant.WORLD_WIDTH * Constant.TILE_WIDTH, Constant.WORLD_HEIGHT * Constant.TILE_HEIGHT);
			_cache.display();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TextureCreationException e) {
			e.printStackTrace();
		}
	}

	public void onDraw(RenderWindow app, RenderStates render, double animProgress) {
		if (_blur == null) {
			try {
				_blur = new Shader();
				_blur.loadFromFile((new File("data/gauss.frag")).toPath(), Shader.Type.FRAGMENT);
				_blur.setParameter("texture", _cache.getTexture());
				_blur.setParameter("blur_radius", 0.0005f);
				Shader.bind(_blur);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ShaderSourceException e) {
				e.printStackTrace();
			}
		}

		if (_spriteCache == null) {
			_spriteCache = new Sprite(_cache.getTexture());
		}
		
		app.draw(_spriteCache, new RenderStates(render, _blur));
	}

	private void diffuseLight(int fromX, int fromY, int toX, int toY, int x, int y, int light, int pass, double bright) {
		for (int j = 0; j < light; j++) {
			for (double i = 0; i < Math.PI * 2; i += 0.1) {
				double offsetX = (int)Math.round(Math.cos(i) * j);
				double offsetY = (int)Math.round(Math.sin(i) * j);
				int x2 = x+(int)offsetX;
				int y2 = y+(int)offsetY;
				double radius = Math.sqrt(Math.pow(Math.abs(offsetX), 2) + Math.pow(Math.abs(offsetY), 2));
				if (x2 >= fromX && y2 >= fromY && x2 <= toX && y2 <= toY && isFree(x, y, x+(int)offsetX, y+(int)offsetY)) {
					WorldArea area = ServiceManager.getWorldMap().getArea(x2, y2);
					if (area != null && area.getLightPass() < pass) {
						area.addLight(Math.min(Math.max(bright - radius * 0.15, 0), 1));
						area.setLightPass(pass);
					}
				}
			}
		}
	}

	private boolean isFree(int x, int y, int x2, int y2) {
		if (x == x2 && y == y2) {
			return true;
		}

		int fromX = Math.min(x, x2);
		int fromY = Math.min(y, y2);
		int toX = Math.max(x, x2);
		int toY = Math.max(y, y2);
		for (int i = fromX; i <= toX; i++) {
			for (int j = fromY; j <= toY; j++) {
				StructureItem structure = ServiceManager.getWorldMap().getStructure(i, j);
				if (structure != null && (structure.isWall() || structure.isDoor() || structure.isHull())) {
					return false;
				}
			}
		}
		return true;
	}

	public void initLight() {
		_invalidate = true;
	}

	public void refresh(ItemBase item) {

		// TODO
		//		refresh(item.getX() - item.getLight(),
		//				item.getY() - item.getLight(),
		//				item.getX() + item.getLight(),
		//				item.getY() + item.getLight());

		_invalidate = true;
	}

	private void fixLight(int fromX, int fromY, int toX, int toY) {
		int mapWidth = ServiceManager.getWorldMap().getWidth();
		int mapHeight = ServiceManager.getWorldMap().getHeight();
		fromX = Math.max(fromX, 0);
		fromY = Math.max(fromY, 0);
		toX = Math.min(toX, mapWidth);
		toY = Math.min(toY, mapHeight);

		for (int x = fromX; x < toX; x++) {
			for (int y = fromY; y < toY; y++) {
				// Wall
				WorldArea area = ServiceManager.getWorldMap().getArea(x, y);
				if (area != null && area.getStructure() != null && (area.getStructure().isWall() || area.getStructure().isDoor()) && area.getLight() <= 0) {
					WorldArea areaLeft = ServiceManager.getWorldMap().getArea(x-1, y);
					WorldArea areaRight = ServiceManager.getWorldMap().getArea(x+1, y);
					double lightRight = areaRight != null ? areaRight.getLight() : 0;
					double lightLeft = areaLeft != null ? areaLeft.getLight() : 0;
					area.setLight(Math.max(lightRight, lightLeft) * 0.75);
				}

			}
		}
	}

	@Override
	public void onRefresh(int frame) {
		if (_invalidate) {
			_invalidate = false;
			
			int fromX = 0;
			int fromY = 0;
			int toX = ServiceManager.getWorldMap().getWidth();
			int toY = ServiceManager.getWorldMap().getHeight();

			int mapWidth = ServiceManager.getWorldMap().getWidth();
			int mapHeight = ServiceManager.getWorldMap().getHeight();
			fromX = Math.max(fromX, 0);
			fromY = Math.max(fromY, 0);
			toX = Math.min(toX, mapWidth);
			toY = Math.min(toY, mapHeight);

			// Reset brightness for areas
			for (int x = fromX; x < toX; x++) {
				for (int y = fromY; y < toY; y++) {
					ServiceManager.getWorldMap().getArea(x, y).setLightPass(0);
					ServiceManager.getWorldMap().getArea(x, y).setLight(0);
				}
			}

			// Compute lights and set areas
			int pass = 0;
			for (int x = fromX; x < toX; x++) {
				for (int y = fromY; y < toY; y++) {
					WorldArea area = ServiceManager.getWorldMap().getArea(x, y);
					if (area.hasLightSource()) {
						diffuseLight(fromX, fromY, toX, toY, x, y, LIGHT_DISTANCE, ++pass, (double)area.getLightSource() / 10);
					}
					if (area.getItem() != null && area.getItem().getLight() > 0) {
						diffuseLight(fromX, fromY, toX, toY, x, y, LIGHT_DISTANCE, ++pass, (double)area.getItem().getLight() / 10);
					}
					if (area.getStructure() != null && area.getStructure().getLight() > 0) {
						diffuseLight(fromX, fromY, toX, toY, x, y, LIGHT_DISTANCE, ++pass, (double)area.getStructure().getLight() / 10);
					}
					if (area.getRessource() != null && area.getRessource().getLight() > 0) {
						diffuseLight(fromX, fromY, toX, toY, x, y, LIGHT_DISTANCE, ++pass, (double)area.getRessource().getLight() / 10);
					}
				}
			}

			// Reset brightness for areas
			for (int x = fromX; x < toX; x++) {
				for (int y = fromY; y < toY; y++) {
					ServiceManager.getWorldMap().getArea(x, y).setLightPass(0);
				}
			}

			// Windows
			for (int x = fromX; x < toX; x++) {
				for (int y = fromY; y < toY; y++) {
					WorldArea area = ServiceManager.getWorldMap().getArea(x, y);
					WorldArea areaBellow = ServiceManager.getWorldMap().getArea(x, y+1);
					if (areaBellow != null && areaBellow.getLight() > 0) {
						if (area.getStructure() != null && area.getStructure().isWindow()) {
							diffuseLight(fromX, fromY, toX, toY, x, y-1, 10, ++pass, areaBellow.getLight() * 0.75);
						}
						if (area.getStructure() != null && (area.getStructure().isWall() || area.getStructure().isDoor())) {
							area.setLight(areaBellow.getLight());
						}
					}
				}
			}

			fixLight(fromX-10, fromY-10, toX+10, toY+10);

			// Draw shadows
			_cache.clear(new Color(0, 0, 0, 0));
			RectangleShape shape = new RectangleShape(new Vector2f(32, 32));
			RectangleShape wallShape = new RectangleShape(new Vector2f(32, 48));
			RectangleShape halfShape = new RectangleShape(new Vector2f(32, 16));

			halfShape.setFillColor(new Color(0, 0, 0, 0));
			for (int x = 0; x < mapWidth; x++) {
				for (int y = 0; y < mapHeight; y++) {
					WorldArea area = ServiceManager.getWorldMap().getArea(x, y);
					StructureItem structure = ServiceManager.getWorldMap().getStructure(x, y);
					StructureItem structureBellow = ServiceManager.getWorldMap().getStructure(x, y+1);

					Color color = new Color(10, 10, 30, 200 - (int)(area.getLight() * 255));

					if (structure != null && (structure.isWall() || structure.isDoor())) {
						if (structureBellow != null && (structureBellow.isWall() || structureBellow.isDoor())) {
							shape.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT - 16);
							shape.setFillColor(color);
							_cache.draw(shape);
						}

						else {
							wallShape.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT - 16);
							wallShape.setFillColor(color);
							_cache.draw(wallShape);
						}
					}

					else if (structureBellow != null && (structureBellow.isWall() || structureBellow.isDoor())) {
						halfShape.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
						halfShape.setFillColor(color);
						_cache.draw(halfShape);
					}

					else {
						shape.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
						shape.setFillColor(color);
						_cache.draw(shape);
					}
				}
			}
		}
	}

	@Override
	public void invalidate(int x, int y) {
		_invalidate = true;
	}

	@Override
	public void invalidate() {
		_invalidate = true;
	}

}
