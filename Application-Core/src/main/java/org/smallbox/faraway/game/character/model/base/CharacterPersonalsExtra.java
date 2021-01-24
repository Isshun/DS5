package org.smallbox.faraway.game.character.model.base;

import com.badlogic.gdx.graphics.Color;

public class CharacterPersonalsExtra extends CharacterExtra {
    private static final Color COLOR_FEMALE = new Color(255 / 255f, 180 / 255f, 220 / 255f, 1f);
    private static final Color COLOR_MALE = new Color(110 / 255f, 200 / 255f, 255 / 255f, 1f);

    public enum Gender {
        NONE,
        MALE,
        FEMALE
    }

    protected Gender            _gender;
    protected String            _firstName;
    protected boolean           _isGay;
    protected String            _lastName;
    protected String            _birthName;
    protected Color             _color;
    protected double            _old;

    public CharacterPersonalsExtra(CharacterModel character, String name, String lastName, double old, Gender gender) {
        super(character);

        _old = old;
        _gender = gender;
        _firstName = name;
        _isGay = (int)(Math.random() * 100) % 10 == 0;
        _lastName = lastName;
        if (name == null) {
            for (int i = 0; i < 20; i++) {
                if (_firstName == null || (_firstName + " " + _lastName).length() > 16) {
                    _firstName = CharacterName.getFirstname(_gender) + " ";
                    _lastName = lastName != null ? lastName : CharacterName.getLastName();
                }
            }
        }
        _birthName = _lastName;
    }

    public String       getName() { return _firstName + _lastName; }
    public Gender       getGender() { return _gender; }
    public String       getLastName() { return _lastName; }
    public String       getEnlisted() { return "april 25"; }
    public String       getBirthName() { return _birthName; }
    public String       getFirstName() { return _firstName; }
    public Color        getColor() { return _color; }
    public double       getOld() { return _old; }

    public boolean      isGay() { return _isGay; }

    public void         setName(String name) { _firstName = name; }
    public void         setFirstName(String firstName) { _firstName = firstName + " "; }
    public void         setLastName(String lastName) { _lastName = lastName; }
    public void         setColor(Color color) { _color = color; }
    public void         setOld(double old) { _old = old; }

    public void         setGender(Gender gender) {
        _gender = gender;
        _color = _gender == Gender.FEMALE ? COLOR_FEMALE : COLOR_MALE;
    }
}
