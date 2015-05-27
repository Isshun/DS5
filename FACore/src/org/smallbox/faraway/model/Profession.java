package org.smallbox.faraway.model;

import org.smallbox.faraway.Color;

public class Profession {

	private Type 	_type;
	private String 	_name;
	private Color _color;
	private Color 	_colorText;

	public Profession(Type type, String name, Color color, Color colorText) {
		_type = type;
		_name = name;
		_color = color;
		_colorText = colorText;
	}

	public enum Type {
		ENGINEER, OPERATION, DOCTOR, SCIENCE, SECURITY, CHILD, STUDENT, NONE
		
	}

	public Type getType() {
		return _type;
	}

	public String getName() {
		return _name;
	}

	public Color getColor() {
		return _color;
	}

	public Color getTextColor() {
		return _colorText;
	}

}
