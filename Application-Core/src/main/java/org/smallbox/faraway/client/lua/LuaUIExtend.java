package org.smallbox.faraway.client.lua;

import org.apache.commons.lang3.StringUtils;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.smallbox.faraway.client.ClientLuaModuleManager;
import org.smallbox.faraway.client.RotateAnimation;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.OnFocusListener;
import org.smallbox.faraway.client.ui.engine.views.*;
import org.smallbox.faraway.client.ui.engine.views.widgets.*;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.util.log.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LuaUIExtend extends LuaExtend {

    private final UIManager uiManager;
    private final ClientLuaModuleManager clientLuaModuleManager;
    private final LuaControllerManager luaControllerManager;

    public LuaUIExtend() {
        uiManager = DependencyManager.getInstance().getDependency(UIManager.class);
        clientLuaModuleManager = DependencyManager.getInstance().getDependency(ClientLuaModuleManager.class);
        luaControllerManager = DependencyManager.getInstance().getDependency(LuaControllerManager.class);
    }

    @Override
    public boolean accept(String type) {
        switch (type) {
            case "view":
            case "list":
            case "grid":
            case "label":
            case "minimap":
            case "image":
            case "dropdown":
                return true;
        }
        return false;
    }

//    public RootView debug(Globals globals, LuaValue value) {
//        String rootName =
//                StringUtils.isNotBlank(getString(value, "name", null)) ? getString(value, "name", null) :
//                        StringUtils.isNotBlank(getString(value, "id", null)) ? getString(value, "id", null) : "";
//
//        View view = createView(null, globals, value, true, 0, null, rootName, 1);
//
//        RootView rootView = new RootView();
//        rootView.setView(view);
//
//        return rootView;
//    }

    @Override
    public void extend(Data data, ModuleBase module, Globals globals, LuaValue value, File dataDirectory) {
        String rootName =
                StringUtils.isNotBlank(getString(value, "name", null)) ? getString(value, "name", null) :
                        StringUtils.isNotBlank(getString(value, "id", null)) ? getString(value, "id", null) : "";

//        if (value.get("debug").isnil()) {
//            return;
//        }

        if (uiManager.getRootViews().stream().anyMatch(rootView -> rootView.getView().getName().equals(rootName))) {
            return;
        }

        if (uiManager.getSubViews().stream().anyMatch(subView -> subView.getName().equals(rootName))) {
            return;
        }

//        boolean inGame = getBoolean(value, "in_game", true);
//        UIFrame frame = new UIFrame(module);
//        frame.setInGame(inGame);

        boolean isGameView = !rootName.startsWith("base.ui.menu.");

        View view = createView(module, globals, value, true, 0, null, rootName, 1, isGameView);

        RootView rootView = new RootView();
        rootView.setView((CompositeView) view);

//        frame.addView(view);
//        frame.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        frame.setLevel(getInt(value, "level", 0));
//        frame.setLayer(getInt(value, "layer", 0));
//        frame.setName(view.getName());
//        frame.setVisible(true);

        if (value.get("parent").isnil() && rootName.startsWith("base.ui.menu.")) {
            if (!value.get("controller").isnil()) {
                try {
                    rootView.getView().setController((LuaController) DependencyManager.getInstance().getDependency(Class.forName(value.get("controller").tojstring())));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            uiManager.addMenuView(rootView);
        } else if (value.get("parent").isnil()) {
            uiManager.addRootView(rootView);
        } else {
            uiManager.addSubView(view, value.get("parent").tojstring());
        }
    }

    private void readViewMandatory(ModuleBase module, Globals globals, LuaValue value, boolean inGame, int deep, CompositeView parent, View view) {
        view.setParent(parent);
        view.setDeep(deep);
        view.setInGame(inGame);
        view.setFocusable(getBoolean(value, "focusable", false));
        view.setActive(getBoolean(value, "active", true));

        readString(value, "id", v -> {
            view.setId(v);
            view.setName(v);
            LuaStyleManager.getInstance().applyStyleFromId(v, view);
        });

        readString(value, "name", v -> {
            view.setId(v);
            view.setName(v);
            Log.debug("Deprecated parameter: " + v);
        });

        readString(value, "action", view::setActionName);
        readInt(value, "layer", view::setLayer);
        readString(value, "group", view::setGroup);
        readBoolean(value, "visible", view::setVisible, true);

        CompositeView.instanceOf(view).ifPresent(compositeView -> readBoolean(value, "sorted", compositeView::setSorted));
    }

    private void customizeViewCosmetic(ModuleBase module, Globals globals, LuaValue value, boolean inGame, int deep, View parent, View view) {
        readSpecificUILabel(value, view);
        readSpecificUICheckBox(value, view);
        readSpecificUIImage(value, view);

        readLua(value, "align", v -> view.setAlign(VerticalAlign.valueOf(v.get(1).toString().toUpperCase()), HorizontalAlign.valueOf(v.get(2).toString().toUpperCase())));
        readLua(value, "effects", v -> view.setEffect(new FadeEffect(getInt(v, "duration", 0))));
        readLua(value, "animations", v -> view.setAnimation(new RotateAnimation(getInt(v, "duration", 0))));
        readInt(value, "border", v -> view.getStyle().setBorderColor(v));

        readLua(value, "background", v -> {
            if (v.istable()) {
                readLong(v, "regular", view::setRegularBackgroundColor, -1);
                if (view.getRegularBackground() != -1) {
                    view.getStyle().setBackgroundColor(view.getRegularBackground());
                }

                readInt(v, "focus", view::setFocusBackgroundColor, -1);
                if (view.getFocusBackground() != -1) {
                    view.getEvents().setOnFocusListener(new OnFocusListener() {
                        @Override
                        public void onEnter(View view) {
                            view.getStyle().setBackgroundColor(view.getFocusBackground());
                        }

                        @Override
                        public void onExit(View view) {
                            view.getStyle().setBackgroundColor(view.getRegularBackground());
                        }
                    });
                }
            } else {
                view.getStyle().setBackgroundColor(v.toint());
            }
        });

        readLua(value, "padding", v -> {
            if (!v.istable()) view.getGeometry().setPadding(v.toint(), v.toint(), v.toint(), v.toint());
            else if (v.length() == 4) view.getGeometry().setPadding(v.get(1).toint(), v.get(2).toint(), v.get(3).toint(), v.get(4).toint());
            else if (v.length() == 2) view.getGeometry().setPadding(v.get(1).toint(), v.get(2).toint(), v.get(1).toint(), v.get(2).toint());
            else if (v.length() == 1) view.getGeometry().setPadding(v.toint(), v.toint(), v.toint(), v.toint());
        });

    }

    private void readEvents(ModuleBase module, Globals globals, LuaValue value, boolean inGame, int deep, View parent, View view) {
        LuaValue luaView = CoerceJavaToLua.coerce(view);

        LuaValue onFocus = value.get("on_focus");
        if (!onFocus.isnil()) {
            int regularBackground = value.get("background").toint();
            int focusBackground = onFocus.get("background").toint();
            view.getEvents().setOnFocusListener(new OnFocusListener() {
                @Override
                public void onEnter(View view) {
                    view.getStyle().setBackgroundColor(focusBackground);
                }

                @Override
                public void onExit(View view) {
                    view.getStyle().setBackgroundColor(regularBackground);
                }
            });
        }

        LuaValue onClick = value.get("on_click");
        if (!onClick.isnil()) {
            view.getEvents().setOnClickListener((int x, int y) -> {
                try {
                    if (onClick.isfunction()) {
                        onClick.call(luaView);
                    } else if (onClick.isstring()) {
                        globals.load(onClick.tojstring()).call(luaView);
                    }
                } catch (LuaError e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public View createView(ModuleBase module, Globals globals, LuaValue value, boolean inGame, int deep, CompositeView parent, String path, int index, boolean isGameView) {
        return createView(module, globals, value, inGame, deep, parent, path, index, isGameView, true);
    }

    public View createView(ModuleBase module, Globals globals, LuaValue value, boolean inGame, int deep, CompositeView parent, String path, int index, boolean isGameView, boolean runAfter) {

        // Create view for type
        View view = createViewFromType(module, value);

        view.setPath(path);
        view.setIndex(index);
        view.setGameView(isGameView);

        // Add mandatory value
        readViewMandatory(module, globals, value, inGame, deep, parent, view);
        readString(value, "style", styleName -> applyStyle(module, globals, value, inGame, deep, parent, view, styleName));
        readBoolean(value, "special", view::setSpecial);
        customizeViewCosmetic(module, globals, value, inGame, deep, parent, view);
        readGeometry(module, globals, value, inGame, deep, parent, view);
        readEvents(module, globals, value, inGame, deep, parent, view);
        readTemplate(module, globals, value, inGame, deep, view, path, isGameView);

        // Add subviews
        readTable(value, "views", (subValue, i) -> ((CompositeView) view).addView(
                createView(module, globals, subValue, inGame, deep + 1, (CompositeView) view, path + "." + i, i, isGameView, runAfter)
        ));

        // Set controller
        readString(value, "controller", controllerName -> luaControllerManager.setControllerView(controllerName, (CompositeView) view));

        return view;
    }

    private void readSpecificUILabel(LuaValue value, View view) {
        if (view instanceof UILabel) {
            UILabel label = (UILabel) view;
            readString(value, "text", label::setText);
            readInt(value, "text_size", label::setTextSize);
            readInt(value, "text_color", label::setTextColor);
            // TODO
            //readString(value, "text_align", label::setTextAlign);
        }
    }
    private void readSpecificUICheckBox(LuaValue value, View view) {
        if (view instanceof UICheckBox) {
            UICheckBox checkBox = (UICheckBox) view;
            readString(value, "text", checkBox::setText);
            readInt(value, "text_size", checkBox::setTextSize);
            readInt(value, "text_color", checkBox::setTextColor);
        }
    }

    private void readSpecificUIImage(LuaValue value, View view) {
        if (view instanceof UIImage) {
            UIImage imageView = (UIImage) view;
            readString(value, "src", imageView::setImage);
            readLua(value, "texture_rect", v -> imageView.setTextureRect(v.get(1).toint(), v.get(2).toint(), v.get(3).toint(), v.get(4).toint()));
        }
    }

    /**
     * Read template from lua.
     * When template contains several views, they are encapsulated in UIList
     */
    private void readTemplate(ModuleBase module, Globals globals, LuaValue value, boolean inGame, int deep, View view, String path, boolean isGameView) {
        if (!value.get("template").isnil()) {
            ((CompositeView) view).setTemplate(() -> {
                List<View> templateViews = new ArrayList<>();
                readTable(value, "template", (subValue, i) -> templateViews.add(
                        createView(module, globals, subValue, inGame, deep + 1, (CompositeView) view, path + "." + i, i, isGameView, false)
                ));

                if (templateViews.size() == 1) {
                    return templateViews.get(0);
                }

                if (templateViews.size() > 1) {
                    return new UIList(module, templateViews);
                }

                return null;
            });
        }
    }

    private View createViewFromType(ModuleBase module, LuaValue value) {
        switch (value.get("type").toString()) {
            case "view":
                return new UIFrame(module);
            case "minimap":
                return createMinimapView(module, value);
            case "dropdown":
                return createDropDownView(module, value);
            case "checkbox":
                return new UICheckBox(module);
            case "list":
                return new UIList(module);
            case "grid":
                return createGridView(module, value);
            case "image":
                return new UIImage(module);
            case "label":
                return new UILabel(module);
        }

        throw new GameException(LuaUIExtend.class, "Unknown view type: " + value.get("type").toString());
    }

    private View createDropDownView(ModuleBase module, LuaValue value) {
        View view = new UIDropDown(module);
        uiManager.addDropsDowns((UIDropDown) view);
        return view;
    }

    private View createMinimapView(ModuleBase module, LuaValue value) {
        return new View(module) {

            @Override
            public void draw(GDXRenderer renderer, int x, int y) {
                if (_isVisible) {
                    geometry.setFinalX(getAlignedX() + geometry.getMarginLeft() + x);
                    geometry.setFinalY(geometry.getY() + geometry.getMarginTop() + y);
                }
            }

            @Override
            public int getContentWidth() {
                return 0;
            }

            @Override
            public int getContentHeight() {
                return 0;
            }

        };
    }

    private View createGridView(ModuleBase module, LuaValue value) {
        UIGrid grid = new UIGrid(module);

        readInt(value, "columns", grid::setColumns);
        readInt(value, "row_height", grid::setRowHeight);
        readInt(value, "column_width", grid::setColumnWidth);

        return grid;
    }

    private void applyStyle(ModuleBase module, Globals globals, LuaValue value, boolean inGame, int deep, View parent, View view, String styleName) {
        value = uiManager.getStyle(styleName);
        Log.warning("Unable to find style: " + styleName);
        if (value != null) {
            customizeViewCosmetic(module, globals, value, inGame, deep, parent, view);
            readGeometry(module, globals, value, inGame, deep, parent, view);
        }
    }

    private void readGeometry(ModuleBase module, Globals globals, LuaValue value, boolean inGame, int deep, View parent, View view) {
        readLua(value, "size", v -> view.setSize(v.get(1).toint(), v.get(2).toint()));
        readLua(value, "size", v -> view.getGeometry().setFixedSize(v.get(1).toint(), v.get(2).toint()));
        readLua(value, "position", v -> view.setPosition(v.get(1).toint(), v.get(2).toint()));

        readLua(value, "margin", v -> {
            if (v.length() == 4) {
                view.getGeometry().setMargin(v.get(1).toint(), v.get(2).toint(), v.get(3).toint(), v.get(4).toint());
            }
            if (v.length() == 2) {
                view.getGeometry().setMargin(v.get(1).toint(), v.get(2).toint(), v.get(1).toint(), v.get(2).toint());
            }
            if (v.length() == 1) {
                view.getGeometry().setMargin(v.toint(), v.toint(), v.toint(), v.toint());
            }
        });
    }
}