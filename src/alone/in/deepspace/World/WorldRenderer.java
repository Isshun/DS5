package alone.in.deepspace.World;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Clock;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import alone.in.deepspace.Character.ServiceManager;
import alone.in.deepspace.Managers.PathManager;
import alone.in.deepspace.Managers.Region;
import alone.in.deepspace.Managers.Region.Door;
import alone.in.deepspace.Managers.RoomManager;
import alone.in.deepspace.Managers.SpriteManager;
import alone.in.deepspace.Models.Room;
import alone.in.deepspace.UserInterface.UserInterface;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.Utils.Log;
import alone.in.deepspace.Utils.ObjectPool;
import alone.in.deepspace.Utils.Settings;

public class WorldRenderer {
	private RenderWindow			_app;
	private SpriteManager			_spriteManager;
	private Font					_font;
	private UserInterface			_ui;
	private RectangleShape 			_shape;
	private RectangleShape 			_shapeDebug;
	private int 					_lastSpecialY;
	private int 					_lastSpecialX;
	private Sprite 					_spriteCache;
	private Sprite 					_lightSpriteCache;
	private RenderTexture 			_textureCache;
	private RenderTexture 			_lightCache;
	private boolean 				_hasChanged;
	private int						_pass;

	private Set<Vector2i> 			_changed;

	public WorldRenderer(RenderWindow app, SpriteManager spriteManager, UserInterface ui) throws IOException, TextureCreationException {
		_ui = ui;
		_app = app;
		_spriteManager = spriteManager;
		_shape = new RectangleShape();
		_shape.setSize(new Vector2f(Constant.TILE_SIZE, Constant.TILE_SIZE));
		_shapeDebug = new RectangleShape();
		_changed = new HashSet<Vector2i>();
		
		_spriteCache = new Sprite();
		_lightSpriteCache = new Sprite();
		
//		_sprite.setTextureRect(new IntRect(0, 0, Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT));

		_textureCache = new RenderTexture();
		_textureCache.create(Constant.WORLD_WIDTH * Constant.TILE_SIZE, Constant.WORLD_HEIGHT * Constant.TILE_SIZE);
		_textureCache.setSmooth(true);
		_textureCache.display();
		
		_lightCache = new RenderTexture();
		_lightCache.create(Constant.WORLD_WIDTH * Constant.TILE_SIZE, Constant.WORLD_HEIGHT * Constant.TILE_SIZE);
		_lightCache.display();
		
		_hasChanged = true;

		// TODO
		//_font.loadFromFile((new File("res/xolonium/Xolonium-Regular.otf")).toPath());
	}

	public void	refresh(RenderStates render) {

		int fromX = Math.max(_ui.getRelativePosXMin(0)-1, 0);
		int fromY = Math.max(_ui.getRelativePosYMin(0)-1, 0);
		int toX = Math.min(_ui.getRelativePosXMax(Constant.WINDOW_WIDTH)+1, ServiceManager.getWorldMap().getWidth());
		int toY = Math.min(_ui.getRelativePosYMax(Constant.WINDOW_HEIGHT)+1, ServiceManager.getWorldMap().getHeight());

		// Debug() << "Renderer: " << fromX << " to: " << toX;

		Clock display_timer = new Clock();
		if (_pass > 0 || _hasChanged || _changed.size() > 0) {
			if (_hasChanged) {
				refreshFloor(render, 0, 0, Constant.WORLD_WIDTH, Constant.WORLD_HEIGHT);
				refreshStructure(render, 0, 0, Constant.WORLD_WIDTH, Constant.WORLD_HEIGHT);
			} else {
				if (_pass == 0) {
					_pass = 4;
				}
				for (Vector2i vector: _changed) {
					refreshFloor(render, vector.x - 1, vector.y - 1, vector.x + 2, vector.y + 2);
				}
				refreshStructure(render, 0, 0, Constant.WORLD_WIDTH, Constant.WORLD_HEIGHT);
			}
			_changed.clear();
			_hasChanged = false;
		}
		Sprite sp = new Sprite(_textureCache.getTexture());
		_app.draw(sp, render);

		drawDebug(render, fromX, fromY, toX, toY, 0);
		
		long elapsed = display_timer.getElapsedTime().asMilliseconds();
		if (elapsed > 3)
			Log.info("display floor: " + elapsed + "ms");

		
//		display_timer.restart();
//		Log.info("display structure: " + display_timer.getElapsedTime().asMicroseconds());

		display_timer.restart();
		refreshItems(render, fromX, fromY, toX, toY);
//		Log.info("display items: " + display_timer.getElapsedTime().asMicroseconds());

//		Vector<DebugPos> debugPath = ServiceManager.getWorldMap().getDebug();
//		DebugPos startDebugPath = ServiceManager.getWorldMap().getStartDebug();
//		DebugPos stopDebugPath = ServiceManager.getWorldMap().getStopDebug();
//		Sprite sprite = null;
//		if (debugPath != null) {
//			for (DebugPos pos: debugPath) {
//				if (pos.inPath) {
//					sprite = SpriteManager.getInstance().getBullet(2);
//				} else {
//					sprite = SpriteManager.getInstance().getBullet(0);
//				}
//				sprite.setPosition(pos.x * Constant.TILE_SIZE, pos.y * Constant.TILE_SIZE);
//				_app.draw(sprite, render);
//			}
//		}
//		if (startDebugPath != null) {
//			sprite = SpriteManager.getInstance().getBullet(2);
//			sprite.setPosition(startDebugPath.x * Constant.TILE_SIZE, startDebugPath.y * Constant.TILE_SIZE);
//			_app.draw(sprite, render);
//		}
//		if (stopDebugPath != null) {
//			sprite = SpriteManager.getInstance().getBullet(3);
//			sprite.setPosition(stopDebugPath.x * Constant.TILE_SIZE, stopDebugPath.y * Constant.TILE_SIZE);
//			_app.draw(sprite, render);
//		}

		// Draw debug
		if (Settings.getInstance().isDebug()) {
			drawDebug(render, fromX, fromY, toX, toY, 10);
		}
	}

	// TODO: random
	void	refreshFloor(RenderStates render, int fromX, int fromY, int toX, int toY) {
		for (int i = toX-1; i >= fromX; i--) {
			for (int j = toY-1; j >= fromY; j--) {
				if (i >= 0 && j >= 0 && i < Constant.WORLD_WIDTH && j < Constant.WORLD_HEIGHT) {
					// Oxygen
					WorldArea area = ServiceManager.getWorldMap().getArea(i, j);
	
					// Structure
					StructureItem structure = ServiceManager.getWorldMap().getStructure(i, j);
					
					Room room = RoomManager.getInstance().get(i, j);
					if (structure != null && structure.roomCanBeSet() == false) {
						structure = ServiceManager.getWorldMap().getStructure(i, j-1);
						room = RoomManager.getInstance().get(i, j-1);
					}
	
					if (structure != null) {
						
						// Greenhouse
						if (structure.isType(BaseItem.Type.STRUCTURE_GREENHOUSE)) {
							int index = room != null && room.isType(Room.Type.GARDEN) ? 0 : 2;
							Sprite sprite = _spriteManager.getGreenHouse(index + (structure.isWorking() ? 1 : 0));
							sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
							_textureCache.draw(sprite);
							
							WorldRessource ressource = ServiceManager.getWorldMap().getRessource(i, j);
							if (ressource != null && ressource.getMatterSupply() > 0) {
								sprite = _spriteManager.getRessource(ressource);
								sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
								_textureCache.draw(sprite);
							}
						}
						
						// Floor
						else {
							int roomId = room != null ? room.getType().ordinal() : 0;
							Sprite sprite = _spriteManager.getFloor(structure, roomId, 0);
							sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
							_textureCache.draw(sprite);
							
//							RectangleShape shape = new RectangleShape(new Vector2f(32, 32));
//							shape.setFillColor(new Color(0, 0, 0, 155 * (12 - area.getLight()) / 12));
//							shape.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
//							_texture.draw(shape);
						}
					}
					// Ressource
					else {
						Sprite sprite = _spriteManager.getExterior(i + j * 42);
						sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
						_textureCache.draw(sprite);
	
	//					if (area.getStructure() != null && area.getStructure().isType(BaseItem.Type.STRUCTURE_FLOOR)) {
	//						if (area.getOxygen() < 25) {
	//							sprite = _spriteManager.getNoOxygen();
	//							_app.draw(sprite, render);
	//						} else if (area.getOxygen() < 100) {
	//							_shape.setFillColor(ObjectPool.getColor(255, 0, 0, area.getOxygen() * 125 / 100));
	//							_shape.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
	//							_app.draw(_shape, render);
	//						}
	//					}
						
						WorldRessource ressource = ServiceManager.getWorldMap().getRessource(i, j);
						if (ressource != null) {
							sprite = _spriteManager.getRessource(ressource);
							sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
							_textureCache.draw(sprite);
						}
					}
				}
			}
		}
		
		_spriteCache.setTexture(_textureCache.getTexture());
	}

	//TODO: random
	void	refreshStructure(RenderStates render, int fromX, int fromY, int toX, int toY) {
		_lastSpecialX = -1;
		_lastSpecialY = -1;
		int offsetWall = (Constant.TILE_SIZE / 2 * 3) - Constant.TILE_SIZE;

		switch (_pass) {
		case 1: toX /= 2; toY /= 2; break;
		case 2: fromX = toX / 2; toY /= 2; break;
		case 3: toX /= 2; fromY = toY / 2; break;
		case 4: fromX = toX / 2; fromY = toY / 2; break;
		}
		
		_pass--;
		
		Sprite sprite = null;
		for (int j = toY-1; j >= fromY; j--) {
			for (int i = toX-1; i >= fromX; i--) {
				if (i >= 0 && j >= 0 && i < Constant.WORLD_WIDTH && j < Constant.WORLD_HEIGHT) {
					StructureItem item = ServiceManager.getWorldMap().getStructure(i, j);
					if (item != null) {
	
						// Structure except floor
						if (item.isStructure() && !item.isType(BaseItem.Type.STRUCTURE_FLOOR)) {
	
							// Door
							if (item.isType(BaseItem.Type.STRUCTURE_DOOR)) {
								// if (_characterManager.getCharacterAtPos(i, j) != null
								// 	  || _characterManager.getCharacterAtPos(i+1, j) != null
								// 	  || _characterManager.getCharacterAtPos(i-1, j) != null
								// 	  || _characterManager.getCharacterAtPos(i, j+1) != null
								// 	  || _characterManager.getCharacterAtPos(i, j-1) != null) {
								// 	_spriteManager.getWall(item, 2, &sprite, 0, 0);
								// } else {
								sprite = _spriteManager.getWall(item, 0, 0, 0);
								// }
								sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE - offsetWall);
							}
	
							// Wall
							else if (item.isType(BaseItem.Type.STRUCTURE_WALL)) {
								sprite = drawWall(item, i, j, offsetWall);
							}	  
						}
	
						// // floor
						// else {
						// 	_spriteManager.getFloor(item, item.getZoneId(), item.getRoomId(), &sprite);
						// 	sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE);
						// }
	
						if (sprite != null) {
							_textureCache.draw(sprite);
						}
					}
				}
			}
		}
	}

	private Sprite drawWall(StructureItem item, int i, int j, int offsetWall) {
		Sprite sprite = null;

		StructureItem bellow = ServiceManager.getWorldMap().getStructure(i, j+1);
		StructureItem right = ServiceManager.getWorldMap().getStructure(i+1, j);
		StructureItem left = ServiceManager.getWorldMap().getStructure(i-1, j);
		StructureItem above = ServiceManager.getWorldMap().getStructure(i, j-1);

		Room room = RoomManager.getInstance().get(i, j + 1);
		int zone = room != null ? room.getType().ordinal() : 0;

		// bellow is a wall
		if (bellow != null && (bellow.isType(BaseItem.Type.STRUCTURE_WALL) || bellow.isType(BaseItem.Type.STRUCTURE_DOOR))) {
			StructureItem bellowBellow = ServiceManager.getWorldMap().getStructure(i, j+2);
			if (bellow.isType(BaseItem.Type.STRUCTURE_DOOR) ||
					bellowBellow == null || (!bellowBellow.isType(BaseItem.Type.STRUCTURE_WALL) && !bellowBellow.isType(BaseItem.Type.STRUCTURE_DOOR))) {
				StructureItem bellowRight = ServiceManager.getWorldMap().getStructure(i+1, j+1);
				StructureItem bellowLeft = ServiceManager.getWorldMap().getStructure(i-1, j+1);
				boolean wallOnRight = bellowRight != null && (bellowRight.isType(BaseItem.Type.STRUCTURE_WALL) || bellowRight.isType(BaseItem.Type.STRUCTURE_DOOR));
				boolean wallOnLeft = bellowLeft != null && (bellowLeft.isType(BaseItem.Type.STRUCTURE_WALL) || bellowLeft.isType(BaseItem.Type.STRUCTURE_DOOR));
				
				if (wallOnRight && wallOnLeft) {
					sprite = _spriteManager.getWall(item, 5, 0, 0);
				} else if (wallOnLeft) {
					boolean wallOnSupRight = right != null && (right.isType(BaseItem.Type.STRUCTURE_WALL) || right.isType(BaseItem.Type.STRUCTURE_DOOR));
					if (wallOnSupRight) {
						sprite = _spriteManager.getWall(item, 1, 5, 0);
					} else {
						sprite = _spriteManager.getWall(item, 5, 2, 0);
					}
				} else if (wallOnRight) {
					boolean wallOnSupLeft = left != null && (left.isType(BaseItem.Type.STRUCTURE_WALL) || left.isType(BaseItem.Type.STRUCTURE_DOOR));
					if (wallOnSupLeft) {
						sprite = _spriteManager.getWall(item, 1, 4, 0);
					} else {
						sprite = _spriteManager.getWall(item, 5, 1, 0);
					}
				} else {
					sprite = _spriteManager.getWall(item, 5, 3, 0);
				}
			} else {
				boolean wallOnRight = right != null && (right.isType(BaseItem.Type.STRUCTURE_WALL) || right.isType(BaseItem.Type.STRUCTURE_DOOR));
				boolean wallOnLeft = left != null && (left.isType(BaseItem.Type.STRUCTURE_WALL) || left.isType(BaseItem.Type.STRUCTURE_DOOR));
				if (wallOnRight && wallOnLeft) {
					sprite = _spriteManager.getWall(item, 1, 0, 0);
				} else if (wallOnLeft) {
					sprite = _spriteManager.getWall(item, 1, 2, 0);
				} else if (wallOnRight) {
					sprite = _spriteManager.getWall(item, 1, 1, 0);
				} else {
					sprite = _spriteManager.getWall(item, 1, 3, 0);
				}
			}
			if (sprite != null) {
				sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE - offsetWall);
			}
		}

//		// above is a wall
//		else if (above != null && above.isType(BaseItem.Type.STRUCTURE_WALL)) {
//			sprite = _spriteManager.getWall(item, 5, 0, 0);
//			sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE - offsetWall);
//		}

		// No wall above or bellow
		else if ((bellow == null || bellow.getType() != BaseItem.Type.STRUCTURE_WALL)) {

			// Check double wall
			boolean doubleWall = false;
			if (right != null && right.isComplete() && right.isType(BaseItem.Type.STRUCTURE_WALL) &&
					(_lastSpecialY != j || _lastSpecialX != i+1)) {
				StructureItem aboveRight = ServiceManager.getWorldMap().getStructure(i+1, j-1);
				StructureItem bellowRight = ServiceManager.getWorldMap().getStructure(i+1, j+1);
				if ((aboveRight == null || aboveRight.getType() != BaseItem.Type.STRUCTURE_WALL) &&
						(bellowRight == null || bellowRight.getType() != BaseItem.Type.STRUCTURE_WALL)) {
					doubleWall = true;
				}
			}

			// Normal
			if (bellow == null) {
				// Double wall
				if (doubleWall) {
					sprite = _spriteManager.getWall(item, 4, i+j, zone);
					_lastSpecialX = i;
					_lastSpecialY = j;
				}
				// Single wall
				else {
					sprite = _spriteManager.getWall(item, 0, 0, zone);
				}
			}
			// Special
			else {
				// Double wall
				if (doubleWall) {
					sprite = _spriteManager.getWall(item, 2, i+j, zone);
					_lastSpecialX = i;
					_lastSpecialY = j;
				}
				// Single wall
				else {
					sprite = _spriteManager.getWall(item, 3, i+j, zone);
				}
			}
			if (sprite != null) {
				sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE - offsetWall);
			}
		}

		// // left is a wall
		// else if (left != null && left.type == BaseItem.STRUCTURE_WALL) {
		// 	_spriteManager.getWall(item, 2, &sprite);
		// 	sprite.setPosition(i * TILE_SIZE - TILE_SIZE, j * TILE_SIZE - offset);
		// }

		// single wall
		else {
			sprite = _spriteManager.getWall(item, 0, 0, 0);
			sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE - offsetWall);
		}

		return sprite;
	}

	void	refreshItems(RenderStates render, int fromX, int fromY, int toX, int toY) {
		int offsetY = -16;
		int offsetX = 2;

		for (int i = fromX-1; i <= toX; i++) {
			for (int j = fromY-1; j <= toY; j++) {
				BaseItem item = ServiceManager.getWorldMap().getItem(i, j);

				if (item != null) {

					// Draw item
					if (item.getType() != BaseItem.Type.STRUCTURE_FLOOR && item.isStructure() == false) {
						Sprite sprite = _spriteManager.getItem(item);
						if (sprite != null) {
							if (item.isStructure()) {
								sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
							} else {
								sprite.setPosition(i * Constant.TILE_SIZE + offsetX, j * Constant.TILE_SIZE + offsetY);
							}
							_app.draw(sprite, render);
						}
					}

					//					// Draw battery
					//					if (item.isComplete() && !item.isSupply()) {
					//						Sprite sprite;
					//
					//						_spriteManager.getSprite(SpriteManager.IC_BATTERY, sprite);
					//						sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
					//
					//						_app.draw(sprite, render);
					//					}
				}
			}
		}
	}

	void	drawDebug(RenderStates render, int fromX, int fromY, int toX, int toY, int k) {
		
//		Color color = new Color(0, 0, 0);
//		_shapeDebug.setSize(ObjectPool.getVector2f(Constant.TILE_SIZE, Constant.TILE_SIZE));
//		_shapeDebug.setFillColor(new Color(250, 200, 200, 100));
//
		Text text = ObjectPool.getText();
		text.setFont(SpriteManager.getInstance().getFont());
		text.setCharacterSize(10);
		
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
//			_app.draw(text, render);
//		}

//		List<Region> regions = PathManager.getInstance().getRegions();
//		for (Region region : regions) {
//			for (int i = region.fromX; i <= region.toX; i++) {
//				for (int j = region.fromY; j <= region.toY; j++) {
//					if (i == region.fromX || i == region.toX || j == region.fromY || j == region.toY) {
//						_shapeDebug.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
//						_app.draw(_shapeDebug, render);
//					}
//				}
//			}
//		}
//		
		_lightCache = new RenderTexture();
		try {
			_lightCache.create(Constant.WORLD_WIDTH * Constant.TILE_SIZE, Constant.WORLD_HEIGHT * Constant.TILE_SIZE);
		} catch (TextureCreationException e) {
			e.printStackTrace();
		}
		_lightCache.display();
		
//		_lightCache.clear(new Color(0, 0, 0, 100));
		
		RectangleShape shape = null;
		RectangleShape fullShape = new RectangleShape(new Vector2f(320, 320));
		RectangleShape halfShape = new RectangleShape(new Vector2f(32, 16));
		
		
		for (int i = toX-1; i >= fromX; i--) {
			for (int j = toY-1; j >= fromY; j--) {
				WorldArea item = ServiceManager.getWorldMap().getArea(i, j);
				StructureItem structure = ServiceManager.getWorldMap().getStructure(i, j);
				StructureItem structureBellow = ServiceManager.getWorldMap().getStructure(i, j+1);

				if (structure != null && structure.isFloor() && structureBellow != null && structureBellow.isFloor() == false) {
					shape = halfShape;
					shape.setFillColor(new Color(0, 200, 0, 100));
					shape.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE + 16);
					_lightCache.draw(shape);
				} else {
					shape = fullShape;
				}

				//				//
//				if (item == null) {
//					item = ServiceManager.getWorldMap().getArea(i, j);
//				}
//
//				if (item != null) {
//					_shapeDebug.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
//					_app.draw(_shapeDebug, render);
//				}
//				//
//				text.setStyle(Text.REGULAR);
				if (item.getLight() != 0) {
					text.setString(String.valueOf(item.getLight()));
					text.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
					_lightCache.draw(text);
				}

				if (structure == null || structure.isFloor()) {
					if (k == 0 || k == 1) {
//						shape.setFillColor(new Color(200, 0, 0, 155 * (12 - item.getLight()) / 12));
//						shape.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
//						_lightCache.draw(shape);
					}
				}
				else {
					if (k == 0 || k == 2) {
						shape.setFillColor(new Color(0, 0, 200, 100));
						shape.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
						_lightCache.draw(shape);
					}
				}

			}
		}
		ObjectPool.release(text);

		_app.draw((new Sprite(_lightCache.getTexture())), render);
	}

	public void invalidate(int x, int y) {
		_changed.add(new Vector2i(x, y));
	}

}
