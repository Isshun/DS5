package org.smallbox.faraway.ui.panel.right;

import org.smallbox.faraway.core.SpriteManager;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.engine.renderer.GDXRenderer;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.CharacterTypeInfo;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.ToolTips;
import org.smallbox.faraway.game.model.character.BuffModel;
import org.smallbox.faraway.game.model.character.DiseaseModel;
import org.smallbox.faraway.game.model.character.base.*;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.model.job.JobUse;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface.Mode;
import org.smallbox.faraway.ui.engine.Colors;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.view.*;
import org.smallbox.faraway.ui.panel.BaseRightPanel;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.StringUtils;
import org.smallbox.faraway.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PanelCharacter extends BaseRightPanel {
    private interface OnGaugeRefresh {
        void onGaugeRefresh(UILabel label, UIImage image, CharacterModel character, CharacterNeeds needs, CharacterTypeInfo type);
    }

    private static final int    NB_MAX_BUFFS = 20;
    private static final int    NB_MAX_DISEASES = 20;
    private static final int    NB_MAX_RELATION = 18;
    private static final int    NB_INVENTORY_PER_LINE = 10;
    private static final int    NB_COLUMNS_NEEDS = 22;

    private static final Color  COLOR_BUTTON_INACTIVE = new Color(0x298596);
    private static final Color  COLOR_BUTTON_ACTIVE = new Color(0xafcd35);
    private ViewFactory _viewFactory;

    private static final OnGaugeRefresh[] GAUGES = new OnGaugeRefresh[] {
            (label, image, character, needs, type) -> refreshNeed(label, image, "Energy", character, needs.energy, type.needs.energy),
            (label, image, character, needs, type) -> refreshNeed(label, image, "Food", character, needs.food, type.needs.food),
            (label, image, character, needs, type) -> refreshNeed(label, image, "Oxygen", character, needs.oxygen, type.needs.oxygen),
            (label, image, character, needs, type) -> refreshNeed(label, image, "Happiness", character, needs.happiness, type.needs.happiness),
            (label, image, character, needs, type) -> refreshNeed(label, image, "Relation", character, needs.relation, type.needs.relation),
            (label, image, character, needs, type) -> refreshNeed(label, image, "Amusement", character, needs.joy, type.needs.joy),
    };

    private static final Color COLOR_DEAD = new Color(180, 180, 180);
    private static final Color COLOR_0 = new Color(120, 255, 255);
    private static final Color COLOR_1 = new Color(209, 203, 69);
    private static final Color COLOR_2 = new Color(247, 57, 57);
    private static final Color COLOR_3 = new Color(247, 57, 57);

    private CharacterModel 		_character;
    private UILabel             _lbName;
    private ColorView _cursor;
    private UIImage[] 		    _shapes = new UIImage[GAUGES.length];
    private UILabel[] 			_values = new UILabel[GAUGES.length];
    private UILabel             _lbJob;
    private UILabel[] 			_lbBuffs = new UILabel[NB_MAX_BUFFS];
    private UILabel[] 			_lbDiseases = new UILabel[NB_MAX_DISEASES];
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
    private View _selectedPriority;
    private FrameLayout         _priorityOverlay;
    private int                 _priorityOverlayOffset;
    private List<View>          _priorityViews;
    private int                 _framePrioritiesPosY;
    private int                 _priorityOverlayPosition;

    public PanelCharacter(Mode mode, GameEventListener.Key shortcut) {
        super(mode, shortcut, "data/ui/panels/info_character.yml");
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout, FrameLayout panel) {
        findById("bt_monitoring").setOnClickListener(view -> switchView("frame_monitoring"));
        findById("bt_personal_report").setOnClickListener(view -> switchView("frame_personal_report"));
        findById("bt_priorities").setOnClickListener(view -> switchView("frame_priorities"));
        findById("bt_inventory").setOnClickListener(view -> switchView("frame_inventory"));

        createNeedsInfo();
        createBasicInformation();
        createRelationShip();
        createInventoryInfo();

        findById("frame_monitoring").setVisible(true);
        findById("frame_personal_report").setVisible(false);
        findById("frame_priorities").setVisible(false);
        findById("frame_inventory").setVisible(false);

        _lbName = (UILabel) findById("lb_name");
        _lbJob = (UILabel) findById("lb_current_job");

        FrameLayout frameBuffs = (FrameLayout)findById("frame_buffs_entries");
        for (int i = 0; i < NB_MAX_BUFFS; i++) {
            _lbBuffs[i] = ViewFactory.getInstance().createTextView();
            _lbBuffs[i].setCharacterSize(14);
            _lbBuffs[i].setPosition(0, 20 * i);
            frameBuffs.addView(_lbBuffs[i]);
        }

        FrameLayout frameDiseases = (FrameLayout)findById("frame_health_entries");
        for (int i = 0; i < NB_MAX_DISEASES; i++) {
            _lbDiseases[i] = ViewFactory.getInstance().createTextView();
            _lbDiseases[i].setCharacterSize(14);
            _lbDiseases[i].setPosition(0, 20 * i);
            frameDiseases.addView(_lbDiseases[i]);
        }

        findById("frame_equipment_detail").setVisible(false);

        if (_character != null) {
            select(_character);
        }
    }

    private void switchView(String viewId) {
        findById("frame_monitoring").setVisible("frame_monitoring".equals(viewId));
        findById("frame_priorities").setVisible("frame_priorities".equals(viewId));
        findById("frame_personal_report").setVisible("frame_personal_report".equals(viewId));
        findById("frame_inventory").setVisible("frame_inventory".equals(viewId));
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

        findById("lb_info_gender").setOnClickListener(view -> _ui.getSelector().select(ToolTips.GENDER));

        _lbEnlisted = (UILabel) findById("lb_info_enlisted");

        _lbBirthName = (UILabel) findById("lb_info_birtname");
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
        for (int i = 0; i < GAUGES.length; i++) {
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
                _familyEntries[i].setOnClickListener(view -> _ui.getSelector().select(relation.getSecond()));

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
        if (_character != null && _character.needRefresh()) {
            initCharacter();
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
            refreshStatsEquipments();
            refreshStatsBody();
            refreshStats();

            // Relations
            if (update % 20 == 0 || _nbRelation != _character.getRelations().getRelations().size()) {
                refreshRelations();
            }

            refreshLastReports();
            refreshHealth();
            refreshInfo();
            refreshDebug();

            ((UILabel)findById("lb_body_heat")).setString("Body heat: " + (int)(_character.getBodyHeat() * 10) / 10f);
        }

        refreshTalents();
        refreshTimeTable();
    }

    private void refreshStatsBody() {
        findById("frame_stats_body").setVisible(false);

        String body = _character.getType().label;
        ((UILabel) findById("lb_ethnic_group")).setString(null);
        for (ItemInfo info: _character.getEquipments()) {
            if ("body".equals(info.equipment.location)) {
                findById("frame_stats_body").setVisible(true);

                body += " (" + info.label + ")";

                ItemInfo.EquipmentEffect effects = info.equipment.effects.get(0);

                String buffValue = "Buff ";
                if (effects.buff != null) {
                    if (effects.buff.cold != 0) buffValue += "cold: " + effects.buff.cold + ", ";
                    if (effects.buff.heat != 0) buffValue += "heat: " + effects.buff.heat + ", ";
                    if (effects.buff.oxygen != 0) buffValue += "o2: " + effects.buff.oxygen + ", ";
                }
                ((UILabel) findById("lb_stats_buff")).setString(buffValue);

                String absorbValue = "Absorb ";
                if (effects.debuff != null) {
                    if (effects.debuff.cold != 0) absorbValue += "cold: " + effects.debuff.cold + ", ";
                    if (effects.debuff.heat != 0) absorbValue += "heat: " + effects.debuff.heat + ", ";
                    if (effects.debuff.oxygen != 0) absorbValue += "o2: " + effects.debuff.oxygen + ", ";
                }
                ((UILabel) findById("lb_stats_absorb")).setString(absorbValue);

                String resistsValue = "Resists ";
                if (effects.resist != null) {
                    if (effects.resist.cold != 0) resistsValue += "cold: " + effects.resist.cold + ", ";
                    if (effects.resist.heat != 0) resistsValue += "heat: " + effects.resist.heat + ", ";
                    if (effects.resist.oxygen != 0) resistsValue += "o2: " + effects.resist.oxygen + ", ";
                }
                ((UILabel) findById("lb_stats_resist")).setString(resistsValue);

                ((UILabel) findById("lb_ethnic_group")).setString(body);
            }
        }
    }

    private void refreshStats() {
        CharacterStats stats = _character.getStats();

        String buffValue = "Buff ";
        if (stats.buff != null) {
            if (stats.buff.cold != 0) buffValue += "cold: " + stats.buff.coldScore + ", ";
            if (stats.buff.heat != 0) buffValue += "heat: " + stats.buff.heatScore + ", ";
            if (stats.buff.oxygen != 0) buffValue += "o2: " + stats.buff.oxygenScore + ", ";
        }
        ((UILabel) findById("lb_total_buff")).setString(buffValue);

        String absorbValue = "Absorb ";
        if (stats.debuff != null) {
            if (stats.debuff.cold != 0) absorbValue += "cold: " + stats.debuff.coldScore + ", ";
            if (stats.debuff.heat != 0) absorbValue += "heat: " + stats.debuff.heatScore + ", ";
            if (stats.debuff.oxygen != 0) absorbValue += "o2: " + stats.debuff.oxygenScore + ", ";
        }
        ((UILabel) findById("lb_total_absorb")).setString(absorbValue);

        String resistsValue = "Resists ";
        if (stats.resist != null) {
            if (stats.resist.cold != 0) resistsValue += "cold: " + stats.resist.coldScore + " (" + (int)(stats.resist.cold * 100) + "%), ";
            if (stats.resist.heat != 0) resistsValue += "heat: " + stats.resist.heatScore + " (" + (int)(stats.resist.heat * 100) + "%), ";
            if (stats.resist.oxygen != 0) resistsValue += "o2: " + stats.resist.oxygenScore + " (" + (int)(stats.resist.oxygen * 100) + "%), ";
        }
        ((UILabel) findById("lb_total_resist")).setString(resistsValue);
    }

    private void refreshTimeTable() {

    }

    private void initCharacter() {
        _lbName.setString(_character.getInfo().getName().toUpperCase());
        _lbName.setColor(_character.getInfo().getColor());
        _nbRelation = -1;
        createTalents();
        createTimeTable();
    }

    private void createTimeTable() {
        FrameLayout frame = (FrameLayout)findById("frame_timetable_entries");
        frame.removeAllViews();
        for (int h = 0; h < Game.getInstance().getPlanet().getInfo().dayDuration; h++) {
            final int finalH = h;
            UILabel lbRow = ViewFactory.getInstance().createTextView(350, 20);
            lbRow.setString(" " + (h < 9 ? " " : "") + (h + 1) + "h");
            lbRow.setCharacterSize(14);
            lbRow.setPosition(0, 21 * h);
            lbRow.setAlign(Align.CENTER_VERTICAL);
            lbRow.setOnClickListener(view -> {
                int mode = (_character.getTimetable().get(finalH) + 1) % 4;
                _character.getTimetable().set(finalH, mode);
                setTimetableMode(view, mode);
            });
            setTimetableMode(lbRow, _character.getTimetable().get(finalH));
            frame.addView(lbRow);
        }
    }

    private void setTimetableMode(View view, int mode) {
        switch (mode) {
            case 0: view.setBackgroundColor(new Color(0x263b3f)); break;
            case 1: view.setBackgroundColor(new Color(0x298596)); break;
            case 2: view.setBackgroundColor(new Color(0xb0cd35)); break;
            case 3: view.setBackgroundColor(new Color(0x882246)); break;
        }
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

    private void refreshStatsEquipments() {
        if (_character.getEquipmentViewIds() != null) {
            for (String[] equipmentViewId : _character.getEquipmentViewIds()) {
                setEquipment((UILabel)findById(equipmentViewId[0]), equipmentViewId[1]);
            }

            Map<String, Integer> totalResist = new HashMap<>();
            Map<String, Integer> totalDebuff = new HashMap<>();
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

                        // Check debuff
                        if (effect.debuff != null) {
                            checkAndAddEquipmentEffect(totalDebuff, "cold", effect.debuff.cold);
                            checkAndAddEquipmentEffect(totalDebuff, "heat", effect.debuff.heat);
                            checkAndAddEquipmentEffect(totalDebuff, "damage", effect.debuff.damage);
                        }

                        // Check buff
                        if (effect.buff != null) {
                            checkAndAddEquipmentEffect(totalBuff, "cold", effect.buff.cold);
                            checkAndAddEquipmentEffect(totalBuff, "heat", effect.buff.heat);
                            checkAndAddEquipmentEffect(totalBuff, "damage", effect.buff.damage);
                        }
//
//                        // Check buff
//                        if (effect.buff != null) {
//                            checkAndAddEquipmentEffect(totalBuff, "sight", effect.buff.sight);
//                            checkAndAddEquipmentEffect(totalBuff, "grow", effect.buff.grow);
//                            checkAndAddEquipmentEffect(totalBuff, "repair", effect.buff.repair);
//                            checkAndAddEquipmentEffect(totalBuff, "build", effect.buff.build);
//                            checkAndAddEquipmentEffect(totalBuff, "craft", effect.buff.craft);
//                            checkAndAddEquipmentEffect(totalBuff, "cook", effect.buff.cook);
//                            checkAndAddEquipmentEffect(totalBuff, "speed", effect.buff.speed);
//                            checkAndAddEquipmentEffect(totalBuff, "tailoring", effect.buff.tailoring);
//                        }
                    }
                }
            }

            List<String> resists = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : totalResist.entrySet()) {
                resists.add(entry.getKey() + ": " + (entry.getValue() > 0 ? "+" + entry.getValue() : entry.getValue()));
            }
            ((UILabel) findById("lb_equipment_total_resist")).setString("Resists: " + String.join(", ", resists));

            List<String> absorb = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : totalDebuff.entrySet()) {
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
            FrameLayout frame = (FrameLayout)findById("frame_debug");
            frame.removeAllViews();
            addDebugView(frame, "Environment: " + Game.getWorldManager().getEnvironmentValue(_character.getX(), _character.getY(), GameData.config.environmentDistance));
            addDebugView(frame, "Mood change: " + _character.getNeeds().happinessChange);
            addDebugView(frame, "Heat: " + _character.getNeeds().heat);
            addDebugView(frame, "heat diff: " + (_character.getParcel().getTemperature() - (_character.getNeeds().heat - _character.getType().thermolysis)));
            addDebugView(frame, "heat buff: " + _character.getStats().buff.heat);
            addDebugView(frame, "heat diff real: " + _character.getNeeds().heatDifferenceReal);
        }
    }

    private void refreshHealth() {
        if (_character.getStats().deathMessage != null) {
            ((UILabel) findById("lb_health")).setString(_character.getStats().deathMessage);
        }

        // Don't use foreach for CME reason
        int line = 0;
        int length = _character._diseases.size();
        for (int i = 0; i < length; i++) {
            DiseaseModel disease = _character._diseases.get(i);
            _lbDiseases[line].setString(disease.message);
            line++;
        }
        for (; line < NB_MAX_DISEASES; line++) {
            _lbDiseases[line].setString("");
        }
    }

    private void refreshLastReports() {
        // Don't use foreach for CME reason
        int line = 0;
        int length = _character._buffs.size();
        for (int i = 0; i < length; i++) {
            BuffModel buff = _character._buffs.get(i);
            if (buff.level > 0) {
                int mood = buff.mood;
                _lbBuffs[line].setString(StringUtils.getDashedString(buff.message, (mood > 0 ? "+" : "") + mood, NB_COLUMNS));
                if (mood < -10) {
                    _lbBuffs[line].setColor(COLOR_2);
                } else if (mood < 0) {
                    _lbBuffs[line].setColor(COLOR_1);
                } else {
                    _lbBuffs[line].setColor(COLOR_0);
                }
                line++;
            }
        }
        for (; line < NB_MAX_BUFFS; line++) {
            _lbBuffs[line].setString("");
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
        if (gender != null && !gender.equals(_lastGender)) {
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
        if (_character.isSleeping()) {
            if (_character.getJob() != null && _character.getJob() instanceof JobUse & _character.getJob().getItem() != null && _character.getJob().getItem().isSleepingItem()) {
                _lbJob.setDashedString(job.getLabel(), job.getProgressPercent() + "%", NB_COLUMNS);
            } else {
                _lbJob.setString("Sleep on floor");
                _lbJob.setOnClickListener(null);
            }
        } else if (job != null) {
            _lbJob.setDashedString(job.getLabel(), job.getProgressPercent() + "%", NB_COLUMNS);
            if (job.getItem() != null) {
                //_lbJob.setOnClickListener(view -> _ui.getSelector().select(job.getItem()));
            }
        } else {
            _lbJob.setString(Strings.LN_NO_JOB);
            _lbJob.setOnClickListener(null);
        }
    }

    private void refreshInventory() {
        _lbInventory.setString(Strings.LB_INVENTORY);
//        _lbInventory.setString(StringUtils.getDashedString(Strings.LB_INVENTORY,
//                _character.getInventorySpace() - _character.getInventoryLeftSpace() + "/" + _character.getInventorySpace(), 29));

        ((UILabel) findById("lb_inventory_entry")).setString(_character.getInventory() != null ?
                _character.getInventory().getLabel() + " (" + _character.getInventory().getQuantity() + ")" : "");

//        for (int i = 0; i < Constant.CHARACTER_INVENTORY_SPACE; i++) {
//            if (_character.getInventory().size() > i) {
//                MapObjectModel item = _character.getInventory().getRoom(i);
//                _lbInventoryEntries[i].setImage(SpriteManager.getInstance().getIconDrawable(item.getInfo()));
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
//
//            if (info.effects != null) {
//                for (ItemInfo.EquipmentEffect effect: info.equipment.effects) {
//                    // Check resist
//                    if (effect.resist != null) {
//                        Map<String, Integer> totalStats = new HashMap<>();
//                        checkAndAddEquipmentEffect(totalStats, "cold", effect.resist.cold);
//                        checkAndAddEquipmentEffect(totalStats, "heat", effect.resist.heat);
//                        checkAndAddEquipmentEffect(totalStats, "damage", effect.resist.damage);
//
//                        List<String> resists = new ArrayList<>();
//                        for (Map.Entry<String, Integer> entry: totalStats.entrySet()) {
//                            resists.add(entry.getKey() + ": " + (entry.getValue() > 0 ? "+" + entry.getValue() : entry.getValue()));
//                        }
//                        ((UILabel)findById("lb_equipment_resist")).setString("[R]: " + String.join(", ", resists));
//                    }
//
//                    // Check debuff
//                    if (effect.debuff != null) {
//                        Map<String, Integer> totalStats = new HashMap<>();
//                        checkAndAddEquipmentEffect(totalStats, "cold", effect.debuff.cold);
//                        checkAndAddEquipmentEffect(totalStats, "heat", effect.debuff.heat);
//                        checkAndAddEquipmentEffect(totalStats, "damage", effect.debuff.damage);
//
//                        List<String> absorbs = new ArrayList<>();
//                        for (Map.Entry<String, Integer> entry: totalStats.entrySet()) {
//                            absorbs.add(entry.getKey() + ": " + (entry.getValue() > 0 ? "+" + entry.getValue() : entry.getValue()));
//                        }
//                        ((UILabel)findById("lb_equipment_absorb")).setString("[A]: " + String.join(", ", absorbs));
//                    }
//
//                    // Check buff
//                    if (effect.buff != null) {
//                        Map<String, Integer> totalStats = new HashMap<>();
//                        checkAndAddEquipmentEffect(totalStats, "sight", effect.buff.sight);
//                        checkAndAddEquipmentEffect(totalStats, "speed", effect.buff.speed);
//
//                        List<String> buffs = new ArrayList<>();
//                        for (Map.Entry<String, Integer> entry: totalStats.entrySet()) {
//                            buffs.add(entry.getKey() + ": " + (entry.getValue() > 0 ? "+" + entry.getValue() : entry.getValue()));
//                        }
//                        ((UILabel)findById("lb_equipment_resist")).setString("[B]: " + String.join(", ", buffs));
//                    }
//                }
//            }
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
        CharacterTypeInfo type = _character.getType();
        int valueLevel3 = 10;
        int valueLevel2 = 50;
        int index = 0;
        for (OnGaugeRefresh gauge: GAUGES){
            if (_values[index] != null) {
                gauge.onGaugeRefresh(_values[index], _shapes[index], _character, needs, type);
                index++;
            }
        }
    }

    private static void refreshNeed(UILabel lbNeed, UIImage imgNeed, String label, CharacterModel character, double value, CharacterTypeInfo.NeedInfo needInfo) {
        value = Math.min(Math.max(value, 0), 100);
        int level = 0;
        Color color = COLOR_0;
        if (value < needInfo.critical) { level = 3; color = COLOR_2; }
        else if (value < needInfo.warning) { level = 2; color = COLOR_1; }
        float size = Math.max(Math.round(180.0f / 100 * value / 10) * 10, 10);

        if (!character.isAlive()) {
            value = 0;
            size = 10;
            color = COLOR_DEAD;
            level = 4;
        }

        imgNeed.setSize((int) (size * GameData.config.uiScale), (int) (12 * GameData.config.uiScale));
        imgNeed.setTextureRect(0, (int) (level * 16), (int) (size * GameData.config.uiScale), (int) (12 * GameData.config.uiScale));

        lbNeed.setString(StringUtils.getDashedString(label, String.valueOf((int) value), NB_COLUMNS_NEEDS));
        lbNeed.setColor(color);
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

    public void onDraw(GDXRenderer renderer, Viewport viewport) {
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
    public boolean onMouseEvent(GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y) {
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

    public void select(CharacterModel character) {
        _character = character;
        initCharacter();
        onCharacterSelect(character);
    }
}
