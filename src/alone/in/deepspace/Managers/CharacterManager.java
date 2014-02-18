package alone.in.DeepSpace.Managers;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.Transform;

import alone.in.DeepSpace.Models.BaseItem;
import alone.in.DeepSpace.Models.Character;
import alone.in.DeepSpace.Models.Job;
import alone.in.DeepSpace.Models.Profession;
import alone.in.DeepSpace.Utils.Constant;
import alone.in.DeepSpace.Utils.Log;
import alone.in.DeepSpace.World.WorldArea;
import alone.in.DeepSpace.World.WorldMap;


public class CharacterManager {
	public static final Profession professions[] = {
		new Profession(Profession.Type.ENGINEER, "Engineer", new Color(255, 255, 50), new Color(50, 50, 50)),
		new Profession(Profession.Type.OPERATION, "Technician", new Color(128, 0, 0), new Color(255, 255, 255)),
		new Profession(Profession.Type.DOCTOR, "Doctor", new Color(50, 200, 0), new Color(255, 255, 255)),
		new Profession(Profession.Type.SCIENCE, "Science", new Color(50, 100, 255), new Color(255, 255, 255)),
		new Profession(Profession.Type.SECURITY, "Security", new Color(42, 42, 42), new Color(255, 255, 255)),
		new Profession(Profession.Type.NONE, "", new Color(0, 0, 0), new Color(0, 0, 0))
	};

	private static CharacterManager	_self;
	private ArrayList<Character> 	_characters;
	private int 					_count;
	private Sprite 					_selection;

	public CharacterManager() throws IOException {
	  Log.debug("CharacterManager");
	  
	  // Selection
	  Texture texture = new Texture();
	  texture.loadFromFile((new File("res/cursor.png").toPath()));
	  _selection = new Sprite();
	  _selection.setTexture(texture);
	  _selection.setTextureRect(new IntRect(0, 32, 32, Constant.CHAR_HEIGHT));
	  
	  _characters = new ArrayList<Character>();
	  _count = 0;

	  Log.debug("CharacterManager done");
	}


	public void	assignJobs() {

		if (JobManager.getInstance().getCountFree() > 0) {
			for (Character c: _characters) {
				if (c.getJob() == null) {
					Job job = JobManager.getInstance().getJob(c);
					if (job != null) {
						Log.debug("assignJobs to " + c.getName());
						job.setCharacter(c);
						c.setJob(job);
					}
				}
			}
		}
	}

	public void	create() {
	  add(0, 0, Profession.Type.ENGINEER);
	  add(1, 0, Profession.Type.OPERATION);
	  add(2, 0, Profession.Type.DOCTOR);
	  add(3, 0, Profession.Type.SCIENCE);
	  add(4, 0, Profession.Type.SECURITY);
	}

	public void	load(final String filePath) {
		Log.error("Load characters: " + filePath);

		int x, y, professionType;
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
						professionType = Integer.valueOf(values[2]);
						Character c = new Character(_count++, x, y, values[3]);
						c.setProfession(CharacterManager.getProfessionType(professionType));
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

	private static Profession.Type getProfessionType(int index) {
		  if (index == 1) {return Profession.Type.ENGINEER; }
		  if (index == 2) {return Profession.Type.OPERATION; }
		  if (index == 3) {return Profession.Type.DOCTOR; }
		  if (index == 4) {return Profession.Type.SCIENCE; }
		  if (index == 5) {return Profession.Type.SECURITY; }
		  return Profession.Type.NONE;
	}


	void	save(final String filePath) {
//	  ofstream ofs(filePath, ios_base.app);
//	  std.list<Character>.iterator it;
//
//	  if (ofs.is_open()) {
//		ofs + "BEGIN CHARACTERS\n";
//
//		for (it = _characters.begin(); it != _characters.end(); ++it) {
//		  Character c = it;
//		  ofs + c.getX() + "\t"
//			  + c.getY() + "\t"
//	 		  + c.getProfessionId() + "\t"
//	 		  + c.getName() + "\n";
//		}
//		ofs + "END CHARACTERS\n";
//
//		ofs.close();
//	  } else {
//		Error() + "Unable to open save file: " + filePath;
//	  }
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

	public void    update(int count) {
	  for (Character c: _characters) {
		c.action();
	    c.update();
		c.move();

		if (count % 10 == 0) {
		  c.updateNeeds(count);
		}
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

	Character		assignJob(Job job) {
	  if (job == null) {
		Log.error(" try to assign null job");
		return null;
	  }

	  Character bestCharacter = null;

	  JobManager.Action jobAction = job.getAction();

	  for (Character c: _characters) {
		if (c.getJob() == null) {

		  if (bestCharacter == null) {
			bestCharacter = c;
		  }

		  // build action . only engineer
		  if (jobAction == JobManager.Action.BUILD && c.getProfession().getType() == Profession.Type.ENGINEER) {
			if (bestCharacter == null || c.getProfessionScore(Profession.Type.ENGINEER) > bestCharacter.getProfessionScore(Profession.Type.ENGINEER)) {
			  bestCharacter = c;
			}
		  }
		}
	  }

	  if (bestCharacter != null) {

		// TODO: remove if invalid

		// Action build
		if (job.getAction() == JobManager.Action.BUILD) {
		  BaseItem jobItem = job.getItem();
		  BaseItem item = WorldMap.getInstance().getItem(job.getX(), job.getY());
		  WorldArea area = WorldMap.getInstance().getArea(job.getX(), job.getY());
		  if (item != null && item.isComplete() && area != null && area.isComplete()) {
			Log.error("CharacterManager: Job ACTION_BUILD on complete item");
			return null;
		  }
		  if (jobItem == null && item != null) {
			jobItem = item;
		  }
		  if (jobItem == null && area != null) {
			jobItem = area;
		  }
		  if (jobItem == null) {
			jobItem = WorldMap.getInstance().putItem(job.getItemType(), job.getX(), job.getY());
		  }
		}

		// Action gather
		else if (job.getAction() == JobManager.Action.GATHER) {
		  BaseItem jobItem = job.getItem();
		  if (jobItem == null) {
			Log.error("CharacterManager: Job ACTION_GATHER on missing item");
			return null;
		  }
		}

		Log.info("assign " + job.getId() + " to " + bestCharacter);
	  	job.setCharacter(bestCharacter);
		bestCharacter.setJob(job);
	  }

	  return bestCharacter;
	}

	public Character		add(int x, int y) {
	  if (_count + 1 > Constant.LIMIT_CHARACTER) {
		Log.error("LIMIT_CHARACTER reached");
		return null;
	  }

	  Character c = new Character(_count++, x, y, null);
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

	  Character c = new Character(_count++, x, y, null);
	  c.setProfession(profession);
	  _characters.add(c);

	  return c;
	}

	Character		getUnemployed(Profession.Type professionId) {
	  for (Character c: _characters) {
		if (c.getProfession().getType() == professionId && c.getJob() == null) {
		  return c;
		}
	  }

	  return null;
	}

	public void	refresh(RenderWindow app, RenderStates render, double animProgress) throws IOException {

	  for (Character c: _characters) {
		int posX = c.getX() * Constant.TILE_SIZE - (Constant.CHAR_WIDTH - Constant.TILE_SIZE) + 2;
		int posY = c.getY() * Constant.TILE_SIZE - (Constant.CHAR_HEIGHT - Constant.TILE_SIZE) + 0;
		Character.Direction direction = c.getDirection();

		// TODO: ugly
		int offset = 0;

		if (direction == Character.Direction.DIRECTION_TOP ||
			direction == Character.Direction.DIRECTION_BOTTOM ||
			direction == Character.Direction.DIRECTION_RIGHT ||
			direction == Character.Direction.DIRECTION_LEFT)
		  offset = (int) ((1-animProgress) * Constant.TILE_SIZE);

		if (direction == Character.Direction.DIRECTION_TOP_RIGHT ||
			direction == Character.Direction.DIRECTION_TOP_LEFT  ||
			direction == Character.Direction.DIRECTION_BOTTOM_RIGHT ||
			direction == Character.Direction.DIRECTION_BOTTOM_LEFT)
		  offset = (int) ((1-animProgress) * Constant.TILE_SIZE);

		int dirIndex = 0;
		if (direction == Character.Direction.DIRECTION_BOTTOM) { posY -= offset; dirIndex = 0; }
		if (direction == Character.Direction.DIRECTION_TOP) { posY += offset; dirIndex = 1; }
		if (direction == Character.Direction.DIRECTION_RIGHT) { posX -= offset; dirIndex = 2; }
		if (direction == Character.Direction.DIRECTION_LEFT) { posX += offset; dirIndex = 3; }
		if (direction == Character.Direction.DIRECTION_BOTTOM_RIGHT) { posY -= offset; posX -= offset; dirIndex = 4; }
		if (direction == Character.Direction.DIRECTION_BOTTOM_LEFT) { posY -= offset; posX += offset; dirIndex = 5; }
		if (direction == Character.Direction.DIRECTION_TOP_RIGHT) { posY += offset; posX -= offset; dirIndex = 6; }
		if (direction == Character.Direction.DIRECTION_TOP_LEFT) { posY += offset; posX += offset; dirIndex = 7; }
	
		if (direction == Character.Direction.DIRECTION_TOP_RIGHT)
		  direction = Character.Direction.DIRECTION_RIGHT;
		if (direction == Character.Direction.DIRECTION_TOP_LEFT)
		  direction = Character.Direction.DIRECTION_LEFT;
		if (direction == Character.Direction.DIRECTION_BOTTOM_RIGHT)
		  direction = Character.Direction.DIRECTION_RIGHT;
		if (direction == Character.Direction.DIRECTION_BOTTOM_LEFT)
		  direction = Character.Direction.DIRECTION_LEFT;

		// end ugly

		int frame = c.getFrameIndex() / 20 % 4;

		Profession profession = c.getProfession();
		
		Sprite sprite = SpriteManager.getInstance().getCharacter(profession, dirIndex, frame);
		
		sprite.setPosition(posX, posY);
//		if (c.getNeeds().isSleeping()) {
//		  sprite.setTextureRect(new IntRect(0, Constant.CHAR_HEIGHT, Constant.CHAR_WIDTH, Constant.CHAR_HEIGHT));
//	 	} else if (direction == Character.Direction.DIRECTION_NONE) {
//		  sprite.setTextureRect(new IntRect(0, 0, Constant.CHAR_WIDTH, Constant.CHAR_HEIGHT));
//		} else {
//		  sprite.setTextureRect(new IntRect(Constant.CHAR_WIDTH * frame, Constant.CHAR_HEIGHT * dirIndex, Constant.CHAR_WIDTH, Constant.CHAR_HEIGHT));
//		}
		
		app.draw(sprite, render);

		// Selection
		if (c.getSelected()) {
		  _selection.setPosition(posX, posY);
		  app.draw(_selection, render);
		}
	  }
	}

//	public Sprite	getSprite(Sprite sprite, Profession.Type functionId, int index) {
//
//	  if (functionId == Profession.Type.SECURITY) {
//		sprite.setTexture(_textures[1]);
//	  } else {
//		sprite.setTexture(_textures[0]);
//	  }
//
//	  for (Character c: _characters) {
//		int posX = c.getX() * Constant.TILE_SIZE - (Constant.CHAR_WIDTH - Constant.TILE_SIZE);
//		int posY = c.getY() * Constant.TILE_SIZE - (Constant.CHAR_HEIGHT - Constant.TILE_SIZE + Constant.TILE_SIZE / 2);
//
//		// Sprite
//		sprite.setPosition(posX, posY);
//		if (c.getNeeds().isSleeping()) {
//		  sprite.setTextureRect(new IntRect(0, Constant.CHAR_HEIGHT, Constant.CHAR_WIDTH, Constant.CHAR_HEIGHT));
//		} else {
//		  sprite.setTextureRect(new IntRect(Constant.CHAR_WIDTH * (index % 4), 0, Constant.CHAR_WIDTH, Constant.CHAR_HEIGHT));
//		}
//
//	  }
//
//	  return sprite;
//	}


	public static CharacterManager getInstance() {
		if (_self == null) {
			try {
				_self = new CharacterManager();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return _self;
	}


	public List<Character> getList() {
		return _characters;
	}


	public int getCount() {
		return _count;
	}

}
