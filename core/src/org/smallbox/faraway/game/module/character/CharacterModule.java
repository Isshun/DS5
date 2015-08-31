package org.smallbox.faraway.game.module.character;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.MovableModel.Direction;
import org.smallbox.faraway.game.model.character.HumanModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.job.BaseJobModel.JobAbortReason;
import org.smallbox.faraway.game.module.GameModule;
import org.smallbox.faraway.game.module.GameUIModule;
import org.smallbox.faraway.util.Strings;
import org.smallbox.faraway.util.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CharacterModule extends GameUIModule {
	private CharacterModuleWindow 		        _window;
	private BlockingQueue<CharacterModel> 		_characters;
	private List<CharacterModel> 				_addOnUpdate;
	private int 								_count;

	public CharacterModule() {
		_characters = new LinkedBlockingQueue<>();
		_addOnUpdate = new ArrayList<>();
		_count = 0;
	}

    @Override
    public boolean isMandatory() {
        return true;
    }

    public Collection<CharacterModel> 	getCharacters() { return _characters; }
	public int 							getCount() { return _count; }

	// TODO
	public CharacterModel getNext(CharacterModel character) {
		for (CharacterModel c: _characters) {
			if (c == character) {
				return c;
			}
		}

		return null;
	}

    @Override
	public void onCreate() {
        _window = new CharacterModuleWindow();
        _window.setVisible(false);
        addWindow(_window);
    }

    @Override
	public void onUpdate(int tick) {
		// Add new born
		_characters.addAll(_addOnUpdate);
		_addOnUpdate.clear();

		CharacterModel characterToRemove = null;

		for (CharacterModel c: _characters) {
			// Check if characters is dead
			if (!c.isAlive()) {

				// Cancel job
				if (c.getJob() != null) {
					JobModule.getInstance().quitJob(c.getJob(), JobAbortReason.DIED);
				}

				if (!c.getBuffs().isEmpty()) {
					c.getBuffs().clear();
				}

//				characterToRemove = c;
			}

			else {
				if (tick % 10 == c.getLag()) {
					// Assign job
					if (c.getJob() == null && !c.isSleeping()) {
						JobModule.getInstance().assign(c);
					}

					// Update characters (buffs, stats)
					c.update();
				}

				// Update needs
				c.getNeeds().update();

				c.setDirection(Direction.NONE);
				c.action();
				c.move();
				c.fixPosition();
			}
		}

		if (characterToRemove != null) {
			_characters.remove(characterToRemove);
		}

		if (tick % 10 == 0) {
			for (CharacterModel c: _characters) {
				c.longUpdate();
			}
		}
	}

	// TODO: heavy
	public CharacterModel getCharacterAtPos(int x, int y) {
		printDebug("getCharacterAtPos: " + x + "x" + y);

		for (CharacterModel c: _characters) {
			if (c.getX() == x && c.getY() == y) {
				printDebug("getCharacterAtPos: found");
				return c;
			}
		}

		return null;
	}

	public CharacterModel add(CharacterModel character) {
		_count++;
		_addOnUpdate.add(character);

		Game.getInstance().notify(observer -> observer.onAddCharacter(character));

		return character;
	}

	public void remove(CharacterModel c) {
		c.setIsDead();
		c.getInfo().setName(Strings.LB_DECEADED);
	}

	public CharacterModel getCharacter(int characterId) {
		for (CharacterModel c: _characters) {
			if (c.getId() == characterId) {
				return c;
			}
		}
		return null;
	}

	public CharacterModel addRandom(int x, int y) {
		CharacterModel character = new HumanModel(Utils.getUUID(), x, y, null, null, 16);
		add(character);
		return character;
	}

	public CharacterModel addRandom(ParcelModel parcel) {
		CharacterModel character = new HumanModel(Utils.getUUID(), parcel.x, parcel.y, null, null, 16);
		add(character);
		return character;
	}

	public void addRandom(Class<? extends CharacterModel> cls) {
		try {
			Constructor<? extends CharacterModel> constructor = cls.getConstructor(int.class, int.class, int.class, String.class, String.class, double.class);
			CharacterModel character = constructor.newInstance(Utils.getUUID(), 0, 0, null, null, 16);
			add(character);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public boolean havePeopleOnProximity(CharacterModel character) {
		for (CharacterModel c: _characters) {
			if (c != character && Math.abs(c.getX() - character.getX()) + Math.abs(c.getY() - character.getY()) < 4) {
				return true;
			}
		}
		return false;
	}

	public void onSelectCharacter(CharacterModel character) {
        _window.setVisible(true);
		_window.select(character);
	}

	public void onDeselect() {
		_window.setVisible(false);
	}

}
