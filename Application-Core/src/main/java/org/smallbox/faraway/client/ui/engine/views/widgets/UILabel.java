package org.smallbox.faraway.client.ui.engine.views.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.client.render.layer.GDXRenderer;
import org.smallbox.faraway.core.engine.ColorUtils;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.util.StringUtils;

public class UILabel extends View {
    public static final int    REGULAR = 0;
    public static final int    BOLD = 1;
    public static final int    ITALIC = 2;
    public static final int    UNDERLINED = 3;
    private int _hash1;
    private int _hash2;
    private int _hash3;
    private int _hash4;

    private String      _text = "";
    private int         _textSize = 14;
    private Color       _textColor = Color.BLACK;
    private int         _maxLength;

    public UILabel(ModuleBase module) {
        super(module);
    }

    public void setMaxLength(int maxLength) {
        _maxLength = maxLength;
    }

    public View setSize(int width, int height) {
        return super.setSize(width, height != -1 ? height : 18);
    }

    @Override
    public int hashCode() {
        return _text.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return _text.equals(object);
    }

    public UILabel setText(String string) {
        if (string == null) {
            setStringValue("");
            return this;
        }

        int hash = string.hashCode();
        if (hash != _hash1) {
            setStringValue(applicationData != null && applicationData.hasString(hash) ? applicationData.getString(hash) : string);
        }
        return this;
    }

    public void setText(String str1, String str2) {
        if (str1 == null) { str1 = ""; }
        if (str2 == null) { str2 = ""; }

        int hash1 = str1.hashCode();
        int hash2 = str2.hashCode();
        if (hash1 != _hash1 || hash2 != _hash2) {
            str1 = applicationData != null && applicationData.hasString(hash1) ? applicationData.getString(hash1) : str1;
            str2 = applicationData != null && applicationData.hasString(hash2) ? applicationData.getString(hash2) : str2;
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
            str1 = applicationData != null && applicationData.hasString(hash1) ? applicationData.getString(hash1) : str1;
            str2 = applicationData != null && applicationData.hasString(hash2) ? applicationData.getString(hash2) : str2;
            str3 = applicationData != null && applicationData.hasString(hash3) ? applicationData.getString(hash3) : str3;
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
            str1 = applicationData != null && applicationData.hasString(hash1) ? applicationData.getString(hash1) : str1;
            str2 = applicationData != null && applicationData.hasString(hash2) ? applicationData.getString(hash2) : str2;
            str3 = applicationData != null && applicationData.hasString(hash3) ? applicationData.getString(hash3) : str3;
            str4 = applicationData != null && applicationData.hasString(hash4) ? applicationData.getString(hash4) : str4;
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
        string = applicationData != null && applicationData.hasString(hash) ? applicationData.getString(hash) : string;
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

    public UILabel setTextColor(Color color) {
        _textColor = color;
        return this;
    }

    public UILabel setTextColor(long color) {
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
        _text = string;
    }

    public UILabel setTextSize(int size) {
        _textSize = size;
        if (_height == -1) {
            _height = (int)(gdxRenderer.getFont(_textSize).getLineHeight() * 1.2);
        }
        return this;
    }

    public void setStyle(int style) {
    }


    public Color getColor() {
        return _textColor;
    }

    public UILabel setDashedString(String label, String value, int nbColumns) {
        _text = StringUtils.getDashedString(label, value, nbColumns);
        return this;
    }

    @Override
    public String getString() {
        return _text;
    }

    public String getText() {
        return _text;
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

            if (_align == Align.RIGHT) {
                _offsetX = (_width - getContentWidth());
            }

            if (_align == Align.CENTER_VERTICAL) {
                _offsetY = (_height - getContentHeight()) / 2;
            }

//            renderer.drawPixel(getAlignedX() + x + _offsetX + _paddingLeft + _marginLeft, getAlignedY() + y + _offsetY + _paddingTop + _marginTop, _textSize, _gdxTextColor, _text);
            renderer.drawFont((batch, font) -> {
                int finalX = getAlignedX() + x + _offsetX + _paddingLeft + _marginLeft;
                int finalY = getAlignedY() + y + _offsetY + _paddingTop + _marginTop;

                int tagOffsetX = 0;

                if (_text.contains("{")) {

                    boolean inTag = false;
                    boolean inTagMeta = false;
                    StringBuilder sb = new StringBuilder();
                    StringBuilder sbTag = new StringBuilder();
                    StringBuilder sbTagMeta = new StringBuilder();
                    for (int i = 0; i < _text.length(); i++) {
                        if (_text.charAt(i) == '{') {
                            inTagMeta = true;
                        }

                        else if (_text.charAt(i) == '}') {
                            inTag = false;

                            if (sbTagMeta.toString().contains("icon")) {
                                Sprite sprite = spriteManager.getIcon("[base]/res/ic_blueprint.png");
                                sprite.setPosition(finalX - 8, finalY - 8);
                                sprite.draw(batch);
                                tagOffsetX += 32;
                                sb.append("   ");
                            }

                            if (sbTagMeta.toString().contains("blue")) {
                                font.setColor(com.badlogic.gdx.graphics.Color.BLUE);
                            }

                            if (sbTagMeta.toString().contains("red")) {
                                font.setColor(com.badlogic.gdx.graphics.Color.RED);
                            }

                            if (sbTag != null) {
                                font.draw(batch, sbTag.toString(), finalX + tagOffsetX, finalY);
                                sbTag = null;
                            }
                        }

                        else if (inTagMeta && _text.charAt(i) == ';') {
                            inTag = true;
                            inTagMeta = false;
                            sbTag = new StringBuilder();
                        }

                        else if (inTagMeta) {
                            sbTagMeta.append(_text.charAt(i));
                        }

                        else if (inTag) {
                            sb.append(' ');
                            sbTag.append(_text.charAt(i));
                        }

                        else {
                            sb.append(_text.charAt(i));
                        }
                    }

                    font.setColor(_textColor);
                    font.draw(batch, sb.toString(), finalX, finalY);
                }

                else {
                    font.setColor(_textColor);
                    font.draw(batch, _text, finalX, finalY);
                }
            }, _textSize);
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
        if (_text != null) {
//            return (int) ApplicationClient.gdxRenderer.getFont(_textSize).getBounds(_text).width;
            return (int) (_text.length() * gdxRenderer.getFont(_textSize).getRegion().getRegionWidth());
        }
        return 0;
    }

    @Override
    public int getContentHeight() {
        if (_text != null) {
//            return (int) ApplicationClient.gdxRenderer.getFont(_textSize).getBounds(_text).height;
            return (int) gdxRenderer.getFont(_textSize).getLineHeight();
        }
        return 0;
    }

    public static UILabel create(ModuleBase module) {
        UILabel label = new UILabel(module);
        label.setSize(100, 32);
        return label;
    }

    public static UILabel createFast(String text, Color textColor) {
        return create(null)
                .setText(text)
                .setTextColor(textColor);
    }

    @Override
    public String toString() { return _text; }

}
