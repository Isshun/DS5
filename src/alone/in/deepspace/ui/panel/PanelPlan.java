package alone.in.deepspace.ui.panel;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Strings;
import alone.in.deepspace.engine.ui.ButtonView;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.ui.UserInterface.Mode;
import alone.in.deepspace.util.Constant;

public class PanelPlan extends BasePanel {
	private static final int 		FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int 		FRAME_HEIGHT = Constant.WINDOW_HEIGHT;

	public enum PanelMode {
		GATHER, MINING, DUMP, NONE
	}

	protected PanelMode _mode;

	public PanelMode getPanelMode() { return _mode; }
	public void setMode(PanelMode mode) { _mode = mode; }

	public PanelPlan(Mode mode) {
		super(mode, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 32), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT - 32));
		  
		_mode = PanelMode.NONE;
		
		setBackgroundColor(new Color(0, 0, 0, 150));

		ButtonView btGather = new ButtonView(new Vector2f(80, 32));
		btGather.setString(Strings.LB_GATHER);
		btGather.setId(42);
		btGather.setBackgroundColor(Color.RED);
		btGather.setPosition(0, 0);
		btGather.setCharacterSize(22);
		btGather.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				_mode = PanelMode.GATHER;
			}
		});
		addView(btGather);
		  
		ButtonView btMining = new ButtonView(new Vector2f(80, 32));
		btMining.setString(Strings.LB_MINING);
		btMining.setPosition(0, 50);
		btMining.setBackgroundColor(Color.RED);
		btMining.setCharacterSize(22);
		btMining.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				_mode = PanelMode.MINING;
			}
		});
		addView(btMining);

		ButtonView btDump = new ButtonView(new Vector2f(80, 32));
		btDump.setString(Strings.LB_DUMP);
		btDump.setPosition(0, 100);
		btDump.setBackgroundColor(Color.RED);
		btDump.setCharacterSize(22);
		btDump.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				_mode = PanelMode.DUMP;
			}
		});
		addView(btDump);
	}
	@Override
	protected void onCreate() {
		// TODO Auto-generated method stub
		
	}
}
