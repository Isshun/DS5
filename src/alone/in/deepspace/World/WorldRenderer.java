package alone.in.deepspace.World;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.IntRect;
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

import alone.in.deepspace.Managers.RoomManager;
import alone.in.deepspace.Managers.SpriteManager;
import alone.in.deepspace.Models.Room;
import alone.in.deepspace.UserInterface.UserInterface;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.Utils.Log;
import alone.in.deepspace.Utils.ObjectPool;
import alone.in.deepspace.Utils.Settings;

public class WorldRenderer {
	private static WorldRenderer	_self;

	private RenderWindow			_app;
	private SpriteManager			_spriteManager;
	private Font					_font;
	private UserInterface			_ui;
	private RectangleShape 			_shape;
	private RectangleShape 			_shapeDebug;
	private int 					_lastSpecialY;
	private int 					_lastSpecialX;
	private Sprite 					_sprite;
	private RenderTexture 			_texture;
	private boolean 				_hasChanged;
	private int						_pass;

	private Set<Vector2i> 			_changed;

	public WorldRenderer(RenderWindow app, SpriteManager spriteManager, UserInterface ui) throws IOException, TextureCreationException {
		_self = this;
		_ui = ui;
		_app = app;
		_spriteManager = spriteManager;
		_shape = new RectangleShape();
		_shape.setSize(new Vector2f(Constant.TILE_SIZE, Constant.TILE_SIZE));
		_shapeDebug = new RectangleShape();
		_changed = new HashSet<Vector2i>();

		_sprite = new Sprite();
//		_sprite.setTextureRect(new IntRect(0, 0, Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT));

		_texture = new RenderTexture();
		_texture.create(Constant.WORLD_WIDTH * Constant.TILE_SIZE, Constant.WORLD_HEIGHT * Constant.TILE_SIZE);
		_texture.setSmooth(true);
		_texture.display();
		
		_hasChanged = true;

		// TODO
		//_font.loadFromFile((new File("res/xolonium/Xolonium-Regular.otf")).toPath());
	}

	public void	refresh(RenderStates render) {

		int fromX = Math.max(_ui.getRelativePosXMin(0)-1, 0);
		int fromY = Math.max(_ui.getRelativePosYMin(0)-1, 0);
		int toX = Math.min(_ui.getRelativePosXMax(Constant.WINDOW_WIDTH)+1, WorldMap.getInstance().getWidth());
		int toY = Math.min(_ui.getRelativePosYMax(Constant.WINDOW_HEIGHT)+1, WorldMap.getInstance().getHeight());

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
		_app.draw(_sprite, render);
		long elapsed = display_timer.getElapsedTime().asMicroseconds();
		if (elapsed > 4000)
			Log.info("display floor: " + elapsed);

//		display_timer.restart();
//		Log.info("display structure: " + display_timer.getElapsedTime().asMicroseconds());

		display_timer.restart();
		refreshItems(render, fromX, fromY, toX, toY);
//		Log.info("display items: " + display_timer.getElapsedTime().asMicroseconds());

//		Vector<DebugPos> debugPath = WorldMap.getInstance().getDebug();
//		DebugPos startDebugPath = WorldMap.getInstance().getStartDebug();
//		DebugPos stopDebugPath = WorldMap.getInstance().getStopDebug();
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
			drawDebug(render, fromX, fromY, toX, toY);
		}
	}

	// TODO: random
	void	refreshFloor(RenderStates render, int fromX, int fromY, int toX, int toY) {
		for (int i = toX-1; i >= fromX; i--) {
			for (int j = toY-1; j >= fromY; j--) {
				if (i >= 0 && j >= 0 && i < Constant.WORLD_WIDTH && j < Constant.WORLD_HEIGHT) {
					// Oxygen
					WorldArea area = WorldMap.getInstance().getArea(i, j);
	
					// Structure
					StructureItem structure = WorldMap.getInstance().getStructure(i, j);
					
					Room room = RoomManager.getInstance().get(i, j);
					if (structure != null && structure.isType(BaseItem.Type.STRUCTURE_FLOOR) == false) {
						structure = WorldMap.getInstance().getStructure(i, j-1);
						room = RoomManager.getInstance().get(i, j-1);
					}
	
					if (structure != null) {
						int roomId = room != null ? room.getType().ordinal() : 0;
	
						Sprite sprite = _spriteManager.getFloor(structure, roomId, 0);
						sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
						_texture.draw(sprite);
					}
					// Ressource
					else {
						Sprite sprite = _spriteManager.getExterior(i + j * 42);
						sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
						_texture.draw(sprite);
	
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
						
						WorldRessource ressource = WorldMap.getInstance().getRessource(i, j);
						if (ressource != null) {
							sprite = _spriteManager.getRessource(ressource);
							sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
							_texture.draw(sprite);
						}
					}
				}
			}
		}
		
		_sprite.setTexture(_texture.getTexture());
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
					StructureItem item = WorldMap.getInstance().getStructure(i, j);
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
							_texture.draw(sprite);
						}
					}
				}
			}
		}
	}

	private Sprite drawWall(StructureItem item, int i, int j, int offsetWall) {
		Sprite sprite = null;

		StructureItem bellow = WorldMap.getInstance().getStructure(i, j+1);
		StructureItem right = WorldMap.getInstance().getStructure(i+1, j);
		StructureItem left = WorldMap.getInstance().getStructure(i-1, j);
		StructureItem above = WorldMap.getInstance().getStructure(i, j-1);

		Room room = RoomManager.getInstance().get(i, j + 1);
		int zone = room != null ? room.getType().ordinal() : 0;

		// bellow is a wall
		if (bellow != null && (bellow.isType(BaseItem.Type.STRUCTURE_WALL) || bellow.isType(BaseItem.Type.STRUCTURE_DOOR))) {
			StructureItem bellowBellow = WorldMap.getInstance().getStructure(i, j+2);
			if (bellow.isType(BaseItem.Type.STRUCTURE_DOOR) ||
					bellowBellow == null || (!bellowBellow.isType(BaseItem.Type.STRUCTURE_WALL) && !bellowBellow.isType(BaseItem.Type.STRUCTURE_DOOR))) {
				StructureItem bellowRight = WorldMap.getInstance().getStructure(i+1, j+1);
				StructureItem bellowLeft = WorldMap.getInstance().getStructure(i-1, j+1);
				boolean wallOnRight = bellowRight != null && (bellowRight.isType(BaseItem.Type.STRUCTURE_WALL) || bellowRight.isType(BaseItem.Type.STRUCTURE_DOOR));
				boolean wallOnLeft = bellowLeft != null && (bellowLeft.isType(BaseItem.Type.STRUCTURE_WALL) || bellowLeft.isType(BaseItem.Type.STRUCTURE_DOOR));
				
				if (wallOnRight && wallOnLeft) {
					sprite = _spriteManager.getWall(item, 5, 0, 0);
				} else if (wallOnLeft) {
					sprite = _spriteManager.getWall(item, 5, 2, 0);
				} else if (wallOnRight) {
					sprite = _spriteManager.getWall(item, 5, 1, 0);
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
			sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE - offsetWall);
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
				StructureItem aboveRight = WorldMap.getInstance().getStructure(i+1, j-1);
				StructureItem bellowRight = WorldMap.getInstance().getStructure(i+1, j+1);
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
			sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE - offsetWall);
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
				BaseItem item = WorldMap.getInstance().getItem(i, j);

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

	void	drawDebug(RenderStates render, int fromX, int fromY, int toX, int toY) {
		Color color = new Color(0, 0, 0);
		_shapeDebug.setSize(ObjectPool.getVector2f(Constant.TILE_SIZE, Constant.TILE_SIZE));
		_shapeDebug.setFillColor(new Color(250, 200, 200, 100));

		Text text = ObjectPool.getText();
		text.setFont(SpriteManager.getInstance().getFont());
		text.setCharacterSize(10);
		text.setColor(color);
		text.setString("gg");

		for (int i = toX-1; i >= fromX; i--) {
			for (int j = toY-1; j >= fromY; j--) {
				WorldArea item = WorldMap.getInstance().getArea(i, j);
				//
				if (item == null) {
					item = WorldMap.getInstance().getArea(i, j);
				}

				if (item != null) {
					_shapeDebug.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
					_app.draw(_shapeDebug, render);
				}
				//
				text.setStyle(Text.REGULAR);
				text.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
				//			_app.draw(text, render);
			}
		}
		ObjectPool.release(text);
	}

	public static WorldRenderer getInstance() {
		return _self;
	}

	public void invalidate(int x, int y) {
		_changed.add(new Vector2i(x, y));
	}

}