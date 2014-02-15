package alone.in.deepspace;
import java.io.File;
import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.UserInterface.UserInterface;


public class WorldRenderer {
	RenderWindow	_app;
	SpriteManager		_spriteManager;
	Font			_font;
	UserInterface		_ui;
	private Sprite _sprite;
	private RectangleShape _shape;
	private RectangleShape _shapeDebug;
	WorldRenderer(RenderWindow app, SpriteManager spriteManager, UserInterface ui) throws IOException {
		_ui = ui;
		_app = app;
		_spriteManager = spriteManager;
		_sprite = new Sprite();
		_shape = new RectangleShape();
		_shape.setSize(new Vector2f(Constant.TILE_SIZE, Constant.TILE_SIZE));
		_shapeDebug = new RectangleShape();

		// TODO
		//_font.loadFromFile((new File("res/xolonium/Xolonium-Regular.otf")).toPath());
	}

	void	draw(RenderStates render) {

		// TODO
//		int fromX = max(_ui.getRelativePosX(0)-1, 0);
//	  int fromY = max(_ui.getRelativePosY(0)-1, 0);
//	  // int toX = _ui.getRelativePosX(WorldMap.getInstance().getWidth());
//	  // int toY = _ui.getRelativePosY(WorldMap.getInstance().getHeight());
//	  int toX = min(_ui.getRelativePosX(Constant.WINDOW_WIDTH)+1, WorldMap.getInstance().getWidth());
//	  int toY = min(_ui.getRelativePosY(Constant.WINDOW_HEIGHT)+1, WorldMap.getInstance().getHeight());
		int fromX = 0;
		int fromY = 0;
		int toX = 100;
		int toY = 50;

	  // Debug() << "Renderer: " << fromX << " to: " << toX;

	  drawFloor(render, fromX, fromY, toX, toY);
	  drawStructure(render, fromX, fromY, toX, toY);
	  drawItems(render, fromX, fromY, toX, toY);

	  // Draw debug
	  if (Settings.getInstance().isDebug()) {
		drawDebug(render, fromX, fromY, toX, toY);
	  }
	}

	void	drawFloor(RenderStates render, int fromX, int fromY, int toX, int toY) {
	  for (int i = toX-1; i >= fromX; i--) {
		for (int j = toY-1; j >= fromY; j--) {

			// TODO
			//srand(i * j);
		  WorldArea item = WorldMap.getInstance().getArea(i, j);
		  if (item.isType(BaseItem.Type.NONE)) {
			_spriteManager.getExterior(_sprite);
			_sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
			_app.draw(_sprite, render);
		  } else if (item.isType(BaseItem.Type.STRUCTURE_DOOR)) {
			_spriteManager.getSprite(item, _sprite);
			_sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
			_app.draw(_sprite, render);
		  } else if (item.isRessource()) {
			_spriteManager.getRessource(item, _sprite);
			_sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
			_app.draw(_sprite, render);
		  } else {
			_spriteManager.getFloor(item, item.getZoneId(), item.getRoomId(), _sprite);
			_sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
			_app.draw(_sprite, render);

			// Oxygen
			if (item.isType(BaseItem.Type.STRUCTURE_FLOOR)) {
			  if (item.getOxygen() < 25) {
				_spriteManager.getNoOxygen(_sprite);
				_app.draw(_sprite, render);
			  } else if (item.getOxygen() < 100) {
				_shape.setFillColor(ObjectPool.getColor(255, 0, 0, item.getOxygen() * 125 / 100));
				_shape.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
				_app.draw(_shape, render);
			  }
			}
		  }
		}
	  }
	}

	void	drawStructure(RenderStates render, int fromX, int fromY, int toX, int toY) {
	  int lastSpecialX = -1;
	  int lastSpecialY = -1;
	  int offsetWall = (Constant.TILE_SIZE / 2 * 3) - Constant.TILE_SIZE;

	  for (int j = toY-1; j >= fromY; j--) {
		for (int i = toX-1; i >= fromX; i--) {
			//TODO
			//srand(i * j);
		  int r = (int) Math.random();
		  WorldArea item = WorldMap.getInstance().getArea(i, j);
		  if (item != null) {

			// Structure except floor
			if (item.isStructure() && !item.isType(BaseItem.Type.STRUCTURE_FLOOR)) {

			  WorldArea bellow = WorldMap.getInstance().getArea(i, j+1);
			  WorldArea right = WorldMap.getInstance().getArea(i+1, j);
			  WorldArea left = WorldMap.getInstance().getArea(i-1, j);
			  WorldArea above = WorldMap.getInstance().getArea(i, j-1);
		  
			  // Door
			  if (item.isType(BaseItem.Type.STRUCTURE_DOOR)) {
				// if (_characterManager.getCharacterAtPos(i, j) != null
				// 	  || _characterManager.getCharacterAtPos(i+1, j) != null
				// 	  || _characterManager.getCharacterAtPos(i-1, j) != null
				// 	  || _characterManager.getCharacterAtPos(i, j+1) != null
				// 	  || _characterManager.getCharacterAtPos(i, j-1) != null) {
				// 	_spriteManager.getWall(item, 2, &sprite, 0, 0);
				// } else {
				_spriteManager.getWall(item, 0, _sprite, 0, 0);
				// }
				_sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE - offsetWall);
			  }

			  // Wall
			  else if (item.isType(BaseItem.Type.STRUCTURE_WALL)) {

				// bellow is a wall
				if (bellow != null && bellow.isType(BaseItem.Type.STRUCTURE_WALL)) {
				  _spriteManager.getWall(item, 1, _sprite, 0, 0);
				  _sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE - offsetWall);
				}

				// No wall above or bellow
				else if ((above == null || above.getType() != BaseItem.Type.STRUCTURE_WALL) &&
						 (bellow == null || bellow.getType() != BaseItem.Type.STRUCTURE_WALL)) {

				  // Check double wall
				  boolean doubleWall = false;
				  if (right != null && right.isComplete() && right.isType(BaseItem.Type.STRUCTURE_WALL) &&
					  (lastSpecialY != j || lastSpecialX != i+1)) {
					WorldArea aboveRight = WorldMap.getInstance().getArea(i+1, j-1);
					WorldArea bellowRight = WorldMap.getInstance().getArea(i+1, j+1);
					if ((aboveRight == null || aboveRight.getType() != BaseItem.Type.STRUCTURE_WALL) &&
						(bellowRight == null || bellowRight.getType() != BaseItem.Type.STRUCTURE_WALL)) {
					  doubleWall = true;
					}
				  }

				  // Normal
				  if (bellow == null) {
					// Double wall
					if (doubleWall) {
					  _spriteManager.getWall(item, 4, _sprite, r, 0);
					  lastSpecialX = i;
					  lastSpecialY = j;
					}
					// Single wall
					else {
					  _spriteManager.getWall(item, 0, _sprite, 0, 0);
					}
				  }
				  // Special
				  else {
					// Double wall
					if (doubleWall) {
					  _spriteManager.getWall(item, 2, _sprite, r, bellow.getZoneId());
					  lastSpecialX = i;
					  lastSpecialY = j;
					}
					// Single wall
					else {
					  _spriteManager.getWall(item, 3, _sprite, r, bellow.getZoneId());
					}
				  }
				  _sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE - offsetWall);
				}

				// // left is a wall
				// else if (left != null && left.type == BaseItem.STRUCTURE_WALL) {
				// 	_spriteManager.getWall(item, 2, &sprite);
				// 	sprite.setPosition(i * TILE_SIZE - TILE_SIZE, j * TILE_SIZE - offset);
				// }

				// single wall
				else {
				  _spriteManager.getWall(item, 0, _sprite, 0, 0);
				  _sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE - offsetWall);
				}

			  }	  
			}

			// // floor
			// else {
			// 	_spriteManager.getFloor(item, item.getZoneId(), item.getRoomId(), &sprite);
			// 	sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE);
			// }

			_app.draw(_sprite, render);
		  }
		}
	  }
	}

	void	drawItems(RenderStates render, int fromX, int fromY, int toX, int toY) {
		int offsetY = -16;
		int offsetX = 2;

		for (int i = toX-1; i >= fromX; i--) {
			for (int j = toY-1; j >= fromY; j--) {
				BaseItem item = WorldMap.getInstance().getItem(i, j);

				if (item != null) {

					// Draw item
					if (item.getType() != BaseItem.Type.STRUCTURE_FLOOR && item.isStructure() == false) {
						_spriteManager.getSprite(item, _sprite);

						if (item.isStructure()) {
							_sprite.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
						} else {
							_sprite.setPosition(i * Constant.TILE_SIZE + offsetX, j * Constant.TILE_SIZE + offsetY);
						}

						_app.draw(_sprite, render);
					}

					// TODO
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
	  int offsetY = -16;
	  int offsetX = 2;

	  for (int i = toX-1; i >= fromX; i--) {
		for (int j = toY-1; j >= fromY; j--) {
		  // BaseItem* item = WorldMap.getInstance().getItem(i, j);
		  WorldArea item = WorldMap.getInstance().getArea(i, j);

		  if (item == null) {
			item = WorldMap.getInstance().getArea(i, j);
		  }

		  if (item != null) {
			_shapeDebug.setSize(ObjectPool.getVector2f(Constant.TILE_SIZE, Constant.TILE_SIZE));
			_shapeDebug.setFillColor(new Color(250, 200, 200, 100));
			_shapeDebug.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
			_app.draw(_shapeDebug, render);

			Text text = new Text();
			text.setFont(_font);
			text.setCharacterSize(10);
			text.setColor(new Color(0, 0, 0));
			text.setStyle(Text.REGULAR);
			text.setString(String.valueOf(item.getRoomId()));
			text.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
			_app.draw(text, render);
		  }
		}
	  }
	}

}
