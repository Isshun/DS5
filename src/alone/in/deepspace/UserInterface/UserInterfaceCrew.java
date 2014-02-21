package alone.in.deepspace.UserInterface;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;

import alone.in.deepspace.Character.Character;
import alone.in.deepspace.Character.CharacterManager;
import alone.in.deepspace.Managers.SpriteManager;
import alone.in.deepspace.Utils.Constant;

public class UserInterfaceCrew extends UserSubInterface {

	private static class ViewHolder {
		public Text 	lbName;
		public Text 	lbProfession;
		public Sprite	thumb;
	}
	private static final int 	FRAME_WIDTH = 380;
	private static final int	FRAME_HEIGHT = Constant.WINDOW_HEIGHT;

	private static int CREW_LINE_HEIGHT = 42;
	private static int CREW_LINE_WIDTH  = 350;

	private CharacterManager     _characterManager;
	private List<ViewHolder> _viewHolderList;

	public UserInterfaceCrew(RenderWindow app, int tileIndex) throws IOException {
		super(app, tileIndex, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 0), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
		  
		setBackgroundColor(new Color(100, 0, 0, 180));
		
		_viewHolderList = new ArrayList<ViewHolder>();
		_characterManager = CharacterManager.getInstance();
	}

	void  addCharacter(RenderWindow app, int index, Character character) {
		int x = 0;
		int y = index;

		ViewHolder view = null;
		if (index >= _viewHolderList.size()) {
			view = new ViewHolder();
			
			// Name
			view.lbName = new Text();
			view.lbName.setFont(SpriteManager.getInstance().getFont());
			view.lbName.setCharacterSize(14);
			view.lbName.setString(character.getName());
			view.lbName.setStyle(Text.REGULAR);
			view.lbName.setPosition(Constant.UI_PADDING + 42,
					   Constant.UI_PADDING + (CREW_LINE_HEIGHT * y));
		
			view.lbProfession = new Text();
			view.lbProfession.setString(character.getName());
			view.lbProfession.setPosition(_posX + Constant.UI_PADDING + Constant.CHAR_WIDTH + Constant.UI_PADDING + (CREW_LINE_WIDTH * x),
						   _posY + Constant.UI_PADDING + 3 + (CREW_LINE_HEIGHT * y));

//		  // Function
//		  Profession function = character.getProfession();
//		  text.setString(function.getName());
//		  text.setPosition(_posX + Constant.UI_PADDING + Constant.CHAR_WIDTH + Constant.UI_PADDING + (CREW_LINE_WIDTH * x),
//						   _posY + Constant.UI_PADDING + (CREW_LINE_HEIGHT * y) + 22);
//		  text.setColor(function.getColor());
//		  _app.draw(text, _render);
		
			_viewHolderList.add(view);
		} else {
			view = _viewHolderList.get(index);
		}
		
		app.draw(view.lbName, _render);
		app.draw(view.lbProfession, _render);
		view.thumb = SpriteManager.getInstance().getCharacter(character.getProfession(), 0, 0);
		view.thumb.setPosition(Constant.UI_PADDING + (CREW_LINE_WIDTH * x),
				Constant.UI_PADDING + (CREW_LINE_HEIGHT * y));
		app.draw(view.thumb, _render);
	}
	
	@Override
	public void onRefresh(RenderWindow app) {
		List<Character> characters = _characterManager.getList();
		int i = 0;
		for (Character c: characters) {
			addCharacter(app, i++, c);
		}
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
//		text.setString("Total: " + CharacterManager.getInstance().getCount());
//	    text.setPosition(_posTileX,
//						 _posTileY + TITLE_SIZE + Constant.UI_PADDING);
//	    _app.draw(text);
//	  }
//	
//	  Profession[] professions = CharacterManager.getInstance().getProfessions();
//	  for (int i = 0; i < professions.length; i++) {
//		RectangleShape shape = new RectangleShape();
//		shape.setSize(new Vector2f(24, 24));
//		shape.setFillColor(professions[i].getColor());
//		shape.setPosition(Constant.UI_PADDING + (i * 28),
//						  _posTileY + TITLE_SIZE + Constant.UI_PADDING + 32);
//		_app.draw(shape);
//	
//		int count = CharacterManager.getInstance().getCount(professions[i].getType());
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
	
	protected boolean	checkKey(Keyboard.Key key) {
	  super.checkKey(key);
	
	  if (key == Keyboard.Key.C) {
		toogle();
		return true;
	  }
	
	  return false;
	}
}
