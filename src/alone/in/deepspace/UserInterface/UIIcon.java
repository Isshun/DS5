package alone.in.DeepSpace.UserInterface;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import alone.in.DeepSpace.Managers.SpriteManager;
import alone.in.DeepSpace.Models.BaseItem;
import alone.in.DeepSpace.Models.BaseItem.Type;

public class UIIcon {

	private RectangleShape 	_background;
	private Vector2f		_pos;
	private int 			_posX;
	private int 			_posY;
	private Sprite			_icon;
	private Text _text;
	private Type _type;

	public UIIcon(int typeIndex) {
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
		_pos = new Vector2f(x, y);
		_posX = x;
		_posY = y;
		
		if (_icon != null) {
			_icon.setPosition(x + 23, y + 63);
		}
		if (_background != null) {
			_background.setPosition(x + 20, y + 60);
		}
		if (_text != null) {
			_text.setPosition(x + 26, y + 117);
		}
	}

	public void setBackground(Color color) {
		_background = new RectangleShape();
		_background.setSize(new Vector2f(62, 80));
		_background.setFillColor(color);
		if (_pos != null) {
			_background.setPosition(_posX + 20, _posY + 60);
		}
	}


	public void refresh(RenderWindow _app) {
		if (_background != null) {
			_app.draw(_background);
		}
		if (_icon != null) {
			_app.draw(_icon);
		}
		if (_text != null) {
			_app.draw(_text);
		}
	}

}
