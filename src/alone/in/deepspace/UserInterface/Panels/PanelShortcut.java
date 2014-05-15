package alone.in.deepspace.UserInterface.Panels;

import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Engine.ui.ButtonView;
import alone.in.deepspace.Engine.ui.OnClickListener;
import alone.in.deepspace.Engine.ui.TextView;
import alone.in.deepspace.Engine.ui.View;
import alone.in.deepspace.UserInterface.OnFocusListener;
import alone.in.deepspace.UserInterface.UserInterface;
import alone.in.deepspace.UserInterface.UserSubInterface;
import alone.in.deepspace.UserInterface.UserInterface.Mode;
import alone.in.deepspace.Utils.Constant;

public class PanelShortcut extends UserSubInterface {
	private static final int FRAME_WIDTH = Constant.WINDOW_WIDTH;
	private static final int FRAME_HEIGHT = 120;
	
	public PanelShortcut(RenderWindow app, final UserInterface userInterface) throws IOException {
		super(app, 0, new Vector2f(0, Constant.WINDOW_HEIGHT - FRAME_HEIGHT), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
		
		setBackgroundColor(new Color(200, 50, 140, 150));
		
		TextView lbEngineering = new TextView(new Vector2f(140, 36));
		lbEngineering.setCharacterSize(14);
		lbEngineering.setColor(Color.WHITE);
		lbEngineering.setString("Build");
		lbEngineering.setPadding(8, 20, 10, 50);
		lbEngineering.setBackgroundColor(new Color(0, 0, 255, 180));
		lbEngineering.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				userInterface.setMode(UserInterface.Mode.BUILD);
			}
		});
		lbEngineering.setPosition(new Vector2f(10, 6));
		addView(lbEngineering);
		
		ButtonView lbOperation = new ButtonView(new Vector2f(140, 36), "Jobs");
		lbOperation.setOnFocusListener(new OnFocusListener() {			
			@Override
			public void onExit(View view) {
				view.setBackgroundColor(new Color(0, 255, 0, 180));
			}
			
			@Override
			public void onEnter(View view) {
				view.setBackgroundColor(Color.RED);
			}
		});
//		lbOperation.setCharacterSize(14);
//		lbOperation.setColor(Color.WHITE);
		lbOperation.setBackgroundColor(new Color(0, 255, 0, 180));
		lbOperation.setPosition(new Vector2f(160, 6));
	//	lbOperation.setPadding(8, 20, 10, 50);
		lbOperation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				userInterface.setMode(UserInterface.Mode.JOBS);
			}
		});
		addView(lbOperation);
		
		TextView lbCrew = new TextView(new Vector2f(140, 36));
		lbCrew.setCharacterSize(14);
		lbCrew.setColor(Color.WHITE);
		lbCrew.setPadding(8, 20, 10, 50);
		lbCrew.setBackgroundColor(new Color(0, 255, 255, 180));
		lbCrew.setPosition(new Vector2f(310, 6));
		lbCrew.setString("Crew");
		lbCrew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				userInterface.setMode(UserInterface.Mode.CREW);
			}
		});
		addView(lbCrew);
		
		TextView lbRoom = new TextView(new Vector2f(140, 36));
		lbRoom.setCharacterSize(14);
		lbRoom.setColor(Color.WHITE);
		lbRoom.setPadding(8, 20, 10, 50);
		lbRoom.setBackgroundColor(new Color(0, 150, 180, 255));
		lbRoom.setPosition(new Vector2f(460, 6));
		lbRoom.setString("Room");
		lbRoom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				userInterface.setMode(UserInterface.Mode.ROOM);
			}
		});
		addView(lbRoom);

		TextView lbDebug = new TextView(new Vector2f(140, 36));
		lbDebug.setCharacterSize(14);
		lbDebug.setColor(Color.WHITE);
		lbDebug.setPadding(8, 20, 10, 45);
		lbDebug.setBackgroundColor(new Color(150, 200, 180, 255));
		lbDebug.setPosition(new Vector2f(610, 6));
		lbDebug.setString("Debug");
		lbDebug.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				userInterface.setMode(UserInterface.Mode.DEBUG);
			}
		});
		addView(lbDebug);
}

	@Override
	public void onRefresh(RenderWindow app) {
	}
}
