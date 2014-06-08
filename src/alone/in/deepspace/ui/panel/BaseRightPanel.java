package alone.in.deepspace.ui.panel;

import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard.Key;

import alone.in.deepspace.engine.ui.ButtonView;
import alone.in.deepspace.engine.ui.ColorView;
import alone.in.deepspace.engine.ui.Colors;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.ui.UserInterface.Mode;
import alone.in.deepspace.util.Constant;

public abstract class BaseRightPanel extends BasePanel {
	protected static final int FRAME_WIDTH = Constant.PANEL_WIDTH;
	protected static final int FRAME_HEIGHT = Constant.WINDOW_HEIGHT;

	public BaseRightPanel(Mode mode, Key shortcut) {
		super(mode, shortcut, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 0), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));

		setBackgroundColor(Colors.BACKGROUND);
		View border = new ColorView(new Vector2f(4, FRAME_HEIGHT));
		border.setBackgroundColor(Colors.BORDER);
		super.addView(border);
		
		if (mode != Mode.NONE) {
			ButtonView btBack = new ButtonView(new Vector2f(100, 32));
			btBack.setTextPadding(1, 6);
			btBack.setCharacterSize(FONT_SIZE_TITLE);
			btBack.setString("[    ]");
			btBack.setPosition(20, 10);
			btBack.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					_ui.back();
				}
			});
			super.addView(btBack);
			
			TextView lbBack = new TextView();
			lbBack.setString("Back");
			lbBack.setCharacterSize(FONT_SIZE_TITLE);
			lbBack.setColor(Colors.LINK_ACTIVE);
			lbBack.setPosition(20, 10);
			lbBack.setPadding(1, 20);
			super.addView(lbBack);
			
			ColorView rectangleUnderline = new ColorView(new Vector2f((int)(4 * 12.5), 1));
			rectangleUnderline.setPosition(40, 36);
			rectangleUnderline.setBackgroundColor(Colors.LINK_ACTIVE);
			super.addView(rectangleUnderline);
		}
	}

	@Override
	public void addView(View view) {
		if (_mode != Mode.NONE) {
			view.setPosition(view.getPosX(), view.getPosY() + 42);
		}
		super.addView(view);
	}
	

}
