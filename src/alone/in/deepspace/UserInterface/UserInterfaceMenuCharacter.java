package alone.in.DeepSpace.UserInterface;
import java.io.File;
import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.Transform;
import org.jsfml.system.Vector2f;

import alone.in.DeepSpace.Models.Character;
import alone.in.DeepSpace.Models.CharacterNeeds;
import alone.in.DeepSpace.Models.Profession;
import alone.in.DeepSpace.Utils.Constant;


public class UserInterfaceMenuCharacter extends UserSubInterface {
	private static final String[] texts = {"Food", "Oxygen", "Happiness", "Energy", "Relation", "Security", "Health", "Sickness", "Injuries", "Satiety", "Sleep"};
	private static final int MENU_CHARACTER_FONT_SIZE = 20;
	private static final int MENU_CHARACTER_MESSAGE_FONT_SIZE = 16;

	private static final int MENU_PADDING_TOP = 34;
	private static final int MENU_PADDING_LEFT = 16;
	private static final int UI_FONT_SIZE = 20;
	
	private RenderWindow     _app;
	private Font			_font;
	private Sprite			_background;
	private Texture			_backgroundTexture;
	private Character           _character;

	UserInterfaceMenuCharacter(RenderWindow app) throws IOException {
		super(app, 5);
		
		_app = app;
		_character = null;
	}

	void	init() throws IOException {
		_backgroundTexture = new Texture();
		_backgroundTexture.loadFromFile((new File("res/menu1.png")).toPath());
		_background = new Sprite();
		_background.setTexture(_backgroundTexture);
		_background.setTextureRect(new IntRect(0, 0, 380, 420));

		_font = new Font();
		_font.loadFromFile((new File("res/fonts/xolonium_regular.otf")).toPath());
	}

	void  setCharacter(Character character) {
		if (_character != null) {
			_character.setSelected(false);
		}
		if (character != null) {
			character.setSelected(true);
		}
		_character = character;
	}
	
	public Character  getCharacter() { return _character; }

	void	addMessage(int posX, int posY, int width, int height, CharacterNeeds.Message value, RenderStates render) {
	  String msg = null;

	  switch (value) {
	  case MSG_HUNGRY:
		msg = "MSG_HUNGRY";
		break;
	  case MSG_STARVE:
		msg = "MSG_STARVE";
		break;
	  case MSG_NEED_OXYGEN:
		msg = "MSG_NEED_OXYGEN";
		break;
	  case MSG_SLEEP_ON_FLOOR:
		msg = "SLEEP_ON_FLOOR";
		break;
	  case MSG_SLEEP_ON_CHAIR:
		msg = "SLEEP_ON_CHAIR";
		break;
	  case MSG_NO_WINDOW:
		msg = "MSG_NO_WINDOW";
		break;
	  case MSG_BLOCKED:
		msg = "MSG_BLOCKED";
		break;
	  default:
		return;
	  }

	  Text text = new Text();
	  text.setString(msg);
	  text.setFont(_font);
	  text.setCharacterSize(MENU_CHARACTER_MESSAGE_FONT_SIZE);
	  text.setStyle(Text.REGULAR);
	  text.setPosition(posX, posY);
	  _app.draw(text, render);
	}

	void  addGauge(int posX, int posY, int width, int height, int value, String label, RenderStates render) {
	    Text text = new Text();
	    text.setString(label);
	    text.setFont(_font);
	    text.setCharacterSize(MENU_CHARACTER_FONT_SIZE);
	    text.setStyle(Text.REGULAR);
	    text.setPosition(posX, posY);
	    _app.draw(text, render);

	    RectangleShape shapeBg = new RectangleShape();
	    shapeBg.setSize(new Vector2f(width, height));
	    shapeBg.setFillColor(new Color(100, 200, 0));
	    shapeBg.setPosition(posX, posY + MENU_CHARACTER_FONT_SIZE + 8);
	    _app.draw(shapeBg, render);

	    RectangleShape shape = new RectangleShape();
	    shape.setSize(new Vector2f(width * value / 100, height));
	    shape.setFillColor(new Color(200, 255, 0));
	    shape.setPosition(posX, posY + MENU_CHARACTER_FONT_SIZE + 8);
	    _app.draw(shape, render);
	}

	void	refresh(int frame) {

	  Transform			transform = new Transform();
	  transform = Transform.translate(transform, Constant.WINDOW_WIDTH - 380 - 64, 250);
	  
	  RenderStates		render = new RenderStates(transform);

	  // Background
	  _app.draw(_background, render);

	  if (_character != null) {

	    // Name
	    Text text = new Text();
	    text.setString(_character.getName());
	    text.setFont(_font);
	    text.setCharacterSize(MENU_CHARACTER_FONT_SIZE);
	    text.setStyle(Text.REGULAR);
	    text.setPosition(MENU_PADDING_LEFT + 0, MENU_PADDING_TOP);
	    _app.draw(text, render);

	    // Job
		Profession function = _character.getProfession();
	    Text job = new Text();
	    job.setString(function.getName());
	    job.setFont(_font);
	    job.setCharacterSize(24);
	    job.setColor(function.getColor());
	    job.setStyle(Text.REGULAR);
	    job.setPosition(MENU_PADDING_LEFT + 0, MENU_PADDING_TOP + MENU_CHARACTER_FONT_SIZE);
	    _app.draw(job, render);

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

	      addGauge(MENU_PADDING_LEFT + 180 * (i % 2),
	               10 + 50 * (i / 2) + (UI_FONT_SIZE + 16) + MENU_PADDING_TOP,
	               160,
	               12,
	               value,
				   texts[i],
				   render);
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

	  }
	}


}
