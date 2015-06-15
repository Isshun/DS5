package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.*;
import org.smallbox.faraway.engine.ui.*;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.engine.util.StringUtils;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.model.BuffModel;
import org.smallbox.faraway.model.CharacterBuffModel;
import org.smallbox.faraway.model.ProfessionModel;
import org.smallbox.faraway.model.ToolTips;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.character.CharacterModel.Gender;
import org.smallbox.faraway.model.character.CharacterNeeds;
import org.smallbox.faraway.model.character.CharacterRelation;
import org.smallbox.faraway.model.job.JobModel;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface.Mode;

import java.util.List;
import java.util.stream.Collectors;

public class PanelCharacter extends BaseRightPanel {
    private static final int NB_MAX_BUFFS = 20;
    private ViewFactory _viewFactory;

    private static final String[] texts = {"Food", "Oxygen", "Happiness", "Energy", "Relation", "Security", "Health", "Sickness", "Injuries", "Satiety", "unused", "Work"};

    private static final int NB_GAUGE = 7;
    private static final int NB_MAX_RELATION = 18;
    private static final int NB_INVENTORY_PER_LINE = 10;

    private static final int NB_COLUMNS_NEEDS = 22;

    private static final Color COLOR_0 = new Color(120, 255, 255);
    private static final Color COLOR_1 = new Color(209, 203, 69);
    private static final Color COLOR_2 = new Color(247, 57, 57);
    private static final Color COLOR_3 = new Color(247, 57, 57);

    private CharacterModel 		_character;
    private TextView 			_lbName;
    private TextView 			_lbProfession;
    private ColorView 			_cursor;
    private ImageView[] 		_shapes = new ImageView[NB_GAUGE];
    private TextView[] 			_values = new TextView[NB_GAUGE];
    private TextView 			_lbJob;
    private TextView[] 			_lbBuffs = new TextView[NB_MAX_BUFFS];
    private TextView[] 			_familyEntries;
    private TextView[] 			_familyRelationEntries;
    private int 				_nbRelation;
    private TextView 			_lbOld;

    private int 				_animRemain;
    private TextView	 		_lbGender;
    private int 				_lastOld;
    private Gender				_lastGender;
    private ProfessionModel     _lastProfession;
    private TextView 			_animLabel;
    private String 				_animValue;
    private int 				_animFrame;
    private int 				_animGauge;
    private String 				_lastEnlisted;
    private TextView 			_lbEnlisted;
    private String 				_lastBirthName;
    private TextView 			_lbBirthName;
    private TextView 			_lbInventory;

    public PanelCharacter(Mode mode, GameEventListener.Key shortcut) {
        super(mode, shortcut, "data/ui/panels/info_character.yml");
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        findById("bt_monitoring").setOnClickListener(view -> switchView("frame_monitoring"));
        findById("bt_personal_report").setOnClickListener(view -> switchView("frame_personal_report"));
        findById("bt_priorities").setOnClickListener(view -> switchView("frame_priorities"));
        findById("bt_inventory").setOnClickListener(view -> switchView("frame_inventory"));
        findById("bt_health").setOnClickListener(view -> switchView("frame_health"));

        createNeedsInfo();
        createBasicInformation();
        createRelationShip();
        createInventoryInfo();

        findById("frame_monitoring").setVisible(true);
        findById("frame_personal_report").setVisible(false);
        findById("frame_priorities").setVisible(false);
        findById("frame_inventory").setVisible(false);
        findById("frame_health").setVisible(false);

        _lbName = (TextView) findById("lb_name");
        _lbJob = (TextView) findById("lb_current_job");

        FrameLayout frameBuffs = (FrameLayout)findById("frame_buffs_entries");
        for (int i = 0; i < NB_MAX_BUFFS; i++) {
            _lbBuffs[i] = ViewFactory.getInstance().createTextView();
            _lbBuffs[i].setCharacterSize(14);
            _lbBuffs[i].setPosition(0, 20 * i);
            frameBuffs.addView(_lbBuffs[i]);
        }
    }

    private void switchView(String viewId) {
        findById("frame_monitoring").setVisible("frame_monitoring".equals(viewId));
        findById("frame_priorities").setVisible("frame_priorities".equals(viewId));
        findById("frame_personal_report").setVisible("frame_personal_report".equals(viewId));
        findById("frame_inventory").setVisible("frame_inventory".equals(viewId));
        findById("frame_health").setVisible("frame_health".equals(viewId));
    }

    private void createPriorities() {
        FrameLayout frameEntries = (FrameLayout) findById("frame_priorities_entries");
        frameEntries.clearAllViews();

        for (CharacterModel.TalentEntry priority: _character.getTalents()) {
            TextView lbPriority = _viewFactory.createTextView();
            lbPriority.setString(priority.name + " (" + (int)priority.level + ")");
            lbPriority.setCharacterSize(16);
            lbPriority.resetSize();
            lbPriority.setPosition(0, priority.index * 28);
            frameEntries.addView(lbPriority);

            TextView btPriorityUp = _viewFactory.createTextView();
            btPriorityUp.setString("[up]");
            btPriorityUp.setCharacterSize(16);
            btPriorityUp.resetSize();
            btPriorityUp.setPosition(240, priority.index * 28);
            btPriorityUp.setOnClickListener(view -> _character.movePriority(priority, priority.index - 1));
            frameEntries.addView(btPriorityUp);

            TextView btPriorityDown = _viewFactory.createTextView();
            btPriorityDown.setString("[down]");
            btPriorityDown.setCharacterSize(16);
            btPriorityDown.resetSize();
            btPriorityDown.setPosition(280, priority.index * 28);
            btPriorityDown.setOnClickListener(view -> _character.movePriority(priority, priority.index + 1));
            frameEntries.addView(btPriorityDown);
        }
    }

    @Override
    protected void onCreate(ViewFactory factory) {
        _viewFactory = factory;

        _cursor = factory.createColorView(8, 16);
        _cursor.setBackgroundColor(Colors.LINK_INACTIVE);
        addView(_cursor);

//        // Tip
//        _lbTip = factory.createTextView(FRAME_WIDTH, LINE_HEIGHT);
//        _lbTip.setCharacterSize(FONT_SIZE_TITLE);
//        _lbTip.setBackgroundColor(new Color(255, 255, 255, 100));
//        _lbTip.setVisible(false);
//        addView(_lbTip);
    }

    private void createBasicInformation() {
        _lbOld = (TextView) findById("lb_info_old");

        _lbGender = (TextView) findById("lb_info_gender");
        _lbGender.setOnClickListener(view -> _ui.select(ToolTips.GENDER));

        _lbProfession = (TextView) findById("lb_info_profession");

        _lbEnlisted = (TextView) findById("lb_info_enlisted");

        _lbBirthName = (TextView) findById("lb_info_birthname");
    }

    private void createInventoryInfo() {
        _lbInventory = (TextView) findById("lb_inventory");

        FrameLayout frameInventoryEntries = (FrameLayout)findById("frame_inventory_entries");
        frameInventoryEntries.clearAllViews();
        ImageView[] lbInventoryEntries = new ImageView[Constant.CHARACTER_INVENTORY_SPACE];
        for (int i = 0; i < Constant.CHARACTER_INVENTORY_SPACE; i++) {
            final int x2 = i % NB_INVENTORY_PER_LINE;
            final int y2 = i / NB_INVENTORY_PER_LINE;
            lbInventoryEntries[i] = ViewFactory.getInstance().createImageView();
            lbInventoryEntries[i].setPosition(x2 * 28, 32 + y2 * 28);
            frameInventoryEntries.addView(lbInventoryEntries[i]);
        }
    }

    private void createNeedsInfo() {
        for (int i = 0; i < NB_GAUGE; i++) {
            addGauge(i);
        }
    }

    private void createRelationShip() {
        FrameLayout layoutFamily = (FrameLayout) findById("frame_relationship");

        _familyEntries = new TextView[NB_MAX_RELATION];
        _familyRelationEntries = new TextView[NB_MAX_RELATION];
        for (int i = 0; i < NB_MAX_RELATION; i++) {
            _familyEntries[i] = ViewFactory.getInstance().createTextView(400, 22);
            _familyEntries[i].setCharacterSize(FONT_SIZE);
            _familyEntries[i].setPosition(0, 32 + 22 * i);
            layoutFamily.addView(_familyEntries[i]);

            _familyRelationEntries[i] = ViewFactory.getInstance().createTextView(100, 32);
            _familyRelationEntries[i].setCharacterSize(FONT_SIZE);
            _familyRelationEntries[i].setPosition(280, 32 + 22 * i);
            layoutFamily.addView(_familyRelationEntries[i]);
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
                _familyEntries[i].setOnClickListener(view -> _ui.select(relation.getSecond()));

                String relationName = relation.getRelationLabel();
                switch (relation.getRelation()) {
                    case CHILDREN:
                    case BROTHER:
                    case SISTER:
                    case HALF_BROTHER:
                    case HALF_SISTER:
                        relationName += " (" + (int)relation.getSecond().getOld() + "yo)";
                        break;
                    default: break;
                }

                _familyEntries[i].setString(StringUtils.getDashedString(left, relationName, NB_COLUMNS));

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

    void  addGauge(int index) {
        switch (index) {
            case 0:
                _values[index] = (TextView)findById("lb_status_food");
                _shapes[index] = (ImageView)findById("img_status_food");
                _shapes[index].setImage(SpriteManager.getInstance().getIcon("data/res/needbar.png"));
                break;
            case 1:
                _values[index] = (TextView)findById("lb_status_o2");
                _shapes[index] = (ImageView)findById("img_status_o2");
                _shapes[index].setImage(SpriteManager.getInstance().getIcon("data/res/needbar.png"));
                break;
            case 2:
                _values[index] = (TextView)findById("lb_status_happiness");
                _shapes[index] = (ImageView)findById("img_status_happiness");
                _shapes[index].setImage(SpriteManager.getInstance().getIcon("data/res/needbar.png"));
                break;
            case 3:
                _values[index] = (TextView)findById("lb_status_power");
                _shapes[index] = (ImageView)findById("img_status_power");
                _shapes[index].setImage(SpriteManager.getInstance().getIcon("data/res/needbar.png"));
                break;
            case 4:
                _values[index] = (TextView)findById("lb_status_relation");
                _values[index].setOnFocusListener(null);
                _values[index].setOnClickListener(view -> switchView("frame_personal_report"));
                _shapes[index] = (ImageView)findById("img_status_relation");
                _shapes[index].setImage(SpriteManager.getInstance().getIcon("data/res/needbar.png"));
                break;
            case 5:
                _values[index] = (TextView)findById("lb_status_security");
                _shapes[index] = (ImageView)findById("img_status_security");
                _shapes[index].setImage(SpriteManager.getInstance().getIcon("data/res/needbar.png"));
                break;
            case 6:
                _values[index] = (TextView)findById("lb_status_health");
                _values[index].setOnFocusListener(null);
                _values[index].setOnClickListener(view -> switchView("frame_health"));
                _shapes[index] = (ImageView)findById("img_status_health");
                _shapes[index].setImage(SpriteManager.getInstance().getIcon("data/res/needbar.png"));
                break;
        }
    }

    @Override
    public void onRefresh(int update) {
        CharacterModel character = _ui.getSelectedCharacter();
        if (character != null && (_character != character || character.needRefresh())) {

            if (_character != null) {
                _character.setSelected(false);
            }

            _character = character;
            _character.setSelected(true);
            _lbName.setString(_character.getName().toUpperCase());
            _lbName.setColor(_character.getColor());
            _nbRelation = -1;

//            // Reset gauges
//            for (int i = 0; i < NB_GAUGE; i++) {
//                _values[i].setVisible(false);
//            }
//            _animGauge = 0;

            createPriorities();
        }

        _cursor.setVisible(!_cursor.isVisible());

        _animGauge++;

//        if (_animRemain > 0) {
//            return;
//        }

        if (_character != null) {
            refreshJob(_character.getJob());
            refreshNeeds();
            refreshInventory();

            // Relations
            if (update % 20 == 0 || _nbRelation != _character.getNbRelations()) {
                refreshRelations();
            }

            refreshLastReports();
            refreshInfo();

            ((TextView)findById("lb_body_heat")).setString("Body heat: " + (int)(_character.getBodyHeat() * 10) / 10f);
        }
    }

    private void refreshLastReports() {
        // TODO: heavy
        List<BuffModel.BuffLevelModel> buffs = _character.getBuffs().stream()
                .filter(CharacterBuffModel::isActive)
                .map(CharacterBuffModel::getActiveLevel)
                .collect(Collectors.toList());

        for (int i = 0; i < NB_MAX_BUFFS; i++) {
            if (i < buffs.size()) {
                _lbBuffs[i].setString(StringUtils.getDashedString(
                        buffs.get(i).label,
                        (buffs.get(i).effects.mood > 0 ? "+" : "") + buffs.get(i).effects.mood, NB_COLUMNS));
                _lbBuffs[i].setColor(buffs.get(i).effects.mood < 0 ? COLOR_2 : COLOR_0);
                _lbBuffs[i].setVisible(true);
            } else {
                _lbBuffs[i].setVisible(false);
            }
        }
    }

    private void refreshInfo() {
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
            _lbProfession.setOnClickListener(view -> {
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

    private void refreshJob(final JobModel job) {
        if (job != null) {
            _lbJob.setString(StringUtils.getDashedString(job.getLabel(), job.getProgressPercent() + "%", NB_COLUMNS));
            if (job.getItem() != null) {
                _lbJob.setOnClickListener(view -> _ui.select(job.getItem()));
            }
        } else if (_character.isSleeping()) {
            _lbJob.setString("Sleep on floor");
            _lbJob.setOnClickListener(null);
        } else {
            _lbJob.setString(Strings.LN_NO_JOB);
            _lbJob.setOnClickListener(null);
        }
    }

    private void refreshInventory() {
        _lbInventory.setString(StringUtils.getDashedString(Strings.LB_INVENTORY,
            _character.getInventorySpace() - _character.getInventoryLeftSpace() + "/" + _character.getInventorySpace(), 29));

        if (_character.getInventory() != null) {
            ((TextView) findById("lb_inventory_entry")).setString(_character.getInventory().getLabel() + " (" + _character.getInventory().getQuantity() + ")");
        }

//        for (int i = 0; i < Constant.CHARACTER_INVENTORY_SPACE; i++) {
//            if (_character.getInventory().size() > i) {
//                MapObjectModel item = _character.getInventory().getRoom(i);
//                _lbInventoryEntries[i].setImage(SpriteManager.getInstance().getIcon(item.getInfo()));
//            } else {
//                _lbInventoryEntries[i].setImage(null);
//            }
//        }
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

            if (_shapes[i] != null) {
                _shapes[i].setSize((int) size, 12);
                _shapes[i].setTextureRect(0, level * 16, (int) size, 12);
            }

            if (_values[i] != null) {
                _values[i].setString(StringUtils.getDashedString(texts[i], String.valueOf(value), NB_COLUMNS_NEEDS));
                _values[i].setColor(color);
            }
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

//        if (_character != null) {
//            for (int i = 0; i < _animGauge && i < NB_GAUGE; i++) {
//                _values[i].setVisible(true);
//                renderer.draw(_shapes[i], this._effect);
//            }
//        }
    }
}
