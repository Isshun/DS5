package alone.in.deepspace.ui.panel;

import org.jsfml.system.Vector2f;

import alone.in.deepspace.Strings;
import alone.in.deepspace.engine.ui.ButtonView;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.ui.UserInterface;
import alone.in.deepspace.util.Constant;

public class PanelPlan extends BasePanel {
	private static final int 		FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int 		FRAME_HEIGHT = Constant.WINDOW_HEIGHT;

	public enum PanelMode {
		GATHER, MINING, DUMP, NONE
	}

	public PanelPlan(UserInterface.Mode mode) {
		super(mode, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 32), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT - 32), true);
	}

	@Override
	protected void onCreate() {
		ButtonView btGather = new ButtonView(new Vector2f(120, 36));
		btGather.setString(Strings.LB_GATHER);
		btGather.setPadding(3, 16);
		btGather.setPosition(0, 0);
		btGather.setCharacterSize(20);
		btGather.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				_ui.select(PanelMode.GATHER);
			}
		});
		addView(btGather);
		  
		ButtonView btMining = new ButtonView(new Vector2f(120, 36));
		btMining.setString(Strings.LB_MINING);
		btMining.setPadding(3, 16);
		btMining.setPosition(0, 50);
		btMining.setCharacterSize(20);
		btMining.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				_ui.select(PanelMode.MINING);
			}
		});
		addView(btMining);

		ButtonView btDump = new ButtonView(new Vector2f(120, 36));
		btDump.setString(Strings.LB_DUMP);
		btDump.setPadding(3, 16);
		btDump.setPosition(0, 100);
		btDump.setCharacterSize(20);
		btDump.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				_ui.select(PanelMode.DUMP);
			}
		});
		addView(btDump);
	}
}
