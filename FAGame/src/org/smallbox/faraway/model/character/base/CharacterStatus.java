//package org.smallbox.faraway.model.character.base;
//
//import org.smallbox.faraway.Color;
//import org.smallbox.faraway.Strings;
//import org.smallbox.faraway.engine.ui.Colors;
//import org.smallbox.faraway.manager.ServiceManager;
//import org.smallbox.faraway.model.ToolTips;
//import org.smallbox.faraway.model.ToolTips.ToolTip;
//import org.smallbox.faraway.model.item.MapObjectModel;
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
