package org.smallbox.faraway.ui.panel.info;

import org.smallbox.faraway.ui.engine.OnClickListener;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.view.UILabel;

import java.util.ArrayList;
import java.util.List;

public class PanelInfoItemOptions {

	private ArrayList<UILabel> _options;
	private int _posX;
	private int _posY;

	public PanelInfoItemOptions(int x, int y) {
		_options = new ArrayList<>();
		_posX = x;
		_posY = y;
	}
	
	public UILabel add(String str, OnClickListener onClickListener) {
	    UILabel text = ViewFactory.getInstance().createTextView(100, 20);
	    text.setText(str);
	    text.setTextSize(14);
	    text.setPosition(_posX, _posY + 20 * _options.size());
	    text.setOnClickListener(onClickListener);
		_options.add(text);

		return text;
	}

	public List<UILabel> getOptions() {
		return _options;
	}

}
