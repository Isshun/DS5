package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.engine.renderer.GDXRenderer;
import org.smallbox.faraway.game.GameObserver;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInteraction;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.UserInterface.Mode;
import org.smallbox.faraway.ui.engine.LayoutFactory;
import org.smallbox.faraway.ui.engine.OnClickListener;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.view.UIFrame;
import org.smallbox.faraway.ui.engine.view.UILabel;
import org.smallbox.faraway.ui.engine.view.View;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;

import java.util.ArrayList;

public abstract class BasePanel extends UIFrame implements LayoutFactory.OnLayoutLoaded, GameObserver {
    protected static final int 	LINE_HEIGHT = 20;
	protected static final int 	FONT_SIZE_TITLE = 22;
	protected static final int 	FONT_SIZE = 14;
	protected static final int 	NB_COLUMNS = Constant.NB_COLUMNS;
	protected static final int 	NB_COLUMNS_TITLE = Constant.NB_COLUMNS_TITLE;

    protected UserInterface 		_ui;
	protected Mode 					_mode;
	private boolean 				_alwaysVisible;
	private GameEventListener.Key 	_shortcut;
	protected UserInteraction 		_interaction;
	protected boolean 				_isVisible;
	private boolean 				_isLoaded;
    private final String            _layoutPath;
	private int 					_nbDraw;
	private long 					_totalDrawTime;
	private int 					_nbRefresh;
	private long 					_totalRefreshTime;
    protected int 					_debugIndex;

	public void			toogle() { _isVisible = !_isVisible; }
	public void			open() { _isVisible = true; }
	public void			close() { _isVisible = false; }
	public boolean		isOpen() { return _isVisible; }

	public BasePanel(Mode mode, GameEventListener.Key shortcut, int x, int y, int width, int height, String layoutPath) {
        _layoutPath = layoutPath;
        _shortcut = shortcut;
		_mode = mode;
		_isVisible = false;
        _views = new ArrayList<>();
        setSize(width, height);
        setPosition(x, y);
	}

	public BasePanel(Mode mode, GameEventListener.Key shortcut, String layoutPath) {
        _layoutPath = layoutPath;
        _shortcut = shortcut;
		_mode = mode;
		_isVisible = false;
        _views = new ArrayList<>();
	}

//    public void setSize(int width, int height) {
//        super.setSize(width * GameData.config.resolution[0] / Constant.BASE_WIDTH, height * GameData.config.resolution[1] / Constant.BASE_HEIGHT);
//    }

    protected void setAlwaysVisible(boolean alwaysVisible) {
		_alwaysVisible = alwaysVisible;
		_isVisible = alwaysVisible;
	}

	public void init(ViewFactory viewFactory, LayoutFactory layoutFactory, UserInterface ui, UserInteraction interaction) {
		removeAllViews();
		_ui = ui;
		_interaction = interaction;
		onCreate(viewFactory);
        if (_layoutPath != null) {
            layoutFactory.load(_layoutPath, this, this);
        }
	}
	
	public boolean	checkKey(GameEventListener.Key key) {
		if (_isVisible) {
			return onKey(key);
		}
		return false;
	}

	protected boolean onKey(GameEventListener.Key key) {
		return false;
	}
	
	public void setUI(UserInterface ui) {
		_ui = ui;
	}

	public boolean	onMouseMove(int x, int y) {
		if (_isVisible && x > _x && x < _x + 800 && y > _y && y < _y + 600) {
			return true;
		}

		return false;
	}

	public boolean	catchClick(int x, int y) {
		if (_isVisible && x > _x && x < _x + _width && y > _y && y < _y + _height) {
			return true;
		}
		return false;
	}

	public boolean	mouseRelease(GameEventListener.MouseButton button, int x, int y) {
		return _isVisible;
	}
	
	public void refresh(int update) {
		if (_isVisible) {
			_debugIndex = 0;
			long time = System.currentTimeMillis();
			onRefresh(update);
			_totalRefreshTime += (System.currentTimeMillis() - time);
			_nbRefresh++;
		}
	}

	protected void addDebugView(UIFrame frame, String text) {
		addDebugView(frame, text, null);
	}

	protected void addDebugView(UIFrame frame, String text, OnClickListener clickListener) {
		UILabel lbCommand = ViewFactory.getInstance().createTextView();
		lbCommand.setText(text);
		lbCommand.setTextSize(14);
		lbCommand.setPosition(6, 38 + 20 * _debugIndex++);
		lbCommand.setSize(230, 20);
		lbCommand.setTextAlign(Align.CENTER_VERTICAL);
		lbCommand.setOnClickListener(clickListener);
		frame.addView(lbCommand);
	}

	protected void onRefresh(int update) {
	}

	public Mode getMode() {
		return _mode;
	}

	public boolean isAlwaysVisible() {
		return _alwaysVisible;
	}

	public boolean drawCursor() {
		return false;
	}

	public void setVisible(boolean visible) {
		_isVisible = visible;

		if (visible) {
			onOpen();
		} else {
			onClose();
		}
	}

	protected void onClose() {
	}

	protected void onOpen() {
	}

	public GameEventListener.Key getShortcut() {
		return _shortcut;
	}

    public boolean isVisible() {
        return _isVisible;
    }

    public void draw(GDXRenderer renderer, int x, int y) {
        if (_isVisible) {
			long time = System.currentTimeMillis();

			if (_backgroundColor != null) {
                renderer.draw(_backgroundColor, _x, _y, _width, _height);
            }

            for (View view : _views) {
                view.draw(renderer, x, y);
            }

            onDraw(renderer, null);

			_totalDrawTime += (System.currentTimeMillis() - time);
			_nbDraw++;
		}
    }

    protected void onDraw(GDXRenderer renderer, Viewport viewport) {
	}

    @Override
    public int getContentWidth() {
        return _width;
    }

    public void reload() {
    }

    @Override
    public int getContentHeight() {
        return _height;
    }

	public void setLoaded() {
		_isLoaded = true;
	}

	public boolean isLoaded() {
		return _isLoaded;
	}

    protected void onCreate(ViewFactory factory) {
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout, UIFrame panel) {
    }

	public boolean onMouseEvent(GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y) {
		return false;
	}

	public void dump() {
		if (_nbRefresh != 0) {
			Log.notice("Manager: " + this.getClass().getSimpleName() + ",\trefresh: " + _nbRefresh + ",\tavg time: " + _totalRefreshTime / _nbRefresh);
		}
		if (_nbDraw != 0) {
			Log.notice("Manager: " + this.getClass().getSimpleName() + ",\tdraw: " + _nbDraw + ",\tavg time: " + _totalDrawTime / _nbDraw);
		}
	}
}
