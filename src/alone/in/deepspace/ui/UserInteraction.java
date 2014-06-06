package alone.in.deepspace.ui;

import java.io.File;
import java.io.IOException;

import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.window.Mouse;

import alone.in.deepspace.Game;
import alone.in.deepspace.engine.Viewport;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.Cursor;
import alone.in.deepspace.model.item.ItemInfo;
import alone.in.deepspace.model.item.StructureItem;
import alone.in.deepspace.model.item.WorldResource;
import alone.in.deepspace.model.job.Job;
import alone.in.deepspace.model.room.Room;
import alone.in.deepspace.model.room.Room.Type;
import alone.in.deepspace.ui.panel.PanelPlan.PanelMode;
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
							JobManager.getInstance().build(Game.getData().getItemInfo("base.wall"), x, y);
						}
						// item = ServiceManager.getWorldMap().putItem(x, y, BaseItem.STRUCTURE_WALL);
					} else {
						Log.warning("2");
						// TODO
						JobManager.getInstance().build(Game.getData().getItemInfo("base.floor"), x, y);
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

	public void plan(PanelMode plan, int startX, int startY, int toX, int toY) {
		switch (plan) {
		case DUMP: planDump(startX, startY, toX, toY); break;
		case GATHER: planGather(startX, startY, toX, toY); break;
		case MINING: planMining(startX, startY, toX, toY); break;
		default: break;
		}
	}

	public void roomType(Type roomType, int clickX, int clickY, int fromX, int fromY, int toX, int toY) {
		if (roomType == Room.Type.NONE) {
			Game.getRoomManager().removeRoom(fromX, fromY, toX, toY, roomType);
		} else {
			Game.getRoomManager().putRoom(clickX, clickY, fromX, fromY, toX, toY, roomType, null);
		}
		
	}

}
