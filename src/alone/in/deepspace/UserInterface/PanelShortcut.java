package alone.in.DeepSpace.UserInterface;

import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.DeepSpace.UserInterface.Utils.OnClickListener;
import alone.in.DeepSpace.UserInterface.Utils.UIText;
import alone.in.DeepSpace.UserInterface.Utils.UIView;
import alone.in.DeepSpace.Utils.Constant;

public class PanelShortcut extends UserSubInterface {
	private static final int FRAME_WIDTH = Constant.WINDOW_WIDTH;
	private static final int FRAME_HEIGHT = 120;
	private int _used;
	private UserInterface _userInterface;
	
	public PanelShortcut(RenderWindow app, final UserInterface userInterface) throws IOException {
		super(app, 0, new Vector2f(0, Constant.WINDOW_HEIGHT - FRAME_HEIGHT), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
		
		setBackgroundColor(new Color(200, 50, 140, 150));
		
		UIText lbEngineering = new UIText(new Vector2f(200, 32));
		lbEngineering.setCharacterSize(14);
		lbEngineering.setColor(Color.WHITE);
		lbEngineering.setString("Engineering");
		lbEngineering.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(UIView view) {
				userInterface.setMode(UserInterface.Mode.BUILD);
			}
		});
		lbEngineering.setPosition(new Vector2f(10, 6));
		addView(lbEngineering);
		
		UIText lbOperation = new UIText(new Vector2f(10, 10));
		lbOperation.setCharacterSize(14);
		lbOperation.setColor(Color.WHITE);
		lbOperation.setPosition(new Vector2f(100, 6));
		lbOperation.setString("Operation");
		lbOperation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(UIView view) {
				userInterface.setMode(UserInterface.Mode.JOBS);
			}
		});
		addView(lbOperation);
		
		UIText lbCrew = new UIText(new Vector2f(10, 10));
		lbCrew.setCharacterSize(14);
		lbCrew.setColor(Color.WHITE);
		lbCrew.setPosition(new Vector2f(200, 6));
		lbCrew.setString("Crew");
		lbCrew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(UIView view) {
				userInterface.setMode(UserInterface.Mode.CREW);
			}
		});
		addView(lbCrew);
	}
}
