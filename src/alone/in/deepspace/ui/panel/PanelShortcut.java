package alone.in.deepspace.ui.panel;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.engine.renderer.MiniMapRenderer;
import alone.in.deepspace.engine.ui.ColorView;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.OnFocusListener;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.ui.UserInterface.Mode;
import alone.in.deepspace.util.Constant;

public class PanelShortcut extends BasePanel {
	private static final int FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int FRAME_HEIGHT = Constant.WINDOW_HEIGHT;

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
			new PanelEntry("  [ UILD]  ", 		"B", 	3, Mode.BUILD),
			new PanelEntry("[ CCUPATION]  ", 	"O",	1, Mode.JOBS),
			new PanelEntry("  [ EOPLE]  ", 		"P", 	3, Mode.CREW),
			new PanelEntry("   [ OOM]  ", 		"R", 	4, Mode.ROOM),
			new PanelEntry("  [ EBUG]  ", 		"D", 	3, Mode.DEBUG),
			new PanelEntry("   [ LAN]  ", 		"P",	4, Mode.PLAN),
			new PanelEntry("[ ARETAKER] ", 		"C",	1, Mode.PLAN),
			new PanelEntry("  [ TATS] ", 		"S",	3, 	Mode.STATS)
	};

	private MiniMapRenderer	_miniMapRenderer;

	public PanelShortcut(Mode mode) {
		super(mode, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 32), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
	}
	
	@Override
	protected void onCreate() {
		_miniMapRenderer = new MiniMapRenderer(_viewport);
		int posX = 24;
		int posY = 244;
		int i = 0;
		for (PanelEntry entry: _entries) {
			final PanelEntry e = entry;

			TextView label = new TextView(new Vector2f(175, 36));
			label.setColor(new Color(120, 255, 255));
			label.setPosition(new Vector2f(posX, posY));
			label.setString(e.label);
			label.setCharacterSize(20);
			label.setPadding(3, 16);
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
			shortcut.setPadding(3, 16);
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

	private void addBorder(int width, int height) {
		// Left
		View border = new ColorView(new Vector2f(4, height));
		border.setBackgroundColor(new Color(37, 70, 72));
		border.setPosition(new Vector2f(24, 20));
		addView(border);

		// Right
		border = new ColorView(new Vector2f(4, height));
		border.setBackgroundColor(new Color(37, 70, 72));
		border.setPosition(new Vector2f(24 + width - 4, 20));
		addView(border);

		// Top
		border = new ColorView(new Vector2f(width, 4));
		border.setBackgroundColor(new Color(37, 70, 72));
		border.setPosition(new Vector2f(24, 20));
		addView(border);

		// Bottom
		border = new ColorView(new Vector2f(width, 4));
		border.setBackgroundColor(new Color(37, 70, 72));
		border.setPosition(new Vector2f(24, 20 + height - 4));
		addView(border);
	}

	@Override
	public void onDraw(RenderWindow app, RenderStates render) {
		_miniMapRenderer.onDraw(app, render, 0);
	}

	@Override
	protected void onRefresh(int update) {
		_miniMapRenderer.onRefresh(update);
	}
}
