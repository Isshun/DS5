package org.smallbox.faraway;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.smallbox.faraway.core.ui.GDXFrameLayout;
import org.smallbox.faraway.core.ui.GDXImageView;
import org.smallbox.faraway.core.ui.GDXLabel;
import org.smallbox.faraway.ui.UIGrid;
import org.smallbox.faraway.ui.UIList;
import org.smallbox.faraway.ui.engine.UIEventManager;
import org.smallbox.faraway.ui.engine.view.UIImage;
import org.smallbox.faraway.ui.engine.view.UILabel;
import org.smallbox.faraway.ui.engine.view.View;

/**
 * Created by Alex on 28/09/2015.
 */
public class LuaLayoutFactory {
    public static View createView(LuaModuleManager luaModuleManager, Globals globals, LuaValue value) {
        View view = null;

        int width = -1, height = -1;
        LuaValue size = value.get("size");
        if (!size.isnil()) {
            width = size.get(1).toint();
            height = size.get(2).toint();
        }

        switch (value.get("type").toString()) {
            case "view":
                view = new GDXFrameLayout(width, height);
                break;

            case "list":
                view = new UIList(width, height);
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

                grid.setSize(grid.getColumns() * grid.getColumnWidth(), grid.getHeight());

                view = grid;
                break;

            case "image":
                UIImage image = new GDXImageView(width, height);

                LuaValue src = value.get("src");
                if (!src.isnil()) {
                    image.setImagePath(src.toString());
                }

                LuaValue textureRect = value.get("texture_rect");
                if (!textureRect.isnil()) {
                    image.setTextureRect(textureRect.get(1).toint(), textureRect.get(2).toint(), textureRect.get(3).toint(), textureRect.get(4).toint());
                }

                view = image;
                break;

            case "label":
                if (height == -1) {
                    height = 20;
                }

                UILabel label = new GDXLabel(width, height);

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
            LuaValue id = value.get("id");
            if (!id.isnil()) {
                view.setId(id.toString().hashCode());
            }

            LuaValue visible = value.get("visible");
            if (!visible.isnil()) {
                view.setVisible(visible.toboolean());
            } else {
                view.setVisible(true);
            }

            LuaValue position = value.get("position");
            if (!position.isnil()) {
                view.setPosition(position.get(1).toint(), position.get(2).toint());
            }

            LuaValue background = value.get("background");
            if (!background.isnil()) {
                view.setBackgroundColor(background.toint());
            }

            LuaValue padding = value.get("padding");
            if (!padding.isnil()) {
                view.setPadding(padding.toint(), padding.toint());
            }

            LuaValue onClick = value.get("on_click");
            if (!onClick.isnil()) {
                UIEventManager.getInstance().setOnClickListener(view, v -> {
                    try {
                        if (onClick.isfunction()) {
                            onClick.call();
                        } else if (onClick.isstring()) {
                            globals.load(onClick.tojstring()).call();
                        }
                    } catch (LuaError e) {
                        e.printStackTrace();
                    }
                });
            }

            LuaValue onEvent = value.get("on_event");
            if (!onEvent.isnil()) {
                LuaValue luaView = CoerceJavaToLua.coerce(view);
                luaModuleManager.addLuaEventListener((event, luaData) -> {
                    try {
                        onEvent.call(LuaValue.valueOf(event), luaView, luaData);
                    } catch (LuaError e) {
                        e.printStackTrace();
                    }
                });
            }

            LuaValue onLoad = value.get("on_load");
            if (!onLoad.isnil()) {
                LuaValue luaView = CoerceJavaToLua.coerce(view);
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
                LuaValue luaView = CoerceJavaToLua.coerce(view);
                luaModuleManager.addLuaRefreshListener(() -> {
                    try {
                        onRefresh.call(luaView);
                    } catch (LuaError e) {
                        e.printStackTrace();
                    }
                });
            }

            LuaValue subViews = value.get("views");
            if (!subViews.isnil()) {
                for (int i = 1; i <= subViews.length(); i++) {
                    view.addView(LuaLayoutFactory.createView(luaModuleManager, globals, subViews.get(i)));
                }
            }

            view.resetSize();
        }

        return view;
    }
}
