package alone.in.deepspace.UserInterface;
import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Models.Character;
import alone.in.deepspace.Models.CharacterNeeds;
import alone.in.deepspace.UserInterface.Utils.UIText;
import alone.in.deepspace.Utils.Constant;


public class PanelCharacter extends UserSubInterface {
	private static final String[] texts = {"Food", "Oxygen", "Happiness", "Energy", "Relation", "Security", "Health", "Sickness", "Injuries", "Satiety", "Sleep"};

	private static final int PADDING_V = 34;
	private static final int PADDING_H = 16;
	private static final int FONT_SIZE = 20;
	private static final int LINE_HEIGHT = 28;
	
	private Character        _character;
	private UIText _lbName;
	private UIText _lbProfession;

	private static final int FRAME_WIDTH = 380;
	private static final int FRAME_HEIGHT = Constant.WINDOW_HEIGHT;
	
	private RectangleShape[] _shapes = new RectangleShape[11];
	
	PanelCharacter(RenderWindow app) throws IOException {
		super(app, 0, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 32), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
		
		setBackgroundColor(new Color(0, 0, 0, 150));

		// Name
		_lbName = new UIText(new Vector2f(FRAME_WIDTH, LINE_HEIGHT));
		_lbName.setCharacterSize(FONT_SIZE);
		_lbName.setColor(Color.WHITE);
		_lbName.setPosition(new Vector2f(PADDING_H, PADDING_V));
		addView(_lbName);

		// Profession
		_lbProfession = new UIText(new Vector2f(FRAME_WIDTH, LINE_HEIGHT));
		_lbProfession.setCharacterSize(FONT_SIZE);
		_lbProfession.setColor(Color.WHITE);
		_lbProfession.setPosition(new Vector2f(PADDING_H, PADDING_V + LINE_HEIGHT));
		addView(_lbProfession);
		
	    for (int i = 0; i < 11; i++) {
	      addGauge(PADDING_H + 180 * (i % 2),
	               32 + 50 * (i / 2) + (FONT_SIZE + 16) + PADDING_V,
	               160,
	               12,
	               i);
	    }
	}

	void  setCharacter(Character character) {
		if (_character != null) {
			_character.setSelected(false);
		}
		if (character != null) {
			character.setSelected(true);
			_lbName.setString(character.getName());
			_lbProfession.setString(character.getProfession().getName());
		}
		_character = character;
	}
	
	public Character  getCharacter() { return _character; }

//	void	addMessage(int posX, int posY, int width, int height, CharacterNeeds.Message value, RenderStates render) {
//	  String msg = null;
//
//	  switch (value) {
//	  case MSG_HUNGRY:
//		msg = "MSG_HUNGRY";
//		break;
//	  case MSG_STARVE:
//		msg = "MSG_STARVE";
//		break;
//	  case MSG_NEED_OXYGEN:
//		msg = "MSG_NEED_OXYGEN";
//		break;
//	  case MSG_SLEEP_ON_FLOOR:
//		msg = "SLEEP_ON_FLOOR";
//		break;
//	  case MSG_SLEEP_ON_CHAIR:
//		msg = "SLEEP_ON_CHAIR";
//		break;
//	  case MSG_NO_WINDOW:
//		msg = "MSG_NO_WINDOW";
//		break;
//	  case MSG_BLOCKED:
//		msg = "MSG_BLOCKED";
//		break;
//	  default:
//		return;
//	  }
//
//	  Text text = new Text();
//	  text.setString(msg);
//	  text.setFont(_font);
//	  text.setCharacterSize(MENU_CHARACTER_MESSAGE_FONT_SIZE);
//	  text.setStyle(Text.REGULAR);
//	  text.setPosition(posX, posY);
//	  _app.draw(text, render);
//	}

	void  addGauge(int posX, int posY, int width, int height, int index) {

		// Name
		UIText text = new UIText(new Vector2f(width, height));
		text.setCharacterSize(FONT_SIZE);
		text.setColor(Color.WHITE);
		text.setString(texts[index]);
		text.setPosition(new Vector2f(posX, posY));
		addView(text);

		_shapes[index] = new RectangleShape();
		_shapes[index].setSize(new Vector2f(width, height));
		_shapes[index].setFillColor(new Color(100, 200, 0));
		_shapes[index].setPosition(posX, posY + FONT_SIZE + 8);
		
		//	    Text text = new Text();
//	    text.setString(label);
//	    text.setFont(_font);
//	    text.setCharacterSize(MENU_CHARACTER_FONT_SIZE);
//	    text.setStyle(Text.REGULAR);
//	    text.setPosition(posX, posY);
//	    _app.draw(text, render);
//
//	    RectangleShape shapeBg = new RectangleShape();
//	    shapeBg.setSize(new Vector2f(width, height));
//	    shapeBg.setFillColor(new Color(100, 200, 0));
//	    shapeBg.setPosition(posX, posY + MENU_CHARACTER_FONT_SIZE + 8);
//	    _app.draw(shapeBg, render);
//
//	    RectangleShape shape = new RectangleShape();
//	    shape.setSize(new Vector2f(width * value / 100, height));
//	    shape.setFillColor(new Color(200, 255, 0));
//	    shape.setPosition(posX, posY + MENU_CHARACTER_FONT_SIZE + 8);
//	    _app.draw(shape, render);
	}

	@Override
	public void onRefresh() {
	  if (_character != null) {

		  CharacterNeeds needs = _character.getNeeds();
		  for (int i = 0; i < 11; i++) {

		      int value = 0;
		      switch (i) {
		      case 0: value = Math.min(Math.max(needs.getFood(), 0), 100); break;
		      case 1: value = Math.min(Math.max(needs.getOxygen(), 0), 100); break;
		      case 2: value = Math.min(Math.max(needs.getHappiness(), 0), 100); break;
		      case 3: value = Math.min(Math.max(needs.getEnergy(), 0), 100); break;
			  case 4: value = Math.min(Math.max(needs.getRelation(), 0), 100); break;
			  case 5: value = Math.min(Math.max(needs.getSecurity(), 0), 100); break;
			  case 6: value = Math.min(Math.max(needs.getHealth(), 0), 100); break;
			  case 7: value = Math.min(Math.max(needs.getSickness(), 0), 100); break;
			  case 8: value = Math.min(Math.max(needs.getInjuries(), 0), 100); break;
			  case 9: value = Math.min(Math.max(needs.getSatiety(), 0), 100); break;
			  case 10: value = Math.min(Math.max(needs.getSleeping(), 0), 100); break;
		      }
		      _shapes[i].setSize(new Vector2f(160.0f / 100 * value, 12));
		      
			  _app.draw(_shapes[i], _render);
		  }


//	    // Name
//	    Text text = new Text();
//	    text.setString(_character.getName());
//	    text.setFont(_font);
//	    text.setCharacterSize(MENU_CHARACTER_FONT_SIZE);
//	    text.setStyle(Text.REGULAR);
//	    text.setPosition(MENU_PADDING_LEFT + 0, MENU_PADDING_TOP);
//	    _app.draw(text, render);
//
//	    // Job
//		Profession function = _character.getProfession();
//	    Text job = new Text();
//	    job.setString(function.getName());
//	    job.setFont(_font);
//	    job.setCharacterSize(24);
//	    job.setColor(function.getColor());
//	    job.setStyle(Text.REGULAR);
//	    job.setPosition(MENU_PADDING_LEFT + 0, MENU_PADDING_TOP + MENU_CHARACTER_FONT_SIZE);
//	    _app.draw(job, render);
//
//		CharacterNeeds needs = _character.getNeeds();
//	    for (int i = 0; i < 11; i++) {
//
//	      addGauge(MENU_PADDING_LEFT + 180 * (i % 2),
//	               10 + 50 * (i / 2) + (UI_FONT_SIZE + 16) + MENU_PADDING_TOP,
//	               160,
//	               12,
//	               value,
//				   texts[i],
//				   render);
	    }

	    // TODO
//		int messages = _character.getMessages();
//	    for (int i = 0; i < CHARACTER_Math.max_MESSAGE; i++) {
//		  if (messages[i] == -1 || messages[i] > frame - 100) {
//			addMessage(MENU_PADDING_LEFT,
//					   280 + (i * UI_FONT_SIZE) + MENU_PADDING_TOP,
//					   UI_WIDTH - MENU_PADDING_TOP * 2,
//					   UI_FONT_SIZE,
//					   i,
//					   render);
//		  }
//	    }

//	  }
	}

}
