package alone.in.deepspace.manager;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.Color;

import alone.in.deepspace.Game;
import alone.in.deepspace.Strings;
import alone.in.deepspace.model.Movable.Direction;
import alone.in.deepspace.model.Profession;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.job.Job.Abort;
import alone.in.deepspace.util.Constant;
import alone.in.deepspace.util.Log;

public class CharacterManager {

	public static final Profession professions[] = {
		new Profession(Profession.Type.ENGINEER, "Engineer", new Color(255, 255, 50), new Color(50, 50, 50)),
		new Profession(Profession.Type.OPERATION, "Technician", new Color(128, 0, 0), new Color(255, 255, 255)),
		new Profession(Profession.Type.DOCTOR, "Doctor", new Color(50, 200, 0), new Color(255, 255, 255)),
		new Profession(Profession.Type.SCIENCE, "Science", new Color(50, 100, 255), new Color(255, 255, 255)),
		new Profession(Profession.Type.SECURITY, "Security", new Color(42, 42, 42), new Color(255, 255, 255)),
		new Profession(Profession.Type.NONE, "", new Color(0, 0, 0), new Color(0, 0, 0))
	};
	
	public static Profession professionsChild = new Profession(Profession.Type.CHILD, "Child", new Color(0, 0, 0), new Color(0, 0, 0));
	public static Profession professionsStudent = new Profession(Profession.Type.STUDENT, "Student", new Color(0, 0, 0), new Color(0, 0, 0));

	private ArrayList<Character> 	_characters;
	private int 					_count;
	private List<Character> 		_addOnUpdate;
	
	public CharacterManager() {
		Log.debug("CharacterManager");

		_characters = new ArrayList<Character>();
		_addOnUpdate = new ArrayList<Character>();
		new ArrayList<Character>();
		_count = 0;

		Log.debug("CharacterManager done");
	}

	public List<Character> 	getList() { return _characters; }
	public int 				getCount() { return _count; }

	public void	create() {
		add(0, 0, Profession.Type.ENGINEER);
		add(1, 0, Profession.Type.OPERATION);
		add(2, 0, Profession.Type.DOCTOR);
		add(3, 0, Profession.Type.SCIENCE);
		add(4, 0, Profession.Type.SECURITY);
	}

	// TODO
	public Character	getNext(Character character) {
		for (Character c: _characters) {
			if (c == character) {
				return c;
			}
		}

		return null;
	}

	// TODO: heavy
	public int			getCount(Profession.Type professionId) {
		int count = 0;

		for (Character c: _characters) {
			if (c.getProfession().getType() == professionId) {
				count++;
			}
		}

		return count;
	}

	public Profession[]	getProfessions() {
		return professions;
	}

	public void    onUpdate(int update) {
		Log.debug("CharacterManager: update");
		
		// Add new born
		_characters.addAll(_addOnUpdate);
		_addOnUpdate.clear();

		Character characterToRemove = null;
		
		for (Character c: _characters) {
			// Check if character is dead
			if (c.isDead()) {
				if (c.getJob() != null) {
					// Cancel job
					JobManager.getInstance().abort(c.getJob(), Abort.DIED);
					
					// Remove from rooms
					Game.getRoomManager().removeFromRooms(c);
				}
				characterToRemove = c;
			}
			
			else {
				// Assign job
				if (c.getJob() == null && update % 10 == c.getLag() && c.isSleeping() == false) {
					JobManager.getInstance().assignJob(c);
				}

				// Update needs
				if (update % 10 == 0) {
					c.updateNeeds(update);
				}
				
				c.setDirection(Direction.NONE);
				c.action();
				c.move();
			}
		}
		
		if (characterToRemove != null) {
			_characters.remove(characterToRemove);
		}

		Log.debug("CharacterManager: update done");
	}

	public void onLongUpdate() {
		for (Character c: _characters) {
			c.longUpdate();
		}
	}

	// TODO: heavy
	public Character        getCharacterAtPos(int x, int y) {
		Log.debug("getCharacterAtPos: " + x + "x" + y);

		for (Character c: _characters) {
			if (c.getX() == x && c.getY() == y) {
				Log.debug("getCharacterAtPos: found");
				return c;
			}
		}

		return null;
	}

	public Character		add(int x, int y) {
		if (_count + 1 > Constant.LIMIT_CHARACTER) {
			Log.error("LIMIT_CHARACTER reached");
			return null;
		}

		Character c = new Character(_count++, x, y, null, null, 24);
		Profession profession = professions[_count % professions.length];
		c.setProfession(profession.getType());
		_characters.add(c);

		return c;
	}

	public Character		add(int x, int y, Profession.Type profession) {
		if (_count + 1 > Constant.LIMIT_CHARACTER) {
			Log.error("LIMIT_CHARACTER reached");
			return null;
		}

		Character c = new Character(_count++, x, y, null, null, 24);
		c.setProfession(profession);
		_characters.add(c);

		return c;
	}
	
	public Character add(Character c) {
		_count++;
		_addOnUpdate.add(c);
		return c;
	}
	
	public void remove(Character c) {
		c.setIsDead();
		c.setName(Strings.LB_DECEADED);
	}

	Character		getUnemployed(Profession.Type professionId) {
		for (Character c: _characters) {
			if (c.getProfession().getType() == professionId && c.getJob() == null) {
				return c;
			}
		}

		return null;
	}


	public void clear() {
		for (Character c: _characters) {
			c.setIsDead();
		}
		//_characters.clear();
		//JobManager.getInstance().clear();
	}

	// TODO: heavy
	public Character getCharacter(int characterId) {
		for (Character c: _characters) {
			if (c.getId() == characterId) {
				return c;
			}
		}
		return null;
	}
}
