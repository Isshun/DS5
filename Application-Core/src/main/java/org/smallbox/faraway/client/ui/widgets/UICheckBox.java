package org.smallbox.faraway.client.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.ui.event.OnCheckListener;
import org.smallbox.faraway.client.ui.extra.Align;
import org.smallbox.faraway.core.module.ModuleBase;

public class UICheckBox extends View {
    public static final int    REGULAR = 0;
    public static final int    BOLD = 1;
    public static final int    ITALIC = 2;
    public static final int    UNDERLINED = 3;
    private int _hash1;
    private int _hash2;
    private int _hash3;
    private int _hash4;

    private String      _string;
    private int         _textSize = 14;
    private Color       _textColor;
    private int         _maxLength;
    private boolean     _checked;

    public UICheckBox(ModuleBase module) {
        super(module);
    }

    public UICheckBox setChecked(boolean checked) { _checked = checked; return this; }

    public UICheckBox setOnCheckListener(OnCheckListener onCheckListener) {
        events.setOnClickListener(() -> {
            _checked = !_checked;
//            onCheckListener.onCheck(_checked, x < geometry.getFinalX() + 32);
        });
        return this;
    }

    public void setMaxLength(int maxLength) {
        _maxLength = maxLength;
    }

    public View setSize(int width, int height) {
        return super.setSize(width, height != -1 ? height : 18);
    }

    public UICheckBox setText(String string) {
        if (string == null) {
            setStringValue("");
            return this;
        }

        int hash = string.hashCode();
        if (hash != _hash1) {
            setStringValue(dataManager != null && dataManager.hasString(hash) ? dataManager.getString(hash) : string);
        }
        return this;
    }

    public void setText(String str1, String str2) {
        if (str1 == null) { str1 = ""; }
        if (str2 == null) { str2 = ""; }

        int hash1 = str1.hashCode();
        int hash2 = str2.hashCode();
        if (hash1 != _hash1 || hash2 != _hash2) {
            str1 = dataManager != null && dataManager.hasString(hash1) ? dataManager.getString(hash1) : str1;
            str2 = dataManager != null && dataManager.hasString(hash2) ? dataManager.getString(hash2) : str2;
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
            str1 = dataManager != null && dataManager.hasString(hash1) ? dataManager.getString(hash1) : str1;
            str2 = dataManager != null && dataManager.hasString(hash2) ? dataManager.getString(hash2) : str2;
            str3 = dataManager != null && dataManager.hasString(hash3) ? dataManager.getString(hash3) : str3;
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
            str1 = dataManager != null && dataManager.hasString(hash1) ? dataManager.getString(hash1) : str1;
            str2 = dataManager != null && dataManager.hasString(hash2) ? dataManager.getString(hash2) : str2;
            str3 = dataManager != null && dataManager.hasString(hash3) ? dataManager.getString(hash3) : str3;
            str4 = dataManager != null && dataManager.hasString(hash4) ? dataManager.getString(hash4) : str4;
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
            string = dataManager != null && dataManager.hasString(hash) ? dataManager.getString(hash) : string;
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
//            string = _applicationData != null && _applicationData.hasString(hash) ? _applicationData.getString(hash) : string;
//            setStringValue(String.format(string, value));
////        }
//    }

    public UICheckBox setTextColor(Color color) {
        _textColor = color;
        return this;
    }

    public UICheckBox setTextColor(int color) {
        _textColor = new Color(color);
        return this;
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
        if (getHeight() == -1) {
            geometry.setHeight((int)(fontManager.getFont(_textSize).getLineHeight() * 1.2));
        }
    }

    public void setStyle(int style) {
    }


    public Color getColor() {
        return _textColor;
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
    public void draw(BaseRenderer renderer, int x, int y) {
        super.draw(renderer, x, y);

        if (_isVisible) {
            if (_align == Align.CENTER) {
                geometry.setOffsetX((getWidth() - getContentWidth()) / 2);
                geometry.setOffsetY((getHeight() - getContentHeight()) / 2);
            }

            if (_align == Align.CENTER_VERTICAL) {
                geometry.setOffsetY((getHeight() - getContentHeight()) / 2);
            }

            renderer.drawText(getAlignedX() + x + geometry.getOffsetX() + geometry.getPaddingLeft() + geometry.getMarginLeft(), getAlignedY() + y + geometry.getOffsetY() + geometry.getPaddingTop() + geometry.getMarginTop(), _checked ? "[x]" : "[ ]", _textColor, _textSize);
            renderer.drawText(getAlignedX() + x + geometry.getOffsetX() + geometry.getPaddingLeft() + geometry.getMarginLeft() + 32, getAlignedY() + y + geometry.getOffsetY() + geometry.getPaddingTop() + geometry.getMarginTop(), _string, _textColor, _textSize);
        }
    }

    @Override
    public int getContentWidth() {
        if (_string != null) {
//            return (int) gdxRenderer.getFont(_textSize).getBounds(_string).width;
            return (int) (_string.length() * fontManager.getFont(_textSize).getRegion().getRegionWidth());
        }
        return 0;
    }

    @Override
    public int getContentHeight() {
        if (_string != null) {
//            return (int) gdxRenderer.getFont(_textSize).getBounds(_string).height;
            return (int) fontManager.getFont(_textSize).getLineHeight();
        }
        return 0;
    }

    public static UICheckBox create(ModuleBase module) {
        return new UICheckBox(module);
    }
}
