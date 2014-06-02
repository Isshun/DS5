package alone.in.deepspace.ui.panel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.engine.renderer.MiniMapRenderer;
import alone.in.deepspace.engine.ui.LinkView;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.OnFocusListener;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.manager.ResourceManager;
import alone.in.deepspace.ui.UserInterface.Mode;
import alone.in.deepspace.util.Constant;

public class PanelShortcut extends BasePanel {
	private static final int FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int FRAME_HEIGHT = Constant.WINDOW_HEIGHT;
	private static final int NB_COLUMNS_STATS = NB_COLUMNS / 2 - 1;

	private static class PanelEntry {
		Mode		mode;
		String		label;
		String 		shortcut;
		int 		shortcutPos;

		public PanelEntry(String label, String shortcut, int shortcutPos, Mode mode) {
			this.shortcut = shortcut;
			this.label = label;
			this.mode = mode;
			this.shortcutPos = shortcutPos;
		}
	}

	private PanelEntry	_entries[] = {
			new PanelEntry("[ UILD]", 		"B", 	1, Mode.BUILD),
			new PanelEntry("[ CCUPATION]", 	"O",	1, Mode.JOBS),
			new PanelEntry("[ REW]", 		"C", 	1, Mode.CREW),
			new PanelEntry("[ OOM]", 		"R", 	1, Mode.ROOM),
			new PanelEntry("[ EBUG]", 		"D", 	1, Mode.DEBUG),
			new PanelEntry("[ LAN]", 		"P",	1, Mode.PLAN),
			new PanelEntry("[ ARETAKER]", 	"C",	1, Mode.PLAN),
			new PanelEntry("[ TATS]", 		"S",	1, 	Mode.STATS)
	};

	private MiniMapRenderer	_miniMapRenderer;
	private TextView _lbTime;
	private TextView _lbResFood;

	public PanelShortcut(Mode mode) {
		super(mode, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 0), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
	}
	
	@Override
	protected void onCreate() {
		_lbTime = new TextView();
		_lbTime.setCharacterSize(FONT_SIZE);
		addView(_lbTime);
		
		_miniMapRenderer = new MiniMapRenderer(_viewport);
		int posX = 24;
		int posY = 244;
		
		TextView lbResources = new TextView();
		lbResources.setString("Resources");
		lbResources.setPosition(posX, posY);
		lbResources.setCharacterSize(FONT_SIZE_TITLE);
		addView(lbResources);
		posY += 42;
		
		_lbResFood = new LinkView();
		_lbResFood.setCharacterSize(FONT_SIZE);
		_lbResFood.setPosition(posX, posY);
		addView(_lbResFood);
		posY += 32;

		TextView lbActions = new TextView();
		lbActions.setString("Actions");
		lbActions.setPosition(posX, posY);
		lbActions.setCharacterSize(FONT_SIZE_TITLE);
		addView(lbActions);
		posY += 42;

		int i = 0;
		for (PanelEntry entry: _entries) {
			final PanelEntry e = entry;

			int paddingX = (int)(175 / 2 - (entry.label.length() / 2) * 11.8 - (entry.label.length() % 2 == 0 ? 0 : 5.5));
			
			TextView label = new TextView(new Vector2f(175, 36));
			label.setColor(new Color(120, 255, 255));
			label.setPosition(new Vector2f(posX, posY));
			label.setString(e.label);
			label.setCharacterSize(20);
			label.setPadding(3, paddingX);
			label.setBackgroundColor(new Color(29, 85, 96, 100));
			label.setOnFocusListener(new OnFocusListener() {
				@Override
				public void onEnter(View view) {
					view.setBackgroundColor(new Color(29, 85, 96, 180));
				}

				@Override
				public void onExit(View view) {
					view.setBackgroundColor(new Color(29, 85, 96, 100));
				}
			});
			label.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					_ui.toogleMode(e.mode);
				}
			});
			addView(label);

			TextView shortcut = new TextView(new Vector2f(160, 36));
			shortcut.setColor(new Color(176, 205, 53));
			shortcut.setPosition(new Vector2f(posX + (int)(e.shortcutPos * 11.8), posY));
			shortcut.setString(e.shortcut);
			shortcut.setCharacterSize(20);
			shortcut.setPadding(3, paddingX);
			shortcut.setStyle(TextView.UNDERLINED);
			addView(shortcut);

			if (i % 2 == 0) {
				posX += 197;
			} else {
				posX = 24;
				posY += 62;
			}
			i++;
		}
	}	

	@Override
	public void onDraw(RenderWindow app, RenderStates render) {
		_miniMapRenderer.onDraw(app, render, 0);
	}

	@Override
	protected void onRefresh(int update) {
		_miniMapRenderer.onRefresh(update);
		
		//update * 1000000
		Calendar cal = Calendar.getInstance();
		cal.set(2175, Calendar.JANUARY, 1, 0, 0);
		cal.add(Calendar.HOUR, update);
		
		Date date = cal.getTime();
		DateFormat formater = new SimpleDateFormat("dd MMMM y");
		_lbTime.setString(formater.format(date));
		
		_lbResFood.setDashedString("Food", String.valueOf(ResourceManager.getInstance().getFood()), NB_COLUMNS_STATS);
	}
}
