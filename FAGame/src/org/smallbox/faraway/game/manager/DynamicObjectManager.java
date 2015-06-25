package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.engine.SpriteManager;
import org.smallbox.faraway.engine.SpriteModel;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.model.item.MapObjectModel;
import org.smallbox.faraway.game.model.item.TempItem;
import org.smallbox.faraway.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DynamicObjectManager extends BaseManager {
	private ArrayList<MapObjectModel>	 		_objects;
	private ArrayList<TempItem>	 		_tempItems;
	private int 						_count;
	private int 						_countFire;
	private ArrayList<TempItem> 		_toTempItemDestroy;

	public DynamicObjectManager() {
	  Log.debug("FoeManager");
	  
	  _objects = new ArrayList<MapObjectModel>();
	  _tempItems = new ArrayList<TempItem>();
	  _tempItems.add(new TempItem(0, 0, 100));
	  _toTempItemDestroy = new ArrayList<TempItem>();
	  
	  _count = 0;
	  
	  Log.debug("FoeManager done");
	}
	
	public void	refresh(GFXRenderer renderer, RenderEffect effect, double animProgress) throws IOException {
		for (MapObjectModel o: _objects) {
			SpriteModel sprite = SpriteManager.getInstance().getItem(o);
			renderer.draw(sprite, effect);
		}

		for (TempItem t: _tempItems) {
			t.refresh(renderer, effect, animProgress);
		}
	}
	
	public int getCount() {
		return _count;
	}

	public void add(ItemModel item) {
		_objects.add(item);
	}

	@Override
	protected void onUpdate(int tick) {
//		for (BaseItem o: _objects) {
//			Sprite sprite = SpriteManager.getInstance().getItem(o);
//			app.draw(sprite, _renderEffect);
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
			List<CharacterModel> characters = Game.getCharacterManager().getList();
			for (CharacterModel c: characters) {
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
