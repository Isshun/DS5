package alone.in.deepspace.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;

import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.Utils.Log;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.UserItem;

public class DynamicObjectManager {
	private static DynamicObjectManager	_self;

	private ArrayList<BaseItem>	 		_objects;
	private ArrayList<TempItem>	 		_tempItems;
	private int 						_count;
	private Sprite 						_selection;
	private int 						_countFire;
	private ArrayList<TempItem> 		_toTempItemDestroy;

	public DynamicObjectManager() throws IOException {
	  Log.debug("FoeManager");
	  
	  // Selection
	  Texture texture = new Texture();
	  texture.loadFromFile((new File("res/cursor.png").toPath()));
	  _selection = new Sprite();
	  _selection.setTexture(texture);
	  _selection.setTextureRect(new IntRect(0, 32, 32, Constant.CHAR_HEIGHT));
	  
	  _objects = new ArrayList<BaseItem>();
	  _tempItems = new ArrayList<TempItem>();
	  _tempItems.add(new TempItem(0, 0, 100));
	  _toTempItemDestroy = new ArrayList<TempItem>();
	  
	  _count = 0;
	  
	  Log.debug("FoeManager done");
	}
	
	public void	refresh(RenderWindow app, RenderStates render, double animProgress) throws IOException {
		for (BaseItem o: _objects) {
			Sprite sprite = SpriteManager.getInstance().getItem(o);
			app.draw(sprite, render);
		}

		for (TempItem t: _tempItems) {
			t.refresh(app, render, animProgress);
		}
	}
	
	public static DynamicObjectManager getInstance() {
		if (_self == null) {
			try {
				_self = new DynamicObjectManager();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return _self;
	}


	public int getCount() {
		return _count;
	}

	public void add(UserItem item) {
		_objects.add(item);
	}

	public void update() {
//		for (BaseItem o: _objects) {
//			Sprite sprite = SpriteManager.getInstance().getItem(o);
//			app.draw(sprite, render);
//		}

		_toTempItemDestroy.clear();

		for (TempItem t: _tempItems) {
			t.update();
			if (t.lifespan < 0) {
				_toTempItemDestroy.add(t);
			}
		}
		
		for (TempItem t: _toTempItemDestroy) {
			_tempItems.remove(t);
		}
		
		int x = 0;
		int y = 0;
		int range = 20;

		if (_countFire++ % 4 != 0) {
			List<Character> characters = ServiceManager.getCharacterManager().getList();
			for (Character c: characters) {
				if (c.getX() > x - range && c.getX() < x + range && c.getY() > y - range && c.getY() < y + range ) {
					int offsetX = Math.abs(c.getX() - x);
					int offsetY = Math.abs(c.getY() - y);
					
					double distance = Math.max(offsetX, offsetY);
					double velocityX = offsetX * 3 / distance;
					double velocityY = offsetY * 3 / distance;
			
					TempItem t = new TempItem(0, 0, 5);
					t.offsetX = (int)velocityX;
					t.offsetY = (int)velocityY;
					//_tempItems.add(t);

					return;
				}
			}
		}
	}

}
