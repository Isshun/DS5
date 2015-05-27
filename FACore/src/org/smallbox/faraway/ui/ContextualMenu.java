package org.smallbox.faraway.ui;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.Renderer;
import org.smallbox.faraway.engine.Viewport;
import org.smallbox.faraway.engine.ui.OnClickListener;
import org.smallbox.faraway.engine.ui.OnFocusListener;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.engine.ui.View;
import org.smallbox.faraway.ui.UserInterface.Mode;
import org.smallbox.faraway.ui.panel.BasePanel;

import java.util.ArrayList;
import java.util.List;

public class ContextualMenu extends BasePanel {
	public final static int		LINE_HEIGHT = 22;
	public final static int		PADDING_V = 4;
	private final static int	PADDING_H = 4;
	private int					ENTRY_WIDTH;
	private int					ENTRY_HEIGHT = LINE_HEIGHT;
	
	private List<TextView>		_entries;
	private ContextualMenu 		_subMenu;
	private int 				_initPosX;
	private int 				_initPosY;
	
	public ContextualMenu(int tileIndex, int x, int y, int width, int height, Viewport viewport) {
		super(Mode.NONE, null, x, y, width, height);
		setVisible(true);
		
		ENTRY_WIDTH = (int) (width - PADDING_H * 2);
//		_posX = (int) pos.x - viewport.getPosX();
//		_posY = (int) pos.y - viewport.getPosY();
		
		_initPosX = (int) x;
		_initPosY = (int) y;
		
		_entries = new ArrayList<>();
		
		setBackgroundColor(new Color(0, 0, 0, 140));
	}
	
	public void addEntry(String label, OnClickListener listener, final OnFocusListener onFocusListener) {
		final TextView text = new TextView(ENTRY_WIDTH, ENTRY_HEIGHT);
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
	public void onDraw(Renderer renderer, RenderEffect effect) {
		if (_subMenu != null) {
			_subMenu.draw(renderer, null);
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

	@Override
	protected void onCreate() {
		// TODO Auto-generated method stub
		
	}

}
