package alone.in.deepspace.manager;

import java.util.ArrayList;
import java.util.List;

import alone.in.deepspace.Game;
import alone.in.deepspace.Utils.Log;
import alone.in.deepspace.engine.ISavable;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.CharacterNeeds;
import alone.in.deepspace.model.ItemInfo;
import alone.in.deepspace.model.Job;
import alone.in.deepspace.model.Job.Abort;
import alone.in.deepspace.model.Room;
import alone.in.deepspace.model.UserItem;
import alone.in.deepspace.model.WorldRessource;

public class JobManager implements ISavable {
	public enum Action {
		NONE, BUILD, GATHER, USE, MOVE, STORE, DESTROY, WORK, MINING
	}

	private static JobManager	sSelf;

	private ArrayList<Job> 		_jobs;
	private ArrayList<BaseItem> _routineItems;
	private int 				_id;

	JobManager() {
		Log.debug("JobManager");

		_id = 0;
		_jobs = new ArrayList<Job>();
		_routineItems = new ArrayList<BaseItem>();

		Log.debug("JobManager done");
	}

	public List<Job>	getJobs() { return _jobs; };

	// TODO
	public void	load(final String filePath) {
//		Log.error("Load jobs: " + filePath);
//
//		boolean	inBlock = false;
//		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//			String line = null;
//
//			while ((line = br.readLine()) != null) {
//
//				// Start block
//				if ("BEGIN JOBS".equals(line)) {
//					inBlock = true;
//				}
//
//				// End block
//				else if ("END JOBS".equals(line)) {
//					inBlock = false;
//				}
//
//				// Item
//				else if (inBlock) {
//					String[] values = line.split("\t");
//					if (values.length == 6) {
//						int x = Integer.valueOf(values[0]);
//						int y = Integer.valueOf(values[1]);
//						int action = Integer.valueOf(values[2]);
//						int characterId = Integer.valueOf(values[3]);
//						int characterRequireId = Integer.valueOf(values[4]);
//						int itemType = Integer.valueOf(values[5]);
//						Job job = new Job(++_id, x, y);
//						job.setAction(JobManager.getActionFromIndex(action));
//						if (characterId > 0) {
//							Character c = ServiceManager.getCharacterManager().getCharacter(characterId);
//							job.setCharacter(c);
//						}
//						if (characterRequireId > 0) {
//							Character c = ServiceManager.getCharacterManager().getCharacter(characterId);
//							job.setCharacterRequire(c);
//						}
//						if (itemType > 0) {
//							BaseItem.Type type = BaseItem.getTypeIndex(itemType);
//							job.setItemType(type);
//
//							// Add item
//							UserItem item = ServiceManager.getWorldMap().getItem(x, y);
//							StructureItem structure = ServiceManager.getWorldMap().getStructure(x, y);
//							if (item != null && item.isType(type)) {
//								job.setItem(item);
//							}
//							if (structure != null && structure.isType(type)) {
//								job.setItem(structure);
//							}
//						}
//
//						// Not restart already completed jobs
//						if (job.getAction() == Action.BUILD && job.getItem() != null && job.getItem().isComplete()) {
//							Log.warning("job already complete, abort");
//						} else {
//							addJob(job);
//						}
//					}
//				}
//
//			}
//		}
//		catch (FileNotFoundException e) {
//			Log.error("Unable to open save file: " + filePath);
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	private static Action getActionFromIndex(int action) {
		switch(action) {
		case 0: return Action.NONE;
		case 1: return Action.BUILD;
		case 2: return Action.GATHER;
		case 3: return Action.USE;
		case 4: return Action.MOVE;
		case 5: return Action.STORE;
		case 6: return Action.DESTROY;
		}
		return null;
	}

	// TODO
	public void	save(final String filePath) {
//		Log.info("Save jobs: " + filePath);
//
//		try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
//			bw.write("BEGIN JOBS\n");
//			for (Job j: _jobs) {
//				int currentCharacterId = j.getCharacter() != null ? j.getCharacter().getId() : 0;
//				int requireCharacterId = j.getCharacterRequire() != null ? j.getCharacterRequire().getId() : 0;
//				int itemType = j.getItem() != null ? j.getItem().getType().ordinal() : 0;
//				bw.write(j.getX() + "\t" + j.getY() + "\t" + j.getAction().ordinal() + "\t" + currentCharacterId + "\t" + requireCharacterId + "\t" + itemType + "\n");
//			}
//			bw.write("END JOBS\n");
//		} catch (FileNotFoundException e) {
//			Log.error("Unable to open save file: " + filePath);
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		Log.info("Save jobs: " + filePath + " done");
	}

	public Job	build(BaseItem item) {
		if (item == null) {
			Log.error("JobManager: build on null item");
			return null;
		}
		
		if (item.isComplete()) {
			Log.error("Build item: already complete, nothing to do");
			return null;
		}
		
		Job job = new Job(++_id, item.getX(), item.getY());
		job.setAction(JobManager.Action.BUILD);
		job.setItem(item);

		addJob(job);

		return job;
	}

	public Job	gather(BaseItem ressource) {
		if (ressource == null) {
			Log.error("JobManager: gather on null area");
			return null;
		}

		// return if job already exist for this item
		for (Job job: _jobs) {
			if (job.getItem() == ressource) {
				return null;
			}
		}

		Job job = new Job(++_id, ressource.getX(), ressource.getY());
		job.setAction(Action.GATHER);
		job.setItem(ressource);

		addJob(job);

		return job;
	}

	public void	removeJob(BaseItem item) {
		List<Job> toRemove = new ArrayList<Job>();

		for (Job job: _jobs) {
			if (job.getItem() == item) {
				toRemove.add(job);
			}
		}

		for (Job job: toRemove) {
			_jobs.remove(job);
		}
	}

	public Job	build(ItemInfo info, int x, int y) {
		BaseItem item = null;

		// Structure
		if (info.isStructure) {
			BaseItem current = ServiceManager.getWorldMap().getStructure(x, y);
			if (current != null && current.getInfo().equals(info)) {
				Log.error("Build structure: already exist on this area");
				return null;
			}
			item = ServiceManager.getWorldMap().putItem(info, x, y);
		}

		// Item
		else if (info.isUserItem) {
			BaseItem current = ServiceManager.getWorldMap().getItem(x, y);
			if (current != null && current.getInfo().equals(info)) {
				Log.error("Build item: already exist on this area");
				return null;
			} else if (current != null) {
				Log.error("JobManager: add build on non null item");
				return null;
			} else if (ServiceManager.getWorldMap().getStructure(x, y) == null
					|| ServiceManager.getWorldMap().getStructure(x, y).isFloor() == false) {
				Log.error("JobManager: add build on non invalid structure (null or not STRUCTURE_FLOOR)");
				return null;
			} else {
				item = ServiceManager.getWorldMap().putItem(info, x, y);
			}
		}

		// Ressource
		else if (info.isRessource) {
			BaseItem currentItem = ServiceManager.getWorldMap().getItem(x, y);
			BaseItem currentRessource = ServiceManager.getWorldMap().getRessource(x, y);
			if (currentRessource != null && currentRessource.getInfo().equals(info)) {
				Log.error("Build item: already exist on this area");
				return null;
			} else if (currentItem != null) {
				Log.error("JobManager: add build on non null item");
				return null;
			} else {
				item = ServiceManager.getWorldMap().putItem(info, x, y);
			}
		}

		return build(item);
	}

	// TODO: one pass + check profession
	public Job getJob(Character character) {
		Log.debug("bestJob: start");

		Job bestJob = getJobForCharacterNeed(character);
		if (bestJob != null) {
			return bestJob;  
		}

		int bestDistance = -1;
		
		// Character is full: go back to storage area
		if (character.isFull()) {
			Room room = RoomManager.getInstance().getNearFreeStorage(character.getPosX(), character.getPosY());
			if (room != null) {
				Job job = new Job(++_id, room.getX(), room.getY());
				job.setAction(JobManager.Action.STORE);
				job.setCharacterRequire(character);
				addJob(job);
				return job;
			}
		}

		{
			int x = character.getX();
			int y = character.getY();
			for (Job job: _jobs) {
				// TODO: restart job after fail
				if ((job.getCharacter() == null || job.getCharacter().getJob() == null) && job.getFail() <= 0 && job.getAction() != Action.GATHER) {
					int distance = Math.abs(x - job.getX()) + Math.abs(y - job.getY());
					if (distance < bestDistance || bestDistance == -1) {
						if (job.getAction() == Action.BUILD && ResourceManager.getInstance().getMatter() == 0) {
							continue;
						}
						bestJob = job;
						bestDistance = distance;
					}
				}
			}
		}

		if (bestJob == null) {
			int x = character.getX();
			int y = character.getY();
			for (Job job: _jobs) {
				// TODO: restart job after fail
				if (job.getCharacter() == null && job.getFail() <= 0) {
					int distance = Math.abs(x - job.getX()) + Math.abs(y - job.getY());
					if (distance < bestDistance || bestDistance == -1) {
						bestJob = job;
						bestDistance = distance;
					}
				}
			}
		}

		if (bestJob != null) {
			Log.debug("bestjob: " + bestDistance + " (" + bestJob.getX() + ", " + bestJob.getY() + ")");
		} else {
			Log.debug("bestjob: null");
		}

		return bestJob;
	}

	private Job getJobForCharacterNeed(Character character) {
		CharacterNeeds needs = character.getNeeds();
		if (needs.getFood() < 20) {
			// TODO
			ItemFilter itemFilter = new ItemFilter();
			itemFilter.food = true;
			UserItem item = ServiceManager.getWorldMap().getNearest(itemFilter, character.getPosX(), character.getPosY());
			if (item != null) {
				return createUseJob(item);
			}
		}

		return null;
	}

	//	// TODO: ugly
	//	Job	getJob() {
	//	  if (_count == 0) {
	//		return null;
	//	  }
	//
	//	  int i = 0;
	//	  std.list<Job>.iterator it = _jobs.begin();
	//	  while (i++ < _start % _count) {
	//		it++;
	//	  }
	//
	//	  for (i = 0; i < _count; i++) {
	//		if (it == _jobs.end()) {
	//		  it = _jobs.begin();
	//		}
	//
	//		if ((it).getCharacter() == null) {
	//		  return it;
	//		}
	//
	//		it++;
	//	  }
	//
	//	  return null;
	//	}

	public void	abort(Job job, Abort reason) {
		Log.debug("Job abort: " + job.getId());

		// Job is invalid or USE action, don't resume
		if (reason == Job.Abort.INVALID || job.getAction() == Action.USE) {
			_jobs.remove(job);
			if (job.getItem() != null) {
				job.getItem().setOwner(null);
			}
			
			if (job.getSlot() != null) {
				job.getSlot().release();
			}
		}

		// Regular job, reset
		else {
			job.setFail(reason, Game.getFrame());
			job.setCharacter(null);
		}
	}

	public void	complete(Job job) {
		Log.debug("Job complete: " + job.getId());

		if (job.getItem() != null) {
			job.getItem().setOwner(null);
		}
		
		if (job.getSlot() != null) {
			job.getSlot().release();
		}
		
		_jobs.remove(job);
	}

	// TODO: change name by filter
	public Job need(Character character, String itemName) {
		Log.debug("JobManager: Character '" + character.getName() + "' need item #" + itemName);

		BaseItem item = ServiceManager.getWorldMap().find(itemName, true);
		if (item != null) {
			return createUseJob(item);
		}
		return null;
	}

	void	addJob(Job job) {
		_jobs.add(job);
	}

	public static JobManager getInstance() {
		if (sSelf == null) {
			sSelf = new JobManager();
		}
		return sSelf;
	}

	public static String getActionName(Action action) {
		switch (action) {
		case NONE: 		return "none";
		case BUILD: 	return "build";
		case GATHER: 	return "gather";
		case MOVE: 		return "move";
		case USE: 		return "use";
		case STORE: 	return "store";
		case WORK: 		return "work";
		case DESTROY:	return "destroy";
		case MINING:	return "mine";
		}
		return null;
	}

	public void clear() {
		_jobs.clear();
	}

	public Job storeItem(BaseItem item) {
		Job job = new Job(++_id, item.getX(), item.getY());
		job.setAction(JobManager.Action.STORE);
		job.setItem(item);
		addJob(job);
		
		return job;
	}

	public void destroyItem(BaseItem item) {
		Job job = new Job(++_id, item.getX(), item.getY());
		job.setAction(JobManager.Action.DESTROY);
		job.setItem(item);
		addJob(job);
	}

	public void askStoreCarry() {
		// TODO Auto-generated method stub
		
	}

	public void move(Character character, int x, int y) {
		Job job = new Job(++_id, x, y);
		job.setAction(JobManager.Action.MOVE);
		job.setCharacterRequire(character);
		addJob(job);
	}

	public Job createRoutineJob(Character c) {
		Log.info("createRoutineJob");
		
		// Character has item to store
		if (c.getCarried().size() > 0) {
			// TODO
			ItemInfo info = ServiceManager.getData().getItemInfo("base.storage");
			return storeItem(ServiceManager.getWorldMap().getNearest(info, c.getPosX(), c.getPosY()));
		}
		
		// Play with random object
		UserItem toy = ServiceManager.getWorldMap().getRandomToy(c.getPosX(), c.getPosY());
		if (toy != null) {
			return createUseJob(toy);
		}
		
		BaseItem bestItem = null;
		int bestDistance = Integer.MAX_VALUE;
		
		for (BaseItem item: _routineItems) {
			if (item.isFree()) {
				int distance = Math.abs(item.getX() - c.getX()) + Math.abs(item.getY() - c.getY());
				if (distance < bestDistance) {
					bestItem = item;
				}
			}
		}
		
		if (bestItem != null) {
			return createJobWork(bestItem);
		}
		
		return null;
	}

	private Job createUseJob(BaseItem item) {
		if (!item.hasFreeSlot()) {
			return null;
		}
		
		Job job = new Job(++_id);
		ItemSlot slot = item.takeSlot(job);
		job.setSlot(slot);
		job.setPosition(slot.getX(), slot.getY());
		job.setAction(JobManager.Action.USE);
		job.setItem(item);
		job.setDurationLeft(item.getInfo().onAction.duration);

		addJob(job);
		
		return job;
	}

	private Job createJobWork(BaseItem item) {
		Job job = new Job(++_id, item.getX(), item.getY());
		job.setAction(JobManager.Action.WORK);
		job.setItem(item);

		addJob(job);
		
		return job;
	}

	public void addRoutineItem(BaseItem item) {
		// TODO
		//_routineItems.add(item);
	}

	public Job createGatherJob(int x, int y) {
		WorldRessource res = ServiceManager.getWorldMap().getRessource(x, y);
		if (res == null) {
			return null;
		}
		
		// Resource is not gatherable
		if (res.getInfo().onGather == null) {
			return null;
		}
		
		Job job = new Job(++_id, res.getX(), res.getY());
		job.setAction(JobManager.Action.GATHER);
		job.setItem(res);

		addJob(job);
		
		return job;
	}

	public Job createMiningJob(int x, int y) {
		WorldRessource res = ServiceManager.getWorldMap().getRessource(x, y);
		if (res == null) {
			return null;
		}
		
		// Resource is not minable
		if (res.getInfo().onMine == null) {
			return null;
		}
		
		Job job = new Job(++_id, res.getX(), res.getY());
		job.setAction(JobManager.Action.MINING);
		job.setItem(res);

		addJob(job);
		
		return job;
	}

	public Job createDumpJob(int x, int y) {
		UserItem item = ServiceManager.getWorldMap().getItem(x, y);
		if (item == null) {
			return null;
		}
		
		Job job = new Job(++_id, item.getX(), item.getY());
		job.setAction(JobManager.Action.DESTROY);
		job.setItem(item);

		addJob(job);
		
		return job;
	}

	public Job createStoreJob() {
		// TODO Auto-generated method stub
		return null;
	}

	public Job createMovingJob(int x, int y) {
		Job job = new Job(++_id, x, y);
		job.setAction(JobManager.Action.MOVE);
		
		addJob(job);
		
		return job;
	}

}
