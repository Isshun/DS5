package alone.in.deepspace.UserInterface.Panels;

import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Strings;
import alone.in.deepspace.UserInterface.UserSubInterface;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.engine.ui.ButtonView;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.View;

public class PanelPlan extends UserSubInterface {

	private static final int 		MENU_AREA_CONTENT_FONT_SIZE = 16;

	private static final int 		MENU_PADDING_TOP = 34;
	private static final int 		MENU_PADDING_LEFT = 16;
	  
	private static final int 		FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int 		FRAME_HEIGHT = Constant.WINDOW_HEIGHT;

	public enum Mode {
		GATHER, MINING, DUMP, NONE
	}

	protected Mode _mode;
	
	public PanelPlan(RenderWindow app) throws IOException {
		super(app, 0, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 32), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT - 32));
		  
		_mode = Mode.NONE;
		
		setBackgroundColor(new Color(0, 0, 0, 150));

		ButtonView btGather = new ButtonView(new Vector2f(80, 32));
		btGather.setString(Strings.LB_GATHER);
		btGather.setPosition(0, 0);
		btGather.setCharacterSize(22);
		btGather.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				_mode = Mode.GATHER;
			}
		});
		addView(btGather);
		  
		ButtonView btMining = new ButtonView(new Vector2f(80, 32));
		btMining.setString(Strings.LB_MINING);
		btMining.setPosition(0, 50);
		btMining.setCharacterSize(22);
		btMining.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				_mode = Mode.MINING;
			}
		});
		addView(btMining);

		ButtonView btDump = new ButtonView(new Vector2f(80, 32));
		btDump.setString(Strings.LB_DUMP);
		btDump.setPosition(0, 100);
		btDump.setCharacterSize(22);
		btDump.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				_mode = Mode.DUMP;
			}
		});
		addView(btDump);
	}

	@Override
	public void onRefresh(RenderWindow app) {
	}

	public Mode getMode() {
		return _mode;
	}
}
