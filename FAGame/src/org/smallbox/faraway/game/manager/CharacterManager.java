package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.Strings;
import org.smallbox.faraway.util.Utils;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.game.model.Movable.Direction;
import org.smallbox.faraway.game.model.ProfessionModel;
import org.smallbox.faraway.game.model.character.HumanModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.job.JobModel.JobAbortReason;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class CharacterManager extends BaseManager {

	public static final ProfessionModel professions[] = {
		new ProfessionModel(ProfessionModel.Type.ENGINEER, "Engineer", new Color(255, 255, 50), new Color(50, 50, 50)),
		new ProfessionModel(ProfessionModel.Type.OPERATION, "Technician", new Color(128, 0, 0), new Color(255, 255, 255)),
		new ProfessionModel(ProfessionModel.Type.DOCTOR, "Doctor", new Color(50, 200, 0), new Color(255, 255, 255)),
		new ProfessionModel(ProfessionModel.Type.SCIENCE, "Science", new Color(50, 100, 255), new Color(255, 255, 255)),
		new ProfessionModel(ProfessionModel.Type.SECURITY, "Security", new Color(42, 42, 42), new Color(255, 255, 255)),
		new ProfessionModel(ProfessionModel.Type.NONE, "", new Color(0, 0, 0), new Color(0, 0, 0))
	};
	
	public static ProfessionModel professionsChild = new ProfessionModel(ProfessionModel.Type.CHILD, "Child", new Color(0, 0, 0), new Color(0, 0, 0));
	public static ProfessionModel professionsStudent = new ProfessionModel(ProfessionModel.Type.STUDENT, "Student", new Color(0, 0, 0), new Color(0, 0, 0));

	private List<CharacterModel>				_characters;
	private List<CharacterModel> 				_addOnUpdate;
	private int 								_count;

	public CharacterManager() {
		Log.debug("CharacterManager");

		_characters = new ArrayList<>();
		_addOnUpdate = new ArrayList<>();
		_count = 0;

		Log.debug("CharacterManager done");
	}

	public List<CharacterModel> 	getList() { return _characters; }
	public int 				getCount() { return _count; }

	// TODO
	public CharacterModel getNext(CharacterModel character) {
		for (CharacterModel c: _characters) {
			if (c == character) {
				return c;
			}
		}

		return null;
	}

	// TODO: heavy
	public int			getCount(ProfessionModel.Type professionId) {
		int count = 0;

		for (CharacterModel c: _characters) {
			if (c.getProfession().getType() == professionId) {
				count++;
			}
		}

		return count;
	}

	public ProfessionModel[]	getProfessions() {
		return professions;
	}

	public void onUpdate(int tick) {
//		Log.debug("CharacterManager: update");
		
		// Add new born
		_characters.addAll(_addOnUpdate);
		_addOnUpdate.clear();

		CharacterModel characterToRemove = null;
		
		for (CharacterModel c: _characters) {
			// Check if character is dead
			if (c.isDead()) {
				if (c.getJob() != null) {
					// Cancel job
					JobManager.getInstance().quit(c.getJob(), JobAbortReason.DIED);
//
//					// Remove from rooms
//					Game.getRoomManager().removeFromRooms(c);
				}
				characterToRemove = c;
			}
			
			else {
				c.update(tick);

				// Assign job
				if (c.getJob() == null && tick % 10 == c.getLag() && c.isSleeping() == false) {
					JobManager.getInstance().assignJob(c);
				}

				// Update needs
				if (tick % 10 == 0) {
					c.updateNeeds(tick);
				}
				
				c.setDirection(Direction.NONE);
				c.action();
				c.move();
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

//		Log.debug("CharacterManager: update done");
	}

	// TODO: heavy
	public CharacterModel getCharacterAtPos(int x, int y) {
		Log.debug("getCharacterAtPos: " + x + "x" + y);

		for (CharacterModel c: _characters) {
			if (c.getX() == x && c.getY() == y) {
				Log.debug("getCharacterAtPos: found");
				return c;
			}
		}

		return null;
	}

	public CharacterModel add(CharacterModel c) {
		_count++;
		_addOnUpdate.add(c);
		return c;
	}
	
	public void remove(CharacterModel c) {
		c.setIsDead();
		c.setName(Strings.LB_DECEADED);
	}

	CharacterModel getUnemployed(ProfessionModel.Type professionId) {
		for (CharacterModel c: _characters) {
			if (c.getProfession().getType() == professionId && c.getJob() == null) {
				return c;
			}
		}

		return null;
	}


	public void clear() {
		for (CharacterModel c: _characters) {
			c.setIsDead();
		}
		//_characters.clear();
		//JobManager.getInstance().clear();
	}

	// TODO: heavy
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

	public void addRandom(Class<? extends CharacterModel> cls) {
		try {
			Constructor<? extends CharacterModel> ctor = cls.getConstructor(int.class, int.class, int.class, String.class, String.class, double.class);
			CharacterModel character = ctor.newInstance(new Object[] { Utils.getUUID(), 0, 0, null, null, 16 });
			add(character);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public List<CharacterModel> getCharacters() {
		return _characters;
	}
}
