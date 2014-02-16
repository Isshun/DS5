package alone.in.DeepSpace.UserInterface.Utils;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import alone.in.DeepSpace.MainRenderer;
import alone.in.DeepSpace.SpriteManager;

public class UIText extends UIView {

	private Text _text;

	public UIText(Vector2f size) {
		super(size);
		
		_text = new Text();
		_text.setFont(SpriteManager.getInstance().getFont());
	}

	public void setString(String string) {
		_text.setString(string);
	}

	public void setCharacterSize(int size) {
		_text.setCharacterSize(size);
	}

	public void setColor(Color color) {
		_text.setColor(color);
	}

	public void setPosition(Vector2f pos) {
		super.setPosition(pos);
		_text.setPosition(pos);
	}

	@Override
	public void refresh(RenderStates render) {
		if (_isVisible == false) {
			return;
		}
		
		MainRenderer.getInstance().draw(_text, render);
	}

}
