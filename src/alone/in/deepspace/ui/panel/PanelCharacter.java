package alone.in.deepspace.ui.panel;

import java.io.IOException;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Strings;
import alone.in.deepspace.engine.ui.ColorView;
import alone.in.deepspace.engine.ui.FrameLayout;
import alone.in.deepspace.engine.ui.ImageView;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.OnFocusListener;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.manager.SpriteManager;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.Character.Gender;
import alone.in.deepspace.model.CharacterNeeds;
import alone.in.deepspace.model.CharacterRelation;
import alone.in.deepspace.model.CharacterRelation.Relation;
import alone.in.deepspace.model.CharacterStatus;
import alone.in.deepspace.model.Profession;
import alone.in.deepspace.ui.UserSubInterface;
import alone.in.deepspace.util.Constant;
import alone.in.deepspace.util.Settings;
import alone.in.deepspace.util.StringUtils;

public class PanelCharacter extends UserSubInterface {
	private static final String[] texts = {"Food", "Oxygen", "Happiness", "Energy", "Relation", "Security", "Health", "Sickness", "Injuries", "Satiety", "unused", "Work"};

	private static final int FONT_SIZE = 22;
	private static final int FONT_SIZE_SMALL = 14;
	private static final int LINE_HEIGHT = 28;
	private static final int FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int FRAME_HEIGHT = Constant.WINDOW_HEIGHT;
	private static final int NB_GAUGE = 7;
	private static final int NB_MAX_RELATION = 18;
	private static final int NB_INVENTORY_PER_LINE = 10;

	private static final int NB_COLUMNS = 47;
	private static final int NB_COLUMNS_NEEDS = 22;

	private static final Color COLOR_0 = new Color(120, 255, 255);
	private static final Color COLOR_1 = new Color(214, 247, 59);
	private static final Color COLOR_2 = new Color(247, 57, 57);
	private static final Color COLOR_3 = new Color(247, 57, 57);

	private Character   	    _character;
	private TextView 			_lbTip;
	private TextView 			_lbName;
	private TextView 			_lbProfession;
	private ColorView 			_cursor;
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
	private FrameLayout 		_layoutDebug;
	private TextView[] 			_debugEntries;
	private TextView[] 			_debugEntriesValue;
	private FrameLayout 		_layoutJob;
	private FrameLayout 		_layoutNeeds;
	private FrameLayout 		_layoutInventory;

	private int _count;

	private int _animRemain;

	private TextView _lbGender;

	private String _lastThoughts;

	private int _lastOld;
	private Gender	_lastGender;
	private Profession	_lastProfession;

	private TextView _animLabel;

	private String _animValue;

	private int _animFrame;

	private FrameLayout[] _gauges = new FrameLayout[NB_GAUGE];

	private int _animGauge;

	private String _lastEnlisted;

	private TextView _lbEnlisted;

	private String _lastBirthName;

	private TextView _lbBirthName;

	private Color _statusColor;

	private CharacterStatus _lastStatus;

	public PanelCharacter(RenderWindow app) throws IOException {
		super(app, 0, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 32), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT - 32));

		_cursor = new ColorView(new Vector2f(8, 16));
		_cursor.setBackgroundColor(COLOR_TEXT);
		addView(_cursor);

		// Tip
		_lbTip = new TextView(new Vector2f(FRAME_WIDTH, LINE_HEIGHT));
		_lbTip.setCharacterSize(FONT_SIZE);
		_lbTip.setColor(COLOR_LABEL);
		_lbTip.setBackgroundColor(new Color(255, 255, 255, 100));
		_lbTip.setVisible(false);
		addView(_lbTip);
		
		// Name
		_lbName = new TextView(new Vector2f(FRAME_WIDTH, LINE_HEIGHT));
		_lbName.setCharacterSize(FONT_SIZE);
		_lbName.setColor(COLOR_LABEL);
		_lbName.setPosition(new Vector2f(20, 18));
		addView(_lbName);

		createReport(20, 64);
		createJobInfo(20, 136);
		createNeedsInfo(20, 206);
		createBasicInformation(20, 480);
		createInventoryInfo(20, 630);
		createFamilyInfo(20, 700);
		if (Settings.getInstance().isDebug()) {
			createDebug(20, 850);
		}
	}

	private void createReport(int x, int y) {
		int posY = y;
		int posX = x;
		
		TextView text = new TextView();
		text.setCharacterSize(FONT_SIZE);
		text.setColor(COLOR_LABEL);
		text.setString("Last report");
		text.setPosition(posX, posY);
		addView(text);
		posY += 32;
		
		_lbState = new TextView(new Vector2f(300, 20));
		_lbState.setCharacterSize(FONT_SIZE_SMALL);
		_lbState.setPosition(new Vector2f(posX, posY));
		_lbState.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				_ui.showTooltip(_lastStatus.getTip());
			}
		});
		_lbState.setOnFocusListener(new OnFocusListener() {
			@Override
			public void onExit(View view) {
				_lbState.setColor(_statusColor != null ? _statusColor : COLOR_TEXT);
				_lbState.setStyle(TextView.REGULAR);
			}
			
			@Override
			public void onEnter(View view) {
				_lbState.setColor(COLOR_ACTIVE);
				_lbState.setStyle(TextView.UNDERLINED);
			}
		});
		addView(_lbState);
	}

	private void createBasicInformation(int x, int y) {
		int posY = y;
		int posX = x;
		
		TextView text = new TextView();
		text.setCharacterSize(FONT_SIZE);
		text.setColor(COLOR_LABEL);
		text.setString("Staff record");
		text.setPosition(posX, posY);
		addView(text);
		posY += 32;
		
		// Name
		_lbOld = new TextView(null);
		_lbOld.setCharacterSize(FONT_SIZE_SMALL);
		_lbOld.setColor(COLOR_TEXT);
//		_lbOld.setPosition(new Vector2f(FRAME_WIDTH - 65, Constant.UI_PADDING_V + 9));
		_lbOld.setPosition(new Vector2f(posX, posY));
		addView(_lbOld);
		posY += 20;

		_lbGender = new TextView(null);
		_lbGender.setCharacterSize(FONT_SIZE_SMALL);
		_lbGender.setColor(COLOR_TEXT);
		_lbGender.setPosition(new Vector2f(posX, posY));
		addView(_lbGender);
		posY += 20;

		_lbProfession = new TextView(null);
		_lbProfession.setCharacterSize(FONT_SIZE_SMALL);
		_lbProfession.setColor(COLOR_TEXT);
		_lbProfession.setPosition(new Vector2f(posX, posY));
		addView(_lbProfession);
		posY += 20;

		_lbEnlisted = new TextView(null);
		_lbEnlisted.setCharacterSize(FONT_SIZE_SMALL);
		_lbEnlisted.setColor(COLOR_TEXT);
		_lbEnlisted.setPosition(new Vector2f(posX, posY));
		addView(_lbEnlisted);
		posY += 20;

		_lbBirthName = new TextView(null);
		_lbBirthName.setCharacterSize(FONT_SIZE_SMALL);
		_lbBirthName.setColor(COLOR_TEXT);
		_lbBirthName.setPosition(new Vector2f(posX, posY));
		addView(_lbBirthName);
		posY += 20;
	}

	private void createDebug(int x, int y) {
		_layoutDebug = new FrameLayout(new Vector2f(200, 200));
		_layoutDebug.setVisible(true);
		_layoutDebug.setPosition(x, y);
		addView(_layoutDebug);

		TextView lbDebug = new TextView();
		lbDebug.setCharacterSize(FONT_SIZE);
		lbDebug.setPosition(0, 0);
		lbDebug.setString("Debug");
		_layoutDebug.addView(lbDebug);

		_debugEntries = new TextView[NB_MAX_RELATION];
		_debugEntriesValue = new TextView[NB_MAX_RELATION];
		for (int i = 0; i < NB_MAX_RELATION; i++) {
			_debugEntries[i] = new TextView(new Vector2f(400, 22));
			_debugEntries[i].setCharacterSize(FONT_SIZE_SMALL);
			_debugEntries[i].setColor(COLOR_TEXT);
			_debugEntries[i].setPosition(0, 32 + 22 * i);
			_layoutDebug.addView(_debugEntries[i]);

//			_debugEntriesValue[i] = new TextView(new Vector2f(100, 32));
//			_debugEntriesValue[i].setCharacterSize(FONT_SIZE_SMALL);
//			_debugEntriesValue[i].setPosition(280, 32 + 22 * i);
//			_layoutDebug.addView(_debugEntriesValue[i]);
		}		
	}

	private void createInventoryInfo(int x, int y) {
		_layoutInventory = new FrameLayout(new Vector2f(FRAME_WIDTH, 80));
		_layoutInventory.setPosition(x, y);
		addView(_layoutInventory);
		
		TextView lbCarry= new TextView(new Vector2f(FRAME_WIDTH, LINE_HEIGHT));
		lbCarry.setCharacterSize(FONT_SIZE);
		lbCarry.setColor(COLOR_LABEL);
		lbCarry.setString(Strings.LB_INVENTORY);
		lbCarry.setPosition(new Vector2f(0, 0));
		_layoutInventory.addView(lbCarry);

		_lbCarry = new ImageView[Constant.CHARACTER_INVENTORY_SPACE];
		for (int i = 0; i < Constant.CHARACTER_INVENTORY_SPACE; i++) {
			final int x2 = i % NB_INVENTORY_PER_LINE;
			final int y2 = i / NB_INVENTORY_PER_LINE;
			_lbCarry[i] = new ImageView();
			_lbCarry[i].setPosition(new Vector2f(x2 * 28, 32 + y2 * 28));
			_lbCarry[i].setOnFocusListener(new OnFocusListener() {
				@Override
				public void onExit(View view) {
					_lbTip.setVisible(false);
				}
				
				@Override
				public void onEnter(View view) {
					_lbTip.setVisible(true);
					_lbTip.setPosition(new Vector2f(x2 * 28 + 16, 32 + y2 * 28 + 16));
				}
			});
			_layoutInventory.addView(_lbCarry[i]);
		}
	}

	private void createJobInfo(int x, int y) {
		_layoutJob = new FrameLayout(new Vector2f(FRAME_WIDTH, 80));
		_layoutJob.setPosition(x, y);
		addView(_layoutJob);
		
		_lbJob = new TextView(new Vector2f(FRAME_WIDTH, LINE_HEIGHT));
		_lbJob.setCharacterSize(FONT_SIZE);
		_lbJob.setColor(COLOR_LABEL);
		_lbJob.setPosition(0, 0);
		_lbJob.setString(Strings.LB_CHARACTER_INFO_JOB);
		_layoutJob.addView(_lbJob);

		_lbJob2 = new TextView(new Vector2f(FRAME_WIDTH, LINE_HEIGHT));
		_lbJob2.setCharacterSize(FONT_SIZE_SMALL);
		_lbJob2.setColor(COLOR_TEXT);
		_lbJob2.setPosition(0, 32);
		_layoutJob.addView(_lbJob2);
	}

	private void createProfessionInfo(int x, int y) {
		_layoutProfession = new FrameLayout(new Vector2f(FRAME_WIDTH, 80));
		_layoutProfession.setPosition(x, y);
		addView(_layoutProfession);

		TextView lbTitle = new TextView();
		lbTitle.setCharacterSize(FONT_SIZE);
		lbTitle.setPosition(Constant.UI_PADDING_H, Constant.UI_PADDING_V);
		lbTitle.setString(Strings.LB_PROFESSION);
		_layoutProfession.addView(lbTitle);

		_lbProfession = new TextView();
		_lbProfession.setCharacterSize(FONT_SIZE_SMALL);
		_lbProfession.setString(Strings.LB_PROFESSION);
		_lbProfession.setPosition(Constant.UI_PADDING_H * 2, Constant.UI_PADDING_V + 32);
		_layoutProfession.addView(_lbProfession);
	}

	private void createNeedsInfo(int x, int y) {
		_layoutNeeds = new FrameLayout(new Vector2f(FRAME_WIDTH, 370));
		_layoutNeeds.setPosition(x, Constant.UI_PADDING_V + y);
		addView(_layoutNeeds);

		TextView text = new TextView();
		text.setCharacterSize(FONT_SIZE);
		text.setColor(COLOR_LABEL);
		text.setString("Monitoring");
		text.setPosition(0, 0);
		_layoutNeeds.addView(text);

		for (int i = 0; i < NB_GAUGE; i++) {
			addGauge(x + 200 * (i % 2),
					y + 50 * (i / 2) + (FONT_SIZE + 24),
					160,
					12,
					i);
		}
	}

	private void createFamilyInfo(int x, int y) {
		_layoutFamily = new FrameLayout(new Vector2f(200, 200));
		_layoutFamily.setPosition(x, y);
		addView(_layoutFamily);

		TextView lbFamily = new TextView();
		lbFamily.setCharacterSize(FONT_SIZE);
		lbFamily.setString("Relationship");
		_layoutFamily.addView(lbFamily);

		_familyEntries = new TextView[NB_MAX_RELATION];
		_familyRelationEntries = new TextView[NB_MAX_RELATION];
		for (int i = 0; i < NB_MAX_RELATION; i++) {
			_familyEntries[i] = new TextView(new Vector2f(400, 22));
			_familyEntries[i].setCharacterSize(FONT_SIZE_SMALL);
			_familyEntries[i].setPosition(0, 32 + 22 * i);
			_familyEntries[i].setColor(COLOR_TEXT);
			_layoutFamily.addView(_familyEntries[i]);

			_familyRelationEntries[i] = new TextView(new Vector2f(100, 32));
			_familyRelationEntries[i].setCharacterSize(FONT_SIZE_SMALL);
			_familyRelationEntries[i].setPosition(280, 32 + 22 * i);
			_familyRelationEntries[i].setColor(COLOR_TEXT);
			_layoutFamily.addView(_familyRelationEntries[i]);
		}
	}

	public void  setCharacter(Character character) {
		if (_character != null) {
			_character.setSelected(false);
		}
		
		if (character != null) {
			character.setSelected(true);
			_lbName.setString(character.getName().toUpperCase());
			_lbName.setColor(character.getColor());
			_nbRelation = -1;			
//			_lastGender = null;
//			_lastOld = -1;
//			_lastProfession = null;
//			_lastThoughts = null;
			
			// Reset gauges
			for (int i = 0; i < NB_GAUGE; i++) {
				_values[i].setVisible(false);
			}
			_animGauge = 0;
		}
		_character = character;
	}

	private void refreshRelations() {
		List<CharacterRelation> relations = _character.getRelations();
		_nbRelation = relations.size();
		int i = 0;
		for (final CharacterRelation relation: relations) {
			if (i < NB_MAX_RELATION) {
				String left = relation.getSecond().getName();
//				_familyEntries[i].setStyle(TextView.UNDERLINED);
				_familyEntries[i].setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						setCharacter(relation.getSecond());
					}
				});
				_familyEntries[i].setOnFocusListener(new OnFocusListener() {
					@Override
					public void onExit(View view) {
						((TextView)view).setStyle(TextView.REGULAR);
						((TextView)view).setColor(COLOR_TEXT);
						//view.setBackgroundColor(null);
					}

					@Override
					public void onEnter(View view) {
						((TextView)view).setStyle(TextView.UNDERLINED);
						((TextView)view).setColor(COLOR_ACTIVE);
						//view.setBackgroundColor(new Color(40, 40, 80));
					}
				});

				String right = relation.getRelationLabel();
				switch (relation.getRelation()) {
				case CHILDREN:
				case BROTHER:
				case SISTER:
				case HALF_BROTHER:
				case HALF_SISTER:
					right += " (" + (int)relation.getSecond().getOld() + "yo.)";
					break;
				default: break;
				}
				
				_familyEntries[i].setString(StringUtils.getDashedString(left, right, NB_COLUMNS));

				i++;
			}
		}
		for (; i < NB_MAX_RELATION; i++) {
			if (i == 0) {
				_familyEntries[i].setString("no results");
				_familyEntries[i].setStyle(TextView.REGULAR);
				_familyRelationEntries[i].setString("");
			} else {
				_familyEntries[i].setString("");
				_familyRelationEntries[i].setString("");
			}
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
		_shapes[index] = new RectangleShape();
		_shapes[index].setTexture(SpriteManager.getInstance().getTexture());
		_shapes[index].setSize(new Vector2f(width, height));
		_shapes[index].setPosition(posX, posY + FONT_SIZE + 2);

		_values[index] = new TextView();
		_values[index].setCharacterSize(FONT_SIZE_SMALL);
		_values[index].setColor(COLOR_TEXT);
		_values[index].setPosition(posX, posY);
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
	public void onRefresh(int update) {

		if (update % 2 == 0) {
			return;
		}
			_cursor.setVisible(!_cursor.isVisible());
			_animGauge++;

		if (_animRemain > 0) {
			return;
		}
		
		if (_character != null) {
			
			refreshJob();
			refreshNeeds();
			refreshInventory();
			refreshDebug();
			
			// Relations
			if (update % 20 == 0 || _nbRelation != _character.getNbRelations()) {
				refreshRelations();
			}
			
			_lastStatus = _character.getStatus();
			String status = _character.getStatus().getThoughts();
			String time = _character.getStatus().getLastReportDelay() + "sec. ago";
			_lbState.setString(StringUtils.getDashedString(status, time, NB_COLUMNS));
			int level = _character.getStatus().getLevel();
			switch (level) {
			case 0: _statusColor = COLOR_0; break;
			case 1: _statusColor = COLOR_1; break;
			case 2: _statusColor = COLOR_2; break;
			case 3: _statusColor = COLOR_3; break;
			}
			if (_lbState.isFocus() == false) {
				_lbState.setColor(_statusColor);
			}
			
			// Old
			int old = (int)_character.getOld();
			if (old != _lastOld) {
				_lastOld = old;
				startAnim(_lbOld, StringUtils.getDashedString("Old", String.valueOf(old), NB_COLUMNS));
				return;
			}

			// Gender
			Gender gender = _character.getGender();
			if (!gender.equals(_lastGender)) {
				_lastGender = gender;
				startAnim(_lbGender, StringUtils.getDashedString("Gender:", Gender.MALE.equals(gender) ? "male" : "female", NB_COLUMNS));
				return;
			}
			
			// Profession
			Profession profession = _character.getProfession();
			if (!profession.equals(_lastProfession)) {
				_lastProfession = profession;
				startAnim(_lbProfession, StringUtils.getDashedString("Profession:", profession.getName(), NB_COLUMNS));
				return;
			}

			// Enlist
			String enlisted = _character.getEnlisted();
			if (!enlisted.equals(_lastEnlisted)) {
				_lastEnlisted = enlisted;
				startAnim(_lbEnlisted, StringUtils.getDashedString("Enlisted since:", enlisted, NB_COLUMNS));
				return;
			}

			// BirthName
			String birthName = _character.getFirstName() + _character.getBirthName();
			if (!birthName.equals(_lastBirthName)) {
				_lastBirthName = birthName;
				startAnim(_lbBirthName, StringUtils.getDashedString("Birth name:", birthName, NB_COLUMNS));
				return;
			}
			
		}
	}
	
	private void refreshJob() {
		if (_character.getJob() != null) {
			_lbJob2.setString(StringUtils.getDashedString(_character.getJob().getShortLabel(), _character.getJob().getFormatedDuration(), NB_COLUMNS));
		} else {
			_lbJob2.setString(Strings.LN_NO_JOB);
		}
	}

	private void refreshDebug() {
		if (Settings.getInstance().isDebug()) {
			_debugEntries[0].setString(StringUtils.getDashedString("Old", String.valueOf(_character.getOld()), NB_COLUMNS));
			_debugEntries[1].setString(StringUtils.getDashedString("NextChild", String.valueOf(_character.getNextChildAtOld()), NB_COLUMNS));
			_debugEntries[2].setString(StringUtils.getDashedString("IsGay", String.valueOf(_character.isGay()), NB_COLUMNS));
			_debugEntries[3].setString(StringUtils.getDashedString("Gender", String.valueOf(_character.getGender()), NB_COLUMNS));
		}

	}

	private void refreshInventory() {
		for (int i = 0; i < Constant.CHARACTER_INVENTORY_SPACE; i++) {
			if (_character.getCarried().size() > i) {
				BaseItem item = _character.getCarried().get(i);
				_lbCarry[i].setImage(SpriteManager.getInstance().getIcon(item.getInfo()));
				_lbTip.setString(item.getName());
			} else {
				_lbCarry[i].setImage(null);
			}
		}
	}

	private void refreshNeeds() {
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
//			case 7: value = Math.min(Math.max(needs.getSickness(), 0), 100); break;
//			case 8: value = Math.min(Math.max(needs.getInjuries(), 0), 100); break;
//			case 9: value = Math.min(Math.max(needs.getSatiety(), 0), 100); break;
//			case 10: value = Math.min(Math.max(needs.getSleeping(), 0), 100); break;
//			case 11: value = Math.min(Math.max(needs.getWorkRemain(), 0), 100); break;
			}
			float size = Math.max(Math.round(180.0f / 100 * value / 10) * 10, 10);
			int level = 0;
			Color color = COLOR_0;
			if (value < 10) { level = 3; color = COLOR_2; }
			else if (value < 50) { level = 2; color = COLOR_1; }
			_shapes[i].setSize(new Vector2f(size, 12));
			_shapes[i].setTextureRect(new IntRect(0, level * 16, (int)size, 12));
			
			_values[i].setString(StringUtils.getDashedString(texts[i], String.valueOf(value), NB_COLUMNS_NEEDS));
			_values[i].setColor(color);
		}
	}

	private void startAnim(TextView text, String value) {
		_animFrame = 0;
		_animLabel = text;
		_animValue = value;
		_animRemain = NB_COLUMNS;
		_cursor.setPosition(18, text.getPosY());
	}

	private void anim() {
		int pos = NB_COLUMNS - _animRemain + 1;
		_animLabel.setString(_animValue.substring(0, pos));
		_cursor.setPosition(18 + pos * 8, _animLabel.getPosY());
		_animRemain--;
	}

	@Override
	public void onDraw(RenderWindow app, RenderStates render) {

		// Play anim
		if (_animFrame++ % 4 == 0) {
			if (_animRemain > 0) {
				anim();
			}
		}

		if (_character != null) {
			for (int i = 0; i < _animGauge && i < NB_GAUGE; i++) {
				_values[i].setVisible(true);
				app.draw(_shapes[i], _render);
			}
		}
	}

}
