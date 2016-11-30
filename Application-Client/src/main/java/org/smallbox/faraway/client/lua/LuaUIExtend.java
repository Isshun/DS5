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
import org.smallbox.faraway.client.renderer.GDXRenderer;
import org.smallbox.faraway.client.ui.engine.OnFocusListener;
import org.smallbox.faraway.client.ui.engine.views.UIAdapter;
import org.smallbox.faraway.client.ui.engine.views.widgets.*;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.client.FadeEffect;
import org.smallbox.faraway.client.RotateAnimation;
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
        frame.addView(createView(ApplicationClient.luaModuleManager, module, globals, value, inGame, 0, frame));
        frame.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        frame.setLevel(getInt(value, "level", 0));
        ApplicationClient.uiManager.addRootView(frame);
    }

    public View createView(LuaModuleManager luaModuleManager, ModuleBase module, Globals globals, LuaValue value, boolean inGame, int deep, View parent) {
        View view = null;

        switch (value.get("type").toString()) {
            case "view":
                view = new UIFrame(module);
                break;

           case "minimap":
                view = new View(module) {

                    @Override
                    public void draw(GDXRenderer renderer, int x, int y) {
                        if (_isVisible) {
                            _finalX = getAlignedX() + _marginLeft + x;
                            _finalY = _y + _marginTop + y;

//                            if (Application.gameManager.isLoaded()) {
//                                MainRenderer.getInstance().getMinimapRender().draw(renderer, Application.gameManager.getGame().getViewport(), 0);
//                            }
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
                break;

            case "dropdown":
                view = new UIDropDown(module);
                ApplicationClient.uiManager.addDropsDowns((UIDropDown)view);
                break;

            case "list":
                view = new UIList(module);
                break;

            case "grid":
                UIGrid grid = new UIGrid(module);

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

                view = grid;
                break;

            case "image":
                UIImage image = new UIImage(module);

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
                UILabel label = new UILabel(module);

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

            int width = -1, height = -1;
            LuaValue size = value.get("size");
            if (!size.isnil()) {
                width = size.get(1).toint();
                height = size.get(2).toint();
                view.setFixedSize(width, height);
            }
            view.setSize(width, height);

            view.setParent(parent);
            view.setDeep(deep);
            view.setInGame(inGame);
            view.setFocusable(getBoolean(value, "focusable", false));
            view.setActive(getBoolean(value, "active", true));

            LuaValue id = value.get("id");
            if (!id.isnil()) {
                view.setId(id.toString().hashCode());
                view.setName(id.toString());
                LuaStyleManager.getInstance().applyStyleFromId(id.toString(), view);
            }

            LuaValue name = value.get("name");
            if (!name.isnil()) {
                view.setId(name.toString().hashCode());
                view.setName(name.toString());
                Log.warning("Deprecated parameter: " + name.toString());
            }

            LuaValue action = value.get("action");
            if (!action.isnil()) {
                view.setActionName(action.toString());
            }

            LuaValue layer = value.get("layer");
            if (!layer.isnil()) {
                view.setLayer(layer.toint());
            }

            LuaValue align = value.get("align");
            if (!align.isnil()) {
                view.setAlign(
                        View.VerticalAlign.valueOf(align.get(1).toString().toUpperCase()),
                        View.HorizontalAlign.valueOf(align.get(2).toString().toUpperCase()));
                view.setName(action.toString());
            } else {
                view.setAlign(View.VerticalAlign.TOP, View.HorizontalAlign.LEFT);
            }

            LuaValue style = value.get("style");
            if (!style.isnil()) {
                applyStyle(view, style.toString());
            }

            LuaValue group = value.get("group");
            if (!group.isnil()) {
                view.setGroup(group.toString());
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
                final View finalView1 = view;
                view.setAdapter(new UIAdapter(data, new UIAdapter.OnCreateView() {
                    @Override
                    public View onCreateView() {
                        return createView(luaModuleManager, module, globals, subview, inGame, deep + 1, finalView1);
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
                luaModuleManager.addLuaEventListener((event, luaTag, luaData) -> {
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
                luaModuleManager.addLuaLoadListener(() -> {
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
                        view.addView(createView(luaModuleManager, module, globals, subViews.get(i), inGame, deep + 1, view));
                    }
                } else {
                    view.addView(createView(luaModuleManager, module, globals, subViews, inGame, deep + 1, view));
                }
            }

            LuaValue controller = value.get("controller");
            if (!controller.isnil()) {
                LuaControllerManager.getInstance().setControllerView(controller.toString(), view);
            }
        }

        ApplicationClient.uiManager.addView(view);

        return view;
    }

    private void applyStyle(View view, String styleName) {
        view.setAlign(View.VerticalAlign.TOP, View.HorizontalAlign.RIGHT);
        view.setPosition(372, 38);
        view.setSize(372, Application.configurationManager.screen.resolution[1]);
//        if (width != -1 && height != -1) {
            view.setFixedSize(372, Application.configurationManager.screen.resolution[1]);
//        }
        view.setBackgroundColor(0x121c1e);
    }
}