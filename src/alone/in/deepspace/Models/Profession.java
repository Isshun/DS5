package alone.in.DeepSpace.Models;
import org.jsfml.graphics.Color;

public class Profession {

	private Type mType;
	private String mName;
	private Color mColor;
	private Color mColorText;
	private int mId;

	public Profession(Type type, String name, Color color, Color colorText) {
		mType = type;
		mName = name;
		mColor = color;
		mColorText = colorText;
	}

	public enum Type {
		ENGINEER, OPERATION, DOCTOR, SCIENCE, SECURITY, NONE
		
	}

	public Type getType() {
		return mType;
	}

	public String getName() {
		return mName;
	}

	public Color getColor() {
		return mColor;
	}

	public Color getTextColor() {
		return mColorText;
	}

}
