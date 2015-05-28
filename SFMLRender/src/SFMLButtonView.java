//import org.smallbox.faraway.GFXRenderer;
//import org.smallbox.faraway.RenderEffect;
//import org.smallbox.faraway.engine.ui.ButtonView;
//
//public class SFMLButtonView extends ButtonView {
//
//	public SFMLButtonView(int width, int height) {
//		super(width, height);
//	}
//
//	@Override
//	public void draw(GFXRenderer renderer, RenderEffect effect) {
//
//	}
//
//	@Override
//	public void refresh() {
//		if (_textShortcut != null) {
//			_textShortcut.setPosition(_x + _paddingLeft + _textPaddingLeft + ((_shortcutPos + 1) * 12), _y + _paddingTop + _textPaddingTop);
//			_textShortcut.setCharacterSize(_textSize);
//		}
//
//		if (_rectShortcut != null) {
//			_rectShortcut.setPosition(_x + _paddingLeft + _textPaddingLeft + ((_shortcutPos + 1) * 12), _y + _paddingTop + _textPaddingTop + 24);
//		}
//	}
//
//	@Override
//	public int getContentWidth() {
//		return _text.getContentWidth();
//	}
//
//	@Override
//	public int getContentHeight() {
//		return _text.getContentHeight();
//	}
//
//}
//
