package alone.in.deepspace.UserInterface.Utils;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Managers.SpriteManager;
import alone.in.deepspace.Models.BaseItem;
import alone.in.deepspace.Models.BaseItem.Type;

public class UIIcon extends UIView {

	private RectangleShape 	_background;
	private Sprite			_icon;
	private Text _text;
	private Type _type;
	private Text _text2;
	private boolean _multiline;

	public UIIcon(Vector2f size, int typeIndex) {
		super(size);
		_type = BaseItem.getTypeIndex(typeIndex);
		_icon = SpriteManager.getInstance().getIcon(_type);
		_text = new Text();
		
		String name = BaseItem.getItemName(_type);
		int cut = name.indexOf(' ');
		if (cut != -1) {
			_multiline = true;
			String s1 = name.substring(0, cut);
			String s2 = name.substring(cut, name.length());
			
			_text.setString(s1);

			_text2 = new Text();
			_text2.setString(s2);
			_text2.setFont(SpriteManager.getInstance().getFont());
			_text2.setCharacterSize(12);
			_text2.setColor(Color.BLACK);
			_text2.setStyle(Text.REGULAR);
		} else {
			_text.setString(name);
		}
		_text.setFont(SpriteManager.getInstance().getFont());
		_text.setCharacterSize(12);
		_text.setColor(Color.BLACK);
		_text.setStyle(Text.REGULAR);
	}
	
	public void setPosition(int x, int y) {
		super.setPosition(new Vector2f(x, y));
		
		if (_icon != null) {
			_icon.setPosition(x + 3, y + 3);
		}
		if (_background != null) {
			_background.setPosition(x, y);
		}
		if (_text != null) {
			FloatRect rect = _text.getGlobalBounds();
			_text.setPosition(x + 30 - rect.width / 2, y + (_multiline ? 54 : 58));

			if (_text2 != null) {
				FloatRect rect2 = _text2.getGlobalBounds();
				_text2.setPosition(x + 30 - rect2.width / 2, y + 64);
			}
		}
	}

	public void setBackground(Color color) {
		_background = new RectangleShape();
		_background.setSize(new Vector2f(62, 80));
		_background.setFillColor(color);
		if (_pos != null) {
			_background.setPosition(_posX, _posY);
		}
	}

	@Override
	public void onRefresh(RenderWindow app, RenderStates states) {
		if (_background != null) {
			app.draw(_background, states);
		}
		if (_icon != null) {
			app.draw(_icon, states);
		}
		if (_text != null) {
			app.draw(_text, states);
		}
		if (_text2 != null) {
			app.draw(_text2, states);
		}
	}

}
