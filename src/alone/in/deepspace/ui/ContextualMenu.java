package alone.in.deepspace.ui;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.engine.Viewport;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.OnFocusListener;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.engine.ui.View;

public class ContextualMenu extends UserSubInterface {
	public final static int		LINE_HEIGHT = 22;
	public final static int		PADDING_V = 4;
	private final static int	PADDING_H = 4;
	private int					ENTRY_WIDTH;
	private int					ENTRY_HEIGHT = LINE_HEIGHT;
	
	private List<TextView>		_entries;
	private ContextualMenu 		_subMenu;
	private int 				_initPosX;
	private int 				_initPosY;
	
	public ContextualMenu(RenderWindow app, int tileIndex, Vector2f pos, Vector2f size, Viewport viewport) {
		super(app, tileIndex, pos, size);
		setVisible(true);
		
		ENTRY_WIDTH = (int) (size.x - PADDING_H * 2);
//		_posX = (int) pos.x - viewport.getPosX();
//		_posY = (int) pos.y - viewport.getPosY();
		
		_initPosX = (int) pos.x;
		_initPosY = (int) pos.y;
		
		_entries = new ArrayList<TextView>();
		
		setBackgroundColor(new Color(0, 0, 0, 140));
	}
	
	public void addEntry(String label, OnClickListener listener, final OnFocusListener onFocusListener) {
		final TextView text = new TextView(new Vector2f(ENTRY_WIDTH, ENTRY_HEIGHT));
		text.setString(label);
		text.setCharacterSize(12);
		text.setPadding(2, 4);
		text.setPosition(PADDING_H, PADDING_V + _entries.size() * LINE_HEIGHT);
		text.setOnClickListener(listener);
		text.setOnFocusListener(new OnFocusListener() {
			@Override
			public void onExit(View view) {
				text.setBackgroundColor(null);
				if (onFocusListener != null) {
					onFocusListener.onExit(view);
				}
			}
			
			@Override
			public void onEnter(View view) {
				text.setBackgroundColor(new Color(255, 255, 255, 100));
				if (onFocusListener != null) {
					onFocusListener.onEnter(view);
				}
			}
		});
		addView(text);
		_entries.add(text);
	}

	public void addSubMenu(int i, ContextualMenu subMenu) {
		if (_subMenu != null) {
			removeSubMenu();
		}

		_subMenu = subMenu;
		subMenu.setPosition(subMenu.getPosX() + _initPosX, subMenu.getPosY() + _initPosY);
		subMenu.resetPos();
		subMenu.getRect();
		subMenu.setId(80);
	}
	
	@Override
	public void onDraw(RenderWindow app, RenderStates render) {
		if (_subMenu != null) {
			_subMenu.draw(app, null);
		}
		for (View entry: _entries) {
			entry.resetPos();
		}
	}

	public void removeSubMenu() {
		removeView(_subMenu);
		_subMenu = null;
	}

	public void setViewPortPosition(int x, int y) {
		setPosition(_initPosX + x, _initPosY + y);
		resetPos();
		
		if (_subMenu != null) {
			_subMenu.setViewPortPosition(_initPosX + x, _initPosY + y);
		}
	}

}
