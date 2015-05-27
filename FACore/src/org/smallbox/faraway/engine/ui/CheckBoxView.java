package org.smallbox.faraway.engine.ui;

import org.jsfml.system.Vector2f;

import org.smallbox.faraway.engine.util.StringUtils;

public class CheckBoxView extends LinkView {
	
	private static final int NB_COLUMNS_NEEDS = 22;

//	private Sprite 	_iconChecked;
//	private Sprite 	_iconUnChecked;
	private boolean	_isChecked;
	private String _label;

	public CheckBoxView(int width, int height) {
		super(width, height);
		
//		_iconChecked = SpriteManager.getInstance().getIconChecked();
//		_iconUnChecked = SpriteManager.getInstance().getIconUnChecked();
		
		setChecked(false);
//		_textPaddingLeft = 20;
	}

	public void toogleChecked() {
		setChecked(!_isChecked);
	}

	@Override
	public void setString(String text) {
		_label = text;
		super.setString(StringUtils.getDashedString(_label, "[ ]", NB_COLUMNS_NEEDS));
	}
	
	public void setChecked(boolean checked) {
		_isChecked = checked;
		if (_label != null) {
			super.setString(StringUtils.getDashedString(_label, checked ? "[x]" : "[ ]", NB_COLUMNS_NEEDS));
		}
	}

	public boolean getChecked() {
		return _isChecked;
	}

}
