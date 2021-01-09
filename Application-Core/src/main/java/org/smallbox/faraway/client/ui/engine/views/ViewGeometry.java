package org.smallbox.faraway.client.ui.engine.views;

public class ViewGeometry {
    protected double uiScale;
    protected int               finalX;
    protected int               finalY;
    protected int               marginTop;
    protected int               marginRight;
    protected int               marginBottom;
    protected int               marginLeft;
    protected int               width = -1;
    protected int               height = -1;
    protected int               x;
    protected int               y;
    protected int               fixedWidth = -1;
    protected int               fixedHeight = -1;
    protected int               paddingLeft;
    protected int               paddingBottom;
    protected int               paddingRight;
    protected int               paddingTop;
    protected int               offsetX;
    protected int               offsetY;
    protected int originWidth;
    protected int originHeight;

    public ViewGeometry(double uiScale) {
        this.uiScale = uiScale;
    }

    public int          getPosX() { return x; }
    public int          getPosY() { return y; }
    public int          getFinalX() { return finalX; }
    public int          getFinalY() { return finalY; }
//    public int          getHeight() { return height; }
//    public int          getWidth() { return width; }
    public int          getMarginTop() { return marginTop; }
    public int          getMarginRight() { return marginRight; }
    public int          getMarginBottom() { return marginBottom; }
    public int          getMarginLeft() { return marginLeft; }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getOriginWidth() {
        return originWidth;
    }

    public int getOriginHeight() {
        return originHeight;
    }

    public double getUiScale() {
        return uiScale;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getFixedWidth() {
        return fixedWidth;
    }

    public int getFixedHeight() {
        return fixedHeight;
    }

    public int getPaddingLeft() {
        return paddingLeft;
    }

    public int getPaddingBottom() {
        return paddingBottom;
    }

    public int getPaddingRight() {
        return paddingRight;
    }

    public int getPaddingTop() {
        return paddingTop;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setFinalX(int finalX) {
        this.finalX = finalX;
    }

    public void setFinalY(int finalY) {
        this.finalY = finalY;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = (int) (marginTop * uiScale);
    }

    public void setMarginRight(int marginRight) {
        this.marginRight = (int) (marginRight * uiScale);
    }

    public void setMarginBottom(int marginBottom) {
        this.marginBottom = (int) (marginBottom * uiScale);
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = (int) (marginLeft * uiScale);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setFixedWidth(int fixedWidth) {
        this.fixedWidth = fixedWidth;
    }

    public void setFixedHeight(int fixedHeight) {
        this.fixedHeight = fixedHeight;
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public boolean contains(int x, int y) {
        return finalX <= x && finalX + width >= x && finalY <= y && finalY + height >= y;
    }

    public void setPadding(int t, int r, int b, int l) {
        this.paddingTop = (int) (t * uiScale);
        this.paddingRight = (int) (r * uiScale);
        this.paddingBottom = (int) (b * uiScale);
        this.paddingLeft = (int) (l * uiScale);
    }

    public void setFixedSize(int width, int height) {
        this.fixedWidth = (int) (width * uiScale);
        this.fixedHeight = (int) (height * uiScale);
    }

    public void setSize(int width, int height) {
        this.width = (int) (width * uiScale);
        this.height = (int) (height * uiScale);
        this.originWidth = width;
        this.originHeight = height;
    }

    public void setWidth(int width) {
        this.width = (int) (width * uiScale);
        this.originWidth = width;
    }

    public void setHeight(int height) {
        this.height = (int) (height * uiScale);
        this.originHeight = height;
    }

    public void setPositionX(int x) {
        this.x = (int) (x * uiScale);
    }

    public void setPositionY(int y) {
        this.y = (int) (y * uiScale);
    }

    public void setPosition(int x, int y) {
        this.x = (int) (x * uiScale);
        this.y = (int) (y * uiScale);
    }

}
