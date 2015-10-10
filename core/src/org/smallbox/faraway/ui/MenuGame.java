//package org.smallbox.faraway.ui;
//
//import org.smallbox.faraway.util.LoadListener;
//import org.smallbox.faraway.engine.Color;
//import org.smallbox.faraway.engine.renderer.GDXRenderer;
//import org.smallbox.faraway.core.Viewport;
//import org.smallbox.faraway.ui.engine.OnClickListener;
//import org.smallbox.faraway.ui.engine.OnFocusListener;
//import org.smallbox.faraway.ui.engine.ViewFactory;
//import org.smallbox.faraway.ui.engine.views.UIFrame;
//import org.smallbox.faraway.ui.engine.views.UILabel;
//import org.smallbox.faraway.ui.engine.views.View;
//import org.smallbox.faraway.util.Constant;
//
//import java.io.IOException;
//import java.util.List;
//
//public class MenuGame extends MenuBase {
//	private static final int 	FRAME_WIDTH = 640;
//	private static final int 	FRAME_HEIGHT = 480;
//	private int 				_index;
//	private List<UILabel>		_lbFiles;
//	private int 				_nbFiles;
//	private UIFrame _menu;
//	protected int 				_selected = -1;
//
//	public MenuGame(final LoadListener onLoadListener) throws IOException {
//		super(Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT);
//		setBackgroundColor(new Color(0, 0, 0, 150));
//
//		_menu = ViewFactory.getInstance().createFrameLayout();
//		_menu.setSize(FRAME_WIDTH, FRAME_HEIGHT);
//		_menu.setPosition(Constant.WINDOW_WIDTH / 2 - FRAME_WIDTH / 2, Constant.WINDOW_HEIGHT / 2 - FRAME_HEIGHT / 2);
//		_menu.setBackgroundColor(new Color(200, 200, 200, 50));
//		addView(_menu);
//
//		setVisible(true);
//	}
//
//	public void draw(GDXRenderer renderer, Viewport viewport) {
//		refreshEntry(renderer, "New game", 0);
//		refreshEntry(renderer, "Load", 1);
//		refreshEntry(renderer, "Save", 2);
//		refreshEntry(renderer, "Feedback", 3);
//		refreshEntry(renderer, "Exit", 4);
//	}
//
//	@Override
//	public void onDraw(GDXRenderer renderer, Viewport viewport) {
//
//	}
//
//	private void refreshEntry(GDXRenderer renderer, String str, int pos) {
//		UILabel text = ViewFactory.getInstance().createTextView();
//		text.setText(str);
//		text.setTextSize(42);
//		text.setTextColor(Color.CYAN);
//		//text.setTextColor(_selected == pos ? new Color(200, 50, 140, 150) : new Color(50, 140, 200, 150));
//
//		for (int i = -2; i <= 2; i++) {
//			for (int j = -2; j <= 2; j++) {
//				text.setPosition(_menu.getPosX() + i + 200, _menu.getPosY() + j + 52 * pos);
//				text.draw(renderer, null);
//			}
//		}
//
//		text.setTextColor(Color.WHITE);
//		text.setPosition(_menu.getPosX() + 200, _menu.getPosY() + 52 * pos);
//		text.draw(renderer, null);
//	}
//
//	public void addEntry(String str, final int pos, OnClickListener listener) {
//		{
//			UILabel text = ViewFactory.getInstance().createTextView(300, 52);
//			text.setText(str);
//			text.setTextSize(42);
//			text.setTextColor(Color.WHITE);
//			text.setPosition(200, 52 * pos);
//			text.setOnClickListener(listener);
//			text.setOnFocusListener(new OnFocusListener() {
//				@Override
//				public void onExit(View view) {
//					if (_selected == pos) {
//						_selected = -1;
//					}
//				}
//
//				@Override
//				public void onEnter(View view) {
//					_selected = pos;
//				}
//			});
//			_menu.addView(text);
//		}
//	}
//
//	@Override
//	public void onKeyDown() {
//		_index = (_index + 1) % _nbFiles;
//	}
//
//	@Override
//	public void onKeyUp() {
//		_index = _index == 0 ? _nbFiles - 1 : _index - 1;
//	}
//
//	@Override
//	public void onKeyEnter() {
//		_lbFiles.get(_index).onClickListener();
//	}
//
//}
