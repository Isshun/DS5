package alone.in.deepspace.ui.panel;

import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.engine.ui.ButtonView;
import alone.in.deepspace.manager.SpriteManager;

public class CheckBoxView extends ButtonView {

	private Sprite 	_iconChecked;
	private Sprite 	_iconUnChecked;
	private boolean	_isChecked;

	public CheckBoxView(Vector2f size) {
		super(size);
		
		_iconChecked = SpriteManager.getInstance().getIconChecked();
		_iconUnChecked = SpriteManager.getInstance().getIconUnChecked();
		
		setChecked(false);
		_textPaddingLeft = 20;
	}

	public void toogleChecked() {
		setChecked(!_isChecked);
	}

	public void setChecked(boolean checked) {
		_isChecked = checked;
		setIcon(_isChecked ? _iconChecked : _iconUnChecked);
	}

	public boolean getChecked() {
		return _isChecked;
	}

}
