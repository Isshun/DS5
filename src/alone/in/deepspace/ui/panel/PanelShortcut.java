package alone.in.deepspace.ui.panel;

import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.OnFocusListener;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.ui.UserInterface;
import alone.in.deepspace.ui.UserInterface.Mode;
import alone.in.deepspace.ui.UserSubInterface;
import alone.in.deepspace.util.Constant;

public class PanelShortcut extends UserSubInterface {
	private static final int FRAME_WIDTH = Constant.WINDOW_WIDTH;
	private static final int FRAME_HEIGHT = 120;
	
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
			new PanelEntry("   [ REW]  ", 		"C", 	4, Mode.CREW),
			new PanelEntry("   [ OOM]  ", 		"R", 	4, Mode.ROOM),
			new PanelEntry("  [ EBUG]  ", 		"D", 	3, Mode.DEBUG),
			new PanelEntry("   [ LAN]  ", 		"P",	4, Mode.PLAN)
	};
	
	public PanelShortcut(RenderWindow app, final UserInterface userInterface) throws IOException {
		super(app, 0, new Vector2f(0, Constant.WINDOW_HEIGHT - FRAME_HEIGHT), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));

		setBackgroundColor(new Color(18, 28, 30));
		
		int posX = 10;
		for (PanelEntry entry: _entries) {
			final PanelEntry e = entry;
			
			TextView label = new TextView(new Vector2f(160, 36));
			label.setColor(new Color(120, 255, 255));
			label.setPosition(new Vector2f(posX, 6));
			label.setString(e.label);
			label.setCharacterSize(20);
			label.setPadding(3, 8);
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
					userInterface.setMode(e.mode);
				}
			});
			addView(label);
			
			TextView shortcut = new TextView(new Vector2f(160, 36));
			shortcut.setColor(new Color(176, 205, 53));
			shortcut.setPosition(new Vector2f(posX + (int)(e.shortcutPos * 11.8), 6));
			shortcut.setString(e.shortcut);
			shortcut.setCharacterSize(20);
			shortcut.setPadding(3, 8);
			shortcut.setStyle(TextView.UNDERLINED);
			addView(shortcut);
			
			posX += 170;
		}
	}
}
