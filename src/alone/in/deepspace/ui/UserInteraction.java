package alone.in.deepspace.ui;

import java.io.File;
import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.Transform;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Mouse;

import alone.in.deepspace.engine.Viewport;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.Cursor;
import alone.in.deepspace.model.item.ItemInfo;
import alone.in.deepspace.model.item.StructureItem;
import alone.in.deepspace.model.item.WorldResource;
import alone.in.deepspace.model.job.Job;
import alone.in.deepspace.util.Log;

public class UserInteraction {

	public enum Mode {
		NONE,
		BUILD,
		EREASE,
		SELECT
	};

	Texture					_cursorTexture;
	Viewport				_viewport;
	Cursor					_cursor;
	Mode					_mode;
	int						_startPressX;
	int						_startPressY;
	int						_mouseMoveX;
	int						_mouseMoveY;
	Mouse.Button			_button;
	private Sprite 			spriteCursor;
	private Texture 		_texture;
	private RenderWindow	_app;

	UserInteraction(RenderWindow app, Viewport viewport) throws IOException {
		_app = app;
		_viewport = viewport;
		_cursor = new Cursor();
		_cursorTexture = new Texture();
		_cursorTexture.loadFromFile((new File("res/cursor.png")).toPath());
		
		_texture = new Texture();
		_texture.loadFromFile((new File("res/selection.png")).toPath());
		_texture.setRepeated(true);

		_startPressX = 0;
		_startPressY = 0;
		_mouseMoveX = 0;
		_mouseMoveY = 0;
		_button = null;
		_mode = Mode.NONE;

		spriteCursor = new Sprite();
		spriteCursor.setTexture(_cursorTexture);
		spriteCursor.setTextureRect(new IntRect(0, 0, 32, 32));
	}

	void	drawCursor(int startX, int startY, int toX, int toY) {
		startX = Math.max(startX, 0);
		startY = Math.max(startY, 0);
		toX = Math.min(toX, ServiceManager.getWorldMap().getWidth());
		toY = Math.min(toY, ServiceManager.getWorldMap().getHeight());
//
		Transform transform = new Transform();
		RenderStates render = new RenderStates(_viewport.getViewTransform(transform));
//
//		RectangleShape rectangle = new RectangleShape(new Vector2f(toX - startX, toY - startY));
//		rectangle.setTexture(_texture);
//		rectangle.setPosition(new Vector2f(startX, startY));
//		rectangle.setTextureRect(new IntRect(0, 0, toX - startX, toY - startY));
//		rectangle.setFillColor(new Color(255, 255, 255, 255));
//		MainRenderer.getInstance().draw(rectangle, render);

		int border = 3;
		
		RectangleShape rectangleItem = new RectangleShape(new Vector2f(32, 32));
		rectangleItem.setFillColor(new Color(200, 255, 100, 120));
		
		RectangleShape rectangle1 = new RectangleShape(new Vector2f(32, 32));
		rectangle1.setFillColor(new Color(100, 255, 100, 20));
		
		RectangleShape rectangle2 = new RectangleShape(new Vector2f(32, 32));
		rectangle2.setFillColor(new Color(100, 255, 100, 40));

		RectangleShape rectangleTop = new RectangleShape(new Vector2f((toX - startX + 1) * 32 - border * 2, border));
		rectangleTop.setFillColor(new Color(100, 255, 100, 100));
		rectangleTop.setPosition(new Vector2f(startX * 32 + border, startY * 32));
		_app.draw(rectangleTop, render);
		rectangleTop.setPosition(new Vector2f(startX * 32 + border, (toY + 1) * 32 - border));
		_app.draw(rectangleTop, render);

		RectangleShape rectangleLeft = new RectangleShape(new Vector2f(border, (toY - startY + 1) * 32));
		rectangleLeft.setFillColor(new Color(100, 255, 100, 100));
		rectangleLeft.setPosition(new Vector2f(startX * 32, startY * 32));
		_app.draw(rectangleLeft, render);
		rectangleLeft.setPosition(new Vector2f((toX + 1) * 32 - border, startY * 32));
		_app.draw(rectangleLeft, render);
		
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				if ((x + y) % 2 == 0) {
					rectangle1.setPosition(new Vector2f(x * 32, y * 32));
					_app.draw(rectangle1, render);
				} else {
					rectangle2.setPosition(new Vector2f(x * 32, y * 32));
					_app.draw(rectangle2, render);
				}
				
				if (ServiceManager.getWorldMap().getRessource(x, y) != null) {
					rectangleItem.setPosition(new Vector2f(x * 32, y * 32));
					_app.draw(rectangleItem, render);
				}
//				Transform transform = new Transform();
//				RenderStates render = new RenderStates(_viewport.getViewTransform(transform));
//				spriteCursor.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
//				MainRenderer.getInstance().draw(spriteCursor, render);
			}
		}
	}

	//	void	refreshCursor() {
	//	  if (_mode == Mode.MODE_BUILD || _mode == Mode.MODE_EREASE) {
	//		ItemInfo itemInfo = BaseItem.getItemInfo(_itemType);
	//
	//		int width = 1;
	//		int height = 1;
	//		if (itemInfo != null) {
	//			width = itemInfo.width;
	//			height = itemInfo.height;
	//		}
	//			
	//		
	//		// Structure: multiple 1x1 tile
	//		if (_button == Mouse.Button.LEFT) {
	//		  if (BaseItem.isStructure(_itemType)) {
	//			drawCursor(Math.min(_startPressX, _mouseMoveX),
	//					   Math.min(_startPressY, _mouseMoveY),
	//					   Math.max(_startPressX, _mouseMoveX),
	//					   Math.max(_startPressY, _mouseMoveY));
	//		  }
	//
	//		  // Single nxn tile: holding mouse button
	//		  else {
	//			drawCursor(Math.min(_startPressX, _mouseMoveX),
	//					   Math.min(_startPressY, _mouseMoveY),
	//					   Math.min(_startPressX, _mouseMoveX) + width - 1,
	//					   Math.min(_startPressY, _mouseMoveY) + height - 1);
	//		  }
	//		}
	//
	//		// Single 1x1 tile: mouse hover
	//		else {
	//		  drawCursor(_mouseMoveX, _mouseMoveY, _mouseMoveX, _mouseMoveY);
	//		}
	//	  }
	//	}
	//
	//	void	mouseMove(int x, int y) {
	//	  _mouseMoveX = x;
	//	  _mouseMoveY = y;
	//	}
	//
	//	void	mousePress(Mouse.Button button, int x, int y) {
	//	  Log.error("Press: " + y);
	//
	//	  if (button == Mouse.Button.LEFT) {
	//		_button = button;
	//		_startPressX = x;
	//		_startPressY = y;
	//	  }
	//	}
	//
	//	boolean	mouseRelease(Mouse.Button button, int x, int y) {
	//	  if (_mode != Mode.MODE_NONE && button == Mouse.Button.LEFT) {
	//		Log.error("Release: " + y);
	//
	//		int startX = Math.min(_startPressX, _mouseMoveX);
	//		int startY = Math.min(_startPressY, _mouseMoveY);
	//		int toX = Math.max(_startPressX, _mouseMoveX);
	//		int toY = Math.max(_startPressY, _mouseMoveY);
	//
	//		switch (_mode) {
	//		case MODE_BUILD:
	//		  build(startX, startY, toX, toY);
	//		  break;
	//		case MODE_EREASE:
	//		  erease(startX, startY, toX, toY);
	//		  break;
	//		case MODE_SELECT:
	//			break;
	//		case MODE_NONE:
	//			break;
	//		}
	//
	//		_button = null;
	//		_startPressX = -1;
	//		_startPressY = -1;
	//
	//		return true;
	//	  }
	//	  return false;
	//	}

	public void	planBuild(ItemInfo info, int startX, int startY, int toX, int toY) {
		for (int x = toX; x >= startX; x--) {
			for (int y = toY; y >= startY; y--) {

				// Check if resource is present on area
				WorldResource res = ServiceManager.getWorldMap().getRessource(x, y);
				if (res != null) {
					if (res.canBeMined()) {
						JobManager.getInstance().addMineJob(x, y);
					} else if (res.canBeHarvested()) {
						JobManager.getInstance().addGatherJob(x, y);
					}
				}
				
				//TODO
				// Structure
				if (info.name.equals("base.room")) {
					if (x == startX || x == toX || y == startY || y == toY) {
						Log.warning("1");
						// TODO
						StructureItem structure = ServiceManager.getWorldMap().getStructure(x, y);
						if (structure == null || structure.getName().equals("base.door") == false) {
							JobManager.getInstance().build(ServiceManager.getData().getItemInfo("base.wall"), x, y);
						}
						// item = ServiceManager.getWorldMap().putItem(x, y, BaseItem.STRUCTURE_WALL);
					} else {
						Log.warning("2");
						// TODO
						JobManager.getInstance().build(ServiceManager.getData().getItemInfo("base.floor"), x, y);
						// item = ServiceManager.getWorldMap().putItem(x, y, BaseItem.STRUCTURE_FLOOR);
					}
				} else {
					// item = ServiceManager.getWorldMap().putItem(x, y, _menu.getBuildItemType());
					if (info != null) {
						Log.warning("3 " + info.name);
						// TODO
						JobManager.getInstance().build(info, x, y);
						// item = ServiceManager.getWorldMap().putItem(x, y, type);
					}
				}

				// if (item != NULL) {
				// }
			}
		}
	}

	public void removeItem(int startX, int startY, int toX, int toY) {
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				ServiceManager.getWorldMap().removeItem(x, y);
			}
		}
	}

	Mode getMode() { return _mode; }
	void setMode(Mode mode) { _mode = mode; }
	//	void selectBuildItem(BaseItem.Type type) { _mode = Mode.MODE_BUILD; _itemType = type; }
	//	void cancel() { _mode = Mode.MODE_NONE; _itemType = BaseItem.Type.NONE; }
	//	BaseItem.Type getBuildItem() { return _itemType; }

	public Cursor getCursor() {
		return _cursor;
	}

	public void removeStructure(int startX, int startY, int toX, int toY) {
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				ServiceManager.getWorldMap().removeStructure(x, y);
			}
		}
	}

	public void planGather(int startX, int startY, int toX, int toY) {
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				Job job = JobManager.getInstance().createGatherJob(x, y);
				JobManager.getInstance().addJob(job);
			}
		}
	}

	public void planMining(int startX, int startY, int toX, int toY) {
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				Job job = JobManager.getInstance().createMiningJob(x, y);
				JobManager.getInstance().addJob(job);
			}
		}
	}

	public void planDump(int startX, int startY, int toX, int toY) {
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				Job job = JobManager.getInstance().createDumpJob(x, y);
				JobManager.getInstance().addJob(job);
			}
		}
	}

}
