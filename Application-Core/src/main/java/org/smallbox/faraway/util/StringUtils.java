package org.smallbox.faraway.util;

import org.smallbox.faraway.common.NotImplementedException;
import org.smallbox.faraway.core.Application;

public class StringUtils {

    public static String getDashedString(String label, String value, int columns) {
        int hash = label.hashCode();
        label = Application.data != null && Application.data.hasString(hash) ? Application.data.getString(hash) : label;

        StringBuilder sb = new StringBuilder();
        sb.append(label);

        int valueHash = value.hashCode();
        value = Application.data != null && Application.data.hasString(valueHash) ? Application.data.getString(valueHash) : value;

        for (int i = columns - label.replace("_", "").length() - value.length(); i > 0; i--) {
            sb.append('.');
        }

        sb.append(value);

        return sb.toString();
    }

    public static String getDashedString(String label, String value) {
        return getDashedString(label, value, Constant.NB_COLUMNS);
    }

    public static String getStringFromKey(int key) {
//        switch (key) {
//        case A: return "a";
//        case B: return "b";
//        case C: return "c";
//        case D: return "d";
//        case E: return "e";
//        case F: return "f";
//        case G: return "g";
//        case H: return "h";
//        case I: return "i";
//        case J: return "j";
//        case K: return "k";
//        case L: return "l";
//        case M: return "m";
//        case N: return "n";
//        case O: return "o";
//        case P: return "p";
//        case Q: return "q";
//        case R: return "r";
//        case S: return "s";
//        case T: return "t";
//        case U: return "u";
//        case V: return "v";
//        case W: return "w";
//        case X: return "x";
//        case Y: return "y";
//        case Z: return "z";
//        case D_0: return "0";
//        case D_1: return "1";
//        case D_2: return "2";
//        case D_3: return "3";
//        case D_4: return "4";
//        case D_5: return "5";
//        case D_6: return "6";
//        case D_7: return "7";
//        case D_8: return "8";
//        case D_9: return "9";
//        case PERIOD: return ".";
//        case SPACE: return " ";
//        default: return null;
//        }
        throw new NotImplementedException();
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

    public static double formatNumber(double value) {
        return ((int)(value * 100)) / 100.0;
    }
}
