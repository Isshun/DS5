package org.smallbox.faraway.ui;

import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.engine.renderer.GDXRenderer;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.ui.engine.OnClickListener;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.view.UIFrame;
import org.smallbox.faraway.ui.engine.view.UILabel;
import org.smallbox.faraway.ui.engine.view.View;
import org.smallbox.faraway.util.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MenuSave extends MenuBase {
	private static final int 	FRAME_WIDTH = 640;
	private static final int 	FRAME_HEIGHT = 480;
	private int 				_index;
	private List<UILabel>		_lbFiles;
	private int 				_nbFiles;
	private UIFrame _menu;
	private String _saveName = "";
	private UILabel _textEntry;

	public MenuSave(final Game game) {
		super(Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT);
		setBackgroundColor(new Color(0, 0, 0, 150));

		_menu = ViewFactory.getInstance().createFrameLayout(FRAME_WIDTH, FRAME_HEIGHT);
		_menu.setPosition(Constant.WINDOW_WIDTH / 2 - FRAME_WIDTH / 2, Constant.WINDOW_HEIGHT / 2 - FRAME_HEIGHT / 2);
		_menu.setBackgroundColor(new Color(200, 200, 200, 50));
		addView(_menu);

		_textEntry = ViewFactory.getInstance().createTextView();
		_textEntry.setPosition(0, 0);
		_textEntry.setTextSize(14);
		_menu.addView(_textEntry);

		setVisible(true);

		_lbFiles = new ArrayList<>();
		File[] files = null;
		File directoryToScan = new File("saves");
		files = directoryToScan.listFiles();
		_nbFiles = files.length;
		int i = 0;
		for (final File file: files) {
			UILabel lbFile = ViewFactory.getInstance().createTextView(200, 32);
			lbFile.setTextSize(16);
			lbFile.setText(file.getName());
			lbFile.setTextColor(Color.WHITE);
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
	public boolean checkKey(GameEventListener.Key key) {
		switch (key) {
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
			case D_0: _saveName += "0"; return true;
			case D_1: _saveName += "1"; return true;
			case D_2: _saveName += "2"; return true;
			case D_3: _saveName += "3"; return true;
			case D_4: _saveName += "4"; return true;
			case D_5: _saveName += "5"; return true;
			case D_6: _saveName += "6"; return true;
			case D_7: _saveName += "7"; return true;
			case D_8: _saveName += "8"; return true;
			case D_9: _saveName += "9"; return true;
			default: return false;
		}
	}

	@Override
	public void onDraw(GDXRenderer renderer, Viewport viewport) {
		int i = 0;
		for (UILabel lbFile: _lbFiles) {
			lbFile.setTextColor(i++ == _index ? Color.YELLOW : Color.WHITE);
		}
		_textEntry.setText(_saveName);
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
