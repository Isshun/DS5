package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.Game;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.SpriteModel;
import org.smallbox.faraway.engine.ui.ColorView;
import org.smallbox.faraway.engine.ui.ViewFactory;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.manager.WorldManager;
import org.smallbox.faraway.model.item.*;
import org.smallbox.faraway.model.room.Room;
import org.smallbox.faraway.ui.UserInterface;

import java.util.HashSet;
import java.util.Set;

public class WorldRenderer implements IRenderer {
	private static class Vector2i {
		public final int x;
		public final int y;
		public Vector2i(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	private SpriteManager 			_spriteManager;
	private ColorView               _shape;
	private int 					_lastSpecialY;
	private int 					_lastSpecialX;
	private RenderLayer 			_layerStructure;
	private RenderLayer 			_layerItem;
	private boolean 				_hasChanged;
	private int						_pass;

	private Set<Vector2i> 			_changed;
	private WorldManager 			_worldMap;
	private ItemBase 				_itemSelected;
	private int 					_frame;

	public WorldRenderer(SpriteManager spriteManager) {
		_spriteManager = spriteManager;
		_shape = ViewFactory.getInstance().createColorView(Constant.TILE_WIDTH, Constant.TILE_HEIGHT);
		_changed = new HashSet<>();

		_layerStructure = ViewFactory.getInstance().createRenderLayer(Constant.WORLD_WIDTH * Constant.TILE_WIDTH, Constant.WORLD_HEIGHT * Constant.TILE_HEIGHT);
		_layerItem = ViewFactory.getInstance().createRenderLayer(Constant.WORLD_WIDTH * Constant.TILE_WIDTH, Constant.WORLD_HEIGHT * Constant.TILE_HEIGHT);

		_hasChanged = true;
	}

	public void onRefresh(int frame) {
		if (_worldMap == null) {
			_worldMap = Game.getWorldManager();
		}

		UserInterface ui = UserInterface.getInstance();
		_frame = frame;
		int fromX = Math.max(ui.getRelativePosXMin(0)-1, 0);
		int fromY = Math.max(ui.getRelativePosYMin(0)-1, 0);
		int toX = Math.min(ui.getRelativePosXMax(Constant.WINDOW_WIDTH)+1, _worldMap.getWidth());
		int toY = Math.min(ui.getRelativePosYMax(Constant.WINDOW_HEIGHT)+1, _worldMap.getHeight());

		if (_hasChanged || _changed.size() > 0) {
			_layerItem.clear();

			if (_hasChanged) {
				_layerStructure.clear();
				refreshFloor(0, 0, Constant.WORLD_WIDTH, Constant.WORLD_HEIGHT);
				refreshStructure(0, 0, Constant.WORLD_WIDTH, Constant.WORLD_HEIGHT);
//				refreshResource(0, 0, Constant.WORLD_WIDTH, Constant.WORLD_HEIGHT);
				refreshResource(fromX, fromY, toX, toY);
				refreshItems(frame, fromX, fromY, toX, toY);
			} else {
				for (Vector2i vector: _changed) {
					refreshFloor(vector.x - 1, vector.y - 1, vector.x + 2, vector.y + 2);
//					refreshResource(vector.x - 1, vector.y - 1, vector.x + 2, vector.y + 2);
				}
				refreshResource(0, 0, Constant.WORLD_WIDTH, Constant.WORLD_HEIGHT);
				refreshItems(frame, 0, 0, Constant.WORLD_WIDTH, Constant.WORLD_HEIGHT);
				refreshStructure(0, 0, Constant.WORLD_WIDTH, Constant.WORLD_HEIGHT);
			}
			_changed.clear();
			_hasChanged = false;
		}
	}

	public void onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress) {
		_layerStructure.onDraw(renderer, effect);
		_layerItem.onDraw(renderer, effect);
	}

	private void refreshResource(int fromX, int fromY, int toX, int toY) {
		for (int i = toX-1; i >= fromX; i--) {
			for (int j = toY-1; j >= fromY; j--) {
				if (i >= 0 && j >= 0 && i < Constant.WORLD_WIDTH && j < Constant.WORLD_HEIGHT) {
					WorldResource ressource = _worldMap.getResource(i, j);
					if (ressource != null) {
						SpriteModel sprite = _spriteManager.getResource(ressource);
						if (sprite != null) {
							sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT);
							_layerItem.draw(sprite);
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

					Room room = null;
//					Room room = Game.getRoomManager().get(i, j);
//					if (structure != null && structure.roomCanBeSet() == false) {
//						structure = _worldMap.getStructure(i, j-1);
//						room = Game.getRoomManager().get(i, j-1);
//					}

					if (structure != null && structure.isFloor()) {

						// TODO
//						// Greenhouse
						if (structure.getName().equals("base.greenhouse")) {
							int index = room != null && room.isType(Room.RoomType.GARDEN) ? 0 : 2;
							SpriteModel sprite = _spriteManager.getGreenHouse(index + (structure.isWorking() ? 1 : 0));
							sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT);
							_layerStructure.draw(sprite);

							WorldResource ressource = _worldMap.getResource(i, j);
							if (ressource != null && ressource.getMatterSupply() > 0) {
								sprite = _spriteManager.getResource(ressource);
								sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT);
								_layerStructure.draw(sprite);
							}
						}

						// Floor
						else {
//							Room room = Game.getRoomManager().get(i, j + 1);
//							int zone = room != null ? room.getSceneType().ordinal() : 0;
							int roomId = room != null ? room.getType().ordinal() : 0;
							SpriteModel sprite = _spriteManager.getFloor(structure, roomId, 0);
							if (sprite != null) {
								sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT);
								_layerStructure.draw(sprite);
							}

							WorldResource ressource = _worldMap.getResource(i, j);
							if (ressource != null) {
								sprite = _spriteManager.getResource(ressource);
								sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT);
								_layerStructure.draw(sprite);
							}


//							RectangleShape shape = new RectangleShape(new Vector2f(32, 32));
//							shape.setFillColor(new Color(0, 0, 0, 155 * (12 - area.getLight()) / 12));
//							shape.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
//							_texture.draw(shape);
						}
					}

					// No floor
					else {
						SpriteModel sprite = _spriteManager.getExterior(i + j * 42, floor);
						sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT);
						_layerStructure.draw(sprite);
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

		SpriteModel sprite = null;
		for (int j = toY-1; j >= fromY; j--) {
			for (int i = toX-1; i >= fromX; i--) {
				if (i >= 0 && j >= 0 && i < Constant.WORLD_WIDTH && j < Constant.WORLD_HEIGHT) {
					StructureItem item = _worldMap.getStructure(i, j);
					if (item != null) {

						// Door
						if (item.isDoor()) {
							// if (_characterManager.getCharacterAtPos(i, j) != null
							// 	  || _characterManager.getCharacterAtPos(i+1, j) != null
							// 	  || _characterManager.getCharacterAtPos(i-1, j) != null
							// 	  || _characterManager.getCharacterAtPos(i, j+1) != null
							// 	  || _characterManager.getCharacterAtPos(i, j-1) != null) {
							// 	_spriteManager.getWall(item, 2, &sprite, 0, 0);
							// } else {

							sprite = _spriteManager.getSimpleWall(0);
							if (sprite != null) {
								sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT - offsetWall);
								_layerStructure.draw(sprite);
							}

							sprite = _spriteManager.getItem(item);
							if (sprite != null) {
								sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT - 4);
								_layerStructure.draw(sprite);
							}
						}

						// Floor
						else if (item.isFloor()) {
						}

						// Wall
						else if (item.isWall()) {
							sprite = drawWall(item, i, j, offsetWall);
							_layerStructure.draw(sprite);
						}

						// Hull
						else if (item.isHull()) {
							sprite = drawWall(item, i, j, offsetWall);
							_layerStructure.draw(sprite);
						}

						else {
							sprite = SpriteManager.getInstance().getItem(item);
							sprite.setPosition(item.getX() * Constant.TILE_WIDTH, item.getY() * Constant.TILE_HEIGHT);
							_layerStructure.draw(sprite);
						}
					}
				}
			}
		}
	}

	private SpriteModel drawWall(StructureItem item, int i, int j, int offsetWall) {
		SpriteModel sprite = null;

		StructureItem bellow = _worldMap.getStructure(i, j+1);
		StructureItem right = _worldMap.getStructure(i+1, j);
		StructureItem left = _worldMap.getStructure(i-1, j);

		Room room = null;
//		Room room = Game.getRoomManager().get(i, j + 1);
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

		for (int x = fromX-1; x <= toX; x++) {
			for (int y = fromY-1; y <= toY; y++) {
				UserItem item = _worldMap.getItem(x, y);
				if (item != null && item.getX() == x && item.getY() == y) {

					// Regular item
					SpriteModel sprite = _spriteManager.getItem(item, item.getCurrentFrame());
					if (sprite != null) {
						sprite.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
						_layerItem.draw(sprite);
					}

					// Selection
					if (item.isSelected()) {
						_itemSelected = item;
					}
				}

				ConsumableItem consumable = _worldMap.getConsumable(x, y);
				if (consumable != null) {

					// Regular item
					SpriteModel sprite = _spriteManager.getItem(consumable, consumable.getCurrentFrame());
					if (sprite != null) {
						sprite.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
						_layerItem.draw(sprite);
					}

					// Selection
					if (consumable.isSelected()) {
						_itemSelected = consumable;
					}
				}
			}
		}
	}

	//	private void refreshStack(StackItem stack, int x, int y) {
//		SpriteModel sprite = _spriteManager.getIcon(stack.getStackedInfo());
//		if (sprite != null) {
//			sprite.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
//			_layerItem.draw(sprite);
//		}
//		TextView text = SpriteManager.getInstance().createTextView();
//		text.setCharacterSize(12);
//		text.setString("x" + stack.size());
//		text.setPosition(x * Constant.TILE_WIDTH + (stack.size() < 10 ? 18 : 10), y * Constant.TILE_HEIGHT + 18);
//		_layerItem.draw(text);
//	}
//
	private void refreshSelected(GFXRenderer renderer, RenderEffect effect, int frame, ItemBase item) {
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

		SpriteModel sprite = _spriteManager.getSelectorCorner(0);
		sprite.setPosition(x * Constant.TILE_WIDTH - offset, y * Constant.TILE_HEIGHT - offset);
		renderer.draw(sprite, effect);

		sprite = _spriteManager.getSelectorCorner(1);
		sprite.setPosition((x + item.getWidth()) * Constant.TILE_WIDTH - 6 + offset, y * Constant.TILE_HEIGHT - offset);
		renderer.draw(sprite, effect);

		sprite = _spriteManager.getSelectorCorner(2);
		sprite.setPosition(x * Constant.TILE_WIDTH - offset, (y + item.getHeight()) * Constant.TILE_HEIGHT - 6 + offset);
		renderer.draw(sprite, effect);

		sprite = _spriteManager.getSelectorCorner(3);
		sprite.setPosition((x + item.getWidth()) * Constant.TILE_WIDTH - 6 + offset, (y + item.getHeight()) * Constant.TILE_HEIGHT - 6 + offset);
		renderer.draw(sprite, effect);
	}

	public void invalidate(int x, int y) {
//		_changed.add(new Vector2i(x, y));
		_hasChanged = true;
	}

	public void invalidate() {
		_hasChanged = true;
	}

	public void onDrawSelected(GFXRenderer renderer, RenderEffect effect, double animProgress) {
		if (_itemSelected != null) {
			refreshSelected(renderer, effect, _frame, _itemSelected);
		}
	}

}
