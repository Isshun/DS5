package org.smallbox.faraway.client.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.ui.extra.Align;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.util.StringUtils;

public class UILabel extends View {
    public static final int REGULAR = 0;
    public static final int BOLD = 1;
    public static final int ITALIC = 2;
    public static final int UNDERLINED = 3;
    private int _hash1;
    private int _hash2;
    private int _hash3;
    private int _hash4;

    private String _text = "";
    private int _textSize = 14;
    private Color _textColor = Color.BLACK;
    private int _maxLength;
    private boolean outlined;
    private int _textLength;
    private String font;
    private int shadow;
    private Color shadowColor = Color.BLACK;

    public int getShadow() {
        return shadow;
    }

    public UILabel(ModuleBase module) {
        super(module);
    }

    public void setMaxLength(int maxLength) {
        _maxLength = maxLength;
    }

    public int getTextSize() {
        return _textSize;
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
            setStringValue(dataManager != null && dataManager.hasString(hash) ? dataManager.getString(hash) : string);
        }
        return this;
    }

    public void setText(String str1, String str2) {
        if (str1 == null) {
            str1 = "";
        }
        if (str2 == null) {
            str2 = "";
        }

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
        if (str1 == null) {
            str1 = "";
        }
        if (str2 == null) {
            str2 = "";
        }
        if (str3 == null) {
            str3 = "";
        }

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
        if (str1 == null) {
            str1 = "";
        }
        if (str2 == null) {
            str2 = "";
        }
        if (str3 == null) {
            str3 = "";
        }
        if (str4 == null) {
            str4 = "";
        }

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

    public UILabel setTextColor(Color color) {
        _textColor = color;
        return this;
    }

    public UILabel setTextColor(int color) {
        _textColor = new Color(color);
        return this;
    }

    public void setTextLength(int length) {
        _textLength = length;
    }

    public void setStringValue(String string) {
        if (_maxLength > 0) {
            int index = string.indexOf(' ', _maxLength / 2);
            if (index != -1) {
                string = string.substring(0, index) + '\n' + string.substring(index + 1);
            }
        }

        if (string.length() < _textLength) {
            string = org.apache.commons.lang3.StringUtils.leftPad(string, _textLength);
        }

        _text = string;
    }

    private int lineHeight;

    public UILabel setTextSize(int size) {
        _textSize = size;
        lineHeight = (int) fontManager.getFont(_textSize).getLineHeight();
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
    public void draw(BaseRenderer renderer, int x, int y) {
        super.draw(renderer, x, y);

        if (_isVisible) {

            GlyphLayout glyphLayout = new GlyphLayout();
            glyphLayout.setText(fontManager.getFont(font, _textSize), _text);

//            if (_align == Align.RIGHT) {
//                geometry.setOffsetX((int) (getWidth() / 2 - glyphLayout.width / 2));
//            }

            if (_align == Align.LEFT || _align == Align.CENTER || _align == Align.RIGHT || _align == Align.CENTER_VERTICAL) {
                geometry.setOffsetY((int) ((getHeight() - glyphLayout.height) / 2));
            }

            if (_align == Align.BOTTOM_LEFT || _align == Align.BOTTOM_CENTER || _align == Align.BOTTOM_RIGHT) {
                geometry.setOffsetY((int) (getHeight() - glyphLayout.height));
            }

            if (_align == Align.TOP_CENTER || _align == Align.CENTER || _align == Align.BOTTOM_CENTER || _align == Align.CENTER_HORIZONTAL) {
                geometry.setOffsetX((int) (getWidth() / 2 - glyphLayout.width / 2));
            }

            if (_align == Align.TOP_RIGHT || _align == Align.RIGHT || _align == Align.BOTTOM_RIGHT) {
                geometry.setOffsetX((int) (getWidth() - glyphLayout.width));
            }

//            renderer.drawPixel(getAlignedX() + x + _offsetX + _paddingLeft + _marginLeft, getAlignedY() + y + _offsetY + _paddingTop + _marginTop, _textSize, _gdxTextColor, _text);
            renderer.drawText((batch, font) -> {
                int finalX = getAlignedX() + x + geometry.getOffsetX() + geometry.getPaddingLeft() + geometry.getMarginLeft();
                int finalY = getAlignedY() + y + geometry.getOffsetY() + geometry.getPaddingTop() + geometry.getMarginTop();

                int tagOffsetX = 0;

                if (_text.contains("{")) {
//                    resolveTag();
                } else {
                    if (shadow != 0) {
                        font.setColor(shadowColor);
                        font.draw(batch, _text, finalX + shadow, finalY + shadow);
                    }
                    font.setColor(_textColor);
                    font.draw(batch, _text, finalX, finalY);
                }
            }, _textSize, outlined, font);
        }
    }
//
//    private void resolveTag() {
//        boolean inTag = false;
//        boolean inTagMeta = false;
//        StringBuilder sb = new StringBuilder();
//        StringBuilder sbTag = new StringBuilder();
//        StringBuilder sbTagMeta = new StringBuilder();
//        for (int i = 0; i < _text.length(); i++) {
//            if (_text.charAt(i) == '{') {
//                inTagMeta = true;
//            } else if (_text.charAt(i) == '}') {
//                inTag = false;
//
//                if (sbTagMeta.toString().contains("icon")) {
//                    Sprite sprite = spriteManager.getIcon("[base]/res/ic_blueprint.png");
//                    sprite.setPosition(finalX - 8, finalY - 8);
//                    sprite.draw(batch);
//                    tagOffsetX += 32;
//                    sb.append("   ");
//                }
//
//                if (sbTagMeta.toString().contains("blue")) {
//                    font.setColor(com.badlogic.gdx.graphics.Color.BLUE);
//                }
//
//                if (sbTagMeta.toString().contains("red")) {
//                    font.setColor(com.badlogic.gdx.graphics.Color.RED);
//                }
//
//                if (sbTag != null) {
//                    font.draw(batch, sbTag.toString(), finalX + tagOffsetX, finalY);
//                    sbTag = null;
//                }
//            } else if (inTagMeta && _text.charAt(i) == ';') {
//                inTag = true;
//                inTagMeta = false;
//                sbTag = new StringBuilder();
//            } else if (inTagMeta) {
//                sbTagMeta.append(_text.charAt(i));
//            } else if (inTag) {
//                sb.append(' ');
//                sbTag.append(_text.charAt(i));
//            } else {
//                sb.append(_text.charAt(i));
//            }
//        }
//
//        font.setColor(_textColor);
//        font.draw(batch, sb.toString(), finalX, finalY);
//    }

    @Override
    public int getContentWidth() {
        if (_text != null) {
//            return (int) ApplicationClient.fontGenerator.getFont(_textSize).getBounds(_text).width;
//            return (int) (_text.length() * fontManager.getFont(font, _textSize).getRegion().getRegionWidth()) + geometry.getPaddingLeft() + geometry.getPaddingRight();
        }
        return 0;
    }

    @Override
    public int getContentHeight() {
        if (_text != null) {
//            return (int) ApplicationClient.fontGenerator.getFont(_textSize).getBounds(_text).height;
            return (int) fontManager.getFont(_textSize).getLineHeight() + geometry.getPaddingTop() + geometry.getPaddingBottom();
        }
        return 0;
    }

    @Override
    public int getHeight() {
        return super.getHeight() != -1 ? super.getHeight() : lineHeight + geometry.getPaddingTop() + geometry.getPaddingBottom();
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
    public String toString() {
        return _text;
    }

    public void setTextAlign(boolean isAlignLeft, boolean isAlignTop) {
        _isAlignLeft = isAlignLeft;
        _isAlignTop = isAlignTop;
    }

    public void setTextAlign(Align align) {
        _align = align;
    }

    public View setTextAlign(String align) {
        _align = Align.valueOf(org.apache.commons.lang3.StringUtils.upperCase(align));
        return this;
    }

    public void setOutlined(boolean outlined) {
        this.outlined = outlined;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public String getFont() {
        return font;
    }

    public void setShadow(int shadow) {
        this.shadow = shadow;
    }

    public void setShadowColor(int shadowColor) {
        this.shadowColor = new Color(shadowColor);
    }
}
