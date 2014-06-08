package alone.in.deepspace.engine.renderer;

import java.util.HashSet;
import java.util.Set;

import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Clock;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import alone.in.deepspace.Game;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.manager.SpriteManager;
import alone.in.deepspace.manager.WorldManager;
import alone.in.deepspace.model.item.ItemBase;
import alone.in.deepspace.model.item.StructureItem;
import alone.in.deepspace.model.item.WorldResource;
import alone.in.deepspace.model.room.Room;
import alone.in.deepspace.ui.UserInterface;
import alone.in.deepspace.util.Constant;
import alone.in.deepspace.util.Log;

public class WorldRenderer implements IRenderer {
	private SpriteManager			_spriteManager;
	private RectangleShape 			_shape;
	private int 					_lastSpecialY;
	private int 					_lastSpecialX;
	private Sprite 					_spriteCache;
	private RenderTexture 			_textureCache;
	private boolean 				_hasChanged;
	private int						_pass;

	private Set<Vector2i> 			_changed;
	private WorldManager 			_worldMap;
	private ItemBase 				_itemSelected;
	private int 					_frame;

	public WorldRenderer(SpriteManager spriteManager) {
		_spriteManager = spriteManager;
		_shape = new RectangleShape();
		_shape.setSize(new Vector2f(Constant.TILE_WIDTH, Constant.TILE_HEIGHT));
		_changed = new HashSet<Vector2i>();
		_worldMap = ServiceManager.getWorldMap();
		
		_spriteCache = new Sprite();

		try {
			_textureCache = new RenderTexture();
			_textureCache.create(Constant.WORLD_WIDTH * Constant.TILE_WIDTH, Constant.WORLD_HEIGHT * Constant.TILE_HEIGHT);
			_textureCache.setSmooth(true);
			_textureCache.display();
		} catch (TextureCreationException e) {
			e.printStackTrace();
		}
		
		_hasChanged = true;
	}

	public void onRefresh(int frame) {
		UserInterface ui = UserInterface.getInstance();
		_frame = frame;
		int fromX = Math.max(ui.getRelativePosXMin(0)-1, 0);
		int fromY = Math.max(ui.getRelativePosYMin(0)-1, 0);
		int toX = Math.min(ui.getRelativePosXMax(Constant.WINDOW_WIDTH)+1, _worldMap.getWidth());
		int toY = Math.min(ui.getRelativePosYMax(Constant.WINDOW_HEIGHT)+1, _worldMap.getHeight());

		if (_hasChanged || _changed.size() > 0) {
			if (_hasChanged) {
				_textureCache.clear();
				refreshFloor(0, 0, Constant.WORLD_WIDTH, Constant.WORLD_HEIGHT);
				refreshStructure(0, 0, Constant.WORLD_WIDTH, Constant.WORLD_HEIGHT);
				refreshResource(0, 0, Constant.WORLD_WIDTH, Constant.WORLD_HEIGHT);
			} else {
				for (Vector2i vector: _changed) {
					refreshFloor(vector.x - 1, vector.y - 1, vector.x + 2, vector.y + 2);
					refreshResource(vector.x - 1, vector.y - 1, vector.x + 2, vector.y + 2);
				}
				refreshStructure(0, 0, Constant.WORLD_WIDTH, Constant.WORLD_HEIGHT);
			}
			_spriteCache.setTexture(_textureCache.getTexture());
			_changed.clear();
			_hasChanged = false;
		}
		
		refreshItems(frame, fromX, fromY, toX, toY);
	}
	
	public void onDraw(RenderWindow app, RenderStates render, double animProgress) {
		app.draw(_spriteCache, render);
	}

	private void refreshResource(int fromX, int fromY, int toX, int toY) {
		for (int i = toX-1; i >= fromX; i--) {
			for (int j = toY-1; j >= fromY; j--) {
				if (i >= 0 && j >= 0 && i < Constant.WORLD_WIDTH && j < Constant.WORLD_HEIGHT) {
					WorldResource ressource = _worldMap.getRessource(i, j);
					if (ressource != null) {
						Sprite sprite = _spriteManager.getRessource(ressource);
						if (sprite != null) {
							sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT);
							_textureCache.draw(sprite);
						}
					}
				}
			}
		}
	}

	// TODO: random
	void	refreshFloor(int fromX, int fromY, int toX, int toY) {
		int floor = _worldMap.getFloor();
		
		for (int i = toX-1; i >= fromX; i--) {
			for (int j = toY-1; j >= fromY; j--) {
				if (i >= 0 && j >= 0 && i < Constant.WORLD_WIDTH && j < Constant.WORLD_HEIGHT) {
					// Structure
					StructureItem structure = _worldMap.getStructure(i, j);
					
					Room room = Game.getRoomManager().get(i, j);
					if (structure != null && structure.roomCanBeSet() == false) {
						structure = _worldMap.getStructure(i, j-1);
						room = Game.getRoomManager().get(i, j-1);
					}
	
					if (structure != null && structure.isFloor()) {

						// TODO
//						// Greenhouse
						if (structure.getName().equals("base.greenhouse")) {
							int index = room != null && room.isType(Room.Type.GARDEN) ? 0 : 2;
							Sprite sprite = _spriteManager.getGreenHouse(index + (structure.isWorking() ? 1 : 0));
							sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT);
							_textureCache.draw(sprite);
							
							WorldResource ressource = _worldMap.getRessource(i, j);
							if (ressource != null && ressource.getMatterSupply() > 0) {
								sprite = _spriteManager.getRessource(ressource);
								sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT);
								_textureCache.draw(sprite);
							}
						}
						
						// Floor
						else {
//							Room room = Game.getRoomManager().get(i, j + 1);
//							int zone = room != null ? room.getType().ordinal() : 0;
							int roomId = room != null ? room.getType().ordinal() : 0;
							Sprite sprite = _spriteManager.getFloor(structure, roomId, 0);
							if (sprite != null) {
								sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT);
								_textureCache.draw(sprite);
							}
							
							WorldResource ressource = _worldMap.getRessource(i, j);
							if (ressource != null) {
								sprite = _spriteManager.getRessource(ressource);
								sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT);
								_textureCache.draw(sprite);
							}

							
//							RectangleShape shape = new RectangleShape(new Vector2f(32, 32));
//							shape.setFillColor(new Color(0, 0, 0, 155 * (12 - area.getLight()) / 12));
//							shape.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
//							_texture.draw(shape);
						}
					}
					
					// No floor
					else {
						Sprite sprite = _spriteManager.getExterior(i + j * 42, floor);
						sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT);
						_textureCache.draw(sprite);
					}
				}
			}
		}
	}

	//TODO: random
	void	refreshStructure(int fromX, int fromY, int toX, int toY) {
		_lastSpecialX = -1;
		_lastSpecialY = -1;
		int offsetWall = (Constant.TILE_WIDTH / 2 * 3) - Constant.TILE_HEIGHT;

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
					StructureItem item = _worldMap.getStructure(i, j);
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
								sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT - offsetWall);
							}
	
							// Wall
							else if (item.isWall()) {
								sprite = drawWall(item, i, j, offsetWall);
							}	  

							// Hull
							else if (item.isHull()) {
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
								sprite = _spriteManager.getIcon(item.getInfo());
								if (sprite != null) {
									sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT);
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

		StructureItem bellow = _worldMap.getStructure(i, j+1);
		StructureItem right = _worldMap.getStructure(i+1, j);
		StructureItem left = _worldMap.getStructure(i-1, j);

		Room room = Game.getRoomManager().get(i, j + 1);
		int zone = room != null ? room.getType().ordinal() : 0;
		// bellow is a wall
		if (bellow != null && (bellow.isWall() || bellow.isDoor())) {
			StructureItem bellowBellow = _worldMap.getStructure(i, j+2);
			if (bellow.isDoor() ||
					bellowBellow == null || (!bellowBellow.isWall() && !bellowBellow.isDoor())) {
				StructureItem bellowRight = _worldMap.getStructure(i+1, j+1);
				StructureItem bellowLeft = _worldMap.getStructure(i-1, j+1);
				boolean wallOnRight = bellowRight != null && (bellowRight.isWall() || bellowRight.isDoor());
				boolean wallOnLeft = bellowLeft != null && (bellowLeft.isWall() || bellowLeft.isDoor());
				
				if (wallOnRight && wallOnLeft) {
					sprite = _spriteManager.getWall(item, 5, 0, zone);
				} else if (wallOnLeft) {
					boolean wallOnSupRight = right != null && (right.isWall() || right.isDoor());
					if (wallOnSupRight) {
						sprite = _spriteManager.getWall(item, 1, 5, zone);
					} else {
						sprite = _spriteManager.getWall(item, 5, 2, zone);
					}
				} else if (wallOnRight) {
					boolean wallOnSupLeft = left != null && (left.isWall() || left.isDoor());
					if (wallOnSupLeft) {
						sprite = _spriteManager.getWall(item, 1, 4, zone);
					} else {
						sprite = _spriteManager.getWall(item, 5, 1, zone);
					}
				} else {
					sprite = _spriteManager.getWall(item, 5, 3, zone);
				}
			} else {
				boolean wallOnRight = right != null && (right.isWall() || right.isDoor());
				boolean wallOnLeft = left != null && (left.isWall() || left.isDoor());
				if (wallOnRight && wallOnLeft) {
					sprite = _spriteManager.getWall(item, 1, 0, zone);
				} else if (wallOnLeft) {
					sprite = _spriteManager.getWall(item, 1, 2, zone);
				} else if (wallOnRight) {
					sprite = _spriteManager.getWall(item, 1, 1, zone);
				} else {
					sprite = _spriteManager.getWall(item, 1, 3, zone);
				}
			}
			if (sprite != null) {
				sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT - offsetWall);
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
				StructureItem aboveRight = _worldMap.getStructure(i+1, j-1);
				StructureItem bellowRight = _worldMap.getStructure(i+1, j+1);
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
				sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT - offsetWall);
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
			sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT - offsetWall);
		}

		return sprite;
	}

	void	refreshItems(int frame, int fromX, int fromY, int toX, int toY) {
		_itemSelected = null;
		int offsetY = 0;
		int offsetX = 0;

		for (int x = fromX-1; x <= toX; x++) {
			for (int y = fromY-1; y <= toY; y++) {
				ItemBase item = _worldMap.getItem(x, y);
				if (item != null) {
					Sprite sprite = _spriteManager.getItem(item, item.getCurrentFrame());
					if (sprite != null) {
						if (item.isStructure()) {
							sprite.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
						} else {
							sprite.setPosition(x * Constant.TILE_WIDTH + offsetX, y * Constant.TILE_HEIGHT + offsetY);
						}
						_textureCache.draw(sprite);
					}

					// Selection
					if (item.isSelected()) {
						_itemSelected = item;
					}
				}
			}
		}
	}

	private void refreshSelected(RenderWindow app, RenderStates render, int frame, ItemBase item) {
		int x = item.getX();
		int y = item.getY();
		int offset = 0;
		switch (frame % 5) {
		case 1: offset = 1; break;
		case 2: offset = 2; break;
		case 3: offset = 3; break;
		case 4: offset = 2; break;
		case 5: offset = 1; break;
		}
		
		Sprite sprite = _spriteManager.getSelectorCorner(0);
		sprite.setPosition(x * Constant.TILE_WIDTH - offset, y * Constant.TILE_HEIGHT - offset);
		app.draw(sprite, render);

		sprite = _spriteManager.getSelectorCorner(1);
		sprite.setPosition((x + item.getWidth()) * Constant.TILE_WIDTH - 6 + offset, y * Constant.TILE_HEIGHT - offset);
		app.draw(sprite, render);
		
		sprite = _spriteManager.getSelectorCorner(2);
		sprite.setPosition(x * Constant.TILE_WIDTH - offset, (y + item.getHeight()) * Constant.TILE_HEIGHT - 6 + offset);
		app.draw(sprite, render);
		
		sprite = _spriteManager.getSelectorCorner(3);
		sprite.setPosition((x + item.getWidth()) * Constant.TILE_WIDTH - 6 + offset, (y + item.getHeight()) * Constant.TILE_HEIGHT - 6 + offset);
		app.draw(sprite, render);
	}

	public void invalidate(int x, int y) {
		_changed.add(new Vector2i(x, y));
	}

	public void invalidate() {
		_hasChanged = true;
	}

	public void onDrawSelected(RenderWindow app, RenderStates render, double animProgress) {
		if (_itemSelected != null) {
			refreshSelected(app, render, _frame, _itemSelected);
		}
	}

}
