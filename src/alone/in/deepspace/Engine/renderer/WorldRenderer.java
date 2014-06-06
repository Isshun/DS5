package alone.in.deepspace.engine.renderer;

import java.io.IOException;
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

import alone.in.deepspace.manager.RoomManager;
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
	private UserInterface			_ui;
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
	private RenderWindow _app;
	private RenderStates _render;

	public WorldRenderer(SpriteManager spriteManager, UserInterface ui) throws IOException, TextureCreationException {
		_ui = ui;
		_spriteManager = spriteManager;
		_shape = new RectangleShape();
		_shape.setSize(new Vector2f(Constant.TILE_WIDTH, Constant.TILE_HEIGHT));
		_changed = new HashSet<Vector2i>();
		_worldMap = ServiceManager.getWorldMap();

		_spriteCache = new Sprite();

		_textureCache = new RenderTexture();
		_textureCache.create(Constant.WORLD_WIDTH * Constant.TILE_WIDTH, Constant.WORLD_HEIGHT * Constant.TILE_HEIGHT);
		_textureCache.setSmooth(true);
		_textureCache.display();

		_hasChanged = true;
	}

	public void onRefresh(int frame) {
		_frame = frame;
		int fromX = Math.max(_ui.getRelativePosXMin(0)-1, 0);
		int fromY = Math.max(_ui.getRelativePosYMin(0)-1, 0);
		int toX = Math.min(_ui.getRelativePosXMax(Constant.WINDOW_WIDTH)+1, _worldMap.getWidth());
		int toY = Math.min(_ui.getRelativePosYMax(Constant.WINDOW_HEIGHT)+1, _worldMap.getHeight());

		if (_hasChanged || _changed.size() > 0) {
			if (_hasChanged) {
				_textureCache.clear();
				refreshFloor(0, 0, Constant.WORLD_WIDTH, Constant.WORLD_HEIGHT);
				refreshStructure(0, 0, Constant.WORLD_WIDTH, Constant.WORLD_HEIGHT);
				refreshResource(0, 0, Constant.WORLD_WIDTH, Constant.WORLD_HEIGHT);
			} else {
//				for (Vector2i vector: _changed) {
//					refreshFloor(vector.x - 1, vector.y - 1, vector.x + 2, vector.y + 2);
//				}
				refreshFloor(0, 0, Constant.WORLD_WIDTH, Constant.WORLD_HEIGHT);
				refreshStructure(0, 0, Constant.WORLD_WIDTH, Constant.WORLD_HEIGHT);
				refreshResource(0, 0, Constant.WORLD_WIDTH, Constant.WORLD_HEIGHT);
			}
			_changed.clear();
			_hasChanged = false;
		}

		_spriteCache.setTexture(_textureCache.getTexture());
	}

	public void onDraw(RenderWindow app, RenderStates render, double animProgress) {

		// Debug() << "Renderer: " << fromX << " to: " << toX;

		_app = app;
		_render = render;
		
		Clock display_timer = new Clock();
		Sprite sp = new Sprite(_textureCache.getTexture());
		app.draw(sp, render);
		
		refreshItems(_frame, 0, 0, Constant.WORLD_WIDTH, Constant.WORLD_HEIGHT);


		//		if (_itemSelected != null) {
		//			Sprite sprite = _spriteManager.getSelector(_itemSelected, _frame);
		//			sprite.setPosition(_itemSelected.getX() * Constant.TILE_WIDTH, _itemSelected.getY() * Constant.TILE_HEIGHT);
		//			app.draw(sprite, render);
		//		}

		long elapsed = display_timer.getElapsedTime().asMilliseconds();
		if (elapsed > 3)
			Log.info("display floor: " + elapsed + "ms");


		//		display_timer.restart();
		//		Log.info("display structure: " + display_timer.getElapsedTime().asMicroseconds());

		display_timer.restart();
	}

	private void refreshResource(int fromX, int fromY, int toX, int toY) {
		for (int i = toX-1; i >= fromX; i--) {
			for (int j = toY-1; j >= fromY; j--) {
				if (i >= 0 && j >= 0 && i < Constant.WORLD_WIDTH && j < Constant.WORLD_HEIGHT) {
					//					WorldResource ressource = _worldMap.getRessource(i, j);
					//					if (ressource != null && ressource.isDepleted() == false) {
					//						Sprite sprite = _spriteManager.getRessource(ressource, ressource.getTile());
					//						sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT - 16);
					//						_textureCache.draw(sprite);
					//					}
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

					Room room = RoomManager.getInstance().get(i, j);
					if (structure != null && structure.roomCanBeSet() == false) {
						structure = _worldMap.getStructure(i, j-1);
						room = RoomManager.getInstance().get(i, j-1);
					}

					if (structure != null && structure.isFloor()) {

						// TODO
						//						// Greenhouse
						if (structure.getName().equals("base.greenhouse")) {
							int index = room != null && room.isType(Room.Type.GARDEN) ? 0 : 2;
							Sprite sprite = _spriteManager.getGreenHouse(index + (structure.isWorking() ? 1 : 0));
							sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT);
							_textureCache.draw(sprite);
						}

						// Floor
						else {
							int roomId = room != null ? room.getType().ordinal() : 0;
							Sprite sprite = _spriteManager.getFloor(structure, roomId, 0);
							if (sprite != null) {
								sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT);
								_textureCache.draw(sprite);
							}
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

		Room room = RoomManager.getInstance().get(i, j + 1);
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
		int offsetY = 0;
		int offsetX = 0;

		for (int x = fromX-1; x <= toX; x++) {
			for (int y = fromY-1; y <= toY; y++) {
				// Item
				ItemBase item = _worldMap.getItem(x, y);
				if (item != null) {
					Sprite sprite = _spriteManager.getItem(item, item.getCurrentFrame());
					if (sprite != null) {
						if (item.isStructure()) {
							sprite.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
						} else {
							sprite.setPosition(x * Constant.TILE_WIDTH + offsetX, y * Constant.TILE_HEIGHT + offsetY);
						}
						_app.draw(sprite, _render);
					}

					// Selection
					if (item.isSelected()) {
						_itemSelected = item;
						
						int offset = 0;
						switch (frame % 5) {
						case 1: offset = 1; break;
						case 2: offset = 2; break;
						case 3: offset = 3; break;
						case 4: offset = 2; break;
						case 5: offset = 1; break;
						}
						
						sprite = _spriteManager.getSelectorCorner(0);
						sprite.setPosition(x * Constant.TILE_WIDTH - offset, y * Constant.TILE_HEIGHT - offset);
						_app.draw(sprite, _render);

						sprite = _spriteManager.getSelectorCorner(1);
						sprite.setPosition((x + item.getWidth()) * Constant.TILE_WIDTH - 6 + offset, y * Constant.TILE_HEIGHT - offset);
						_app.draw(sprite, _render);
						
						sprite = _spriteManager.getSelectorCorner(2);
						sprite.setPosition(x * Constant.TILE_WIDTH - offset, (y + item.getHeight()) * Constant.TILE_HEIGHT - 6 + offset);
						_app.draw(sprite, _render);
						
						sprite = _spriteManager.getSelectorCorner(3);
						sprite.setPosition((x + item.getWidth()) * Constant.TILE_WIDTH - 6 + offset, (y + item.getHeight()) * Constant.TILE_HEIGHT - 6 + offset);
						_app.draw(sprite, _render);
					}
				}

				refreshResource(x, y);
			}
		}
	}

	// Resource
	private void refreshResource(int x, int y) {
		WorldResource resource = _worldMap.getRessource(x, y);
		if (resource != null) {
			Sprite sprite = _spriteManager.getRessource(resource, resource.getTile(), (int)Math.min(resource.getValue(), 5));
			sprite.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
			_textureCache.draw(sprite);
		}
	}

	public void invalidate(int x, int y) {
		_changed.add(new Vector2i(x, y));
	}

	public void invalidate() {
		_hasChanged = true;
	}

}
