package org.smallbox.faraway.core.game.model.character.base;

import org.smallbox.faraway.core.engine.Color;

/**
 * Created by Alex on 24/06/2015.
 */
public class CharacterInfoModel {
    private static final Color COLOR_FEMALE = new Color(255, 180, 220);
    private static final Color COLOR_MALE = new Color(110, 200, 255);

    public enum Gender {
        NONE,
        MALE,
        FEMALE
    }

    protected Gender                    _gender;
    protected String                    _firstName;
    protected boolean                     _isGay;
    protected String                     _lastName;
    protected String                     _birthName;
    protected Color                     _color;

    public CharacterInfoModel(String name, String lastName) {
        _firstName = name;
        _isGay = (int)(Math.random() * 100) % 10 == 0;
        _lastName = lastName;
        if (name == null) {
            _firstName = CharacterName.getFirstname(_gender) + " ";
            _lastName = lastName != null ? lastName : CharacterName.getLastName();
        }
        _birthName = _lastName;
    }

    public String                    getName() { return _firstName + _lastName; }
    public Gender                     getGender() { return _gender; }
    public String                     getLastName() { return _lastName; }
    public String                   getEnlisted() { return "april 25"; }
    public String                   getBirthName() { return _birthName; }
    public String                   getFirstName() { return _firstName; }
    public Color                     getColor() { return _color; }

    public void                        setName(String name) { _firstName = name; }
    public void                     setFirstName(String firstName) { _firstName = firstName + " "; }
    public void                     setLastName(String lastName) { _lastName = lastName; }
    public void                     setColor(Color color) { _color = color; }

    public void             setGender(Gender gender) {
        _gender = gender;
        _color = _gender == Gender.FEMALE ? COLOR_FEMALE : COLOR_MALE;
    }

    public boolean                     isGay() { return _isGay; }

}
