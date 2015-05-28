package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.*;
import org.smallbox.faraway.engine.ui.*;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.engine.util.Settings;
import org.smallbox.faraway.engine.util.StringUtils;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.model.ProfessionModel;
import org.smallbox.faraway.model.ToolTips;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.character.CharacterModel.Gender;
import org.smallbox.faraway.model.character.CharacterNeeds;
import org.smallbox.faraway.model.character.CharacterRelation;
import org.smallbox.faraway.model.character.CharacterStatus;
import org.smallbox.faraway.model.character.CharacterStatus.Level;
import org.smallbox.faraway.model.item.ItemBase;
import org.smallbox.faraway.model.job.Job;
import org.smallbox.faraway.ui.UserInterface.Mode;

import java.util.List;

public class PanelCharacter extends BaseRightPanel {
	private static final String[] texts = {"Food", "Oxygen", "Happiness", "Energy", "Relation", "Security", "Health", "Sickness", "Injuries", "Satiety", "unused", "Work"};

	private static final int LINE_HEIGHT = 28;
	private static final int NB_GAUGE = 7;
	private static final int NB_MAX_RELATION = 18;
	private static final int NB_INVENTORY_PER_LINE = 10;

	private static final int NB_COLUMNS_NEEDS = 22;

	private static final Color COLOR_0 = new Color(120, 255, 255);
	private static final Color COLOR_1 = new Color(209, 203, 69);
	private static final Color COLOR_2 = new Color(247, 57, 57);
	private static final Color COLOR_3 = new Color(247, 57, 57);

	private CharacterModel _character;
	private TextView 			_lbTip;
	private TextView 			_lbName;
	private TextView 			_lbProfession;
	private ColorView 			_cursor;
	private ColorView[] 		_shapes = new ColorView[NB_GAUGE];
	private TextView[] 			_values = new TextView[NB_GAUGE];
	private TextView 			_lbJob;
	private TextView 			_lbJob2;
	private ImageView[] 		_lbInventoryEntries;
	private TextView 			_lbState;
	private FrameLayout 		_layoutFamily;
	private TextView[] 			_familyEntries;
	private FrameLayout 		_layoutProfession;
	private TextView[] 			_familyRelationEntries;
	private int 				_nbRelation;
	private TextView 			_lbOld;
	private FrameLayout 		_layoutDebug;
	private TextView[] 			_debugEntries;
	private FrameLayout 		_layoutJob;
	private FrameLayout 		_layoutNeeds;
	private FrameLayout 		_layoutInventory;

	private int 				_animRemain;
	private TextView	 		_lbGender;
	private int 				_lastOld;
	private Gender				_lastGender;
	private ProfessionModel _lastProfession;
	private TextView 			_animLabel;
	private String 				_animValue;
	private int 				_animFrame;
	private int 				_animGauge;
	private String 				_lastEnlisted;
	private TextView 			_lbEnlisted;
	private String 				_lastBirthName;
	private TextView 			_lbBirthName;
	private CharacterStatus 	_lastStatus;
	private TextView 			_lbInventory;

	public PanelCharacter(Mode mode, GameEventListener.Key shortcut) {
		super(mode, shortcut);
	}

	@Override
	protected void onCreate(LayoutFactory factory) {
		_cursor = ViewFactory.getInstance().createColorView(8, 16);
		_cursor.setBackgroundColor(Colors.LINK_INACTIVE);
		addView(_cursor);

		// Tip
		_lbTip = ViewFactory.getInstance().createTextView(FRAME_WIDTH, LINE_HEIGHT);
		_lbTip.setCharacterSize(FONT_SIZE_TITLE);
		_lbTip.setBackgroundColor(new Color(255, 255, 255, 100));
		_lbTip.setVisible(false);
		addView(_lbTip);
		
		// Name
		_lbName = ViewFactory.getInstance().createTextView(FRAME_WIDTH, LINE_HEIGHT);
		_lbName.setCharacterSize(FONT_SIZE_TITLE);
		_lbName.setPosition(20, 18);
		addView(_lbName);

		createReport(20, 64);
		createJobInfo(20, 136);
		createNeedsInfo(20, 206);
		createBasicInformation(20, 480);
		createInventoryInfo(20, 635);
		createRelationShip(20, 735);
		if (Settings.getInstance().isDebug()) {
			//createDebug(20, 850);
		}
	}
	
	private void createReport(int x, int y) {
		int posY = y;
		int posX = x;
		
		TextView text = ViewFactory.getInstance().createTextView();
		text.setCharacterSize(FONT_SIZE_TITLE);
		text.setString("Last report");
		text.setPosition(posX, posY);
		addView(text);
		posY += 32;
		
		_lbState = ViewFactory.getInstance().createTextView();
		_lbState.setCharacterSize(FONT_SIZE);
		_lbState.setPosition(posX, posY);
		_lbState.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				_ui.select(_lastStatus.getTip());
			}
		});
		addView(_lbState);
	}

	private void createBasicInformation(int x, int y) {
		int posY = y;
		int posX = x;
		
		TextView text = ViewFactory.getInstance().createTextView();
		text.setCharacterSize(FONT_SIZE_TITLE);
		text.setString("Staff record");
		text.setPosition(posX, posY);
		addView(text);
		posY += 32;
		
		// Name
		_lbOld = ViewFactory.getInstance().createTextView();
		_lbOld.setCharacterSize(FONT_SIZE);
//		_lbOld.setPosition(new Vector2f(FRAME_WIDTH - 65, Constant.UI_PADDING_V + 9));
		_lbOld.setPosition(posX, posY);
		addView(_lbOld);
		posY += 20;

		_lbGender = ViewFactory.getInstance().createTextView();
		_lbGender.setCharacterSize(FONT_SIZE);
		_lbGender.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				_ui.select(ToolTips.GENDER);
			}
		});
		_lbGender.setPosition(posX, posY);
		addView(_lbGender);
		posY += 20;

		_lbProfession = ViewFactory.getInstance().createTextView();
		_lbProfession.setCharacterSize(FONT_SIZE);
		_lbProfession.setPosition(posX, posY);
		addView(_lbProfession);
		posY += 20;

		_lbEnlisted = ViewFactory.getInstance().createTextView();
		_lbEnlisted.setCharacterSize(FONT_SIZE);
		_lbEnlisted.setPosition(posX, posY);
		addView(_lbEnlisted);
		posY += 20;

		_lbBirthName = ViewFactory.getInstance().createTextView();
		_lbBirthName.setCharacterSize(FONT_SIZE);
		_lbBirthName.setPosition(posX, posY);
		addView(_lbBirthName);
		posY += 20;
	}

	private void createDebug(int x, int y) {
		_layoutDebug = ViewFactory.getInstance().createFrameLayout(200, 200);
		_layoutDebug.setVisible(true);
		_layoutDebug.setPosition(x, y);
		addView(_layoutDebug);

		TextView lbDebug = ViewFactory.getInstance().createTextView();
		lbDebug.setCharacterSize(FONT_SIZE_TITLE);
		lbDebug.setPosition(0, 0);
		lbDebug.setString("Debug");
		_layoutDebug.addView(lbDebug);

		_debugEntries = new TextView[NB_MAX_RELATION];
		for (int i = 0; i < NB_MAX_RELATION; i++) {
			_debugEntries[i] = ViewFactory.getInstance().createTextView(400, 22);
			_debugEntries[i].setCharacterSize(FONT_SIZE);
			_debugEntries[i].setPosition(0, 32 + 22 * i);
			_layoutDebug.addView(_debugEntries[i]);

//			_debugEntriesValue[i] = new TextView(new Vector2f(100, 32));
//			_debugEntriesValue[i].setCharacterSize(FONT_SIZE_SMALL);
//			_debugEntriesValue[i].setPosition(280, 32 + 22 * i);
//			_layoutDebug.addView(_debugEntriesValue[i]);
		}		
	}

	private void createInventoryInfo(int x, int y) {
		_layoutInventory = ViewFactory.getInstance().createFrameLayout(FRAME_WIDTH, 80);
		_layoutInventory.setPosition(x, y);
		addView(_layoutInventory);
		
		_lbInventory = ViewFactory.getInstance().createTextView(FRAME_WIDTH, LINE_HEIGHT);
		_lbInventory.setCharacterSize(FONT_SIZE_TITLE);
		_lbInventory.setPosition(0, 0);
		_layoutInventory.addView(_lbInventory);

		_lbInventoryEntries = new ImageView[Constant.CHARACTER_INVENTORY_SPACE];
		for (int i = 0; i < Constant.CHARACTER_INVENTORY_SPACE; i++) {
			final int x2 = i % NB_INVENTORY_PER_LINE;
			final int y2 = i / NB_INVENTORY_PER_LINE;
			_lbInventoryEntries[i] = ViewFactory.getInstance().createImageView();
			_lbInventoryEntries[i].setPosition(x2 * 28, 32 + y2 * 28);
			_lbInventoryEntries[i].setOnFocusListener(new OnFocusListener() {
				@Override
				public void onExit(View view) {
					_lbTip.setVisible(false);
				}
				
				@Override
				public void onEnter(View view) {
					_lbTip.setVisible(true);
					_lbTip.setPosition(x2 * 28 + 16, 32 + y2 * 28 + 16);
				}
			});
			_layoutInventory.addView(_lbInventoryEntries[i]);
		}
	}

	private void createJobInfo(int x, int y) {
		_layoutJob = ViewFactory.getInstance().createFrameLayout(FRAME_WIDTH, 80);
		_layoutJob.setPosition(x, y);
		addView(_layoutJob);
		
		_lbJob = ViewFactory.getInstance().createTextView(FRAME_WIDTH, LINE_HEIGHT);
		_lbJob.setCharacterSize(FONT_SIZE_TITLE);
		_lbJob.setPosition(0, 0);
		_lbJob.setString(Strings.LB_CHARACTER_INFO_JOB);
		_layoutJob.addView(_lbJob);

		_lbJob2 = ViewFactory.getInstance().createTextView();
		_lbJob2.setCharacterSize(FONT_SIZE);
		_lbJob2.setPosition(0, 32);
		_layoutJob.addView(_lbJob2);
	}

	private void createProfessionInfo(int x, int y) {
		_layoutProfession = ViewFactory.getInstance().createFrameLayout(FRAME_WIDTH, 80);
		_layoutProfession.setPosition(x, y);
		addView(_layoutProfession);

		TextView lbTitle = ViewFactory.getInstance().createTextView();
		lbTitle.setCharacterSize(FONT_SIZE_TITLE);
		lbTitle.setPosition(Constant.UI_PADDING_H, Constant.UI_PADDING_V);
		lbTitle.setString(Strings.LB_PROFESSION);
		_layoutProfession.addView(lbTitle);

		_lbProfession = ViewFactory.getInstance().createTextView();
		_lbProfession.setCharacterSize(FONT_SIZE);
		_lbProfession.setString(Strings.LB_PROFESSION);
		_lbProfession.setPosition(Constant.UI_PADDING_H * 2, Constant.UI_PADDING_V + 32);
		_layoutProfession.addView(_lbProfession);
	}

	private void createNeedsInfo(int x, int y) {
		_layoutNeeds = ViewFactory.getInstance().createFrameLayout(FRAME_WIDTH, 370);
		_layoutNeeds.setPosition(x, Constant.UI_PADDING_V + y);
		addView(_layoutNeeds);

		TextView text = ViewFactory.getInstance().createTextView();
		text.setCharacterSize(FONT_SIZE_TITLE);
		text.setString("Monitoring");
		text.setPosition(0, 0);
		_layoutNeeds.addView(text);

		for (int i = 0; i < NB_GAUGE; i++) {
			addGauge(x + 200 * (i % 2),
					y + 50 * (i / 2) + (FONT_SIZE_TITLE + 24),
					160,
					12,
					i);
		}
	}

	private void createRelationShip(int x, int y) {
		_layoutFamily = ViewFactory.getInstance().createFrameLayout(200, 200);
		_layoutFamily.setPosition(x, y);
		addView(_layoutFamily);

		TextView lbFamily = ViewFactory.getInstance().createTextView();
		lbFamily.setCharacterSize(FONT_SIZE_TITLE);
		lbFamily.setString("Relationship");
		_layoutFamily.addView(lbFamily);

		_familyEntries = new TextView[NB_MAX_RELATION];
		_familyRelationEntries = new TextView[NB_MAX_RELATION];
		for (int i = 0; i < NB_MAX_RELATION; i++) {
			_familyEntries[i] = ViewFactory.getInstance().createTextView(400, 22);
			_familyEntries[i].setCharacterSize(FONT_SIZE);
			_familyEntries[i].setPosition(0, 32 + 22 * i);
			_layoutFamily.addView(_familyEntries[i]);

			_familyRelationEntries[i] = ViewFactory.getInstance().createTextView(100, 32);
			_familyRelationEntries[i].setCharacterSize(FONT_SIZE);
			_familyRelationEntries[i].setPosition(280, 32 + 22 * i);
			_layoutFamily.addView(_familyRelationEntries[i]);
		}
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
						_ui.select(relation.getSecond());
					}
				});

				String right = relation.getRelationLabel();
				switch (relation.getRelation()) {
				case CHILDREN:
				case BROTHER:
				case SISTER:
				case HALF_BROTHER:
				case HALF_SISTER:
					right += " (" + (int)relation.getSecond().getOld() + "yo)";
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

	public CharacterModel getCharacter() { return _character; }

	//	void	addMessage(int posX, int posY, int width, int height, CharacterNeeds.Message value, RenderStates _renderEffect) {
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
	//	  _app.draw(text, _renderEffect);
	//	}

	void  addGauge(int posX, int posY, int width, int height, int index) {
		_shapes[index] = ViewFactory.getInstance().createColorView(width, height);
		// TODO
		//_shapes[index].getData().setTexture(SpriteManager.getInstance().getTexture());
		_shapes[index].setPosition(posX, posY + 42 + FONT_SIZE_TITLE + 2);

		_values[index] = ViewFactory.getInstance().createTextView();
		_values[index].setCharacterSize(FONT_SIZE);
		_values[index].setPosition(posX, posY);
		addView(_values[index]);

		//	    Text text = new Text();
		//	    text.setString(label);
		//	    text.setFont(_font);
		//	    text.setCharacterSize(MENU_CHARACTER_FONT_SIZE);
		//	    text.setStyle(Text.REGULAR);
		//	    text.setPosition(posX, posY);
		//	    _app.draw(text, _renderEffect);
		//
		//	    RectangleShape shapeBg = new RectangleShape();
		//	    shapeBg.setSize(new Vector2f(width, height));
		//	    shapeBg.setFillColor(new Color(100, 200, 0));
		//	    shapeBg.setPosition(posX, posY + MENU_CHARACTER_FONT_SIZE + 8);
		//	    _app.draw(shapeBg, _renderEffect);
		//
		//	    RectangleShape shape = new RectangleShape();
		//	    shape.setSize(new Vector2f(width * value / 100, height));
		//	    shape.setFillColor(new Color(200, 255, 0));
		//	    shape.setPosition(posX, posY + MENU_CHARACTER_FONT_SIZE + 8);
		//	    _app.draw(shape, _renderEffect);
	}

	@Override
	public void onRefresh(int update) {
		CharacterModel character = _ui.getSelectedCharacter();
		if (_character != character && character != null) {
			if (_character != null) {
				_character.setSelected(false);
			}
			character.setSelected(true);
			_lbName.setString(character.getName().toUpperCase());
			_lbName.setColor(character.getColor());
			_nbRelation = -1;			
			
			// Reset gauges
			for (int i = 0; i < NB_GAUGE; i++) {
				_values[i].setVisible(false);
			}
			_animGauge = 0;
			_character = character;
		}
		_character = character;
		
		_cursor.setVisible(!_cursor.isVisible());
		
		_animGauge++;

		if (_animRemain > 0) {
			return;
		}
		
		if (_character != null) {
			
			refreshJob(_character.getJob());
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
			Level level = _character.getStatus().getLevel();
			switch (level) {
			case GOOD: _lbState.setColor(COLOR_0); break;
			case MEDIUM: _lbState.setColor(COLOR_1); break;
			case BAD: _lbState.setColor(COLOR_2); break;
			case REALLY_BAD: _lbState.setColor(COLOR_3); break;
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
			final ProfessionModel profession = _character.getProfession();
			if (!profession.equals(_lastProfession)) {
				_lastProfession = profession;
				_lbProfession.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						switch (profession.getType()) {
						case CHILD: _ui.select(ToolTips.PROFESSION_CHILD); break;
						case DOCTOR: _ui.select(ToolTips.PROFESSION_DOCTOR); break;
						case ENGINEER: _ui.select(ToolTips.PROFESSION_ENGINEER); break;
						case NONE: _ui.select(ToolTips.PROFESSION_NONE); break;
						case OPERATION: _ui.select(ToolTips.PROFESSION_OPERATION); break;
						case SCIENCE: _ui.select(ToolTips.PROFESSION_SCIENCE); break;
						case SECURITY: _ui.select(ToolTips.PROFESSION_SECURITY); break;
						case STUDENT: _ui.select(ToolTips.PROFESSION_STUDENT); break;
						}
					}
				});
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
	
	private void refreshJob(final Job job) {
		if (job != null) {
			_lbJob2.setString(StringUtils.getDashedString(job.getShortLabel(), job.hasDuration() ? job.getFormatedDuration() : "", NB_COLUMNS));
			switch (job.getAction()) {
			case BUILD:
			case DESTROY:
			case GATHER:
			case MINING:
			case REFILL:
			case USE:
				_lbJob2.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						_ui.select(job.getItem());
					}
				});
				break;
			case USE_INVENTORY:
				_lbJob2.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						_ui.select(job.getItem().getInfo());
					}
				});
				break;
			default:
				_lbJob2.setOnClickListener(null);
				break;
			
			}
		} else {
			_lbJob2.setString(Strings.LN_NO_JOB);
			_lbJob2.setOnClickListener(null);
		}
	}

	private void refreshDebug() {
		if (Settings.getInstance().isDebug() && _debugEntries != null) {
			_debugEntries[0].setString(StringUtils.getDashedString("Old", String.valueOf(_character.getOld()), NB_COLUMNS));
			_debugEntries[1].setString(StringUtils.getDashedString("NextChild", String.valueOf(_character.getNextChildAtOld()), NB_COLUMNS));
			_debugEntries[2].setString(StringUtils.getDashedString("IsGay", String.valueOf(_character.isGay()), NB_COLUMNS));
			_debugEntries[3].setString(StringUtils.getDashedString("Gender", String.valueOf(_character.getGender()), NB_COLUMNS));
		}

	}

	private void refreshInventory() {
		_lbInventory.setString(StringUtils.getDashedString(Strings.LB_INVENTORY,
				_character.getInventorySpace() - _character.getInventoryLeftSpace() + "/" + _character.getInventorySpace(), 29));
		
		for (int i = 0; i < Constant.CHARACTER_INVENTORY_SPACE; i++) {
			if (_character.getInventory().size() > i) {
				ItemBase item = _character.getInventory().get(i);
				_lbInventoryEntries[i].setImage(SpriteManager.getInstance().getIcon(item.getInfo()));
				_lbTip.setString(item.getName());
			} else {
				_lbInventoryEntries[i].setImage(null);
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
			_shapes[i].setSize((int)size, 12);
            // TODO
//			_shapes[i].setTextureRect(new IntRect(0, level * 16, (int)size, 12));
			
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

	public void onDraw(GFXRenderer renderer, RenderEffect effect) {
		// Play anim
		if (_animFrame++ % 4 == 0) {
			if (_animRemain > 0) {
				anim();
			}
		}
		
		if (_character != null) {
			for (int i = 0; i < _animGauge && i < NB_GAUGE; i++) {
				_values[i].setVisible(true);
				renderer.draw(_shapes[i], this._effect);
			}
		}
	}
}
