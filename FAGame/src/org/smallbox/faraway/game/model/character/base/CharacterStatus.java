//package org.smallbox.faraway.game.model.character.base;
//
//import org.smallbox.faraway.engine.Color;
//import org.smallbox.faraway.Strings;
//import org.smallbox.faraway.ui.engine.Colors;
//import org.smallbox.faraway.game.manager.ServiceManager;
//import org.smallbox.faraway.game.model.ToolTips;
//import org.smallbox.faraway.game.model.ToolTips.ToolTip;
//import org.smallbox.faraway.game.model.item.MapObjectModel;
//
//public class CharacterStatus {
//
//	private static final Color COLOR_GOOD = Colors.LINK_INACTIVE;//new Color(50, 200, 50);
//	private static final Color COLOR_MEDIUM = new Color(200, 200, 50);
//	private static final Color COLOR_BAD = new Color(200, 120, 60);
//	private static final Color COLOR_REALLY_BAD = new Color(200, 0, 0);
//
//	public enum Level {
//		GOOD, BAD, REALLY_BAD, MEDIUM
//	}
//
//	private CharacterModel _character;
//	private CharacterNeeds 	_needs;
//	private Color 			_color;
//	private String 			_thoughts;
//	private String 			_thoughtsShort;
//	private Level 			_level;
//	private ToolTip 		_tooltip;
//
//	public CharacterStatus(CharacterModel character) {
//		_character = character;
//		_needs = character.getNeeds();
//		_color = COLOR_GOOD;
//	}
//
//	public Color getColor() {
//		return _color;
//	}
//
//	public int getLastReportDelay() {
//		return 1;
//	}
//
//	public Level getLevel() {
//		return _level;
//	}
//
//	public ToolTip getTip() {
//		return _tooltip;
//	}
//
//}
