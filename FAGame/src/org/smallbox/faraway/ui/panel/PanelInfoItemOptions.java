package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.ui.engine.OnClickListener;
import org.smallbox.faraway.ui.engine.TextView;
import org.smallbox.faraway.ui.engine.ViewFactory;

import java.util.ArrayList;
import java.util.List;

public class PanelInfoItemOptions {

	private ArrayList<TextView> _options;
	private int _posX;
	private int _posY;

	public PanelInfoItemOptions(int x, int y) {
		_options = new ArrayList<>();
		_posX = x;
		_posY = y;
	}
	
	public TextView add(String str, OnClickListener onClickListener) {
	    TextView text = ViewFactory.getInstance().createTextView(100, 20);
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
