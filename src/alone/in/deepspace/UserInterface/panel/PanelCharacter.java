package alone.in.deepspace.UserInterface.panel;

import java.io.IOException;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Strings;
import alone.in.deepspace.UserInterface.UserSubInterface;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.Utils.Settings;
import alone.in.deepspace.engine.ui.FrameLayout;
import alone.in.deepspace.engine.ui.ImageView;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.manager.SpriteManager;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.CharacterNeeds;
import alone.in.deepspace.model.CharacterRelation;
import alone.in.deepspace.model.CharacterStatus;

public class PanelCharacter extends UserSubInterface {
	private static final String[] texts = {"Food", "Oxygen", "Happiness", "Energy", "Relation", "Security", "Health", "Sickness", "Injuries", "Satiety", "unused", "Work"};

	private static final int FONT_SIZE = 22;
	private static final int FONT_SIZE_SMALL = 14;
	private static final int LINE_HEIGHT = 28;
	private static final int FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int FRAME_HEIGHT = Constant.WINDOW_HEIGHT;
	private static final int NB_GAUGE = 12;
	private static final int NB_MAX_RELATION = 18;
	private static final int NB_INVENTORY_PER_LINE = 10;

	private Character   	    _character;
	private TextView 			_lbName;
	private TextView 			_lbProfession;
	private RectangleShape[] 	_shapes = new RectangleShape[NB_GAUGE];
	private TextView[] 			_values = new TextView[NB_GAUGE];
	private TextView 			_lbJob;
	private TextView 			_lbJob2;
	private ImageView[] 		_lbCarry;
	private TextView 			_lbState;
	private FrameLayout 		_layoutFamily;
	private TextView[] 			_familyEntries;
	private FrameLayout 		_layoutProfession;
	private TextView[] 			_familyRelationEntries;
	private int 				_nbRelation;
	private TextView 			_lbOld;
	private FrameLayout 		_layoutdebug;
	private TextView[] 			_debugEntries;
	private TextView[] 			_debugEntriesValue;

	public PanelCharacter(RenderWindow app) throws IOException {
		super(app, 0, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 32), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT - 32));

		setBackgroundColor(new Color(0, 0, 0, 150));

		// Name
		_lbName = new TextView(new Vector2f(FRAME_WIDTH, LINE_HEIGHT));
		_lbName.setCharacterSize(FONT_SIZE);
		_lbName.setColor(Color.WHITE);
		_lbName.setPosition(new Vector2f(Constant.UI_PADDING_H, Constant.UI_PADDING_V));
		addView(_lbName);

		// Name
		_lbOld = new TextView(null);
		_lbOld.setCharacterSize(FONT_SIZE_SMALL);
		_lbOld.setColor(Color.WHITE);
		_lbOld.setPosition(new Vector2f(FRAME_WIDTH - 65, Constant.UI_PADDING_V + 9));
		addView(_lbOld);

		// Status
		_lbState = new TextView(new Vector2f(FRAME_WIDTH, LINE_HEIGHT));
		_lbState.setCharacterSize(FONT_SIZE_SMALL);
		_lbState.setColor(Color.WHITE);
		_lbState.setPosition(new Vector2f(Constant.UI_PADDING_H, Constant.UI_PADDING_V + 28));
		addView(_lbState);

		createProfessionInfo(0, 34);
		createNeedsInfo(0, 125);
		createJobInfo(0, 480);
		createFamilyInfo(0, 650);
		createInventoryInfo(0, 560);
		if (Settings.getInstance().isDebug()) {
			createDebug(0, 850);
		}
	}

	private void createDebug(int x, int y) {
		_layoutdebug = new FrameLayout(new Vector2f(200, 200));
		_layoutdebug.setVisible(true);
		_layoutdebug.setPosition(Constant.UI_PADDING_H + x, Constant.UI_PADDING_V + y);
		addView(_layoutdebug);

		TextView lbdebug = new TextView();
		lbdebug.setCharacterSize(FONT_SIZE);
		lbdebug.setString("Debug");
		_layoutdebug.addView(lbdebug);

		_debugEntries = new TextView[NB_MAX_RELATION];
		_debugEntriesValue = new TextView[NB_MAX_RELATION];
		for (int i = 0; i < NB_MAX_RELATION; i++) {
			_debugEntries[i] = new TextView(new Vector2f(400, 22));
			_debugEntries[i].setCharacterSize(FONT_SIZE_SMALL);
			_debugEntries[i].setPosition(0, 32 + 22 * i);
			_layoutdebug.addView(_debugEntries[i]);

			_debugEntriesValue[i] = new TextView(new Vector2f(100, 32));
			_debugEntriesValue[i].setCharacterSize(FONT_SIZE_SMALL);
			_debugEntriesValue[i].setPosition(280, 32 + 22 * i);
			_layoutdebug.addView(_debugEntriesValue[i]);
		}		
	}

	private void createInventoryInfo(int x, int y) {
		TextView lbCarry= new TextView(new Vector2f(FRAME_WIDTH, LINE_HEIGHT));
		lbCarry.setCharacterSize(FONT_SIZE);
		lbCarry.setColor(Color.WHITE);
		lbCarry.setString(Strings.LB_INVENTORY);
		lbCarry.setPosition(new Vector2f(Constant.UI_PADDING_H + x, Constant.UI_PADDING_V + y));
		addView(lbCarry);

		_lbCarry = new ImageView[Constant.CHARACTER_INVENTORY_SPACE];
		for (int i = 0; i < Constant.CHARACTER_INVENTORY_SPACE; i++) {
			int x2 = i % NB_INVENTORY_PER_LINE;
			int y2 = i / NB_INVENTORY_PER_LINE;
			_lbCarry[i] = new ImageView();
			_lbCarry[i].setPosition(new Vector2f(Constant.UI_PADDING_H + x + x2 * 28, Constant.UI_PADDING_V + y + 32 + y2 * 28));
			addView(_lbCarry[i]);
		}
	}

	private void createJobInfo(int x, int y) {
		_lbJob = new TextView(new Vector2f(FRAME_WIDTH, LINE_HEIGHT));
		_lbJob.setCharacterSize(FONT_SIZE);
		_lbJob.setColor(Color.WHITE);
		_lbJob.setPosition(new Vector2f(Constant.UI_PADDING_H + x, Constant.UI_PADDING_V + y));
		addView(_lbJob);

		_lbJob2 = new TextView(new Vector2f(FRAME_WIDTH, LINE_HEIGHT));
		_lbJob2.setCharacterSize(FONT_SIZE_SMALL);
		_lbJob2.setColor(Color.WHITE);
		_lbJob2.setPosition(new Vector2f(Constant.UI_PADDING_H + x, Constant.UI_PADDING_V + y + 32));
		addView(_lbJob2);

	}

	private void createProfessionInfo(int x, int y) {
		_layoutProfession = new FrameLayout(new Vector2f(200, 200));
		_layoutProfession.setPosition(x, y);
		addView(_layoutProfession);

		TextView lbTitle = new TextView();
		lbTitle.setCharacterSize(FONT_SIZE);
		lbTitle.setPosition(Constant.UI_PADDING_H + x, y);
		lbTitle.setString(Strings.LB_PROFESSION);
		_layoutProfession.addView(lbTitle);

		_lbProfession = new TextView();
		_lbProfession.setCharacterSize(FONT_SIZE_SMALL);
		_lbProfession.setString(Strings.LB_PROFESSION);
		_lbProfession.setPosition(Constant.UI_PADDING_H + x, y + 32);
		_layoutProfession.addView(_lbProfession);
	}

	private void createNeedsInfo(int x, int y) {
		TextView text = new TextView();
		text.setCharacterSize(FONT_SIZE);
		text.setColor(Color.WHITE);
		text.setString(Strings.LB_NEEDS);
		text.setPosition(Constant.UI_PADDING_H + x, Constant.UI_PADDING_V + y);
		addView(text);

		for (int i = 0; i < NB_GAUGE; i++) {
			addGauge(Constant.UI_PADDING_H + x + 180 * (i % 2),
					y + 50 * (i / 2) + (FONT_SIZE + 16) + Constant.UI_PADDING_V,
					160,
					12,
					i);
		}
	}

	private void createFamilyInfo(int x, int y) {
		_layoutFamily = new FrameLayout(new Vector2f(200, 200));
		_layoutFamily.setVisible(false);
		_layoutFamily.setPosition(Constant.UI_PADDING_H + x, Constant.UI_PADDING_V + y);
		addView(_layoutFamily);

		TextView lbFamily = new TextView();
		lbFamily.setCharacterSize(FONT_SIZE);
		lbFamily.setString(Strings.LB_FAMILY);
		_layoutFamily.addView(lbFamily);

		_familyEntries = new TextView[NB_MAX_RELATION];
		_familyRelationEntries = new TextView[NB_MAX_RELATION];
		for (int i = 0; i < NB_MAX_RELATION; i++) {
			_familyEntries[i] = new TextView(new Vector2f(400, 22));
			_familyEntries[i].setCharacterSize(FONT_SIZE_SMALL);
			_familyEntries[i].setPosition(0, 32 + 22 * i);
			_familyEntries[i].setStyle(TextView.UNDERLINED);
			_layoutFamily.addView(_familyEntries[i]);

			_familyRelationEntries[i] = new TextView(new Vector2f(100, 32));
			_familyRelationEntries[i].setCharacterSize(FONT_SIZE_SMALL);
			_familyRelationEntries[i].setPosition(280, 32 + 22 * i);
			_layoutFamily.addView(_familyRelationEntries[i]);
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

			List<CharacterRelation> relations = character.getFamilyMembers();
			refreshRelations(relations);
		}
		_character = character;
	}

	private void refreshRelations(List<CharacterRelation> relations) {
		_nbRelation = relations.size();
		_layoutFamily.setVisible(relations.size() > 0);
		int i = 0;
		for (final CharacterRelation relation: relations) {
			if (i < NB_MAX_RELATION) {
				_familyEntries[i].setString(relation.getSecond().getName());
				_familyEntries[i].setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						setCharacter(relation.getSecond());
					}
				});
				_familyRelationEntries[i].setString(relation.getRelationLabel());
				i++;
			}
		}
		for (; i < NB_MAX_RELATION; i++) {
			_familyEntries[i].setString("");
			_familyRelationEntries[i].setString("");
		}
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
		text.setCharacterSize(FONT_SIZE_SMALL);
		text.setColor(Color.WHITE);
		text.setString(texts[index]);
		text.setPosition(new Vector2f(posX, posY));
		addView(text);

		_shapes[index] = new RectangleShape();
		_shapes[index].setSize(new Vector2f(width, height));
		_shapes[index].setFillColor(new Color(100, 200, 0));
		_shapes[index].setPosition(posX, posY + FONT_SIZE + 8);

		_values[index] = new TextView();
		_values[index].setCharacterSize(FONT_SIZE_SMALL);
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

			_lbOld.setString((int)_character.getOld() + "yo.");

			CharacterNeeds needs = _character.getNeeds();
			for (int i = 0; i < NB_GAUGE; i++) {

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

			for (int i = 0; i < Constant.CHARACTER_INVENTORY_SPACE; i++) {
				if (_character.getCarried().size() > i) {
					BaseItem item = _character.getCarried().get(i);
					_lbCarry[i].setImage(SpriteManager.getInstance().getIcon(item.getInfo()));
				} else {
					_lbCarry[i].setImage(null);
				}
			}

			List<CharacterRelation> relations = _character.getFamilyMembers();
			if (_nbRelation != relations.size()) {
				refreshRelations(relations);
			}

			if (Settings.getInstance().isDebug()) {
				_debugEntries[0].setString("old");
				_debugEntriesValue[0].setString(String.valueOf(_character.getOld()));

				_debugEntries[1].setString("next child at old");
				_debugEntriesValue[1].setString(String.valueOf(_character.getNextChildAtOld()));

				_debugEntries[2].setString("is gay");
				_debugEntriesValue[2].setString(String.valueOf(_character.isGay()));
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
