package org.smallbox.faraway.ui;

import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;
import org.smallbox.faraway.Color;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.Renderer;
import org.smallbox.faraway.engine.serializer.GameLoadListener;
import org.smallbox.faraway.engine.ui.*;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.manager.SpriteManager;

import java.io.IOException;
import java.util.List;

public class MenuGame extends MenuBase {
	private static final int 	FRAME_WIDTH = 640;
	private static final int 	FRAME_HEIGHT = 480;
	private int 				_index;
	private List<TextView>		_lbFiles;
	private int 				_nbFiles;
	private FrameLayout 		_menu;
	protected int 				_selected = -1;
	
	public MenuGame(final GameLoadListener onLoadListener) throws IOException {
		super(Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT);
		setBackgroundColor(new Color(0, 0, 0, 150));
		
		_menu = new FrameLayout(FRAME_WIDTH, FRAME_HEIGHT);
		_menu.setPosition(new Vector2f(Constant.WINDOW_WIDTH / 2 - FRAME_WIDTH / 2, Constant.WINDOW_HEIGHT / 2 - FRAME_HEIGHT / 2));
		_menu.setBackgroundColor(new Color(200, 200, 200, 50));
		addView(_menu);
		
		setVisible(true);
	}
	
	@Override
	public void onDraw(Renderer renderer, RenderEffect effect) {
		refreshEntry(renderer, "New game", 0);
		refreshEntry(renderer, "Load", 1);
		refreshEntry(renderer, "Save", 2);
		refreshEntry(renderer, "Feedback", 3);
		refreshEntry(renderer, "Exit", 4);
	}
	
	private void refreshEntry(Renderer renderer, String str, int pos) {
		Text text = new Text();
		text.setFont(SpriteManager.getInstance().getFont());
		text.setString(str);
		text.setCharacterSize(42);
		text.setColor(org.jsfml.graphics.Color.CYAN);
		//text.setColor(_selected == pos ? new Color(200, 50, 140, 150) : new Color(50, 140, 200, 150));
		
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				text.setPosition(_menu.getPosX() + i + 200, _menu.getPosY() + j + 52 * pos);
				renderer.draw(text);
			}
		}

		text.setColor(org.jsfml.graphics.Color.WHITE);
		text.setPosition(_menu.getPosX() + 200, _menu.getPosY() + 52 * pos);
		renderer.draw(text);
	}

	public void addEntry(String str, final int pos, OnClickListener listener) {
		{
			TextView text = new TextView(300, 52);
			text.setString(str);
			text.setCharacterSize(42);
			text.setColor(Color.WHITE);
			text.setPosition(200, 52 * pos);
			text.setOnClickListener(listener);
			text.setOnFocusListener(new OnFocusListener() {
				@Override
				public void onExit(View view) {
					if (_selected == pos) {
						_selected = -1;
					}
				}
				
				@Override
				public void onEnter(View view) {
					_selected = pos;
				}
			});
			_menu.addView(text);
		}
	}
	
	@Override
	public void onKeyDown() {
		_index = (_index + 1) % _nbFiles;
	}

	@Override
	public void onKeyUp() {
		_index = _index == 0 ? _nbFiles - 1 : _index - 1;
	}

	@Override
	public void onKeyEnter() {
		_lbFiles.get(_index).onClick();
	}

}
