package org.smallbox.faraway.client.lua;

import com.badlogic.gdx.Gdx;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.FadeEffect;
import org.smallbox.faraway.client.RotateAnimation;
import org.smallbox.faraway.client.renderer.GDXRenderer;
import org.smallbox.faraway.client.ui.engine.OnFocusListener;
import org.smallbox.faraway.client.ui.engine.views.UIAdapter;
import org.smallbox.faraway.client.ui.engine.views.widgets.*;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.game.model.ObjectModel;
import org.smallbox.faraway.util.Log;

import java.io.File;
import java.util.Collection;

/**
 * Created by Alex on 29/09/2015.
 */
public class LuaUIExtend extends LuaExtend {
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

    @Override
    public void extend(ModuleBase module, Globals globals, LuaValue value, File dataDirectory) {
        boolean inGame = getBoolean(value, "in_game", true);
        UIFrame frame = new UIFrame(module);
        frame.setInGame(inGame);

        View view = createView(module, globals, value, inGame, 0, frame);

        frame.addView(view);
        frame.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        frame.setLevel(getInt(value, "level", 0));
        frame.setLayer(getInt(value, "layer", 0));
        ApplicationClient.uiManager.addRootView(frame);
    }

    private void customizeViewMandatory(ModuleBase module, Globals globals, LuaValue value, boolean inGame, int deep, View parent, View view) {
        view.setParent(parent);
        view.setDeep(deep);
        view.setInGame(inGame);
        view.setFocusable(getBoolean(value, "focusable", false));
        view.setActive(getBoolean(value, "active", true));

        readString(value, "id", v -> {
            view.setId(v.hashCode());
            view.setName(v);
            LuaStyleManager.getInstance().applyStyleFromId(v, view);
        });

        readString(value, "name", v -> {
            view.setId(v.hashCode());
            view.setName(v);
            Log.warning("Deprecated parameter: " + v);
        });

        readString(value, "action", view::setActionName);
        readInt(value, "layer", view::setLayer);
        readString(value, "group", view::setGroup);
        readBoolean(value, "visible", view::setVisible, true);
    }

    private void customizeViewCosmetic(ModuleBase module, Globals globals, LuaValue value, boolean inGame, int deep, View parent, View view) {

        // Label only
        if (view instanceof UILabel) {
            UILabel label = (UILabel) view;
            readString(value, "text", label::setText);
            readInt(value, "text_size", label::setTextSize);
            readInt(value, "text_color", label::setTextColor);
        }

        // Checkbox only
        if (view instanceof UICheckBox) {
            UICheckBox checkBox = (UICheckBox) view;
            readString(value, "text", checkBox::setText);
            readInt(value, "text_size", checkBox::setTextSize);
            readInt(value, "text_color", checkBox::setTextColor);
        }

        // Image only
        if (view instanceof UIImage) {
            UIImage imageView = (UIImage) view;
            readString(value, "src", imageView::setImage);
            readLua(value, "texture_rect", v -> imageView.setTextureRect(v.get(1).toint(), v.get(2).toint(), v.get(3).toint(), v.get(4).toint()));
        }

        readLua(value, "size", v -> {
            view.setSize(v.get(1).toint(), v.get(2).toint());
            view.setFixedSize(v.get(1).toint(), v.get(2).toint());
        });

        readLua(value, "align", v -> view.setAlign(
                View.VerticalAlign.valueOf(v.get(1).toString().toUpperCase()),
                View.HorizontalAlign.valueOf(v.get(2).toString().toUpperCase())));

        readLua(value, "effects", v -> view.setEffect(new FadeEffect(getInt(v, "duration", 0))));
        readLua(value, "animations", v -> view.setAnimation(new RotateAnimation(getInt(v, "duration", 0))));

        readLua(value, "margin", v -> {
            if (v.length() == 4) { view.setMargin(v.get(1).toint(), v.get(2).toint(), v.get(3).toint(), v.get(4).toint()); }
            if (v.length() == 2) { view.setMargin(v.get(1).toint(), v.get(2).toint(), v.get(1).toint(), v.get(2).toint()); }
            if (v.length() == 1) { view.setMargin(v.toint(), v.toint(), v.toint(), v.toint()); }
        });

        readLua(value, "position", v -> view.setPosition(v.get(1).toint(), v.get(2).toint()));

        readLua(value, "background", v -> {
            if (v.istable()) {
                readInt(v, "regular", view::setRegularBackgroundColor, -1);
                if (view.getRegularBackground() != -1) {
                    view.setBackgroundColor(view.getRegularBackground());
                }

                readInt(v, "focus", view::setFocusBackgroundColor, -1);
                if (view.getFocusBackground() != -1) {
                    view.setOnFocusListener(new OnFocusListener() {
                        @Override
                        public void onEnter(View view) {
                            view.setBackgroundColor(view.getFocusBackground());
                        }

                        @Override
                        public void onExit(View view) {
                            view.setBackgroundColor(view.getRegularBackground());
                        }
                    });
                }
            } else {
                view.setBackgroundColor(v.toint());
            }
        });

        readInt(value, "padding", v -> view.setPadding(v, v));
    }

    private void customizeViewListeners(ModuleBase module, Globals globals, LuaValue value, boolean inGame, int deep, View parent, View view) {
        LuaValue luaView = CoerceJavaToLua.coerce(view);

        LuaValue onFocus = value.get("on_focus");
        if (!onFocus.isnil()) {
            int regularBackground = value.get("background").toint();
            int focusBackground = onFocus.get("background").toint();
            view.setOnFocusListener(new OnFocusListener() {
                @Override
                public void onEnter(View view) {
                    view.setBackgroundColor(focusBackground);
                }

                @Override
                public void onExit(View view) {
                    view.setBackgroundColor(regularBackground);
                }
            });
        }

        LuaValue adapter = value.get("adapter");
        if (!adapter.isnil()) {
            LuaValue onBind = adapter.get("on_bind");
            LuaValue subview = adapter.get("view");
            Collection data = (Collection) CoerceLuaToJava.coerce(adapter.get("data"), Collection.class);
            final View finalView1 = view;
            view.setAdapter(new UIAdapter(data, new UIAdapter.OnCreateView() {
                @Override
                public View onCreateView() {
                    return createView(module, globals, subview, inGame, deep + 1, finalView1);
                }

                @Override
                public void onBindView(View subview, ObjectModel data) {
                    try {
                        onBind.call(CoerceJavaToLua.coerce(subview), CoerceJavaToLua.coerce(data));
                    } catch (LuaError e) {
                        e.printStackTrace();
                    }
                }
            }));
            readInt(value, "padding", v -> view.setPadding(v, v));
        }

        LuaValue onClick = value.get("on_click");
        if (!onClick.isnil()) {
            view.setOnClickListener((GameEvent event) -> {
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

        LuaValue onEvent = value.get("on_event");
        if (!onEvent.isnil()) {
            ApplicationClient.luaModuleManager.addLuaEventListener((event, luaTag, luaData) -> {
                try {
                    LuaValue ret = onEvent.call(luaView, luaTag.isnil() ? LuaValue.valueOf(event) : luaTag, luaData);
                    return !ret.isnil() && ret.toboolean();
                } catch (LuaError e) {
                    e.printStackTrace();
                }
                return false;
            }, view.inGame());
        }

        LuaValue onGameStart = value.get("on_game_start");
        if (!onGameStart.isnil()) {
            ApplicationClient.luaModuleManager.addLuaLoadListener(() -> {
                try {
                    onGameStart.call(luaView);
                } catch (LuaError e) {
                    e.printStackTrace();
                }
            });
        }

        LuaValue onRefresh = value.get("on_refresh");
        if (!onRefresh.isnil()) {
            final View finalView = view;
            ApplicationClient.luaModuleManager.addLuaRefreshListener((frame) -> {
                if (finalView.isVisible()) {
                    try {
                        onRefresh.call(luaView, LuaInteger.valueOf(frame));
                    } catch (LuaError e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public interface ReadCallback<T> {
        void onReadCallback(T value);
    }

    private void readInt(LuaValue value, String key, ReadCallback<Integer> callback, int... def) {
        LuaValue v = value.get(key);
        if (!v.isnil()) {
            callback.onReadCallback(v.toint());
        } else if (def.length > 0) {
            callback.onReadCallback(def[0]);
        }
    }

    private void readString(LuaValue value, String key, ReadCallback<String> callback) {
        LuaValue v = value.get(key);
        if (!v.isnil()) {
            callback.onReadCallback(v.tojstring());
        }
    }

    private void readBoolean(LuaValue value, String key, ReadCallback<Boolean> callback, boolean... def) {
        LuaValue v = value.get(key);
        if (!v.isnil()) {
            callback.onReadCallback(v.toboolean());
        } else if (def.length > 0) {
            callback.onReadCallback(def[0]);
        }
    }

    private void readLua(LuaValue value, String key, ReadCallback<LuaValue> callback) {
        LuaValue v = value.get(key);
        if (!v.isnil()) {
            callback.onReadCallback(v);
        }
    }

    public View createView(ModuleBase module, Globals globals, LuaValue value, boolean inGame, int deep, View parent) {

        // Create view for type
        View view = createViewFromType(module, value);

        // Add mandatory value
        customizeViewMandatory(module, globals, value, inGame, deep, parent, view);

        // Apply style
        ApplicationClient.luaModuleManager.runAfter(() -> readString(value, "style", styleName -> applyStyle(module, globals, value, inGame, deep, parent, view, styleName)));

        // Apply cosmetic value
        ApplicationClient.luaModuleManager.runAfter(() -> customizeViewCosmetic(module, globals, value, inGame, deep, parent, view));

        // Add listeners
        customizeViewListeners(module, globals, value, inGame, deep, parent, view);

        // Add subviews
        LuaValue subViews = value.get("views");
        if (!subViews.isnil()) {
            if (subViews.get("type").isnil()) {
                for (int i = 1; i <= subViews.length(); i++) {
                    view.addView(createView(module, globals, subViews.get(i), inGame, deep + 1, view));
                }
            } else {
                view.addView(createView(module, globals, subViews, inGame, deep + 1, view));
            }
        }

        // Set controller
        readString(value, "controller", v -> ApplicationClient.luaControllerManager.setControllerView(v, view));

        // Add to ui manager
        ApplicationClient.uiManager.addView(view);

        return view;
    }

    private View createViewFromType(ModuleBase module, LuaValue value) {
        switch (value.get("type").toString()) {
            case "view": return new UIFrame(module);
            case "minimap": return createMinimapView(module, value);
            case "dropdown": return createDropDownView(module, value);
            case "checkbox": return new UICheckBox(module);
            case "list": return new UIList(module);
            case "grid": return createGridView(module, value);
            case "image": return new UIImage(module);
            case "label": return new UILabel(module);
        }

        throw new RuntimeException("Unknown view type");
    }

    private View createDropDownView(ModuleBase module, LuaValue value) {
        View view = new UIDropDown(module);
        ApplicationClient.uiManager.addDropsDowns((UIDropDown)view);
        return view;
    }

    private View createMinimapView(ModuleBase module, LuaValue value) {
        return new View(module) {

            @Override
            public void draw(GDXRenderer renderer, int x, int y) {
                if (_isVisible) {
                    _finalX = getAlignedX() + _marginLeft + x;
                    _finalY = _y + _marginTop + y;
                }
            }

            @Override
            protected void onAddView(View view) {
            }

            @Override
            protected void onRemoveView(View view) {
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
        value = ApplicationClient.uiManager.getStyle(styleName);
        customizeViewCosmetic(module, globals, value, inGame, deep, parent, view);
    }
}