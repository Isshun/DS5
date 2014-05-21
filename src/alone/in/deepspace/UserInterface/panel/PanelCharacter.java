package alone.in.deepspace.UserInterface.panel;

import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.UserInterface.UserSubInterface;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.CharacterNeeds;
import alone.in.deepspace.model.CharacterStatus;

public class PanelCharacter extends UserSubInterface {
	private static final String[] texts = {"Food", "Oxygen", "Happiness", "Energy", "Relation", "Security", "Health", "Sickness", "Injuries", "Satiety", "unused", "Work"};

	private static final int FONT_SIZE = 20;
	private static final int LINE_HEIGHT = 28;
	private static final int FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int FRAME_HEIGHT = Constant.WINDOW_HEIGHT;

	private Character   	    _character;
	private TextView 			_lbName;
	private TextView 			_lbProfession;
	private RectangleShape[] 	_shapes = new RectangleShape[12];
	private TextView[] 			_values = new TextView[12];
	private TextView 			_lbJob;
	private TextView 			_lbJob2;
	private TextView[] 			_lbCarry;

	private TextView _lbState;

	public PanelCharacter(RenderWindow app) throws IOException {
		super(app, 0, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 32), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT - 32));

		setBackgroundColor(new Color(0, 0, 0, 150));

		// Name
		_lbName = new TextView(new Vector2f(FRAME_WIDTH, LINE_HEIGHT));
		_lbName.setCharacterSize(FONT_SIZE);
		_lbName.setColor(Color.WHITE);
		_lbName.setPosition(new Vector2f(Constant.UI_PADDING_H, Constant.UI_PADDING_V));
		addView(_lbName);

		// Status
		_lbState = new TextView(new Vector2f(FRAME_WIDTH, LINE_HEIGHT));
		_lbState.setCharacterSize(14);
		_lbState.setColor(Color.WHITE);
		_lbState.setPosition(new Vector2f(Constant.UI_PADDING_H, Constant.UI_PADDING_V + 28));
		addView(_lbState);

		// Profession
		_lbProfession = new TextView(new Vector2f(FRAME_WIDTH, LINE_HEIGHT));
		_lbProfession.setCharacterSize(FONT_SIZE);
		_lbProfession.setVisible(false);
		_lbProfession.setColor(Color.WHITE);
		_lbProfession.setPosition(new Vector2f(Constant.UI_PADDING_H, Constant.UI_PADDING_V + LINE_HEIGHT));
		addView(_lbProfession);

		for (int i = 0; i < 12; i++) {
			addGauge(Constant.UI_PADDING_H + 180 * (i % 2),
					32 + 50 * (i / 2) + (FONT_SIZE + 16) + Constant.UI_PADDING_V,
					160,
					12,
					i);
		}


		_lbJob = new TextView(new Vector2f(FRAME_WIDTH, LINE_HEIGHT));
		_lbJob.setCharacterSize(FONT_SIZE);
		_lbJob.setColor(Color.WHITE);
		_lbJob.setPosition(new Vector2f(Constant.UI_PADDING_H, Constant.UI_PADDING_V + 400));
		addView(_lbJob);

		_lbJob2 = new TextView(new Vector2f(FRAME_WIDTH, LINE_HEIGHT));
		_lbJob2.setCharacterSize(12);
		_lbJob2.setColor(Color.WHITE);
		_lbJob2.setPosition(new Vector2f(Constant.UI_PADDING_H, Constant.UI_PADDING_V + 432));
		addView(_lbJob2);

		_lbCarry = new TextView[10];
		for (int i = 0; i < 10; i++) {
			_lbCarry[i] = new TextView(new Vector2f(FRAME_WIDTH, LINE_HEIGHT));
			_lbCarry[i].setCharacterSize(12);
			_lbCarry[i].setColor(Color.WHITE);
			_lbCarry[i].setPosition(new Vector2f(Constant.UI_PADDING_H, Constant.UI_PADDING_V + 464 + i * 28));
			addView(_lbCarry[i]);
		}

	}

	public void  setCharacter(Character character) {
		if (_character != null) {
			_character.setSelected(false);
		}
		if (character != null) {
			character.setSelected(true);
			_lbName.setString(character.getName());
			_lbName.setColor(character.getColor());
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
		TextView text = new TextView(new Vector2f(width, height));
		text.setCharacterSize(FONT_SIZE);
		text.setColor(Color.WHITE);
		text.setString(texts[index]);
		text.setPosition(new Vector2f(posX, posY));
		addView(text);

		_shapes[index] = new RectangleShape();
		_shapes[index].setSize(new Vector2f(width, height));
		_shapes[index].setFillColor(new Color(100, 200, 0));
		_shapes[index].setPosition(posX, posY + FONT_SIZE + 8);

		_values[index] = new TextView();
		_values[index].setCharacterSize(12);
		_values[index].setPosition(posX + 130, posY);
		addView(_values[index]);

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
	public void onRefresh(RenderWindow app) {
		if (_character != null) {

			CharacterStatus status = _character.getStatus();
			_lbState.setString(status.getThoughts());
			_lbState.setColor(status.getColor());

			CharacterNeeds needs = _character.getNeeds();
			for (int i = 0; i < 12; i++) {

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
				case 11: value = Math.min(Math.max(needs.getWorkRemain(), 0), 100); break;
				}
				_shapes[i].setSize(new Vector2f(160.0f / 100 * value, 12));
				_values[i].setString(String.valueOf(value));

				app.draw(_shapes[i], _render);

				if (_character.getJob() != null) {
					_lbJob.setString("Job");
					_lbJob2.setString(_character.getJob().getShortLabel());
				} else {
					_lbJob.setString("");
					_lbJob2.setString("");
				}
			}

			for (int i = 0; i < 10; i++) {
				if (_character.getCarried().size() > i) {
					_lbCarry[i].setString(_character.getCarried().get(i).getName());
				} else {
					_lbCarry[i].setString("");
				}
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
