package org.smallbox.faraway.game.model;

import org.smallbox.faraway.engine.Color;

public class ProfessionModel {

	private Type 	_type;
	private String 	_name;
	private Color 	_color;
	private Color 	_colorText;

	public ProfessionModel(Type type, String name, Color color, Color colorText) {
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
