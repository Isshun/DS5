package org.smallbox.faraway.ui;

import org.jsfml.system.Vector2f;
import org.jsfml.window.event.Event;
import org.smallbox.faraway.Color;
import org.smallbox.faraway.Game;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.Renderer;
import org.smallbox.faraway.engine.ui.FrameLayout;
import org.smallbox.faraway.engine.ui.OnClickListener;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.engine.ui.View;
import org.smallbox.faraway.engine.util.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MenuSave extends MenuBase {
	private static final int 	FRAME_WIDTH = 640;
	private static final int 	FRAME_HEIGHT = 480;
	private int 				_index;
	private List<TextView>		_lbFiles;
	private int 				_nbFiles;
	private FrameLayout _menu;
	private String _saveName = "";
	private TextView _textEntry;
	
	public MenuSave(final Game game) {
		super(Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT);
		setBackgroundColor(new Color(0, 0, 0, 150));
		
		_menu = new FrameLayout(FRAME_WIDTH, FRAME_HEIGHT);
		_menu.setPosition(new Vector2f(Constant.WINDOW_WIDTH / 2 - FRAME_WIDTH / 2, Constant.WINDOW_HEIGHT / 2 - FRAME_HEIGHT / 2));
		_menu.setBackgroundColor(new Color(200, 200, 200, 50));
		addView(_menu);
		
		_textEntry = new TextView();
		_textEntry.setPosition(0, 0);
		_textEntry.setCharacterSize(14);
		_menu.addView(_textEntry);
		
		setVisible(true);

		_lbFiles = new ArrayList<TextView>();
		File[] files = null;
		File directoryToScan = new File("saves");
		files = directoryToScan.listFiles();
		_nbFiles = files.length;
		int i = 0;
		for (final File file: files) {
			TextView lbFile = new TextView(200, 32);
			lbFile.setCharacterSize(16);
			lbFile.setString(file.getName());
			lbFile.setColor(Color.WHITE);
			lbFile.setPosition(200, 32 * i);
			lbFile.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
				}
			});
			_lbFiles.add(lbFile);
			_menu.addView(lbFile);
			i++;
		}

		setVisible(true);
	}
	
	@Override
	public boolean checkKey(Event event) {
		switch (event.asKeyEvent().key) {
		case A: _saveName += "a"; return true;
		case B: _saveName += "b"; return true;
		case C: _saveName += "c"; return true;
		case D: _saveName += "d"; return true;
		case E: _saveName += "e"; return true;
		case F: _saveName += "f"; return true;
		case G: _saveName += "g"; return true;
		case H: _saveName += "h"; return true;
		case I: _saveName += "i"; return true;
		case J: _saveName += "j"; return true;
		case K: _saveName += "k"; return true;
		case L: _saveName += "l"; return true;
		case M: _saveName += "m"; return true;
		case N: _saveName += "n"; return true;
		case O: _saveName += "o"; return true;
		case P: _saveName += "p"; return true;
		case Q: _saveName += "q"; return true;
		case R: _saveName += "r"; return true;
		case S: _saveName += "s"; return true;
		case T: _saveName += "t"; return true;
		case U: _saveName += "u"; return true;
		case V: _saveName += "v"; return true;
		case W: _saveName += "w"; return true;
		case X: _saveName += "x"; return true;
		case Y: _saveName += "y"; return true;
		case Z: _saveName += "z"; return true;
		case BACKSPACE: _saveName = _saveName.substring(0, Math.max(_saveName.length()-1, 0)); return true;
		case NUM0: case NUMPAD0: _saveName += "0"; return true;
		case NUM1: case NUMPAD1: _saveName += "1"; return true;
		case NUM2: case NUMPAD2: _saveName += "2"; return true;
		case NUM3: case NUMPAD3: _saveName += "3"; return true;
		case NUM4: case NUMPAD4: _saveName += "4"; return true;
		case NUM5: case NUMPAD5: _saveName += "5"; return true;
		case NUM6: case NUMPAD6: _saveName += "6"; return true;
		case NUM7: case NUMPAD7: _saveName += "7"; return true;
		case NUM8: case NUMPAD8: _saveName += "8"; return true;
		case NUM9: case NUMPAD9: _saveName += "9"; return true;
		case PERIOD: _saveName += ""; return true;
		default: return false;
		}
	}
	
	@Override
	public void onDraw(Renderer renderer, RenderEffect effect) {
		int i = 0;
		for (TextView lbFile: _lbFiles) {
			lbFile.setColor(i++ == _index ? Color.YELLOW : Color.WHITE);
		}
		_textEntry.setString(_saveName);
	}
	
	@Override
	public void onKeyDown() {
		_index = (_index + 1) % _nbFiles;
	}

	@Override
	public void onKeyUp() {
		_index = _index == 0 ? _nbFiles - 1 : _index - 1;
	}

	@Override
	public void onKeyEnter() {
		_lbFiles.get(_index).onClick();
	}

}
