import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;
import org.smallbox.faraway.Color;
import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.engine.ui.Colors;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.engine.util.StringUtils;
import org.smallbox.faraway.manager.SpriteManager;

public class SFMLTextView extends TextView {
	protected Text 			_text;
	protected Text 			_shortcut;
	protected Text 			_shortcutUnderline;
    protected String 		_value;
	private Color 			_color;
    private String          _shortcutString;
    private String          _shortcutUnderlineString;

    public SFMLTextView() {
        super(0, 0);
		create();
	}

	public SFMLTextView(int width, int height) {
		super(width, height);
		create();
	}

	private void create() {
		_text = new Text();
		_text.setFont(((SFMLSpriteManager) SpriteManager.getInstance()).getFont());
		_text.setColor(new org.jsfml.graphics.Color(Colors.TEXT.r, Colors.TEXT.g, Colors.TEXT.b));
	}

    @Override
	public void setString(String string) {
		if (string != null && !string.equals(_value)) {
            if (string.contains("_")) {
                int pos = string.indexOf('_');
                _shortcutString = "";
                _shortcutUnderlineString = "";
                for (int i = 0; i < pos; i++) {
                    _shortcutString += " ";
                    _shortcutUnderlineString += " ";
                }
                _shortcutString += string.substring(pos + 1, pos + 2);
                _shortcutUnderlineString += "_";
                string = string.substring(0, pos) + " " + string.substring(pos + 2);
            }

			_text.setString(string);
			_value = string;
		}
	}

    private int getSFMLStyle(int style) {
        switch (style) {
            case TextView.BOLD: return Text.BOLD;
            case TextView.ITALIC: return Text.ITALIC;
            case TextView.UNDERLINED: return Text.UNDERLINED;
        }
        return Text.REGULAR;
    }

    @Override
	public void setCharacterSize(int size) {
		_text.setCharacterSize(size);
	}

    @Override
	public void setStyle(int style) {
		_text.setStyle(style);
	}

    @Override
	public void setColor(Color color) {
		_color = color;
        if (color != null) {
            _text.setColor(new org.jsfml.graphics.Color(color.r, color.g, color.b));
        } else {
            _text.setColor(org.jsfml.graphics.Color.WHITE);
        }
	}

	@Override
	public Color getColor() {
		return _color;
	}

	@Override
	public void setPosition(int x, int y) {
		super.setPosition(x, y);
		_text.setPosition(new Vector2f(x + _paddingLeft, y + _paddingTop));
	}

    @Override
    public int getContentWidth() {
        return _text != null ? (int) _text.getLocalBounds().width : 0;
    }

    @Override
    public int getContentHeight() {
        return _text != null ? (int) _text.getLocalBounds().height : 0;
    }

    @Override
	public void setPadding(int t, int r, int b, int l) {
		super.setPadding(t, r, b, l);
		_text.setPosition(new Vector2f(_x + _paddingLeft, _y + _paddingTop));
	}
	
	@Override
	public void setPadding(int t, int r) {
		super.setPadding(t, r);
		_text.setPosition(new Vector2f(_x + _paddingLeft, _y + _paddingTop));
	}

	@Override
	public void onDraw(GFXRenderer renderer, RenderEffect effect) {
		//((SFMLRenderer)renderer).draw(_text, effect);
	}

    @Override
    public void draw(GFXRenderer renderer, RenderEffect effect) {
        ((SFMLRenderer)renderer).draw(_text, effect);

        if (_shortcut != null) {
            ((SFMLRenderer)renderer).draw(_shortcut, effect);
            ((SFMLRenderer)renderer).draw(_shortcutUnderline, effect);
        }
    }

    @Override
    public void refresh() {
        //TODO
    }

    @Override
    public void setBackgroundColor(Color color) {
    }

    @Override
    public void setBorderColor(Color color) {
    }

    @Override
	public void setDashedString(String label, String value, int nbColumns) {
		setString(StringUtils.getDashedString(label, value, nbColumns));
	}

    @Override
	public String getString() {
		return _value;
	}

    @Override
    public void init() {
        if (_shortcutString != null) {
            _shortcut = new Text();
            _shortcut.setFont(((SFMLSpriteManager) SpriteManager.getInstance()).getFont());
            _shortcut.setColor(new org.jsfml.graphics.Color(176, 205, 53));
            _shortcut.setPosition(_x, _y);
            _shortcut.setString(_shortcutString);
            _shortcut.setCharacterSize(_text.getCharacterSize());

            _shortcutUnderline = new Text();
            _shortcutUnderline.setFont(((SFMLSpriteManager) SpriteManager.getInstance()).getFont());
            _shortcutUnderline.setColor(new org.jsfml.graphics.Color(176, 205, 53));
            _shortcutUnderline.setPosition(_x, _y);
            _shortcutUnderline.setString(_shortcutUnderlineString);
            _shortcutUnderline.setCharacterSize(_text.getCharacterSize());
        }
    }

    public Drawable getText() {
		return _text;
	}
}
