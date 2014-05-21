package alone.in.deepspace.model;

import org.jsfml.graphics.Color;

import alone.in.deepspace.Strings;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.manager.ServiceManager;

public class CharacterStatus {

	private static final Color COLOR_GOOD = new Color(50, 200, 50);
	private static final Color COLOR_MEDIUM = new Color(200, 200, 50);
	private static final Color COLOR_BAD = new Color(200, 120, 60);
	private static final Color COLOR_REALLY_BAD = new Color(200, 0, 0);
	
	private Character 		_character;
	private CharacterNeeds 	_needs;
	private Color 			_color;

	public CharacterStatus(Character character) {
		_character = character;
		_needs = character.getNeeds();
		_color = COLOR_GOOD;
	}

	public Color getColor() {
		return _color;
	}

	public String getThoughts() {
		if (_needs.isSleeping()) {
			// TODO
			BaseItem item = ServiceManager.getWorldMap().getItem(_character.getPosX(), _character.getPosY());
			if (item != null && item.isSleepingItem()) {
				_color = COLOR_GOOD;
				return "I sleep well, even " + (_needs.getSleeping() / Constant.DURATION_MULTIPLIER) + "h";
			} else {
				_color = COLOR_BAD;
				return Strings.CHARACTER_SAY_NOWHERE_TO_SLEEP;
			}
		}
		if (_needs.isTired()) {
			_color = COLOR_MEDIUM;
			return Strings.CHARACTER_SAY_TIRED;
		}
		if (_needs.isStarved()) {
			_color = COLOR_BAD;
			return Strings.CHARACTER_SAY_STARVING;
		}
		if (_needs.isHungry()) {
			_color = COLOR_GOOD;
			return Strings.CHARACTER_EVERYTHINGS_GOES_RIGHT;
		}
		if (_needs.isSuffocating()) {
			_color = COLOR_REALLY_BAD;
			return Strings.CHARACTER_SAY_NO_OXYGEN;
		}
		if (_needs.isLowOxygen()) {
			_color = COLOR_BAD;
			return Strings.CHARACTER_SAY_LOW_OXYGEN;
		}
		if (_needs.isLonely()) {
			_color = COLOR_MEDIUM;
			return Strings.CHARACTER_SAY_LONELY;
		}
		
		_color = COLOR_GOOD;
		return Strings.CHARACTER_EVERYTHINGS_GOES_RIGHT;
	}

}
