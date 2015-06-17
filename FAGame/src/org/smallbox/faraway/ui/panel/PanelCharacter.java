package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.*;
import org.smallbox.faraway.engine.ui.*;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.engine.util.StringUtils;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.model.*;
import org.smallbox.faraway.model.character.base.CharacterModel;
import org.smallbox.faraway.model.character.base.CharacterModel.Gender;
import org.smallbox.faraway.model.character.base.CharacterNeeds;
import org.smallbox.faraway.model.character.base.CharacterRelation;
import org.smallbox.faraway.model.job.JobModel;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface.Mode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PanelCharacter extends BaseRightPanel {
    private static final int    NB_MAX_BUFFS = 20;
    private static final int    NB_GAUGE = 8;
    private static final int    NB_MAX_RELATION = 18;
    private static final int    NB_INVENTORY_PER_LINE = 10;
    private static final int    NB_COLUMNS_NEEDS = 22;

    private static final Color  COLOR_BUTTON_INACTIVE = new Color(0x298596);
    private static final Color  COLOR_BUTTON_ACTIVE = new Color(0xafcd35);
    private ViewFactory _viewFactory;

    private static final String[] texts = {"Food", "Oxygen", "Happiness", "Energy", "Relation", "Security", "Health", "Amusement", "Injuries", "Satiety", "unused", "Work"};

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

        findById("frame_equipment_detail").setVisible(false);
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
        frameEntries.removeAllViews();

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
        frameInventoryEntries.removeAllViews();
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
            switch (i) {
                case 0:
                    _values[i] = (TextView)findById("lb_status_food");
                    _shapes[i] = (ImageView)findById("img_status_food");
                    break;
                case 1:
                    _values[i] = (TextView)findById("lb_status_o2");
                    _shapes[i] = (ImageView)findById("img_status_o2");
                    break;
                case 2:
                    _values[i] = (TextView)findById("lb_status_happiness");
                    _shapes[i] = (ImageView)findById("img_status_happiness");
                    break;
                case 3:
                    _values[i] = (TextView)findById("lb_status_power");
                    _shapes[i] = (ImageView)findById("img_status_power");
                    break;
                case 4:
                    _values[i] = (TextView)findById("lb_status_relation");
                    if (_values[i] != null) {
                        _values[i].setOnFocusListener(null);
                        _values[i].setOnClickListener(view -> switchView("frame_personal_report"));
                    }
                    _shapes[i] = (ImageView)findById("img_status_relation");
                    break;
                case 5:
                    _values[i] = (TextView)findById("lb_status_security");
                    _shapes[i] = (ImageView)findById("img_status_security");
                    break;
                case 6:
                    _values[i] = (TextView)findById("lb_status_health");
                    if (_values[i] != null) {
                        _values[i].setOnFocusListener(null);
                        _values[i].setOnClickListener(view -> switchView("frame_health"));
                    }
                    _shapes[i] = (ImageView)findById("img_status_health");
                    break;
                case 7:
                    _values[i] = (TextView)findById("lb_status_joy");
                    _shapes[i] = (ImageView)findById("img_status_joy");
                    break;
            }

            // Set texture
            if (_shapes[i] != null) {
                _shapes[i].setImage(SpriteManager.getInstance().getIcon("data/res/needbar.png"));
            }
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

    @Override
    public void onRefresh(int update) {
        CharacterModel character = _ui.getSelectedCharacter();
        if (character != null && character != _character) {
            onCharacterSelect(character);
        }

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
            refreshEquipments();

            // Relations
            if (update % 20 == 0 || _nbRelation != _character.getNbRelations()) {
                refreshRelations();
            }

            refreshLastReports();
            refreshInfo();
            refreshDebug();

            ((TextView)findById("lb_body_heat")).setString("Body heat: " + (int)(_character.getBodyHeat() * 10) / 10f);
        }
    }

    private void onCharacterSelect(CharacterModel character) {
        // Display need frame
        FrameLayout frameNeeds = (FrameLayout) findById("frame_needs");
        frameNeeds.removeAllViews();
        ViewFactory.getInstance().load(character.getNeedViewPath(), frameNeeds::addView);
        createNeedsInfo();

        // Display equipment frame
        FrameLayout frameEquipmentBody = (FrameLayout) findById("frame_equipment_body");
        frameEquipmentBody.removeAllViews();
        ViewFactory.getInstance().load(character.getEquipmentViewPath(), frameEquipmentBody::addView);
    }

    private void refreshEquipments() {
        if (_character.getEquipmentViewIds() != null) {
            for (String[] equipmentViewId : _character.getEquipmentViewIds()) {
                setEquipment((TextView) findById(equipmentViewId[0]), equipmentViewId[1]);
            }

            Map<String, Integer> totalResist = new HashMap<>();
            Map<String, Integer> totalAbsorb = new HashMap<>();
            Map<String, Integer> totalBuff = new HashMap<>();
            for (EquipmentModel equipment : _character.getEquipments()) {
                if (equipment.effects != null) {
                    for (EquipmentModel.EquipmentEffect effect : equipment.effects) {
                        // Check resist
                        if (effect.resist != null) {
                            checkAndAddEquipmentEffect(totalResist, "cold", effect.resist.cold);
                            checkAndAddEquipmentEffect(totalResist, "heat", effect.resist.heat);
                            checkAndAddEquipmentEffect(totalResist, "damage", effect.resist.damage);
                        }

                        // Check absorb
                        if (effect.absorb != null) {
                            checkAndAddEquipmentEffect(totalAbsorb, "cold", effect.absorb.cold);
                            checkAndAddEquipmentEffect(totalAbsorb, "heat", effect.absorb.heat);
                            checkAndAddEquipmentEffect(totalAbsorb, "damage", effect.absorb.damage);
                        }

                        // Check buff
                        if (effect.buff != null) {
                            checkAndAddEquipmentEffect(totalBuff, "sight", effect.buff.sight);
                            checkAndAddEquipmentEffect(totalBuff, "grow", effect.buff.grow);
                            checkAndAddEquipmentEffect(totalBuff, "repair", effect.buff.repair);
                            checkAndAddEquipmentEffect(totalBuff, "build", effect.buff.build);
                            checkAndAddEquipmentEffect(totalBuff, "craft", effect.buff.craft);
                            checkAndAddEquipmentEffect(totalBuff, "cook", effect.buff.cook);
                            checkAndAddEquipmentEffect(totalBuff, "speed", effect.buff.speed);
                            checkAndAddEquipmentEffect(totalBuff, "tailoring", effect.buff.tailoring);
                        }
                    }
                }
            }

            List<String> resists = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : totalResist.entrySet()) {
                resists.add(entry.getKey() + ": " + (entry.getValue() > 0 ? "+" + entry.getValue() : entry.getValue()));
            }
            ((TextView) findById("lb_equipment_total_resist")).setString("Resists: " + String.join(", ", resists));

            List<String> absorb = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : totalAbsorb.entrySet()) {
                absorb.add(entry.getKey() + ": " + (entry.getValue() > 0 ? "+" + entry.getValue() : entry.getValue()));
            }
            ((TextView) findById("lb_equipment_total_absorb")).setString("Absorbs: " + String.join(", ", absorb));

            List<String> buff = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : totalBuff.entrySet()) {
                buff.add(entry.getKey() + ": " + (entry.getValue() > 0 ? "+" + entry.getValue() : entry.getValue()));
            }
            ((TextView) findById("lb_equipment_total_buff")).setString("Buffs: " + String.join(", ", buff));
        }
    }

    private void refreshDebug() {
        if (_character != null) {
            ((TextView)findById("lb_environment")).setString("Environment: " + Game.getWorldManager().getEnvironmentValue(_character.getX(), _character.getY(), GameData.config.environmentDistance));
//            ((TextView)findById("lb_light")).setString();
        }
    }

    private void refreshLastReports() {
        int i = 0;
        for (CharacterBuffModel characterBuff: _character.getBuffs()) {
            if (characterBuff.level != null) {
                int mood = characterBuff.level.effects != null ? characterBuff.level.effects.mood : 0;
                _lbBuffs[i].setString(StringUtils.getDashedString(characterBuff.level.label, (mood > 0 ? "+" : "") + mood, NB_COLUMNS));
                _lbBuffs[i].setColor(mood < 0 ? COLOR_2 : COLOR_0);
                i++;
            }
        }
        for (; i < NB_MAX_BUFFS; i++) {
            _lbBuffs[i].setString("");
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

    private void selectEquipment(View view, EquipmentModel equipment) {
        for (String[] equipmentViewId: _character.getEquipmentViewIds()) {
            findById(equipmentViewId[0]).setBackgroundColor(COLOR_BUTTON_INACTIVE);
        }

        view.setBackgroundColor(COLOR_BUTTON_ACTIVE);

        findById("frame_equipment_detail").setPosition(view.getPosX(), view.getPosY() + 280);
        findById("frame_equipment_detail").setVisible(false);

        if (equipment != null) {
            findById("frame_equipment_detail").setVisible(true);

            if (equipment.effects != null) {
                for (EquipmentModel.EquipmentEffect effect: equipment.effects) {
                    // Check resist
                    if (effect.resist != null) {
                        Map<String, Integer> totalStats = new HashMap<>();
                        checkAndAddEquipmentEffect(totalStats, "cold", effect.resist.cold);
                        checkAndAddEquipmentEffect(totalStats, "heat", effect.resist.heat);
                        checkAndAddEquipmentEffect(totalStats, "damage", effect.resist.damage);

                        List<String> resists = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry: totalStats.entrySet()) {
                            resists.add(entry.getKey() + ": " + (entry.getValue() > 0 ? "+" + entry.getValue() : entry.getValue()));
                        }
                        ((TextView)findById("lb_equipment_resist")).setString("[R]: " + String.join(", ", resists));
                    }

                    // Check absorb
                    if (effect.absorb != null) {
                        Map<String, Integer> totalStats = new HashMap<>();
                        checkAndAddEquipmentEffect(totalStats, "cold", effect.absorb.cold);
                        checkAndAddEquipmentEffect(totalStats, "heat", effect.absorb.heat);
                        checkAndAddEquipmentEffect(totalStats, "damage", effect.absorb.damage);

                        List<String> absorbs = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry: totalStats.entrySet()) {
                            absorbs.add(entry.getKey() + ": " + (entry.getValue() > 0 ? "+" + entry.getValue() : entry.getValue()));
                        }
                        ((TextView)findById("lb_equipment_absorb")).setString("[A]: " + String.join(", ", absorbs));
                    }

                    // Check buff
                    if (effect.buff != null) {
                        Map<String, Integer> totalStats = new HashMap<>();
                        checkAndAddEquipmentEffect(totalStats, "sight", effect.buff.sight);
                        checkAndAddEquipmentEffect(totalStats, "speed", effect.buff.speed);

                        List<String> buffs = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry: totalStats.entrySet()) {
                            buffs.add(entry.getKey() + ": " + (entry.getValue() > 0 ? "+" + entry.getValue() : entry.getValue()));
                        }
                        ((TextView)findById("lb_equipment_resist")).setString("[B]: " + String.join(", ", buffs));
                    }
                }
            }
        }
    }

    private void checkAndAddEquipmentEffect(Map<String, Integer> totalStats, String text, int value) {
        if (value != 0) {
            if (!totalStats.containsKey(text)) {
                totalStats.put(text, 0);
            }
            totalStats.put(text, totalStats.get(text) + value);
        }
    }

    private void setEquipment(TextView view, String location) {
        EquipmentModel equipment = _character.getEquipment(location);
        if (equipment != null) {
            view.setString(equipment.label);
        } else {
            view.setString("");
        }

        view.setOnClickListener(v -> selectEquipment(v, equipment));
    }

    private void refreshNeeds() {
        CharacterNeeds needs = _character.getNeeds();
        for (int i = 0; i < NB_GAUGE; i++) {
            int value = 0;
            switch (i) {
                case 0: value = Math.min(Math.max((int)needs.getFood(), 0), 100); break;
                case 1: value = Math.min(Math.max((int)needs.oxygen, 0), 100); break;
                case 2: value = Math.min(Math.max((int)needs.happiness, 0), 100); break;
                case 3: value = Math.min(Math.max((int)needs.energy, 0), 100); break;
                case 4: value = Math.min(Math.max((int)needs.relation, 0), 100); break;
                case 5: value = Math.min(Math.max((int)needs.security, 0), 100); break;
                case 6: value = Math.min(Math.max((int)needs.health, 0), 100); break;
                case 7: value = Math.min(Math.max((int)needs.joy, 0), 100); break;
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
