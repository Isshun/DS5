package alone.in.DeepSpace.UserInterface;
import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import alone.in.DeepSpace.Managers.CharacterManager;
import alone.in.DeepSpace.Managers.SpriteManager;
import alone.in.DeepSpace.Models.Profession;
import alone.in.DeepSpace.UserInterface.Utils.OnClickListener;
import alone.in.DeepSpace.UserInterface.Utils.UIText;
import alone.in.DeepSpace.UserInterface.Utils.UIView;
import alone.in.DeepSpace.Utils.Constant;
import alone.in.DeepSpace.Utils.ObjectPool;


public class PanelDebug extends UserSubInterface {

	private static final int FRAME_WIDTH = 380;
	private static final float FRAME_HEIGHT = Constant.WINDOW_HEIGHT;
	private int				_index;

	public PanelDebug(RenderWindow app) throws IOException {
		super(app, 0, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 0), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
		
		setBackgroundColor(new Color(200, 50, 140, 150));
		
		// Add character
		UIText txtAddCharacter = new UIText(new Vector2f(20, 20));
		txtAddCharacter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(UIView view) {
				CharacterManager.getInstance().add(0, 0, Profession.Type.ENGINEER);
			}
		});
		txtAddCharacter.setString("Add character");
		txtAddCharacter.setCharacterSize(20);
		txtAddCharacter.setColor(Color.WHITE);
		txtAddCharacter.setPosition(new Vector2f(20, 20));
		addView(txtAddCharacter);
	}

	void  addDebug(final String key, String value) {
		int y = _index * 32;

		Text text = ObjectPool.getText();
		
		text.setFont(SpriteManager.getInstance().getFont());
		text.setCharacterSize(20);
		text.setStyle(Text.REGULAR);
		
		text.setString(key);
		text.setPosition(Constant.WINDOW_WIDTH - 320 + Constant.UI_PADDING, Constant.UI_PADDING + y);
		_app.draw(text);

		text.setString(value);
		text.setPosition(Constant.WINDOW_WIDTH - 320 + Constant.UI_PADDING + 160, Constant.UI_PADDING + y);
		_app.draw(text);

		ObjectPool.release(text);

		_index++;
	}
	
	void  addDebug(final String str) {
		int y = _index * 32;

		Text text = ObjectPool.getText();
		
		text.setFont(SpriteManager.getInstance().getFont());
		text.setCharacterSize(20);
		text.setStyle(Text.REGULAR);
		text.setColor(Color.WHITE);
		
		text.setString(str);
		text.setPosition(0, 0);
		_app.draw(text, _render);

		ObjectPool.release(text);

		_index++;
	}

	void	refresh(int frame, int x, int y) {
		super.refresh();
//		
//		if (_isVisible == false) {
//			return;
//		}
//		
//		_index = 0;
//
////		addDebug("add character");
//		
//	  BaseItem item = WorldMap.getInstance().getItem(x, y);
//
//	  Log.debug("pos: " + x + " x " + y);
//	  Log.debug("item: " + item);
//
//	  if (item != null) {
//		addDebug("type", String.valueOf(item.getType()));
//		addDebug("pos", item.getX() + " x " + item.getY());
//		addDebug("zone req.", String.valueOf(item.getZoneIdRequired()));
//		addDebug("zone", String.valueOf(item.getZoneId()));
//		addDebug("room", String.valueOf(item.getRoomId()));
//	  }
	}

}
