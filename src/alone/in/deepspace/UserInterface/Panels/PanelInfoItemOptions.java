package alone.in.deepspace.UserInterface.Panels;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.system.Vector2f;

import alone.in.deepspace.UserInterface.Utils.OnClickListener;
import alone.in.deepspace.UserInterface.Utils.UIText;

public class PanelInfoItemOptions {

	private ArrayList<UIText> _options;
	private int _posX;
	private int _posY;

	public PanelInfoItemOptions(int x, int y) {
		_options = new ArrayList<UIText>();
		_posX = x;
		_posY = y;
	}
	
	public UIText add(String str, OnClickListener onClickListener) {
	    UIText text = new UIText(new Vector2f(100, 20));
	    text.setString(str);
	    text.setCharacterSize(14);
	    text.setPosition(_posX, _posY + 20 * _options.size());
	    text.setOnClickListener(onClickListener);
		_options.add(text);

		return text;
	}

	public List<UIText> getOptions() {
		return _options;
	}

}
