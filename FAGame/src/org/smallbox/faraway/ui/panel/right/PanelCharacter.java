package org.smallbox.faraway.ui.panel.right;

import org.smallbox.faraway.Strings;
import org.smallbox.faraway.engine.*;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.CharacterBuffModel;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.ToolTips;
import org.smallbox.faraway.game.model.character.base.CharacterInfoModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.character.base.CharacterNeeds;
import org.smallbox.faraway.game.model.character.base.CharacterRelation;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface.Mode;
import org.smallbox.faraway.ui.engine.*;
import org.smallbox.faraway.ui.panel.BaseRightPanel;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.StringUtils;

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
    private UILabel             _lbName;
    private ColorView 			_cursor;
    private UIImage[] 		_shapes = new UIImage[NB_GAUGE];
    private UILabel[] 			_values = new UILabel[NB_GAUGE];
    private UILabel             _lbJob;
    private UILabel[] 			_lbBuffs = new UILabel[NB_MAX_BUFFS];
    private UILabel[] 			_familyEntries;
    private UILabel[] 			_familyRelationEntries;
    private int 				_nbRelation;
    private UILabel             _lbOld;

    private int 				_animRemain;
    private int 				_lastOld;
    private CharacterInfoModel.Gender _lastGender;
    private UILabel             _animLabel;
    private String 				_animValue;
    private int 				_animFrame;
    private int 				_animGauge;
    private String 				_lastEnlisted;
    private UILabel             _lbEnlisted;
    private String 				_lastBirthName;
    private UILabel             _lbBirthName;
    private UILabel             _lbInventory;
    private View                _selectedPriority;
    private FrameLayout         _priorityOverlay;
    private int                 _priorityOverlayOffset;
    private List<View>          _priorityViews;
    private int                 _framePrioritiesPosY;
    private int                 _priorityOverlayPosition;

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

        _lbName = (UILabel) findById("lb_name");
        _lbJob = (UILabel) findById("lb_current_job");

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

    private void createTalents() {
        _priorityViews = new ArrayList<>();

        FrameLayout framePriorities = (FrameLayout) findById("frame_priorities_entries");
        framePriorities.removeAllViews();
        _framePrioritiesPosY = framePriorities.getRect().y;

        _priorityOverlay = ViewFactory.getInstance().createFrameLayout(340, 24);
        _priorityOverlay.setBackgroundColor(new Color(0x323c3e));
//        _priorityOverlay.setBackgroundColor(new Color(255, 255, 255, 100));
        _priorityOverlay.setVisible(false);
        framePriorities.addView(_priorityOverlay);

        for (CharacterModel.TalentEntry priority: _character.getTalents()) {
            FrameLayout framePriority = _viewFactory.createFrameLayout(300, 24);
            framePriority.setPosition(0, priority.index * 28);
//            framePriority.setBackgroundColor(new Color(0x88121c1e));
            framePriority.setData(priority);

            UILabel lbPriority = _viewFactory.createTextView();
            lbPriority.setString(priority.name + " (" + (int) priority.level + ")");
            lbPriority.setCharacterSize(16);
            lbPriority.setPosition(0, 8);
            lbPriority.resetSize();
            framePriority.addView(lbPriority);

            ColorView bgProgress = _viewFactory.createColorView(32, 2);
            bgProgress.setPosition(240, 21);
            bgProgress.setBackgroundColor(new Color(0x555555));
            framePriority.addView(bgProgress);

            ColorView frameProgress = _viewFactory.createColorView(20, 2);
            frameProgress.setPosition(240, 21);
            frameProgress.setBackgroundColor(new Color(0x298596));
            framePriority.addView(frameProgress);

            UILabel lbProgress = _viewFactory.createTextView(32, 14);
            lbProgress.setString(String.valueOf((int)priority.level));
            lbProgress.setCharacterSize(14);
            lbProgress.setPosition(240, 5);
            lbProgress.setAlign(Align.CENTER);
            framePriority.addView(lbProgress);

//            TextView btPriorityDown = _viewFactory.createTextView();
//            btPriorityDown.setString("[down]");
//            btPriorityDown.setCharacterSize(16);
//            btPriorityDown.resetSize();
//            btPriorityDown.setPosition(280, 0);
//            btPriorityDown.setOnClickListener(view -> _character.movePriority(priority, priority.index + 1));
//            framePriority.addView(btPriorityDown);

            framePriorities.addView(framePriority);
            _priorityViews.add(framePriority);
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
        _lbOld = (UILabel) findById("lb_info_old");

        findById("lb_info_gender").setOnClickListener(view -> _ui.select(ToolTips.GENDER));

        _lbEnlisted = (UILabel) findById("lb_info_enlisted");

        _lbBirthName = (UILabel) findById("lb_info_birthname");
    }

    private void createInventoryInfo() {
        _lbInventory = (UILabel) findById("lb_inventory");

        FrameLayout frameInventoryEntries = (FrameLayout)findById("frame_inventory_entries");
        frameInventoryEntries.removeAllViews();
        UIImage[] lbInventoryEntries = new UIImage[Constant.CHARACTER_INVENTORY_SPACE];
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
                    _values[i] = (UILabel)findById("lb_status_food");
                    _shapes[i] = (UIImage)findById("img_status_food");
                    break;
                case 1:
                    _values[i] = (UILabel)findById("lb_status_o2");
                    _shapes[i] = (UIImage)findById("img_status_o2");
                    break;
                case 2:
                    _values[i] = (UILabel)findById("lb_status_happiness");
                    _shapes[i] = (UIImage)findById("img_status_happiness");
                    break;
                case 3:
                    _values[i] = (UILabel)findById("lb_status_power");
                    _shapes[i] = (UIImage)findById("img_status_power");
                    break;
                case 4:
                    _values[i] = (UILabel)findById("lb_status_relation");
                    if (_values[i] != null) {
                        _values[i].setOnFocusListener(null);
                        _values[i].setOnClickListener(view -> switchView("frame_personal_report"));
                    }
                    _shapes[i] = (UIImage)findById("img_status_relation");
                    break;
                case 5:
                    _values[i] = (UILabel)findById("lb_status_security");
                    _shapes[i] = (UIImage)findById("img_status_security");
                    break;
                case 6:
                    _values[i] = (UILabel)findById("lb_status_health");
                    if (_values[i] != null) {
                        _values[i].setOnFocusListener(null);
                        _values[i].setOnClickListener(view -> switchView("frame_health"));
                    }
                    _shapes[i] = (UIImage)findById("img_status_health");
                    break;
                case 7:
                    _values[i] = (UILabel)findById("lb_status_joy");
                    _shapes[i] = (UIImage)findById("img_status_joy");
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

        _familyEntries = new UILabel[NB_MAX_RELATION];
        _familyRelationEntries = new UILabel[NB_MAX_RELATION];
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
        List<CharacterRelation> relations = _character.getRelations().getRelations();
        _nbRelation = relations.size();
        int i = 0;
        for (final CharacterRelation relation: relations) {
            if (i < NB_MAX_RELATION) {
                String left = relation.getSecond().getInfo().getName();
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
                _familyEntries[i].setStyle(UILabel.REGULAR);
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
            _lbName.setString(_character.getInfo().getName().toUpperCase());
            _lbName.setColor(_character.getInfo().getColor());
            _nbRelation = -1;

//            // Reset gauges
//            for (int i = 0; i < NB_GAUGE; i++) {
//                _values[i].setVisible(false);
//            }
//            _animGauge = 0;

            createTalents();
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
            if (update % 20 == 0 || _nbRelation != _character.getRelations().getRelations().size()) {
                refreshRelations();
            }

            refreshLastReports();
            refreshInfo();
            refreshDebug();

            ((UILabel)findById("lb_body_heat")).setString("Body heat: " + (int)(_character.getBodyHeat() * 10) / 10f);
        }

        refreshTalents();
    }

    private void refreshTalents() {
        if (_selectedPriority != null) {
            int posY = _priorityOverlayPosition - _framePrioritiesPosY;
            Log.info("pos: " + posY);
            int i = 0;

            if (posY < 0) {
                _priorityOverlay.setPosition(0, i * 28 + 2);
                i++;
            }

            for (View view: _priorityViews) {
                if (posY > (i * 28 - _priorityOverlayOffset - 8) && posY <= ((i+1) * 28 - _priorityOverlayOffset - 8)) {
                    _priorityOverlay.setPosition(0, i * 28 + 2);
//                    _selectedPriority.setPosition(0, i * 28);
                    i++;
                }
                if (view != _selectedPriority) {
                    view.setPosition(0, i * 28);
                    i++;
                }
            }

            if (posY > (i) * 28 - _priorityOverlayOffset - 8) {
                _priorityOverlay.setPosition(0, i * 28 + 2);
                i++;
            }

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
                setEquipment((UILabel) findById(equipmentViewId[0]), equipmentViewId[1]);
            }

            Map<String, Integer> totalResist = new HashMap<>();
            Map<String, Integer> totalAbsorb = new HashMap<>();
            Map<String, Integer> totalBuff = new HashMap<>();
            for (ItemInfo equipment : _character.getEquipments()) {
                if (equipment.equipment.effects != null) {
                    for (ItemInfo.EquipmentEffect effect: equipment.equipment.effects) {
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
            ((UILabel) findById("lb_equipment_total_resist")).setString("Resists: " + String.join(", ", resists));

            List<String> absorb = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : totalAbsorb.entrySet()) {
                absorb.add(entry.getKey() + ": " + (entry.getValue() > 0 ? "+" + entry.getValue() : entry.getValue()));
            }
            ((UILabel) findById("lb_equipment_total_absorb")).setString("Absorbs: " + String.join(", ", absorb));

            List<String> buff = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : totalBuff.entrySet()) {
                buff.add(entry.getKey() + ": " + (entry.getValue() > 0 ? "+" + entry.getValue() : entry.getValue()));
            }
            ((UILabel) findById("lb_equipment_total_buff")).setString("Buffs: " + String.join(", ", buff));
        }
    }

    private void refreshDebug() {
        if (_character != null) {
            ((UILabel)findById("lb_environment")).setString("Environment: " + Game.getWorldManager().getEnvironmentValue(_character.getX(), _character.getY(), GameData.config.environmentDistance));
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
        CharacterInfoModel.Gender gender = _character.getInfo().getGender();
        if (!gender.equals(_lastGender)) {
            _lastGender = gender;
            startAnim((UILabel)findById("lb_info_gender"), StringUtils.getDashedString("Gender:", CharacterInfoModel.Gender.MALE.equals(gender) ? "male" : "female", NB_COLUMNS));
            return;
        }

        // Enlist
        String enlisted = _character.getInfo().getEnlisted();
        if (!enlisted.equals(_lastEnlisted)) {
            _lastEnlisted = enlisted;
            startAnim(_lbEnlisted, StringUtils.getDashedString("Enlisted since:", enlisted, NB_COLUMNS));
            return;
        }

        // BirthName
        String birthName = _character.getInfo().getFirstName() + _character.getInfo().getBirthName();
        if (!birthName.equals(_lastBirthName)) {
            _lastBirthName = birthName;
            startAnim(_lbBirthName, StringUtils.getDashedString("Birth name:", birthName, NB_COLUMNS));
            return;
        }
    }

    private void refreshJob(final BaseJobModel job) {
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
        _lbInventory.setString(Strings.LB_INVENTORY);
//        _lbInventory.setString(StringUtils.getDashedString(Strings.LB_INVENTORY,
//                _character.getInventorySpace() - _character.getInventoryLeftSpace() + "/" + _character.getInventorySpace(), 29));

        if (_character.getInventory() != null) {
            ((UILabel) findById("lb_inventory_entry")).setString(_character.getInventory().getLabel() + " (" + _character.getInventory().getQuantity() + ")");
        } else {
            ((UILabel) findById("lb_inventory_entry")).setString("");
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

    private void selectEquipment(View view, ItemInfo info) {
        for (String[] equipmentViewId: _character.getEquipmentViewIds()) {
            findById(equipmentViewId[0]).setBackgroundColor(COLOR_BUTTON_INACTIVE);
        }

        view.setBackgroundColor(COLOR_BUTTON_ACTIVE);

        findById("frame_equipment_detail").setPosition(view.getPosX(), view.getPosY() + 280);
        findById("frame_equipment_detail").setVisible(false);

        if (info != null) {
            findById("frame_equipment_detail").setVisible(true);

            if (info.effects != null) {
                for (ItemInfo.EquipmentEffect effect: info.equipment.effects) {
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
                        ((UILabel)findById("lb_equipment_resist")).setString("[R]: " + String.join(", ", resists));
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
                        ((UILabel)findById("lb_equipment_absorb")).setString("[A]: " + String.join(", ", absorbs));
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
                        ((UILabel)findById("lb_equipment_resist")).setString("[B]: " + String.join(", ", buffs));
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

    private void setEquipment(UILabel view, String location) {
        ItemInfo equipment = _character.getEquipment(location);
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
                _shapes[i].setSize((int) (size * GameData.config.uiScale), (int) (12 * GameData.config.uiScale));
                _shapes[i].setTextureRect(0, (int)(level * 16), (int) (size * GameData.config.uiScale), (int)(12 * GameData.config.uiScale));
            }

            if (_values[i] != null) {
                _values[i].setString(StringUtils.getDashedString(texts[i], String.valueOf(value), NB_COLUMNS_NEEDS));
                _values[i].setColor(color);
            }
        }
    }

    private void startAnim(UILabel text, String value) {
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

    @Override
    public boolean onMouseEvent(GameTimer timer, GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y) {
        if (findById("frame_priorities").isVisible()) {
            // Pressed
            if (action == GameEventListener.Action.PRESSED) {
                for (View view: _priorityViews) {
                    if (view.isVisible() && view.getRect().contains(x, y)) {
                        _selectedPriority = view;
//                        _selectedPriority.setVisible(false);
                        _priorityOverlayOffset = y - _selectedPriority.getRect().y;
                        _priorityOverlay.setVisible(true);
                        _priorityOverlay.setPosition(0, y - _framePrioritiesPosY - _priorityOverlayOffset + 2);
                        _priorityOverlayPosition = y - _priorityOverlayOffset;
                        _selectedPriority.setPosition(0, y - _framePrioritiesPosY - _priorityOverlayOffset);
                        return true;
                    }
                }
            }

            if (action == GameEventListener.Action.RELEASED) {
                if (_selectedPriority != null) {
                    int posY = _priorityOverlayPosition - _framePrioritiesPosY;
                    int index = Math.max(0, Math.min(_character.getTalents().size() - 1, (posY + 14) / 28));
                    Log.info("release: " + index);

                    CharacterModel.TalentEntry priority = (CharacterModel.TalentEntry)_selectedPriority.getData();
                    _character.getTalents().remove(priority);
                    _character.getTalents().add(index, priority);

                    _priorityViews.remove(_selectedPriority);
                    _priorityViews.add(index, _selectedPriority);

                    int i = 0;
                    for (View view: _priorityViews) {
                        view.setPosition(0, 28 * i++);
                        view.resetPos();
                    }

                    _priorityOverlay.setVisible(false);
                    _selectedPriority.setVisible(true);
                    _selectedPriority = null;
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean	onMouseMove(int x, int y) {
        if (_selectedPriority != null) {
            _selectedPriority.setPosition(0, y - _framePrioritiesPosY - _priorityOverlayOffset);
            _priorityOverlayPosition = y - _priorityOverlayOffset;
        }
//        if (_isVisible && x > _x && x < _x + 800 && y > _y && y < _y + 600) {
//            return true;
//        }

        return false;
    }

}
