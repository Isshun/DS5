package org.smallbox.faraway.core.engine.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.module.lua.LuaModule;
import org.smallbox.faraway.core.engine.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.game.model.ObjectModel;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.OnFocusListener;
import org.smallbox.faraway.ui.engine.views.UIAdapter;
import org.smallbox.faraway.ui.engine.views.widgets.*;

import java.util.Collection;
import java.util.Collections;

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
            case "map":
            case "image":
            case "dropdown":
                return true;
        }
        return false;
    }

    @Override
    public void extend(LuaModuleManager luaModuleManager, LuaModule module, Globals globals, LuaValue value) {
        boolean inGame = getBoolean(value, "in_game", true);
        UIFrame frame = new UIFrame(-1, -1);
        frame.setInGame(inGame);
        frame.addView(createView(luaModuleManager, globals, value, inGame, 0));
        frame.setModule(module);
        frame.setLevel(getInt(value, "level", 0));
        UserInterface.getInstance()._views.add(frame);
        Collections.sort(UserInterface.getInstance()._views, (v1, v2) -> v1.getLevel() - v2.getLevel());
    }

    public View createView(LuaModuleManager luaModuleManager, Globals globals, LuaValue value, boolean inGame, int deep) {
        View view = null;

        int width = -1, height = -1;
        LuaValue size = value.get("size");
        if (!size.isnil()) {
            width = size.get(1).toint();
            height = size.get(2).toint();
        }

        switch (value.get("type").toString()) {
            case "view":
                view = new UIFrame(width, height);
                break;

            case "dropdown":
                view = new UIDropDown(width, height);
                UserInterface.getInstance()._dropsDowns.add((UIDropDown)view);
                break;

            case "list":
                view = new UIList(width, height);
                if (width != -1 && height != -1) {
                    view.setFixedSize(width, height);
                }
                break;

            case "grid":
                UIGrid grid = new UIGrid(width, height);

                LuaValue columns = value.get("columns");
                if (!columns.isnil()) {
                    grid.setColumns(columns.toint());
                }

                LuaValue rowHeight = value.get("row_height");
                if (!rowHeight.isnil()) {
                    grid.setRowHeight(rowHeight.toint());
                }

                LuaValue columnWidth = value.get("column_width");
                if (!columnWidth.isnil()) {
                    grid.setColumnWidth(columnWidth.toint());
                }

                if (width != -1 && height != -1) {
                    grid.setFixedSize(width, height);
                    grid.setSize(width, height);
                } else {
                    grid.setSize(grid.getColumns() * grid.getColumnWidth(), grid.getHeight());
                }

                view = grid;
                break;

            case "image":
                UIImage image = new UIImage(width, height);

                LuaValue src = value.get("src");
                if (!src.isnil()) {
                    image.setImage(src.toString());
                }

                LuaValue textureRect = value.get("texture_rect");
                if (!textureRect.isnil()) {
                    image.setTextureRect(textureRect.get(1).toint(), textureRect.get(2).toint(), textureRect.get(3).toint(), textureRect.get(4).toint());
                }

                view = image;
                break;

            case "label":
                UILabel label = new UILabel(width, height);

                LuaValue text = value.get("text");
                if (!text.isnil()) {
                    label.setText(text.toString());
                }

                LuaValue textSize = value.get("text_size");
                if (!textSize.isnil()) {
                    label.setTextSize(textSize.toint());
                }

                LuaValue textColor = value.get("text_color");
                if (!textColor.isnil()) {
                    label.setTextColor(textColor.toint());
                }

                view = label;
                break;
        }

        if (view != null) {
            LuaValue luaView = CoerceJavaToLua.coerce(view);

            view.setDeep(deep);
            view.setInGame(inGame);
            view.setFocusable(getBoolean(value, "focusable", false));
            view.setActive(getBoolean(value, "active", true));

            LuaValue id = value.get("id");
            if (!id.isnil()) {
                view.setId(id.toString().hashCode());
                view.setName(id.toString());
            }

            LuaValue name = value.get("name");
            if (!name.isnil()) {
                view.setId(name.toString().hashCode());
                view.setName(name.toString());
            }

            LuaValue align = value.get("align");
            if (!align.isnil()) {
                view.setAlign(
                        View.VerticalAlign.valueOf(align.get(1).toString().toUpperCase()),
                        View.HorizontalAlign.valueOf(align.get(2).toString().toUpperCase()));
                view.setName(name.toString());
            } else {
                view.setAlign(View.VerticalAlign.TOP, View.HorizontalAlign.LEFT);
            }

            LuaValue style = value.get("style");
            if (!style.isnil()) {
                applyStyle(view, style.toString());
            }

            LuaValue visible = value.get("visible");
            if (!visible.isnil()) {
                view.setVisible(visible.toboolean());
            } else {
                view.setVisible(true);
            }

            LuaValue effect = value.get("effects");
            if (!effect.isnil()) {
                view.setEffect(new FadeEffect(getInt(effect, "duration", 0)));
            }

            LuaValue animation = value.get("animations");
            if (!animation.isnil()) {
                view.setAnimation(new RotateAnimation(getInt(animation, "duration", 0)));
            }

            LuaValue margin = value.get("margin");
            if (!margin.isnil()) {
                if (margin.length() == 4) {
                    view.setMargin(margin.get(1).toint(), margin.get(2).toint(), margin.get(3).toint(), margin.get(4).toint());
                }
                if (margin.length() == 2) {
                    view.setMargin(margin.get(1).toint(), margin.get(2).toint(), margin.get(1).toint(), margin.get(2).toint());
                }
                if (margin.length() == 1) {
                    view.setMargin(margin.toint(), margin.toint(), margin.toint(), margin.toint());
                }
            }

            LuaValue position = value.get("position");
            if (!position.isnil()) {
                view.setPosition(position.get(1).toint(), position.get(2).toint());
            }

            LuaValue background = value.get("background");
            if (!background.isnil()) {
                if (background.istable()) {
                    view.setRegularBackgroundColor(getInt(background, "regular", -1));
                    view.setFocusBackgroundColor(getInt(background, "focus", -1));
                    if (view.getRegularBackground() != -1) {
                        view.setBackgroundColor(view.getRegularBackground());
                    }
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
                    view.setBackgroundColor(background.toint());
                }
            }

            LuaValue padding = value.get("padding");
            if (!padding.isnil()) {
                view.setPadding(padding.toint(), padding.toint());
            }

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
                Collection<ObjectModel> data = (Collection) CoerceLuaToJava.coerce(adapter.get("data"), Collection.class);
                view.setAdapter(new UIAdapter(data, new UIAdapter.OnCreateView() {
                    @Override
                    public View onCreateView() {
                        return createView(luaModuleManager, globals, subview, inGame, deep + 1);
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
                view.setPadding(padding.toint(), padding.toint());
            }

            LuaValue onClick = value.get("on_click");
            if (!onClick.isnil()) {
                view.setOnClickListener(() -> {
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
                luaModuleManager.addLuaEventListener((event, luaTag, luaData) -> {
                    try {
                        onEvent.call(luaView, luaTag.isnil() ? LuaValue.valueOf(event) : luaTag, luaData);
                    } catch (LuaError e) {
                        e.printStackTrace();
                    }
                }, view.inGame());
            }

            LuaValue onLoad = value.get("on_load");
            if (!onLoad.isnil()) {
                luaModuleManager.addLuaLoadListener(() -> {
                    try {
                        onLoad.call(luaView);
                    } catch (LuaError e) {
                        e.printStackTrace();
                    }
                });
            }

            LuaValue onRefresh = value.get("on_refresh");
            if (!onRefresh.isnil()) {
                final View finalView = view;
                luaModuleManager.addLuaRefreshListener((frame) -> {
                    if (finalView.isVisible()) {
                        try {
                            onRefresh.call(luaView, LuaInteger.valueOf(frame));
                        } catch (LuaError e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            LuaValue subViews = value.get("views");
            if (!subViews.isnil()) {
                if (subViews.get("type").isnil()) {
                    for (int i = 1; i <= subViews.length(); i++) {
                        view.addView(createView(luaModuleManager, globals, subViews.get(i), inGame, deep + 1));
                    }
                } else {
                    view.addView(createView(luaModuleManager, globals, subViews, inGame, deep + 1));
                }
            }
        }

        return view;
    }

    private void applyStyle(View view, String styleName) {
        view.setAlign(View.VerticalAlign.TOP, View.HorizontalAlign.RIGHT);
        view.setPosition(372, 38);
        view.setSize(372, Application.getInstance().getConfig().screen.resolution[1]);
//        if (width != -1 && height != -1) {
            view.setFixedSize(372, Application.getInstance().getConfig().screen.resolution[1]);
//        }
        view.setBackgroundColor(0x121c1e);
    }
}