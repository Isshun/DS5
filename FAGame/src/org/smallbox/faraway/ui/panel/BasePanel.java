package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.engine.*;
import org.smallbox.faraway.ui.engine.ColorView;
import org.smallbox.faraway.ui.engine.FrameLayout;
import org.smallbox.faraway.ui.engine.View;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInteraction;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.UserInterface.Mode;

import java.util.ArrayList;

public abstract class BasePanel extends FrameLayout implements LayoutFactory.OnLayoutLoaded {
    protected static final int 	LINE_HEIGHT = 20;
	protected static final int 	FONT_SIZE_TITLE = 22;
	protected static final int 	FONT_SIZE = 14;
	protected static final int 	NB_COLUMNS = Constant.NB_COLUMNS;
	protected static final int 	NB_COLUMNS_TITLE = Constant.NB_COLUMNS_TITLE;

    protected UserInterface 		_ui;
	protected Mode 					_mode;
	protected RenderEffect 			_effect;
	private boolean 				_alwaysVisible;
	private GameEventListener.Key 	_shortcut;
	protected UserInteraction 		_interaction;
	private boolean 				_isVisible;
	private ColorView 				_background;
	private boolean 				_isLoaded;
    private final String            _layoutPath;

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

	public void setBackgroundColor(Color color) {
		if (color != null) {
			_background = ViewFactory.getInstance().createColorView(_width, _height);
			_background.setBackgroundColor(color);
			_background.setPosition(_x, _y);
		} else {
			_background = null;
		}
    }

    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        _effect = SpriteManager.getInstance().createRenderEffect();
        _effect.setTranslate(_x, _y);
        resetPos();
    }

    protected void setAlwaysVisible(boolean alwaysVisible) {
		_alwaysVisible = alwaysVisible;
		_isVisible = alwaysVisible;
	}

	public void init(ViewFactory viewFactory, LayoutFactory layoutFactory, UserInterface ui, UserInteraction interaction, RenderEffect effect) {
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
			onRefresh(update);
		}
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

    public void draw(GFXRenderer renderer, RenderEffect effect) {
        if (_isVisible) {
            if (_background != null) {
                _background.draw(renderer, _effect);
            }

            for (View view : _views) {
                view.draw(renderer, _effect);
            }

            onDraw(renderer, effect);
        }
    }

    @Override
    protected void onDraw(GFXRenderer renderer, RenderEffect effect) {
    }

    @Override
    protected void createRender() {

    }

    @Override
    public void refresh() {
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
    public void onLayoutLoaded(LayoutModel layout) {
    }

	public boolean onMouseEvent(GameTimer timer, GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y) {
		return false;
	}
}
