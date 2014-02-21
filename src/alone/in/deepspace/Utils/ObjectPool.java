package alone.in.deepspace.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;


public class ObjectPool {

	private static List<Text> 				mTexts = new ArrayList<Text>();
	private static List<RectangleShape> 	mRectanglesShape = new ArrayList<RectangleShape>();

	private static Map<Integer, Color> 		mColors = new HashMap<Integer, Color>();

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


	public static Text getText() {
		if (mTexts.size() > 0) {
			return mTexts.remove(0);
		}
		return new Text();
	}

	public static RectangleShape getRectangleShape() {
		if (mRectanglesShape.size() > 0) {
			return mRectanglesShape.remove(0);
		}
		return new RectangleShape();
	}
	

	public static void release(Text text) { mTexts.add(text); }

	public static void release(RectangleShape text) { mRectanglesShape.add(text); }

}
