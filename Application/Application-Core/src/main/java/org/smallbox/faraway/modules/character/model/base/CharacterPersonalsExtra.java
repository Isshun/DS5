package org.smallbox.faraway.modules.character.model.base;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.core.engine.ColorUtils;

/**
 * Created by Alex on 24/06/2015.
 */
public class CharacterPersonalsExtra extends CharacterExtra {
    private static final Color COLOR_FEMALE = ColorUtils.fromHex(255, 180, 220);
    private static final Color COLOR_MALE = ColorUtils.fromHex(110, 200, 255);

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
    protected Color _color;
    protected double            _old;

    public CharacterPersonalsExtra(CharacterModel character, String name, String lastName, double old) {
        super(character);

        _old = old;
        _firstName = name;
        _isGay = (int)(Math.random() * 100) % 10 == 0;
        _lastName = lastName;
        if (name == null) {
            _firstName = CharacterName.getFirstname(_gender) + " ";
            _lastName = lastName != null ? lastName : CharacterName.getLastName();
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
