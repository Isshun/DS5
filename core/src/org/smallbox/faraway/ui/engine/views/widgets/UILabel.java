package org.smallbox.faraway.ui.engine.views.widgets;

import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.util.StringUtils;

public class UILabel extends View {
    public static final int    REGULAR = 0;
    public static final int    BOLD = 1;
    public static final int    ITALIC = 2;
    public static final int    UNDERLINED = 3;
    private int _hash1;
    private int _hash2;
    private int _hash3;
    private int _hash4;

    private String                              _string;
    private int                                 _textSize = 14;
    private com.badlogic.gdx.graphics.Color     _gdxTextColor;
    private Color                               _textColor;
    private int                                 _maxLength;

    public UILabel(ModuleBase module) {
        super(module);
    }

    public void setMaxLength(int maxLength) {
        _maxLength = maxLength;
    }

    public View setSize(int width, int height) {
        return super.setSize(width, height != -1 ? height : 18);
    }

    public UILabel setText(String string) {
        if (string == null) {
            setStringValue("");
            return this;
        }

        int hash = string.hashCode();
        if (hash != _hash1) {
            setStringValue(Data.getData() != null && Data.getData().hasString(hash) ? Data.getData().getString(hash) : string);
        }
        return this;
    }

    public void setText(String str1, String str2) {
        if (str1 == null) { str1 = ""; }
        if (str2 == null) { str2 = ""; }

        int hash1 = str1.hashCode();
        int hash2 = str2.hashCode();
        if (hash1 != _hash1 || hash2 != _hash2) {
            str1 = Data.getData() != null && Data.getData().hasString(hash1) ? Data.getData().getString(hash1) : str1;
            str2 = Data.getData() != null && Data.getData().hasString(hash2) ? Data.getData().getString(hash2) : str2;
            setStringValue(str1 + str2);
        }
    }

    public void setText(String str1, String str2, int int3) {
        setText(str1, str2, String.valueOf(int3));
    }

    public void setText(String str1, String str2, String str3) {
        if (str1 == null) { str1 = ""; }
        if (str2 == null) { str2 = ""; }
        if (str3 == null) { str3 = ""; }

        int hash1 = str1.hashCode();
        int hash2 = str2.hashCode();
        int hash3 = str3.hashCode();
        if (hash1 != _hash1 || hash2 != _hash2 || hash3 != _hash3) {
            str1 = Data.getData() != null && Data.getData().hasString(hash1) ? Data.getData().getString(hash1) : str1;
            str2 = Data.getData() != null && Data.getData().hasString(hash2) ? Data.getData().getString(hash2) : str2;
            str3 = Data.getData() != null && Data.getData().hasString(hash3) ? Data.getData().getString(hash3) : str3;
            setStringValue(str1 + str2 + str3);
        }
    }

    public void setText(String str1, String str2, String str3, String str4) {
        if (str1 == null) { str1 = ""; }
        if (str2 == null) { str2 = ""; }
        if (str3 == null) { str3 = ""; }
        if (str4 == null) { str4 = ""; }

        int hash1 = str1.hashCode();
        int hash2 = str2.hashCode();
        int hash3 = str3.hashCode();
        int hash4 = str4.hashCode();
        if (hash1 != _hash1 || hash2 != _hash2 || hash3 != _hash3 || hash4 != _hash4) {
            str1 = Data.getData() != null && Data.getData().hasString(hash1) ? Data.getData().getString(hash1) : str1;
            str2 = Data.getData() != null && Data.getData().hasString(hash2) ? Data.getData().getString(hash2) : str2;
            str3 = Data.getData() != null && Data.getData().hasString(hash3) ? Data.getData().getString(hash3) : str3;
            str4 = Data.getData() != null && Data.getData().hasString(hash4) ? Data.getData().getString(hash4) : str4;
            setStringValue(str1 + str2 + str3 + str4);
        }
    }

    public void setText(String string, int value) {
        if (string == null) {
            setStringValue("");
            return;
        }

        // TODO
        int hash = string.hashCode();
//        if (hash != _hash1) {
            string = Data.getData() != null && Data.getData().hasString(hash) ? Data.getData().getString(hash) : string;
        setStringValue(String.format(string, value));
//        }
    }

//    public void setText(String string, String value) {
//        if (string == null) {
//            setStringValue("");
//            return;
//        }
//
//        // TODO
//        int hash = string.hashCode();
////        if (hash != _hash1) {
//            string = Data.getData() != null && Data.getData().hasString(hash) ? Data.getData().getString(hash) : string;
//            setStringValue(String.format(string, value));
////        }
//    }

    public void setTextColor(Color color) {
        if (color != null && _textColor != color) {
            _gdxTextColor = new com.badlogic.gdx.graphics.Color(color.r / 255f, color.g / 255f, color.b / 255f, color.a / 255f);
        }
        _textColor = color;
    }

    public void setTextColor(int color) {
        _textColor = new Color(color);
        _gdxTextColor = new com.badlogic.gdx.graphics.Color(_textColor.r / 255f, _textColor.g / 255f, _textColor.b / 255f, _textColor.a / 255f);
    }

    public void setStringValue(String string) {
        if (_maxLength > 0) {
            int index = string.indexOf(' ', _maxLength / 2);
            if (index != -1) {
                string = string.substring(0, index) + '\n' + string.substring(index+1);
            }
        }
        _string = string;
    }

    public void setTextSize(int size) {
        _textSize = size;
        if (_height == -1) {
            _height = (int)(GDXRenderer.getInstance().getFont(_textSize).getLineHeight() * 1.2);
        }
    }

    public void setStyle(int style) {
    }


    public Color getColor() {
        return _textColor;
    }

    public void setDashedString(String label, String value, int nbColumns) {
        _string = StringUtils.getDashedString(label, value, nbColumns);
    }

    @Override
    public String getString() {
        return _string;
    }

    public String getText() {
        return _string;
    }

    @Override
    public void init() {
    }

    public View findById(String string) {
        return null;
    }

    @Override
    public void draw(GDXRenderer renderer, int x, int y) {
        super.draw(renderer, x, y);

        if (_isVisible) {
            if (true) {
                if (_align == Align.CENTER) {
                    _offsetX = (_width - getContentWidth()) / 2;
                    _offsetY = (_height - getContentHeight()) / 2;
                }

                if (_align == Align.CENTER_VERTICAL) {
                    _offsetY = (_height - getContentHeight()) / 2;
                }
            }

            renderer.draw(_string, _textSize,
                    getAlignedX() + x + _offsetX + _paddingLeft + _marginLeft,
                    getAlignedY() + y + _offsetY + _paddingTop + _marginTop,
                    _gdxTextColor);
        }
    }

    @Override
    public void addView(View view) {
    }

    @Override
    public int getContentWidth() {
        if (_string != null) {
//            return (int) GDXRenderer.getInstance().getFont(_textSize).getBounds(_string).width;
            return (int) (_string.length() * GDXRenderer.getInstance().getFont(_textSize).getSpaceWidth());
        }
        return 0;
    }

    @Override
    public int getContentHeight() {
        if (_string != null) {
//            return (int) GDXRenderer.getInstance().getFont(_textSize).getBounds(_string).height;
            return (int) GDXRenderer.getInstance().getFont(_textSize).getLineHeight();
        }
        return 0;
    }

    public static UILabel create(ModuleBase module) {
        return new UILabel(module);
    }
}
