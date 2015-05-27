package org.smallbox.faraway.ui.panel;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.system.Vector2f;

import org.smallbox.faraway.engine.ui.OnClickListener;
import org.smallbox.faraway.engine.ui.TextView;

public class PanelInfoItemOptions {

	private ArrayList<TextView> _options;
	private int _posX;
	private int _posY;

	public PanelInfoItemOptions(int x, int y) {
		_options = new ArrayList<TextView>();
		_posX = x;
		_posY = y;
	}
	
	public TextView add(String str, OnClickListener onClickListener) {
	    TextView text = new TextView(100, 20);
	    text.setString(str);
	    text.setCharacterSize(14);
	    text.setPosition(_posX, _posY + 20 * _options.size());
	    text.setOnClickListener(onClickListener);
		_options.add(text);

		return text;
	}

	public List<TextView> getOptions() {
		return _options;
	}

}
