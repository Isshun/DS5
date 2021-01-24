package org.smallbox.faraway.client.ui.engine.views;

import org.smallbox.faraway.client.font.FontManager;
import org.smallbox.faraway.client.RotateAnimation;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.render.GDXRendererBase;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.FadeEffect;
import org.smallbox.faraway.core.config.Config;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.game.Data;

public abstract class View implements Comparable<View> {
    public final static int FILL = -2;

    // Inject all dependency to view once for all, waiting for a clever solution
    protected final SpriteManager spriteManager = DependencyManager.getInstance().getDependency(SpriteManager.class);
    protected final UIEventManager uiEventManager = DependencyManager.getInstance().getDependency(UIEventManager.class);
    protected final GDXRenderer gdxRenderer = DependencyManager.getInstance().getDependency(GDXRenderer.class);
    protected final FontManager fontManager = DependencyManager.getInstance().getDependency(FontManager.class);
    protected final UIManager uiManager = DependencyManager.getInstance().getDependency(UIManager.class);
    protected final Data data = DependencyManager.getInstance().getDependency(Data.class);

    private final ModuleBase _module;
    private boolean _isFocus;
    private boolean _isActive = true;
    private Object _data;
    private int _layer;
    private HorizontalAlign _horizontalAlign = HorizontalAlign.LEFT;
    private VerticalAlign _verticalAlign = VerticalAlign.TOP;
    private String _group;
    private String _path;
    private int _index;
    private LuaController _controller;
    private boolean _isGameView;
    private String _id;

    protected boolean _isVisible = true;
    protected boolean _isAlignLeft = true;
    protected boolean _isAlignTop = true;
    protected CompositeView _parent;
    protected String _actionName;
    protected FadeEffect _effect;
    protected int _deep;
    protected ViewGeometry geometry = new ViewGeometry(this);
    protected ViewEvents events = new ViewEvents(this);
    protected ViewStyle style = new ViewStyle(this);
    protected Align _align = Align.TOP_LEFT;

    public ViewEvents getEvents() {
        return events;
    }

    public View(ModuleBase module) {
        _module = module;
    }

    public void setAlign(VerticalAlign verticalAlign, HorizontalAlign horizontalAlign) {
        _verticalAlign = verticalAlign;
        _horizontalAlign = horizontalAlign;
    }

    public void setDeep(int deep) {
        _deep = deep;
    }

    public int getDeep() {
        return _deep;
    }

    public String getGroup() {
        return _group;
    }

    public void setSpecial(boolean special) {
    }

    public void setPath(String path) {
        _path = path;
    }

    public String getPath() {
        return _path;
    }

    public void setIndex(int index) {
        _index = index;
    }

    public int getIndex() {
        return _index;
    }

    public void setController(LuaController controller) {
        _controller = controller;
    }

    public LuaController getController() {
        return _controller;
    }

    public void setGameView(boolean isGameView) {
        _isGameView = isGameView;
    }

    protected RotateAnimation _animation;
    protected boolean _focusable;
    private int _regularBackground;
    private int _focusBackground;

    public void setAnimation(RotateAnimation animation) {
        _animation = animation;
    }

    public void setFocusable(boolean focusable) {
        _focusable = focusable;
    }

    public boolean isFocus() {
        return _isFocus;
    }

    public boolean isVisible() {
        return _isVisible && (_parent == null || _parent.isVisible());
    }

    public boolean isActive() {
        return _isActive;
    }

    public void setId(String id) {
        _id = id;
    }

    public void setFocus(boolean focus) {
        _isFocus = focus;
    }

    public void setActive(boolean active) {
        _isActive = active;
    }

    public void setParent(CompositeView parent) {
        _parent = parent;
    }

    public void setInGame(boolean inGame) {
    }

    public void setVisible(boolean visible) {

//        // Masque les vues appartenemt au même groupe
//        if (visible && _group != null) {
//            ApplicationClient.uiManager.getViews().stream()
//                    .filter(view -> view != this && _group.equals(view.getGroup()))
//                    .forEach(view -> view.setVisible(false));
//        }

        if (_parent != null && _parent._special) {
            _parent.getViews().forEach(view -> view._isVisible = false);
//            _parent.setVisible(false);
        }

        // Set current view visible
        _isVisible = visible;
    }

    public void setGroup(String group) {
        _group = group;
    }

    public void setEffect(FadeEffect effect) {
        _effect = effect;
    }

    public View setRegularBackgroundColor(int regularBackground) {
        _regularBackground = regularBackground;
        return this;
    }

    public View setFocusBackgroundColor(int focusBackground) {
        _focusBackground = focusBackground;
        return this;
    }

    public void setActionName(String actionName) {
        _actionName = actionName;
    }

    public void setLayer(int layer) {
        _layer = layer;
    }

    public int getLayer() {
        return _layer;
    }

    public CompositeView getParent() {
        return _parent;
    }

    public String getId() {
        return _id;
    }

    public int getRegularBackground() {
        return _regularBackground;
    }

    public int getFocusBackground() {
        return _focusBackground;
    }

    public ModuleBase getModule() {
        return _module;
    }

    protected String getString() {
        return null;
    }

    public FadeEffect getEffect() {
        return _effect;
    }

    public String getActionName() {
        return _actionName;
    }

    @Override
    public int compareTo(View view) {
        return view.hashCode() - hashCode();
    }

    public void draw(GDXRendererBase renderer, int x, int y) {
        if (_isVisible) {
            geometry.setFinalX(getAlignedX() + geometry.getMarginLeft() + x);
            geometry.setFinalY(getAlignedY() + geometry.getMarginTop() + y);

            if (style._backgroundFocusColor != null && _isFocus) {
                renderer.drawPixelUI(geometry.getFinalX(), geometry.getFinalY(), getWidth(), getHeight(), style._backgroundFocusColor);
            } else if (style._backgroundColor != null) {
                renderer.drawPixelUI(geometry.getFinalX(), geometry.getFinalY(), getWidth(), getHeight(), style._backgroundColor);
            }

            if (style._borderColor != null) {
                renderer.drawRectangleUI(geometry.getFinalX(), geometry.getFinalY(), getWidth(), getHeight(), style._borderColor, false);
            }

            if (Config.onDebugView) {
                renderer.drawTextUI(
                        getAlignedX() + x + geometry.getOffsetX() + geometry.getPaddingLeft() + geometry.getMarginLeft(),
                        getAlignedY() + y + geometry.getOffsetY() + geometry.getPaddingTop() + geometry.getMarginTop(),
                        12,
                        com.badlogic.gdx.graphics.Color.CYAN,
                        _id);
            }
        }
    }

    protected void updateSize() {
    }

    public boolean contains(int x, int y) {
        return geometry.contains(x, y);
    }

    public View setSize(int width, int height) {
        geometry.setSize(width, height);

        if (width == FILL && _parent != null) {
            geometry.setWidth(_parent.getGeometry().getWidth());
        }

        if (height == FILL && _parent != null) {
            geometry.setHeight(_parent.getGeometry().getHeight());
        }

        return this;
    }

    public View setPosition(int x, int y) {
        geometry.setPosition(x, y);
        return this;
    }

    public Object getData() {
        return _data;
    }

    public void setData(Object data) {
        _data = data;
    }

    public void remove() {
        _parent = null;
        events.remove();
    }

    public abstract int getContentWidth();

    public abstract int getContentHeight();

    public void init() {
    }

    public ViewGeometry getGeometry() {
        return geometry;
    }

    protected int getAlignedX() {

        // Alignement par rapport au parent
        if (_parent != null) {
            if (_horizontalAlign == HorizontalAlign.CENTER) {
                return (_parent.getWidth() / 2) - (getWidth() / 2) + geometry.getX();
            }
            if (_horizontalAlign == HorizontalAlign.RIGHT) {
                return _parent.getWidth() - getWidth() - geometry.getX();
            }
        }

        // Alignement par rapport à l'écran
        else {
            if (_horizontalAlign == HorizontalAlign.CENTER) {
                return (gdxRenderer.getWidth() / 2) - (getWidth() / 2) + geometry.getX();
            }
            if (_horizontalAlign == HorizontalAlign.RIGHT) {
                return gdxRenderer.getWidth() - getWidth() - geometry.getX();
            }
        }

        return geometry.getX();
    }

    protected int getAlignedY() {

        // Alignement par rapport au parent
        if (_parent != null) {
            if (_verticalAlign == VerticalAlign.CENTER) {
                return (_parent.getHeight() / 2) - (getHeight() / 2) + geometry.getY();
            }
            if (_verticalAlign == VerticalAlign.BOTTOM) {
                return _parent.getHeight() - geometry.getY();
            }
        }

        // Alignement par rapport à l'écran
        else {
            if (_verticalAlign == VerticalAlign.CENTER) {
                return (gdxRenderer.getHeight() / 2) - (getHeight() / 2) + geometry.getY();
            }
            if (_verticalAlign == VerticalAlign.BOTTOM) {
                return gdxRenderer.getHeight() - geometry.getY();
            }
        }

        return geometry.getY();
    }

    @Override
    public String toString() {
        return _path;
    }

    public int getHeight() {
        return geometry.getHeight();
    }

    public int getWidth() {
        return geometry.getWidth();
    }

    public ViewStyle getStyle() {
        return style;
    }

}