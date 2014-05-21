package alone.in.deepspace.UserInterface.Panels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;

import alone.in.deepspace.Strings;
import alone.in.deepspace.UserInterface.UserInterface;
import alone.in.deepspace.UserInterface.UserSubInterface;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.engine.ui.FrameLayout;
import alone.in.deepspace.engine.ui.ImageView;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.OnFocusListener;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.manager.CharacterManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.manager.SpriteManager;
import alone.in.deepspace.model.Character;

public class PanelCrew extends UserSubInterface {

	private static class ViewHolder {
		public TextView 	lbName;
		public TextView 	lbProfession;
		public ImageView		thumb;
		public FrameLayout frame;
		public TextView lbStatus;
		public TextView lbJob;
	}
	private static final int 	FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int	FRAME_HEIGHT = Constant.WINDOW_HEIGHT;
	private static final int CREW_LINE_SPACING = 10;

	private static int CREW_LINE_HEIGHT = 52;
	private static int CREW_LINE_WIDTH  = FRAME_WIDTH - Constant.UI_PADDING * 2;

	private CharacterManager     _characterManager;
	private List<ViewHolder> _viewHolderList;
	private TextView _lbCount;
	private UserInterface _ui;

	public PanelCrew(RenderWindow app, int tileIndex) throws IOException {
		super(app, tileIndex, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 32), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT - 32));
		  
		setBackgroundColor(new Color(0, 0, 0, 180));
		
		_viewHolderList = new ArrayList<ViewHolder>();
		_characterManager = ServiceManager.getCharacterManager();
		
		// Name
		_lbCount = new TextView(new Vector2f(10, 10));
		_lbCount.setCharacterSize(20);
		_lbCount.setColor(Color.WHITE);
		_lbCount.setPosition(new Vector2f(10, 10));
		addView(_lbCount);
	}

	void  addCharacter(RenderWindow app, int index, final Character character) {
		int x = 0;
		int y = index;
		
		if (index >= _viewHolderList.size()) {
			final ViewHolder viewHolder = new ViewHolder();

			// Frame
			viewHolder.frame = new FrameLayout(new Vector2f(CREW_LINE_WIDTH, CREW_LINE_HEIGHT));
			viewHolder.frame.setPosition(Constant.UI_PADDING, 38 + Constant.UI_PADDING + ((CREW_LINE_HEIGHT + CREW_LINE_SPACING) * y));
			viewHolder.frame.setOnFocusListener(new OnFocusListener() {
				@Override
				public void onExit(View view) {
					view.setBackgroundColor(null);
				}
				
				@Override
				public void onEnter(View view) {
					view.setBackgroundColor(new Color(40, 40, 80));
				}
			});
			viewHolder.frame.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					close();
					_ui.setCharacter(character);
				}
			});
			addView(viewHolder.frame);
			
			// Name
			viewHolder.lbName = new TextView();
			viewHolder.lbName.setCharacterSize(14);
			viewHolder.lbName.setColor(character.getColor());
			viewHolder.lbName.setPosition(Constant.UI_PADDING + 32, 6);
			viewHolder.frame.addView(viewHolder.lbName);
		
			// Status
			viewHolder.lbStatus = new TextView();
			viewHolder.lbStatus.setCharacterSize(14);
			viewHolder.lbStatus.setPosition(Constant.UI_PADDING + 32, Constant.UI_PADDING + 16);
			viewHolder.frame.addView(viewHolder.lbStatus);
		
			// Job
			viewHolder.lbJob = new TextView();
			viewHolder.lbJob.setCharacterSize(12);
			viewHolder.lbJob.setPosition(Constant.UI_PADDING + 260, 6);
			viewHolder.frame.addView(viewHolder.lbJob);
		
			// Profession
			viewHolder.lbProfession = new TextView();
			viewHolder.lbProfession.setCharacterSize(14);
			viewHolder.lbProfession.setVisible(false);
			viewHolder.lbProfession.setPosition(CREW_LINE_WIDTH - Constant.UI_PADDING - 100, Constant.UI_PADDING);
			viewHolder.frame.addView(viewHolder.lbProfession);

			viewHolder.thumb = new ImageView(SpriteManager.getInstance().getCharacter(character.getProfession(), 0, 0));
			viewHolder.thumb.setPosition(8, 5);
			viewHolder.frame.addView(viewHolder.thumb);

			
//		  // Function
//		  Profession function = character.getProfession();
//		  text.setString(function.getName());
//		  text.setPosition(_posX + Constant.UI_PADDING + Constant.CHAR_WIDTH + Constant.UI_PADDING + (CREW_LINE_WIDTH * x),
//						   _posY + Constant.UI_PADDING + (CREW_LINE_HEIGHT * y) + 22);
//		  text.setColor(function.getColor());
//		  _app.draw(text, _render);
		
			_viewHolderList.add(viewHolder);
		} else {
			final ViewHolder viewHolder = _viewHolderList.get(index);

			// Name
			viewHolder.lbName.setString(character.getName());
			
			// Job
			viewHolder.lbStatus.setString(character.getStatus().getThoughts());
			viewHolder.lbStatus.setColor(character.getStatus().getColor());
			
			// Job
			if (character.getJob() != null) {
				viewHolder.lbJob.setString(character.getJob().getShortLabel());
				viewHolder.lbJob.setColor(new Color(255, 255, 255));
			} else {
				viewHolder.lbJob.setString(Strings.LB_NO_JOB);
				viewHolder.lbJob.setColor(new Color(255, 255, 255, 100));
			}

			// Profession
			viewHolder.lbProfession.setString(character.getProfession().getName());
		}
		
	}
	
	@Override
	public void onRefresh(RenderWindow app) {
		List<Character> characters = _characterManager.getList();
		int i = 0;
		for (Character c: characters) {
			addCharacter(app, i++, c);
		}
		
		_lbCount.setString("Count: " + characters.size());
	}
	
	void	drawTile() {
//	  super.drawTile(COLOR_TILE_ACTIVE);
//	
//	  Text text = new Text();
//	  text.setFont(SpriteManager.getInstance().getFont());
//	  text.setCharacterSize(FONT_SIZE);
//	
//	  {
//		int matter = ResourceManager.getInstance().getMatter();
//		text.setString("Total: " + ServiceManager.getCharacterManager().getCount());
//	    text.setPosition(_posTileX,
//						 _posTileY + TITLE_SIZE + Constant.UI_PADDING);
//	    _app.draw(text);
//	  }
//	
//	  Profession[] professions = ServiceManager.getCharacterManager().getProfessions();
//	  for (int i = 0; i < professions.length; i++) {
//		RectangleShape shape = new RectangleShape();
//		shape.setSize(new Vector2f(24, 24));
//		shape.setFillColor(professions[i].getColor());
//		shape.setPosition(Constant.UI_PADDING + (i * 28),
//						  _posTileY + TITLE_SIZE + Constant.UI_PADDING + 32);
//		_app.draw(shape);
//	
//		int count = ServiceManager.getCharacterManager().getCount(professions[i].getType());
//		text.setString(String.valueOf(count));
//		text.setColor(professions[i].getTextColor());
//		text.setCharacterSize(10);
//		text.setPosition(Constant.UI_PADDING + (i * 28) + (count < 10 ? 6 : 2),
//						 _posTileY + TITLE_SIZE + Constant.UI_PADDING + 32 + 5);
//	    _app.draw(text);
//	  }
//	
//	  text.setString("Crew");
//	  text.setCharacterSize(TITLE_SIZE);
//	  text.setPosition(_posTileX + Constant.UI_PADDING, _posTileY + Constant.UI_PADDING);
//	  _app.draw(text);
//	  text.setString("C");
//	  text.setStyle(Text.UNDERLINED);
//	  text.setColor(Color.YELLOW);
//	  _app.draw(text);
	}
	
	public boolean	checkKey(Keyboard.Key key) {
	  super.checkKey(key);
	
	  return false;
	}

	public void setUI(UserInterface userInterface) {
		_ui = userInterface;
	}
}
