package alone.in.deepspace.model;

import org.jsfml.graphics.Color;

import alone.in.deepspace.Strings;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.util.Constant;

public class CharacterStatus {

	private static final Color COLOR_GOOD = new Color(50, 200, 50);
	private static final Color COLOR_MEDIUM = new Color(200, 200, 50);
	private static final Color COLOR_BAD = new Color(200, 120, 60);
	private static final Color COLOR_REALLY_BAD = new Color(200, 0, 0);
	
	private Character 		_character;
	private CharacterNeeds 	_needs;
	private Color 			_color;
	private String _thoughts;
	private String _thoughtsShort;

	public CharacterStatus(Character character) {
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
		if (_needs.isSleeping()) {
			// TODO
			BaseItem item = ServiceManager.getWorldMap().getItem(_character.getX(), _character.getY());
			if (item != null && item.isSleepingItem()) {
				_color = COLOR_GOOD;
				_thoughts = Strings.CHARACTER_SAY_SLEEP;
				_thoughtsShort = Strings.THOUGHTS_GOOD;
				return;
			} else {
				_color = COLOR_BAD;
				_thoughts = Strings.CHARACTER_SAY_NOWHERE_TO_SLEEP;
				_thoughtsShort = Strings.THOUGHTS_BAD;
				return;
			}
		}
		if (_needs.isTired()) {
			_color = COLOR_MEDIUM;
			_thoughts = Strings.CHARACTER_SAY_TIRED;
			_thoughtsShort = Strings.THOUGHTS_MEDIUM;
			return;
		}
		if (_needs.isStarved()) {
			_color = COLOR_BAD;
			_thoughts = Strings.CHARACTER_SAY_STARVING;
			_thoughtsShort = Strings.THOUGHTS_BAD;
			return;
		}
		if (_needs.isHungry()) {
			_color = COLOR_GOOD;
			_thoughts = Strings.CHARACTER_EVERYTHINGS_GOES_RIGHT;
			_thoughtsShort = Strings.THOUGHTS_GOOD;
			return;
		}
		if (_needs.isSuffocating()) {
			_color = COLOR_REALLY_BAD;
			_thoughts = Strings.CHARACTER_SAY_NO_OXYGEN;
			_thoughtsShort = Strings.THOUGHTS_REALLY_BAD;
			return;
		}
		if (_needs.isLowOxygen()) {
			_color = COLOR_BAD;
			_thoughts = Strings.CHARACTER_SAY_LOW_OXYGEN;
			_thoughtsShort = Strings.THOUGHTS_BAD;
			return;
		}
		if (_needs.isLonely()) {
			_color = COLOR_MEDIUM;
			_thoughts = Strings.CHARACTER_SAY_LONELY;
			_thoughtsShort = Strings.THOUGHTS_MEDIUM;
			return;
		}
		
		_color = COLOR_GOOD;
		_thoughts = Strings.CHARACTER_EVERYTHINGS_GOES_RIGHT;
		_thoughtsShort = Strings.THOUGHTS_GOOD;
	}

}
