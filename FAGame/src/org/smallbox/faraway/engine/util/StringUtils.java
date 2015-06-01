package org.smallbox.faraway.engine.util;

import org.smallbox.faraway.GameEventListener;
import org.smallbox.faraway.manager.SpriteManager;

public class StringUtils {

	public static String getDashedString(String label, String value, int columns) {
		label = SpriteManager.getInstance().getString(label);

		StringBuilder sb = new StringBuilder();
		sb.append(label);
		
		for (int i = columns - label.replace("_", "").length() - value.length(); i > 0; i--) {
			sb.append('.');
		}
		
		sb.append(value);
		
		return sb.toString();
	}

	public static String getDashedString(String label, String value) {
		return getDashedString(label, value, Constant.NB_COLUMNS);
	}

	public static String getStringFromKey(GameEventListener.Key key) {
		switch (key) {
		case A: return "a";
		case B: return "b";
		case C: return "c";
		case D: return "d";
		case E: return "e";
		case F: return "f";
		case G: return "g";
		case H: return "h";
		case I: return "i";
		case J: return "j";
		case K: return "k";
		case L: return "l";
		case M: return "m";
		case N: return "n";
		case O: return "o";
		case P: return "p";
		case Q: return "q";
		case R: return "r";
		case S: return "s";
		case T: return "t";
		case U: return "u";
		case V: return "v";
		case W: return "w";
		case X: return "x";
		case Y: return "y";
		case Z: return "z";
		case SPACE: return " ";
		default: return null;
		}
	}
}
