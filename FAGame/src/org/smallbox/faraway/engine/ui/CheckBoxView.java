package org.smallbox.faraway.engine.ui;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.RenderEffect;
//import org.smallbox.faraway.engine.util.StringUtils;

public class CheckBoxView extends TextView {
	
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

	@Override
	protected void onDraw(GFXRenderer renderer, RenderEffect effect) {

	}

	@Override
	public void draw(GFXRenderer renderer, RenderEffect effect) {

	}

	@Override
	public void refresh() {

	}

	@Override
	public int getContentWidth() {
		return 0;
	}

	@Override
	public int getContentHeight() {
		return 0;
	}

	public void toogleChecked() {
		setChecked(!_isChecked);
	}

	@Override
	public void setStringValue(String text) {
		_label = text;
//		super.setString(StringUtils.getDashedString(_label, "[ ]", NB_COLUMNS_NEEDS));
	}

	@Override
	public void setCharacterSize(int size) {

	}

	@Override
	public void setStyle(int style) {

	}

	@Override
	public void setColor(Color color) {

	}

	@Override
	public Color getColor() {
		return null;
	}

	@Override
	public void setDashedString(String label, String value, int nbColumns) {

	}

	@Override
	public String getString() {
		return null;
	}

	@Override
	public void init() {

	}

	@Override
	public View findById(String id) {
		return null;
	}

	public void setChecked(boolean checked) {
		_isChecked = checked;
		if (_label != null) {
//			super.setString(StringUtils.getDashedString(_label, checked ? "[x]" : "[ ]", NB_COLUMNS_NEEDS));
		}
	}

	public boolean getChecked() {
		return _isChecked;
	}

}