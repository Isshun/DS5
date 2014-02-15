package alone.in.deepspace;
import java.util.HashMap;
import java.util.Map;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.IntRect;
import org.jsfml.system.Vector2f;


public class ObjectPool {

	
	private static Map<Integer, Color> mColors = new HashMap<Integer, Color>();

	private static Vector2f mVector2f = new Vector2f(0,  0);

	private static Map<Integer, IntRect> mIntRect = new HashMap<Integer, IntRect>();
	
	public static Color getColor(int r, int g, int b, int a) {
		int sum = 0;
		sum += r;
		sum = sum << 8;
		sum += g;
		sum = sum << 8;
		sum += b;
		sum = sum << 8;
		sum += a;

		Color color = mColors.get(sum);
		if (color == null) {
			color = new Color(r, g, b, a);
			mColors.put(new Integer(sum), color);
		}
		
		return color;
	}


	public static Vector2f getVector2f(int tileSize, int tileSize2) {
		return new Vector2f(tileSize,  tileSize2);
	}


	public static IntRect getIntRect(int i, int j, int k, int l) {
//		int sum = 0;
//		sum += i;
//		sum = sum << 16;
//		sum += j;
//		sum = sum << 16;
//		sum += k;
//		sum = sum << 16;
//		sum += l;
//
//		IntRect intRect = mIntRect.get(sum);
//		if (intRect == null) {
//			intRect = new IntRect(i, j, k, l);
//			mIntRect.put(new Integer(sum), new IntRect(i, j, k, l));
//		}
		
		return new IntRect(i, j, k, l);
	}

}
