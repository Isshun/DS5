package alone.in.deepspace.ui.panel;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard.Key;

import alone.in.deepspace.Game;
import alone.in.deepspace.engine.renderer.MainRenderer;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ResourceManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.item.StructureItem;
import alone.in.deepspace.model.item.UserItem;
import alone.in.deepspace.ui.UserInterface;
import alone.in.deepspace.ui.UserInterface.Mode;
import alone.in.deepspace.util.Constant;
import alone.in.deepspace.util.Settings;

public class PanelDebug extends BasePanel {

	private static final int 	FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int	FRAME_HEIGHT = Constant.WINDOW_HEIGHT;
	private UserInterface _ui;
	
	public PanelDebug(Mode mode, Key shortcut) {
		super(mode, shortcut, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 32), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
		
		setBackgroundColor(new Color(200, 50, 140, 150));
		
		// Add character
		TextView txtAddCharacter = new TextView(new Vector2f(200, 32));
		txtAddCharacter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Game.getCharacterManager().addRandom(150, 150);
			}
		});
		txtAddCharacter.setString("Add character");
		txtAddCharacter.setCharacterSize(20);
		txtAddCharacter.setColor(Color.WHITE);
		txtAddCharacter.setPosition(new Vector2f(20, 20));
		addView(txtAddCharacter);

		// Add Ressource
		TextView lbAddMatter = new TextView(new Vector2f(200, 32));
		lbAddMatter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				ResourceManager.getInstance().addMatter(500);
			}
		});
		lbAddMatter.setString("Add matter");
		lbAddMatter.setCharacterSize(20);
		lbAddMatter.setColor(Color.WHITE);
		lbAddMatter.setPosition(new Vector2f(20, 60));
		addView(lbAddMatter);

		// Items
		TextView lbItems = new TextView(new Vector2f(200, 32));
		lbItems.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				_ui.toogleMode(Mode.DEBUGITEMS);
			}
		});
		lbItems.setString("items");
		lbItems.setCharacterSize(20);
		lbItems.setColor(Color.WHITE);
		lbItems.setPosition(new Vector2f(20, 340));
		addView(lbItems);

		// Re-launch jobs 
		TextView lbReLaunchJob = new TextView(new Vector2f(200, 32));
		lbReLaunchJob.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				int width = ServiceManager.getWorldMap().getWidth();
				int height = ServiceManager.getWorldMap().getHeight();
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						StructureItem structure = ServiceManager.getWorldMap().getStructure(x, y);
						if (structure != null && structure.isComplete() == false) {
							JobManager.getInstance().addBuild(structure);
						}
						UserItem item = ServiceManager.getWorldMap().getItem(x, y);
						if (item != null && item.isComplete() == false) {
							JobManager.getInstance().addBuild(item);
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
		TextView lbClearJob = new TextView(new Vector2f(200, 32));
		lbClearJob.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				JobManager.getInstance().clear();
			}
		});
		lbClearJob.setString("Clear jobs");
		lbClearJob.setCharacterSize(20);
		lbClearJob.setColor(Color.WHITE);
		lbClearJob.setPosition(new Vector2f(20, 140));
		addView(lbClearJob);

		// Add seed 
		TextView lbAddSeed = new TextView(new Vector2f(200, 32));
		lbAddSeed.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				ServiceManager.getWorldMap().addRandomSeed();
				ServiceManager.getWorldMap().addRandomSeed();
				ServiceManager.getWorldMap().addRandomSeed();
				ServiceManager.getWorldMap().addRandomSeed();
				ServiceManager.getWorldMap().addRandomSeed();
				ServiceManager.getWorldMap().addRandomSeed();
				ServiceManager.getWorldMap().addRandomSeed();
				ServiceManager.getWorldMap().addRandomSeed();
				ServiceManager.getWorldMap().addRandomSeed();
				ServiceManager.getWorldMap().addRandomSeed();
				ServiceManager.getWorldMap().addRandomSeed();
				ServiceManager.getWorldMap().addRandomSeed();
			}
		});
		lbAddSeed.setString("Add seed");
		lbAddSeed.setCharacterSize(20);
		lbAddSeed.setColor(Color.WHITE);
		lbAddSeed.setPosition(new Vector2f(20, 180));
		addView(lbAddSeed);

		// Add water 
		TextView lbAddWater = new TextView(new Vector2f(200, 32));
		lbAddWater.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				ResourceManager.getInstance().addWater(20);
			}
		});
		lbAddWater.setString("Add water");
		lbAddWater.setCharacterSize(20);
		lbAddWater.setColor(Color.WHITE);
		lbAddWater.setPosition(new Vector2f(20, 220));
		addView(lbAddWater);

		// Kill everyone 
		TextView lbKillEveryone = new TextView(new Vector2f(200, 32));
		lbKillEveryone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Game.getCharacterManager().clear();
				JobManager.getInstance().clear();
			}
		});
		lbKillEveryone.setString("Kill everyone");
		lbKillEveryone.setCharacterSize(20);
		lbKillEveryone.setColor(Color.WHITE);
		lbKillEveryone.setPosition(new Vector2f(20, 300));
		addView(lbKillEveryone);

		// Reset light 
		TextView lbResetLight = new TextView(new Vector2f(200, 32));
		lbResetLight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				((MainRenderer)MainRenderer.getInstance()).initLight();
			}
		});
		lbResetLight.setString("Reset light");
		lbResetLight.setCharacterSize(20);
		lbResetLight.setColor(Color.WHITE);
		lbResetLight.setPosition(new Vector2f(20, 260));
		addView(lbResetLight);
	}

	@Override
	protected void onCreate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onOpen() {
		Settings.getInstance().setDebug(true);
	}

	@Override
	protected void onClose() {
		Settings.getInstance().setDebug(false);
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

}
