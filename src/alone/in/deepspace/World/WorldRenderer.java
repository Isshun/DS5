package alone.in.DeepSpace.World;

import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import alone.in.DeepSpace.Managers.SpriteManager;
import alone.in.DeepSpace.Models.BaseItem;
import alone.in.DeepSpace.UserInterface.UserInterface;
import alone.in.DeepSpace.Utils.Constant;
import alone.in.DeepSpace.Utils.ObjectPool;
import alone.in.DeepSpace.Utils.Settings;

public class WorldRenderer {
	RenderWindow	_app;
	SpriteManager		_spriteManager;
	Font			_font;
	UserInterface		_ui;
	private RectangleShape _shape;
	private RectangleShape _shapeDebug;
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

	public void	draw(RenderStates render) {

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

	// TODO: random
	void	drawFloor(RenderStates render, int fromX, int fromY, int toX, int toY) {
	  for (int i = toX-1; i >= fromX; i--) {
		for (int j = toY-1; j >= fromY; j--) {

			// Oxygen
			WorldArea area = WorldMap.getInstance().getArea(i, j);
			if (area != null) {
				Sprite sprite = _spriteManager.getExterior();
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
					Sprite sprite = _spriteManager.getFloor(item, item.getZoneId(), item.getRoomId());
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
		}
	  }
	}

	//TODO: random
	void	drawStructure(RenderStates render, int fromX, int fromY, int toX, int toY) {
	  int lastSpecialX = -1;
	  int lastSpecialY = -1;
	  int offsetWall = (Constant.TILE_SIZE / 2 * 3) - Constant.TILE_SIZE;

	  Sprite sprite = null;
	  
	  for (int j = toY-1; j >= fromY; j--) {
		for (int i = toX-1; i >= fromX; i--) {
		  int r = (int) Math.random();
		  StructureItem item = WorldMap.getInstance().getStructure(i, j);
		  if (item != null) {

			// Structure except floor
			if (item.isStructure() && !item.isType(BaseItem.Type.STRUCTURE_FLOOR)) {

				StructureItem bellow = WorldMap.getInstance().getStructure(i, j+1);
				StructureItem right = WorldMap.getInstance().getStructure(i+1, j);
				StructureItem left = WorldMap.getInstance().getStructure(i-1, j);
				StructureItem above = WorldMap.getInstance().getStructure(i, j-1);
		  
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
						sprite = _spriteManager.getWall(item, 4, r, 0);
					  lastSpecialX = i;
					  lastSpecialY = j;
					}
					// Single wall
					else {
						sprite = _spriteManager.getWall(item, 0, 0, 0);
					}
				  }
				  // Special
				  else {
					// Double wall
					if (doubleWall) {
						sprite = _spriteManager.getWall(item, 2, r, bellow.getZoneId());
					  lastSpecialX = i;
					  lastSpecialY = j;
					}
					// Single wall
					else {
						sprite = _spriteManager.getWall(item, 3, r, bellow.getZoneId());
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

			  }	  
			}

			// // floor
			// else {
			// 	_spriteManager.getFloor(item, item.getZoneId(), item.getRoomId(), &sprite);
			// 	sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE);
			// }

			_app.draw(sprite, render);
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
