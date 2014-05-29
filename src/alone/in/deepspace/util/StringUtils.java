package alone.in.deepspace.util;

public class StringUtils {

	public static String getDashedString(String label, String value, int columns) {
		StringBuilder sb = new StringBuilder();
		sb.append(label);
		
		for (int i = columns - label.length() - value.length(); i > 0; i--) {
			sb.append('.');
		}
		
		sb.append(value);
		
		return sb.toString();
	}


}
