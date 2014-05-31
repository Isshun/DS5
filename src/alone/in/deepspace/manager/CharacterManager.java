package alone.in.deepspace.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;

import alone.in.deepspace.Strings;
import alone.in.deepspace.engine.ISavable;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.Character.Gender;
import alone.in.deepspace.model.Movable.Direction;
import alone.in.deepspace.model.Profession;
import alone.in.deepspace.model.job.Job;
import alone.in.deepspace.model.job.Job.Abort;
import alone.in.deepspace.util.Constant;
import alone.in.deepspace.util.Log;

public class CharacterManager implements ISavable {

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
	private Sprite 					_selection;
	private List<Character> 		_addOnUpdate;
	private List<Character> 		_removeOnUpdate;

	public CharacterManager() throws IOException {
		Log.debug("CharacterManager");

		// Selection
		Texture texture = new Texture();
		texture.loadFromFile((new File("res/cursor.png").toPath()));
		_selection = new Sprite();
		_selection.setTexture(texture);
		_selection.setTextureRect(new IntRect(0, 32, 32, Constant.CHAR_HEIGHT));

		_characters = new ArrayList<Character>();
		_addOnUpdate = new ArrayList<Character>();
		_removeOnUpdate = new ArrayList<Character>();
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

	public void	load(final String filePath) {
		Log.error("Load characters: " + filePath);

		int x, y, gender;
		boolean	inBlock = false;

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line = null;

			while ((line = br.readLine()) != null) {

				// Start block
				if ("BEGIN CHARACTERS".equals(line)) {
					inBlock = true;
				}

				// End block
				else if ("END CHARACTERS".equals(line)) {
					inBlock = false;
				}

				// Item
				else if (inBlock) {
					String[] values = line.split("\t");
					if (values.length == 4) {
						x = Integer.valueOf(values[0]);
						y = Integer.valueOf(values[1]);
						gender = Integer.valueOf(values[2]);
						int sep = values[3].lastIndexOf(' ');
						String lastName = values[3].substring(sep + 1, values[3].length());
						String firstName = values[3].substring(0, sep + 1);
						Character c = new Character(_count++, x, y, firstName, lastName, 24);
						c.setGender(CharacterManager.getGender(gender));
						_characters.add(c);
					}
				}

			}
		}
		catch (FileNotFoundException e) {
			Log.error("Unable to open save file: " + filePath);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Character.Gender getGender(int gender) {
		if (gender == 1) { return Gender.MALE; }
		if (gender == 2) { return Gender.FEMALE; }
		if (gender == 3) { return Gender.BOTH; }
		return Gender.NONE;
	}

	private static Profession.Type getProfessionType(int index) {
		if (index == 1) {return Profession.Type.ENGINEER; }
		if (index == 2) {return Profession.Type.OPERATION; }
		if (index == 3) {return Profession.Type.DOCTOR; }
		if (index == 4) {return Profession.Type.SCIENCE; }
		if (index == 5) {return Profession.Type.SECURITY; }
		return Profession.Type.NONE;
	}


	public void	save(final String filePath) {
		Log.info("Save characters: " + filePath);

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
			bw.write("BEGIN CHARACTERS\n");
			for (Character c: _characters) {
				bw.write(c.getX() + "\t" + c.getY() + "\t" + c.getGender().ordinal() + "\t" + c.getName() + "\n");
			}
			bw.write("END CHARACTERS\n");
		} catch (FileNotFoundException e) {
			Log.error("Unable to open save file: " + filePath);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.info("Save characters: " + filePath + " done");
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
		
		// Remove died character
		for (Character character: _removeOnUpdate) {
			if (character.getJob() != null) {
				// Cancel job
				JobManager.getInstance().abort(character.getJob(), Abort.DIED);
				
				// Remove from rooms
				RoomManager.getInstance().removeFromRooms(character);
			}
		}
		_characters.removeAll(_removeOnUpdate);
		
		for (Character c: _characters) {
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
		_addOnUpdate.add(c);
		return c;
	}
	
	public void remove(Character c) {
		_removeOnUpdate.add(c);
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
		_characters.clear();
		JobManager.getInstance().clear();
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
