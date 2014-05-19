package alone.in.deepspace.engine.renderer;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jsfml.graphics.Font;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Shader;
import org.jsfml.graphics.ShaderSourceException;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Clock;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import alone.in.deepspace.UserInterface.UserInterface;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.Utils.Log;
import alone.in.deepspace.manager.RoomManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.manager.SpriteManager;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.Room;
import alone.in.deepspace.model.StructureItem;
import alone.in.deepspace.model.WorldArea;
import alone.in.deepspace.model.WorldRessource;

public class WorldRenderer implements IRenderer {
	private SpriteManager			_spriteManager;
	private Font					_font;
	private UserInterface			_ui;
	private RectangleShape 			_shape;
	private RectangleShape 			_shapeDebug;
	private int 					_lastSpecialY;
	private int 					_lastSpecialX;
	private Sprite 					_spriteCache;
	private RenderTexture 			_textureCache;
	private boolean 				_hasChanged;
	private int						_pass;

	private Set<Vector2i> 			_changed;

	public WorldRenderer(RenderWindow app, SpriteManager spriteManager, UserInterface ui) throws IOException, TextureCreationException {
		_ui = ui;
		_spriteManager = spriteManager;
		_shape = new RectangleShape();
		_shape.setSize(new Vector2f(Constant.TILE_SIZE, Constant.TILE_SIZE));
		_shapeDebug = new RectangleShape();
		_changed = new HashSet<Vector2i>();
		
		_spriteCache = new Sprite();

		_textureCache = new RenderTexture();
		_textureCache.create(Constant.WORLD_WIDTH * Constant.TILE_SIZE, Constant.WORLD_HEIGHT * Constant.TILE_SIZE);
		_textureCache.setSmooth(true);
		_textureCache.display();
		
		_hasChanged = true;
	}

	public void onDraw(RenderWindow app, RenderStates render, int frame) {

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
		app.draw(sp, render);

		long elapsed = display_timer.getElapsedTime().asMilliseconds();
		if (elapsed > 3)
			Log.info("display floor: " + elapsed + "ms");

		
//		display_timer.restart();
//		Log.info("display structure: " + display_timer.getElapsedTime().asMicroseconds());

		display_timer.restart();
		refreshItems(app, render, fromX, fromY, toX, toY);
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

						// TODO
//						// Greenhouse
						if (structure.getName().equals("base.greenhouse")) {
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
						if (item.isStructure() && !item.isFloor()) {
	
							// Door
							if (item.isDoor()) {
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
							else if (item.isWall()) {
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
							
							if (item.isWindow()) {
								sprite = SpriteManager.getInstance().getIcon(item.getInfo());
								if (sprite != null) {
									sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
									_textureCache.draw(sprite);
								}
							}
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
		if (bellow != null && (bellow.isWall() || bellow.isDoor())) {
			StructureItem bellowBellow = ServiceManager.getWorldMap().getStructure(i, j+2);
			if (bellow.isDoor() ||
					bellowBellow == null || (!bellowBellow.isWall() && !bellowBellow.isDoor())) {
				StructureItem bellowRight = ServiceManager.getWorldMap().getStructure(i+1, j+1);
				StructureItem bellowLeft = ServiceManager.getWorldMap().getStructure(i-1, j+1);
				boolean wallOnRight = bellowRight != null && (bellowRight.isWall() || bellowRight.isDoor());
				boolean wallOnLeft = bellowLeft != null && (bellowLeft.isWall() || bellowLeft.isDoor());
				
				if (wallOnRight && wallOnLeft) {
					sprite = _spriteManager.getWall(item, 5, 0, 0);
				} else if (wallOnLeft) {
					boolean wallOnSupRight = right != null && (right.isWall() || right.isDoor());
					if (wallOnSupRight) {
						sprite = _spriteManager.getWall(item, 1, 5, 0);
					} else {
						sprite = _spriteManager.getWall(item, 5, 2, 0);
					}
				} else if (wallOnRight) {
					boolean wallOnSupLeft = left != null && (left.isWall() || left.isDoor());
					if (wallOnSupLeft) {
						sprite = _spriteManager.getWall(item, 1, 4, 0);
					} else {
						sprite = _spriteManager.getWall(item, 5, 1, 0);
					}
				} else {
					sprite = _spriteManager.getWall(item, 5, 3, 0);
				}
			} else {
				boolean wallOnRight = right != null && (right.isWall() || right.isDoor());
				boolean wallOnLeft = left != null && (left.isWall() || left.isDoor());
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
//		else if (above != null && above.isWall()) {
//			sprite = _spriteManager.getWall(item, 5, 0, 0);
//			sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE - offsetWall);
//		}

		// No wall above or bellow
		else if (bellow == null || bellow.isWall() == false) {

			// Check double wall
			boolean doubleWall = false;
			if (right != null && right.isComplete() && right.isWall() &&
					(_lastSpecialY != j || _lastSpecialX != i+1)) {
				StructureItem aboveRight = ServiceManager.getWorldMap().getStructure(i+1, j-1);
				StructureItem bellowRight = ServiceManager.getWorldMap().getStructure(i+1, j+1);
				if ((aboveRight == null || aboveRight.isWall() == false) &&
						(bellowRight == null || bellowRight.isWall() == false)) {
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

	void	refreshItems(RenderWindow app, RenderStates render, int fromX, int fromY, int toX, int toY) {
		int offsetY = -16;
		int offsetX = 2;

		for (int i = fromX-1; i <= toX; i++) {
			for (int j = fromY-1; j <= toY; j++) {
				BaseItem item = ServiceManager.getWorldMap().getItem(i, j);

				if (item != null) {

					// Draw item
					if (item.isDoor() == false && item.isStructure() == false) {
						Sprite sprite = _spriteManager.getItem(item);
						if (sprite != null) {
							if (item.isStructure()) {
								sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
							} else {
								sprite.setPosition(i * Constant.TILE_SIZE + offsetX, j * Constant.TILE_SIZE + offsetY);
							}
							app.draw(sprite, render);
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

	public void invalidate(int x, int y) {
		_changed.add(new Vector2i(x, y));
	}

}
