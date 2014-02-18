package alone.in.DeepSpace.UserInterface.Utils;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import alone.in.DeepSpace.Managers.SpriteManager;
import alone.in.DeepSpace.Models.BaseItem;
import alone.in.DeepSpace.Models.BaseItem.Type;

public class UIIcon extends UIView {

	private RectangleShape 	_background;
	private Sprite			_icon;
	private Text _text;
	private Type _type;

	public UIIcon(Vector2f size, int typeIndex) {
		super(size);
		_type = BaseItem.getTypeIndex(typeIndex);
		_icon = SpriteManager.getInstance().getIcon(_type);
		
		_text = new Text();
		_text.setString(BaseItem.getItemName(_type));
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
			_text.setPosition(x + 6, y + 57);
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
	}

}
