package alone.in.deepspace.UserInterface;

import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Character.CharacterManager;
import alone.in.deepspace.Character.Profession;
import alone.in.deepspace.Managers.JobManager;
import alone.in.deepspace.Managers.ResourceManager;
import alone.in.deepspace.UserInterface.Utils.OnClickListener;
import alone.in.deepspace.UserInterface.Utils.UIText;
import alone.in.deepspace.UserInterface.Utils.UIView;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.World.StructureItem;
import alone.in.deepspace.World.UserItem;
import alone.in.deepspace.World.WorldMap;

public class PanelDebug extends UserSubInterface {

	private static final int 	FRAME_WIDTH = 380;
	private static final int	FRAME_HEIGHT = Constant.WINDOW_HEIGHT;
	
	public PanelDebug(RenderWindow app) throws IOException {
		super(app, 0, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 32), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
		
		setBackgroundColor(new Color(200, 50, 140, 150));
		
		// Add character
		UIText txtAddCharacter = new UIText(new Vector2f(200, 32));
		txtAddCharacter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(UIView view) {
				CharacterManager.getInstance().add(0, 0, Profession.Type.ENGINEER);
			}
		});
		txtAddCharacter.setString("Add character");
		txtAddCharacter.setCharacterSize(20);
		txtAddCharacter.setColor(Color.WHITE);
		txtAddCharacter.setPosition(new Vector2f(20, 20));
		addView(txtAddCharacter);

		// Add Ressource
		UIText lbAddMatter = new UIText(new Vector2f(200, 32));
		lbAddMatter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(UIView view) {
				ResourceManager.getInstance().addMatter(500);
			}
		});
		lbAddMatter.setString("Add matter");
		lbAddMatter.setCharacterSize(20);
		lbAddMatter.setColor(Color.WHITE);
		lbAddMatter.setPosition(new Vector2f(20, 60));
		addView(lbAddMatter);

		// Re-launch jobs 
		UIText lbReLaunchJob = new UIText(new Vector2f(200, 32));
		lbReLaunchJob.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(UIView view) {
				int width = WorldMap.getInstance().getWidth();
				int height = WorldMap.getInstance().getHeight();
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						StructureItem structure = WorldMap.getInstance().getStructure(x, y);
						if (structure != null && structure.isComplete() == false) {
							JobManager.getInstance().build(structure);
						}
						UserItem item = WorldMap.getInstance().getItem(x, y);
						if (item != null && item.isComplete() == false) {
							JobManager.getInstance().build(item);
						}
					}
				}
			}
		});
		lbReLaunchJob.setString("Re-launch jobs");
		lbReLaunchJob.setCharacterSize(20);
		lbReLaunchJob.setColor(Color.WHITE);
		lbReLaunchJob.setPosition(new Vector2f(20, 100));
		addView(lbReLaunchJob);

		// Clear jobs 
		UIText lbClearJob = new UIText(new Vector2f(200, 32));
		lbClearJob.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(UIView view) {
				JobManager.getInstance().clear();
			}
		});
		lbClearJob.setString("Clear jobs");
		lbClearJob.setCharacterSize(20);
		lbClearJob.setColor(Color.WHITE);
		lbClearJob.setPosition(new Vector2f(20, 140));
		addView(lbClearJob);

		// Add seed 
		UIText lbAddSeed = new UIText(new Vector2f(200, 32));
		lbAddSeed.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(UIView view) {
				WorldMap.getInstance().addRandomSeed();
				WorldMap.getInstance().addRandomSeed();
				WorldMap.getInstance().addRandomSeed();
				WorldMap.getInstance().addRandomSeed();
				WorldMap.getInstance().addRandomSeed();
				WorldMap.getInstance().addRandomSeed();
				WorldMap.getInstance().addRandomSeed();
				WorldMap.getInstance().addRandomSeed();
				WorldMap.getInstance().addRandomSeed();
				WorldMap.getInstance().addRandomSeed();
				WorldMap.getInstance().addRandomSeed();
				WorldMap.getInstance().addRandomSeed();
			}
		});
		lbAddSeed.setString("Add seed");
		lbAddSeed.setCharacterSize(20);
		lbAddSeed.setColor(Color.WHITE);
		lbAddSeed.setPosition(new Vector2f(20, 180));
		addView(lbAddSeed);

		// Add water 
		UIText lbAddWater = new UIText(new Vector2f(200, 32));
		lbAddWater.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(UIView view) {
				ResourceManager.getInstance().addWater(20);
			}
		});
		lbAddWater.setString("Add water");
		lbAddWater.setCharacterSize(20);
		lbAddWater.setColor(Color.WHITE);
		lbAddWater.setPosition(new Vector2f(20, 220));
		addView(lbAddWater);

		// Kill everyone 
		UIText lbKillEveryone = new UIText(new Vector2f(200, 32));
		lbKillEveryone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(UIView view) {
				CharacterManager.getInstance().clear();
				JobManager.getInstance().clear();
			}
		});
		lbKillEveryone.setString("Kill everyone");
		lbKillEveryone.setCharacterSize(20);
		lbKillEveryone.setColor(Color.WHITE);
		lbKillEveryone.setPosition(new Vector2f(20, 260));
		addView(lbKillEveryone);
	}

//	void  addDebug(final String key, String value) {
//		int y = _index * 32;
//
//		Text text = ObjectPool.getText();
//		
//		text.setFont(SpriteManager.getInstance().getFont());
//		text.setCharacterSize(20);
//		text.setStyle(Text.REGULAR);
//		
//		text.setString(key);
//		text.setPosition(Constant.WINDOW_WIDTH - 320 + Constant.UI_PADDING, Constant.UI_PADDING + y);
//		_app.draw(text);
//
//		text.setString(value);
//		text.setPosition(Constant.WINDOW_WIDTH - 320 + Constant.UI_PADDING + 160, Constant.UI_PADDING + y);
//		_app.draw(text);
//
//		ObjectPool.release(text);
//
//		_index++;
//	}
//	
//	void  addDebug(final String str) {
//		int y = _index * 32;
//
//		Text text = ObjectPool.getText();
//		
//		text.setFont(SpriteManager.getInstance().getFont());
//		text.setCharacterSize(20);
//		text.setStyle(Text.REGULAR);
//		text.setColor(Color.WHITE);
//		
//		text.setString(str);
//		text.setPosition(0, 0);
//		_app.draw(text, _render);
//
//		ObjectPool.release(text);
//
//		_index++;
//	}

	@Override
	public void onRefresh(RenderWindow app) {
//		_index = 0;
//
////		addDebug("add character");
//		
//	  BaseItem item = WorldMap.getInstance().getItem(x, y);
//
//	  Log.debug("pos: " + x + " x " + y);
//	  Log.debug("item: " + item);
//
//	  if (item != null) {
//		addDebug("type", String.valueOf(item.getType()));
//		addDebug("pos", item.getX() + " x " + item.getY());
//		addDebug("zone req.", String.valueOf(item.getZoneIdRequired()));
//		addDebug("zone", String.valueOf(item.getZoneId()));
//		addDebug("room", String.valueOf(item.getRoomId()));
//	  }
	}

}
