package alone.in.deepspace.World;

import java.io.IOException;
import java.util.Vector;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Managers.SpriteManager;
import alone.in.deepspace.Models.BaseItem;
import alone.in.deepspace.Models.Room;
import alone.in.deepspace.UserInterface.RoomManager;
import alone.in.deepspace.UserInterface.UserInterface;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.Utils.ObjectPool;
import alone.in.deepspace.Utils.Settings;
import alone.in.deepspace.World.WorldMap.DebugPos;

public class WorldRenderer {
	RenderWindow			_app;
	SpriteManager			_spriteManager;
	Font					_font;
	UserInterface			_ui;
	private RectangleShape 	_shape;
	private RectangleShape 	_shapeDebug;
	private int 			_lastSpecialY;
	private int 			_lastSpecialX;
	
	public WorldRenderer(RenderWindow app, SpriteManager spriteManager, UserInterface ui) throws IOException {
		_ui = ui;
		_app = app;
		_spriteManager = spriteManager;
		_shape = new RectangleShape();
		_shape.setSize(new Vector2f(Constant.TILE_SIZE, Constant.TILE_SIZE));
		_shapeDebug = new RectangleShape();

		// TODO
		//_font.loadFromFile((new File("res/xolonium/Xolonium-Regular.otf")).toPath());
	}

	public void	refresh(RenderStates render) {

		int fromX = Math.max(_ui.getRelativePosXMin(0)-1, 0);
		int fromY = Math.max(_ui.getRelativePosYMin(0)-1, 0);
		int toX = Math.min(_ui.getRelativePosXMax(Constant.WINDOW_WIDTH)+1, WorldMap.getInstance().getWidth());
		int toY = Math.min(_ui.getRelativePosYMax(Constant.WINDOW_HEIGHT)+1, WorldMap.getInstance().getHeight());

	  // Debug() << "Renderer: " << fromX << " to: " << toX;

		refreshFloor(render, fromX, fromY, toX, toY);
		refreshStructure(render, fromX, fromY, toX, toY);
		refreshItems(render, fromX, fromY, toX, toY);

		Vector<DebugPos> debugPath = WorldMap.getInstance().getDebug();
		DebugPos startDebugPath = WorldMap.getInstance().getStartDebug();
		DebugPos stopDebugPath = WorldMap.getInstance().getStopDebug();
		Sprite sprite = SpriteManager.getInstance().getBullet(0);
		if (debugPath != null) {
			for (DebugPos pos: debugPath) {
				sprite.setPosition(pos.x * Constant.TILE_SIZE, pos.y * Constant.TILE_SIZE);
				_app.draw(sprite, render);
			}
		}
		if (startDebugPath != null) {
			sprite = SpriteManager.getInstance().getBullet(2);
			sprite.setPosition(startDebugPath.x * Constant.TILE_SIZE, startDebugPath.y * Constant.TILE_SIZE);
			_app.draw(sprite, render);
		}
		if (stopDebugPath != null) {
			sprite = SpriteManager.getInstance().getBullet(3);
			sprite.setPosition(stopDebugPath.x * Constant.TILE_SIZE, stopDebugPath.y * Constant.TILE_SIZE);
			_app.draw(sprite, render);
		}

	  // Draw debug
	  if (Settings.getInstance().isDebug()) {
		drawDebug(render, fromX, fromY, toX, toY);
	  }
	}

	// TODO: random
	void	refreshFloor(RenderStates render, int fromX, int fromY, int toX, int toY) {
	  for (int i = toX-1; i >= fromX; i--) {
		for (int j = toY-1; j >= fromY; j--) {

			// Oxygen
			WorldArea area = WorldMap.getInstance().getArea(i, j);
			if (area != null) {
				Sprite sprite = _spriteManager.getExterior(i + j * 42);
				sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
				_app.draw(sprite, render);
	
				if (area.getStructure() != null && area.getStructure().isType(BaseItem.Type.STRUCTURE_FLOOR)) {
					if (area.getOxygen() < 25) {
						sprite = _spriteManager.getNoOxygen();
						_app.draw(sprite, render);
					} else if (area.getOxygen() < 100) {
						_shape.setFillColor(ObjectPool.getColor(255, 0, 0, area.getOxygen() * 125 / 100));
						_shape.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
						_app.draw(_shape, render);
					}
				}
			}

			// Structure
			StructureItem item = WorldMap.getInstance().getStructure(i, j);
			if (item != null) {
				if (item.isType(BaseItem.Type.STRUCTURE_DOOR)) {
					Sprite sprite = _spriteManager.getItem(item);
					sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
					_app.draw(sprite, render);
				} else {
					Room room = RoomManager.getInstance().get(i, j);
					int roomId = room != null ? room.getType().ordinal() : 0;

					Sprite sprite = _spriteManager.getFloor(item, roomId, 0);
					sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
					_app.draw(sprite, render);
				}
			}
		  
			// Ressource
			if (item == null) {
				WorldRessource ressource = WorldMap.getInstance().getRessource(i, j);
				if (ressource != null) {
					Sprite sprite = _spriteManager.getRessource(ressource);
					sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
					_app.draw(sprite, render);
				}
			}
			
//			Room room = RoomManager.getInstance().get(i, j);
//			if (room != null) {
//				Sprite sprite = _spriteManager.getNoOxygen();
//				sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
//				_app.draw(sprite, render);
//			}
		}
	  }
	}

	//TODO: random
	void	refreshStructure(RenderStates render, int fromX, int fromY, int toX, int toY) {
	  _lastSpecialX = -1;
	  _lastSpecialY = -1;
	  int offsetWall = (Constant.TILE_SIZE / 2 * 3) - Constant.TILE_SIZE;

	  Sprite sprite = null;
	  
	  for (int j = toY-1; j >= fromY; j--) {
		for (int i = toX-1; i >= fromX; i--) {
		  int r = (int) Math.random();
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
				_app.draw(sprite, render);
			}
		  }
		}
	  }
	}

	private Sprite drawWall(StructureItem item, int i, int j, int offsetWall) {
		Sprite sprite = null;
		
		StructureItem bellow = WorldMap.getInstance().getStructure(i, j+1);
		StructureItem right = WorldMap.getInstance().getStructure(i+1, j);
		StructureItem above = WorldMap.getInstance().getStructure(i, j-1);

		  Room room = RoomManager.getInstance().get(i, j + 1);
		  int zone = room != null ? room.getType().ordinal() : 0;

		// bellow is a wall
		if (bellow != null && bellow.isType(BaseItem.Type.STRUCTURE_WALL)) {
			sprite = _spriteManager.getWall(item, 1, 0, 0);
			sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE - offsetWall);
		}

		// No wall above or bellow
		else if ((above == null || above.getType() != BaseItem.Type.STRUCTURE_WALL) &&
				 (bellow == null || bellow.getType() != BaseItem.Type.STRUCTURE_WALL)) {

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

						if (item.isStructure()) {
							sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
						} else {
							sprite.setPosition(i * Constant.TILE_SIZE + offsetX, j * Constant.TILE_SIZE + offsetY);
						}

						_app.draw(sprite, render);
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

}
