package alone.in.deepspace.UserInterface;

import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.UserInterface.Utils.OnClickListener;
import alone.in.deepspace.UserInterface.Utils.UIText;
import alone.in.deepspace.UserInterface.Utils.UIView;
import alone.in.deepspace.Utils.Constant;

public class PanelShortcut extends UserSubInterface {
	private static final int FRAME_WIDTH = Constant.WINDOW_WIDTH;
	private static final int FRAME_HEIGHT = 120;
	private int _used;
	private UserInterface _userInterface;
	
	public PanelShortcut(RenderWindow app, final UserInterface userInterface) throws IOException {
		super(app, 0, new Vector2f(0, Constant.WINDOW_HEIGHT - FRAME_HEIGHT), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
		
		setBackgroundColor(new Color(200, 50, 140, 150));
		
		UIText lbEngineering = new UIText(new Vector2f(140, 36));
		lbEngineering.setCharacterSize(14);
		lbEngineering.setColor(Color.WHITE);
		lbEngineering.setString("Build");
		lbEngineering.setPadding(8, 20, 10, 50);
		lbEngineering.setBackgroundColor(new Color(0, 0, 255, 180));
		lbEngineering.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(UIView view) {
				userInterface.setMode(UserInterface.Mode.BUILD);
			}
		});
		lbEngineering.setPosition(new Vector2f(10, 6));
		addView(lbEngineering);
		
		UIText lbOperation = new UIText(new Vector2f(140, 36));
		lbOperation.setCharacterSize(14);
		lbOperation.setColor(Color.WHITE);
		lbOperation.setPosition(new Vector2f(160, 6));
		lbOperation.setPadding(8, 20, 10, 50);
		lbOperation.setBackgroundColor(new Color(0, 255, 0, 180));
		lbOperation.setString("Jobs");
		lbOperation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(UIView view) {
				userInterface.setMode(UserInterface.Mode.JOBS);
			}
		});
		addView(lbOperation);
		
		UIText lbCrew = new UIText(new Vector2f(140, 36));
		lbCrew.setCharacterSize(14);
		lbCrew.setColor(Color.WHITE);
		lbCrew.setPadding(8, 20, 10, 50);
		lbCrew.setBackgroundColor(new Color(0, 255, 255, 180));
		lbCrew.setPosition(new Vector2f(310, 6));
		lbCrew.setString("Crew");
		lbCrew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(UIView view) {
				userInterface.setMode(UserInterface.Mode.CREW);
			}
		});
		addView(lbCrew);
		
		UIText lbRoom = new UIText(new Vector2f(140, 36));
		lbRoom.setCharacterSize(14);
		lbRoom.setColor(Color.WHITE);
		lbRoom.setPadding(8, 20, 10, 50);
		lbRoom.setBackgroundColor(new Color(0, 150, 180, 255));
		lbRoom.setPosition(new Vector2f(460, 6));
		lbRoom.setString("Room");
		lbRoom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(UIView view) {
				userInterface.setMode(UserInterface.Mode.ROOM);
			}
		});
		addView(lbRoom);

		UIText lbDebug = new UIText(new Vector2f(140, 36));
		lbDebug.setCharacterSize(14);
		lbDebug.setColor(Color.WHITE);
		lbDebug.setPadding(8, 20, 10, 45);
		lbDebug.setBackgroundColor(new Color(150, 200, 180, 255));
		lbDebug.setPosition(new Vector2f(610, 6));
		lbDebug.setString("Debug");
		lbDebug.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(UIView view) {
				userInterface.setMode(UserInterface.Mode.DEBUG);
			}
		});
		addView(lbDebug);
}

	@Override
	public void onRefresh(RenderWindow app) {
	}
}
