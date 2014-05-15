package alone.in.deepspace.UserInterface;

import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.UserInterface.Utils.UIText;
import alone.in.deepspace.Utils.Constant;

public class UserInterfaceMessage extends UserSubInterface {

	private static int LINE_INTERVAL = 100;
	private static String[] MESSAGE = {
		"Bonjour commandant, bienvenue sur---------",
		"Gliese-581-c",
		"Je ne peux vous parlez que 10min par période",
		"de rotation alors ne perdons pas de temps"
	};
	
	private static final int 	FRAME_WIDTH = 380;
	private static final int	FRAME_HEIGHT = 200;
	private int 		_frame;
	private UIText[] 	_texts;
	private int _start;

	public UserInterfaceMessage(RenderWindow app) throws IOException {
		super(app, 0, new Vector2f(20, 20), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
		  
		_texts = new UIText[10];
		for (int i = 0; i < 10; i++) {
			_texts[i] = new UIText(null);
			_texts[i].setCharacterSize(14);
			_texts[i].setPosition(10, 20 * i);
			addView(_texts[i]);
		}
		
		setBackgroundColor(new Color(100, 0, 0, 180));
	}

	@Override
	public void onRefresh(RenderWindow app) {
		int offset = _frame - _start;
		int index = (offset / LINE_INTERVAL) - 1;
		if (offset % LINE_INTERVAL == 0) {
			if (index < MESSAGE.length) {
				_texts[index].setString(MESSAGE[index]);
			}
		} else {
			if (offset % 30 == 0 && index + 1 < MESSAGE.length) {
				_texts[index + 1].setString("...");
			}
			if (offset % 60 == 0 && index < MESSAGE.length) {
				_texts[index + 1].setString("");
			}
		}
	}

	public void setFrame(int frame) {
		_frame = frame;
	}

	public void setStart(int start) {
		_start = start;
	}

}
