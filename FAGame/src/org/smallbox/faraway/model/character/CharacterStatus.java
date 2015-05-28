package org.smallbox.faraway.model.character;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.Strings;
import org.smallbox.faraway.engine.ui.Colors;
import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.model.ToolTips;
import org.smallbox.faraway.model.ToolTips.ToolTip;
import org.smallbox.faraway.model.item.ItemBase;

public class CharacterStatus {

	private static final Color COLOR_GOOD = Colors.LINK_INACTIVE;//new Color(50, 200, 50);
	private static final Color COLOR_MEDIUM = new Color(200, 200, 50);
	private static final Color COLOR_BAD = new Color(200, 120, 60);
	private static final Color COLOR_REALLY_BAD = new Color(200, 0, 0);

	public enum Level {
		GOOD, BAD, REALLY_BAD, MEDIUM
	}
	
	private CharacterModel _character;
	private CharacterNeeds 	_needs;
	private Color 			_color;
	private String 			_thoughts;
	private String 			_thoughtsShort;
	private Level 			_level;
	private ToolTip 		_tooltip;

	public CharacterStatus(CharacterModel character) {
		_character = character;
		_needs = character.getNeeds();
		_color = COLOR_GOOD;
	}

	public Color getColor() {
		return _color;
	}

	public String getThoughtsShort() {
		refreshThoughts();
		return _thoughtsShort;
	}

	public String getThoughts() {
		refreshThoughts();
		return _thoughts;
	}
	
	public void refreshThoughts() {
		_tooltip = null;

		// Level 3
		if (_needs.isSuffocating()) {
			_color = COLOR_REALLY_BAD;
			_level = Level.REALLY_BAD;
			_thoughts = Strings.CHARACTER_SAY_NO_OXYGEN;
			_thoughtsShort = Strings.THOUGHTS_REALLY_BAD;
			return;
		}
		
		// Level 2
		if (_needs.isStarved()) {
			_color = COLOR_BAD;
			_level = Level.BAD;
			_tooltip = ToolTips.STATE_STARVING;
			_thoughts = Strings.CHARACTER_SAY_STARVING;
			_thoughtsShort = Strings.THOUGHTS_BAD;
			return;
		}
		if (_needs.isSleeping()) {
			ItemBase item = ServiceManager.getWorldMap().getItem(_character.getX(), _character.getY());
			if (item == null || item.isSleepingItem() == false) {
				_color = COLOR_BAD;
				_level = Level.BAD;
				_tooltip = ToolTips.STATE_EXHAUSTED;
				_thoughts = Strings.CHARACTER_SAY_NOWHERE_TO_SLEEP;
				_thoughtsShort = Strings.THOUGHTS_BAD;
				return;
			}
		}
		if (_needs.isLowOxygen()) {
			_level = Level.BAD;
			_color = COLOR_BAD;
			_thoughts = Strings.CHARACTER_SAY_LOW_OXYGEN;
			_thoughtsShort = Strings.THOUGHTS_BAD;
			return;
		}
		
		// Level 1
		if (_needs.isTired() && _needs.isSleeping() == false) {
			_color = COLOR_MEDIUM;
			_level = Level.MEDIUM;
			_tooltip = ToolTips.STATE_TIRED;
			_thoughts = Strings.CHARACTER_SAY_TIRED;
			_thoughtsShort = Strings.THOUGHTS_MEDIUM;
			return;
		}
		if (_needs.isLonely()) {
			_level = Level.MEDIUM;
			_color = COLOR_MEDIUM;
			_tooltip = ToolTips.STATE_FEELING_BAD;
			_thoughts = Strings.CHARACTER_SAY_LONELY;
			_thoughtsShort = Strings.THOUGHTS_MEDIUM;
			return;
		}
		
		// Level 0
		if (_needs.isHungry()) {
			_color = COLOR_MEDIUM;
			_level = Level.MEDIUM;
			_tooltip = ToolTips.STATE_HUNGER;
			_thoughts = Strings.CHARACTER_EVERYTHINGS_GOES_RIGHT;
			_thoughtsShort = Strings.THOUGHTS_GOOD;
			return;
		}
		if (_needs.isSleeping()) {
			// TODO
			ItemBase item = ServiceManager.getWorldMap().getItem(_character.getX(), _character.getY());
			if (item != null && item.isSleepingItem()) {
				_color = COLOR_GOOD;
				_level = Level.GOOD;
				_tooltip = ToolTips.STATE_SLEEPING;
				_thoughts = Strings.CHARACTER_SAY_SLEEP;
				_thoughtsShort = Strings.THOUGHTS_GOOD;
				return;
			}
		}
		
		_level = Level.GOOD;
		_color = COLOR_GOOD;
		_tooltip = ToolTips.STATE_OK;
		_thoughts = Strings.CHARACTER_EVERYTHINGS_GOES_RIGHT;
		_thoughtsShort = Strings.THOUGHTS_GOOD;
	}

	public int getLastReportDelay() {
		return 1;
	}

	public Level getLevel() {
		return _level;
	}

	public ToolTip getTip() {
		return _tooltip;
	}

}
