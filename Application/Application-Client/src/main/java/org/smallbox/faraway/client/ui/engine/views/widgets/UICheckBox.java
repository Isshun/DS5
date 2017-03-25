package org.smallbox.faraway.client.ui.engine.views.widgets;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.render.layer.GDXRenderer;
import org.smallbox.faraway.core.engine.ColorUtils;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.util.StringUtils;

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
    private Value       _checked = Value.FALSE;

    public enum Value { FALSE, TRUE, PARTIAL }

    public UICheckBox(ModuleBase module) {
        super(module);
    }

    public interface OnCheckListener {
        void onCheck(Value checked);
    }

    public UICheckBox setChecked(Value checked) { _checked = checked; return this; }

    public UICheckBox setOnCheckListener(OnCheckListener onCheckListener) {
        setOnClickListener(event -> {
            _checked = _checked == Value.TRUE ? Value.FALSE : Value.TRUE;
            onCheckListener.onCheck(_checked);
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
            setStringValue(_applicationData != null && _applicationData.hasString(hash) ? _applicationData.getString(hash) : string);
        }
        return this;
    }

    public void setText(String str1, String str2) {
        if (str1 == null) { str1 = ""; }
        if (str2 == null) { str2 = ""; }

        int hash1 = str1.hashCode();
        int hash2 = str2.hashCode();
        if (hash1 != _hash1 || hash2 != _hash2) {
            str1 = _applicationData != null && _applicationData.hasString(hash1) ? _applicationData.getString(hash1) : str1;
            str2 = _applicationData != null && _applicationData.hasString(hash2) ? _applicationData.getString(hash2) : str2;
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
            str1 = _applicationData != null && _applicationData.hasString(hash1) ? _applicationData.getString(hash1) : str1;
            str2 = _applicationData != null && _applicationData.hasString(hash2) ? _applicationData.getString(hash2) : str2;
            str3 = _applicationData != null && _applicationData.hasString(hash3) ? _applicationData.getString(hash3) : str3;
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
            str1 = _applicationData != null && _applicationData.hasString(hash1) ? _applicationData.getString(hash1) : str1;
            str2 = _applicationData != null && _applicationData.hasString(hash2) ? _applicationData.getString(hash2) : str2;
            str3 = _applicationData != null && _applicationData.hasString(hash3) ? _applicationData.getString(hash3) : str3;
            str4 = _applicationData != null && _applicationData.hasString(hash4) ? _applicationData.getString(hash4) : str4;
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
            string = _applicationData != null && _applicationData.hasString(hash) ? _applicationData.getString(hash) : string;
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
        _textColor = ColorUtils.fromHex(color);
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

    public UICheckBox setTextSize(int size) {
        _textSize = size;
        if (_height == -1) {
            _height = (int)(ApplicationClient.gdxRenderer.getFont(_textSize).getLineHeight() * 1.2);
        }
        return this;
    }

    public void setStyle(int style) {
    }


    public Color getColor() {
        return _textColor;
    }

    public UICheckBox setDashedString(String label, String value, int nbColumns) {
        _string = StringUtils.getDashedString(label, value, nbColumns);
        return this;
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
            if (_align == Align.CENTER) {
                _offsetX = (_width - getContentWidth()) / 2;
                _offsetY = (_height - getContentHeight()) / 2;
            }

            if (_align == Align.CENTER_VERTICAL) {
                _offsetY = (_height - getContentHeight()) / 2;
            }

            renderer.drawText(getAlignedX() + x + _offsetX + _paddingLeft + _marginLeft, getAlignedY() + y + _offsetY + _paddingTop + _marginTop, _textSize, _textColor, _checked == Value.TRUE ? "[x]" : _checked == Value.FALSE ? "[ ]" : "[.]");
            renderer.drawText(getAlignedX() + x + _offsetX + _paddingLeft + _marginLeft + 32, getAlignedY() + y + _offsetY + _paddingTop + _marginTop, _textSize, _textColor, _string);
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
        if (_string != null) {
//            return (int) ApplicationClient.gdxRenderer.getFont(_textSize).getBounds(_string).width;
            return (int) (_string.length() * ApplicationClient.gdxRenderer.getFont(_textSize).getSpaceWidth());
        }
        return 0;
    }

    @Override
    public int getContentHeight() {
        if (_string != null) {
//            return (int) ApplicationClient.gdxRenderer.getFont(_textSize).getBounds(_string).height;
            return (int) ApplicationClient.gdxRenderer.getFont(_textSize).getLineHeight();
        }
        return 0;
    }

    public static UICheckBox create(ModuleBase module) {
        return new UICheckBox(module);
    }
}
