package org.smallbox.faraway.client.lua.extend;

import org.apache.commons.lang3.StringUtils;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.smallbox.faraway.client.ClientLuaModuleManager;
import org.smallbox.faraway.client.RotateAnimation;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.lua.LuaControllerManager;
import org.smallbox.faraway.client.lua.LuaStyleManager;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.OnFocusListener;
import org.smallbox.faraway.client.ui.engine.views.*;
import org.smallbox.faraway.client.ui.engine.views.widgets.FadeEffect;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.util.log.Log;

import java.io.File;

public abstract class LuaUIExtend extends LuaExtend {

    @Inject
    protected UIManager uiManager;

    @Inject
    protected ClientLuaModuleManager clientLuaModuleManager;

    @Inject
    protected LuaControllerManager luaControllerManager;

    @Inject
    protected LuaStyleManager luaStyleManager;

    protected abstract void readSpecific(LuaValue value, View view);

    protected abstract View createViewFromType(ModuleBase module, LuaValue value);

    public abstract boolean accept(String type);

    @Override
    public void extend(Data data, ModuleBase module, Globals globals, LuaValue value, File dataDirectory) {
        String rootName =
                StringUtils.isNotBlank(getString(value, "name", null)) ? getString(value, "name", null) :
                        StringUtils.isNotBlank(getString(value, "id", null)) ? getString(value, "id", null) : "";

        if (uiManager.getRootViews().stream().anyMatch(rootView -> rootView.getView().getName().equals(rootName))) {
            return;
        }

        if (uiManager.getSubViews().stream().anyMatch(subView -> subView.getName().equals(rootName))) {
            return;
        }

        boolean isGameView = !rootName.startsWith("base.ui.menu.");

        View view = createView(module, globals, value, true, 0, null, rootName, 1, isGameView);

        RootView rootView = new RootView();
        rootView.setView((CompositeView) view);

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

    private void readViewMandatory(LuaValue value, boolean inGame, int deep, CompositeView parent, View view) {
        view.setParent(parent);
        view.setDeep(deep);
        view.setInGame(inGame);
        view.setFocusable(getBoolean(value, "focusable", false));
        view.setActive(getBoolean(value, "active", true));

        readString(value, "id", v -> {
            view.setId(v);
            view.setName(v);
            luaStyleManager.applyStyleFromId(v, view);
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

    void customizeViewCosmetic(LuaValue value, View view) {
        readSpecific(value, view);

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

    }

    private void readEvents(Globals globals, LuaValue value, View view) {
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

    public View createSubView(ModuleBase module, Globals globals, LuaValue value, boolean inGame, int deep, CompositeView parent, String path, int index, boolean isGameView, boolean runAfter) {
        return clientLuaModuleManager.createView(module, globals, value, inGame, deep, parent, path, index, isGameView, runAfter);
    }

    public View createView(ModuleBase module, Globals globals, LuaValue value, boolean inGame, int deep, CompositeView parent, String path, int index, boolean isGameView, boolean runAfter) {

        // Create view for type
        View view = createViewFromType(module, value);

        view.setPath(path);
        view.setIndex(index);
        view.setGameView(isGameView);

        // Add mandatory value
        readViewMandatory(value, inGame, deep, parent, view);
        readBoolean(value, "special", view::setSpecial);
        customizeViewCosmetic(value, view);
        readGeometry(value, view);
        readEvents(globals, value, view);
        readTemplate(module, globals, value, inGame, deep, view, path, isGameView);

        // Add subviews
        readTable(value, "views", (subValue, i) -> ((CompositeView) view).addView(
                createSubView(module, globals, subValue, inGame, deep + 1, (CompositeView) view, path + "." + i, i, isGameView, runAfter)
        ));

        // Set controller
        readString(value, "controller", controllerName -> luaControllerManager.setControllerView(controllerName, (CompositeView) view));

        readString(value, "style", styleName -> applyStyle(view, styleName));

        return view;
    }

    private void applyStyle(View view, String styleName) {
        LuaValue value = uiManager.getStyle(styleName);
        Log.warning("Unable to find style: " + styleName);
        if (value != null) {
            customizeViewCosmetic(value, view);
            readGeometry(value, view);
        }
    }

    protected void readTemplate(ModuleBase module, Globals globals, LuaValue value, boolean inGame, int deep, View view, String path, boolean isGameView) {
    }

    private void readGeometry(LuaValue value, View view) {
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

        readLua(value, "padding", v -> {
            if (!v.istable()) view.getGeometry().setPadding(v.toint(), v.toint(), v.toint(), v.toint());
            else if (v.length() == 4) view.getGeometry().setPadding(v.get(1).toint(), v.get(2).toint(), v.get(3).toint(), v.get(4).toint());
            else if (v.length() == 2) view.getGeometry().setPadding(v.get(1).toint(), v.get(2).toint(), v.get(1).toint(), v.get(2).toint());
            else if (v.length() == 1) view.getGeometry().setPadding(v.toint(), v.toint(), v.toint(), v.toint());
        });

    }
}