package org.smallbox.faraway.engine.util;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.GameEventListener;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.model.GameData;

public class StringUtils {

	public static String getDashedString(String label, String value, int columns) {
		label = GameData.getData().getString(label);

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

	public static String getPlanetStatsText(int level) {
		switch (level) {
			case -3: return "non-existent";
			case -2: return "rare";
			case -1: return "low";
			default: return "regular";
			case 1: return "abundant";
			case 2: return "extreme";
		}
	}

    public static String getPlanetStatsSymbol(int level) {
        switch (level) {
            case -3: return "---";
            case -2: return "--";
            case -1: return "-";
            default: return "+";
            case 1: return "++";
            case 2: return "+++";
        }
    }

}
