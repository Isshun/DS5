package alone.in.DeepSpace.UserInterface;
import java.io.File;
import java.io.IOException;

import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.Transform;
import org.jsfml.window.Mouse;

import alone.in.DeepSpace.MainRenderer;
import alone.in.DeepSpace.Viewport;
import alone.in.DeepSpace.Managers.JobManager;
import alone.in.DeepSpace.Models.BaseItem;
import alone.in.DeepSpace.Models.BaseItem.Type;
import alone.in.DeepSpace.Models.Cursor;
import alone.in.DeepSpace.Models.ItemInfo;
import alone.in.DeepSpace.Utils.Constant;
import alone.in.DeepSpace.Utils.Log;
import alone.in.DeepSpace.World.WorldMap;


public class UserInteraction {

	 public enum Mode {
		 MODE_NONE,
		 MODE_BUILD,
		 MODE_EREASE,
		 MODE_SELECT
	 };

	 Texture				_cursorTexture;
	 Viewport				_viewport;
	 Cursor					_cursor;
	 Mode					_mode;
	 int					_startPressX;
	 int					_startPressY;
	 int					_mouseMoveX;
	 int					_mouseMoveY;
	 Mouse.Button			_button;
	private Sprite 			spriteCursor;
		  
	UserInteraction(Viewport viewport) throws IOException {
	  _viewport = viewport;
	  _cursor = new Cursor();
	  _cursorTexture = new Texture();
	  _cursorTexture.loadFromFile((new File("res/cursor.png")).toPath());
	  _startPressX = 0;
	  _startPressY = 0;
	  _mouseMoveX = 0;
	  _mouseMoveY = 0;
	  _button = null;
	  _mode = Mode.MODE_NONE;

	  spriteCursor = new Sprite();
	  spriteCursor.setTexture(_cursorTexture);
	  spriteCursor.setTextureRect(new IntRect(0, 0, 32, 32));
	}

	void	drawCursor(int startX, int startY, int toX, int toY) {
	  startX = Math.max(startX, 0);
	  startY = Math.max(startY, 0);
	  toX = Math.min(toX, WorldMap.getInstance().getWidth());
	  toY = Math.min(toY, WorldMap.getInstance().getHeight());
	  for (int x = startX; x <= toX; x++) {
		for (int y = startY; y <= toY; y++) {
	      Transform transform = new Transform();
	      RenderStates render = new RenderStates(_viewport.getViewTransform(transform));
		  spriteCursor.setPosition(x * Constant.TILE_SIZE, y * Constant.TILE_SIZE);
		  MainRenderer.getInstance().draw(spriteCursor, render);
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

	void	build(Type type, int startX, int startY, int toX, int toY) {
	  for (int x = toX; x >= startX; x--) {
		for (int y = toY; y >= startY; y--) {

		  // Structure
		  if (type == BaseItem.Type.STRUCTURE_ROOM) {
			if (x == startX || x == toX || y == startY || y == toY) {
			  Log.warning("1");
			  JobManager.getInstance().build(BaseItem.Type.STRUCTURE_WALL, x, y);
			  // item = WorldMap.getInstance().putItem(x, y, BaseItem.STRUCTURE_WALL);
			} else {
			  Log.warning("2");
			  JobManager.getInstance().build(BaseItem.Type.STRUCTURE_FLOOR, x, y);
			  // item = WorldMap.getInstance().putItem(x, y, BaseItem.STRUCTURE_FLOOR);
			}
		  } else {
			// item = WorldMap.getInstance().putItem(x, y, _menu.getBuildItemType());
			if (type != BaseItem.Type.NONE) {
			  Log.warning("3 " + type + " " + BaseItem.getItemName(type));
			  JobManager.getInstance().build(type, x, y);
			  // item = WorldMap.getInstance().putItem(x, y, type);
			}
		  }

		  // if (item != NULL) {
		  // }
		}
	  }
	}

	void	erease(int startX, int startY, int toX, int toY) {
	  for (int x = startX; x <= toX; x++) {
		for (int y = startY; y <= toY; y++) {
		  WorldMap.getInstance().removeItem(x, y);
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


}
