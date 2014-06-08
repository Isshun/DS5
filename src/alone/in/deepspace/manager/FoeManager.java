package alone.in.deepspace.manager;
import java.io.IOException;
import java.util.ArrayList;

import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;

import alone.in.deepspace.model.Foe;
import alone.in.deepspace.util.Log;


public class FoeManager {
	private static int ID_START = 1000000;
	private ArrayList<Foe>		 	_foes;
	private int 					_count;
	public FoeManager() {
	  Log.debug("FoeManager");
	  
	  _foes = new ArrayList<Foe>();
	  _foes.add(new Foe(ID_START + _count++, 0, 0));
	  _count = 0;

	  Log.debug("FoeManager done");
	}

//	public void	load(final String filePath) {
//		Log.error("Load characters: " + filePath);
//
//		int x, y, professionType;
//		boolean	inBlock = false;
//
//		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//			String line = null;
//
//			while ((line = br.readLine()) != null) {
//
//				// Start block
//				if ("BEGIN CHARACTERS".equals(line)) {
//					inBlock = true;
//				}
//				
//				// End block
//				else if ("END CHARACTERS".equals(line)) {
//					inBlock = false;
//				}
//
//				// Item
//				else if (inBlock) {
//					String[] values = line.split("\t");
//					if (values.length == 4) {
//						x = Integer.valueOf(values[0]);
//						y = Integer.valueOf(values[1]);
//						professionType = Integer.valueOf(values[2]);
//						Character c = new Character(_count++, x, y, values[3]);
//						c.setProfession(FoeManager.getProfessionType(professionType));
//						_foes.add(c);
//					}
//				}
//				
//			}
//		}
//		 catch (FileNotFoundException e) {
//			 Log.error("Unable to open save file: " + filePath);
//			 e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private static Profession.Type getProfessionType(int index) {
//		  if (index == 1) {return Profession.Type.ENGINEER; }
//		  if (index == 2) {return Profession.Type.OPERATION; }
//		  if (index == 3) {return Profession.Type.DOCTOR; }
//		  if (index == 4) {return Profession.Type.SCIENCE; }
//		  if (index == 5) {return Profession.Type.SECURITY; }
//		  return Profession.Type.NONE;
//	}
//
//
//	public void	save(final String filePath) {
//		Log.info("Save characters: " + filePath);
//
//		try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
//			bw.write("BEGIN CHARACTERS\n");
//			for (Character c: _foes) {
//				bw.write(c.getX() + "\t" + c.getY() + "\t" + c.getProfession().getType().ordinal() + "\t" + c.getName() + "\n");
//			}
//			bw.write("END CHARACTERS\n");
//		} catch (FileNotFoundException e) {
//			Log.error("Unable to open save file: " + filePath);
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		Log.info("Save characters: " + filePath + " done");
//	}
	
	public void	onDraw(RenderWindow app, RenderStates render, double animProgress) throws IOException {
		// TODO
//
//		for (Foe c: _foes) {
//			int posX = c.getX() * Constant.TILE_WIDTH - (Constant.CHAR_WIDTH - Constant.TILE_WIDTH) + 2;
//			int posY = c.getY() * Constant.TILE_HEIGHT - (Constant.CHAR_HEIGHT - Constant.TILE_HEIGHT) + 0;
//			Character.Direction direction = c.getDirection();
//
//			// TODO: ugly
//			int offset = 0;
//
//			if (direction == Character.Direction.DIRECTION_TOP ||
//					direction == Character.Direction.DIRECTION_BOTTOM ||
//					direction == Character.Direction.DIRECTION_RIGHT ||
//					direction == Character.Direction.DIRECTION_LEFT)
//				offset = (int) ((1-animProgress) * Constant.TILE_WIDTH);
//
//			if (direction == Character.Direction.DIRECTION_TOP_RIGHT ||
//					direction == Character.Direction.DIRECTION_TOP_LEFT  ||
//					direction == Character.Direction.DIRECTION_BOTTOM_RIGHT ||
//					direction == Character.Direction.DIRECTION_BOTTOM_LEFT)
//				offset = (int) ((1-animProgress) * Constant.TILE_WIDTH);
//
//			int dirIndex = 0;
//			if (direction == Character.Direction.DIRECTION_BOTTOM) { posY -= offset; dirIndex = 0; }
//			if (direction == Character.Direction.DIRECTION_TOP) { posY += offset; dirIndex = 3; }
//			if (direction == Character.Direction.DIRECTION_RIGHT) { posX -= offset; dirIndex = 2; }
//			if (direction == Character.Direction.DIRECTION_LEFT) { posX += offset; dirIndex = 1; }
//			if (direction == Character.Direction.DIRECTION_BOTTOM_RIGHT) { posY -= offset; posX -= offset; dirIndex = 2; }
//			if (direction == Character.Direction.DIRECTION_BOTTOM_LEFT) { posY -= offset; posX += offset; dirIndex = 1; }
//			if (direction == Character.Direction.DIRECTION_TOP_RIGHT) { posY += offset; posX -= offset; dirIndex = 2; }
//			if (direction == Character.Direction.DIRECTION_TOP_LEFT) { posY += offset; posX += offset; dirIndex = 1; }
//		
//			if (direction == Character.Direction.DIRECTION_TOP_RIGHT)
//				direction = Character.Direction.DIRECTION_RIGHT;
//			if (direction == Character.Direction.DIRECTION_TOP_LEFT)
//				direction = Character.Direction.DIRECTION_LEFT;
//			if (direction == Character.Direction.DIRECTION_BOTTOM_RIGHT)
//				direction = Character.Direction.DIRECTION_RIGHT;
//			if (direction == Character.Direction.DIRECTION_BOTTOM_LEFT)
//				direction = Character.Direction.DIRECTION_LEFT;
//
//			// end ugly
//
//			int frame = c.getFrameIndex() / 20 % 4;
//			
//			Sprite sprite = SpriteManager.getInstance().getFoe(null, dirIndex, frame);
//			
//			sprite.setPosition(posX, posY);
////			if (c.getNeeds().isSleeping()) {
////			  sprite.setTextureRect(new IntRect(0, Constant.CHAR_HEIGHT, Constant.CHAR_WIDTH, Constant.CHAR_HEIGHT));
////		 	} else if (direction == Character.Direction.DIRECTION_NONE) {
////			  sprite.setTextureRect(new IntRect(0, 0, Constant.CHAR_WIDTH, Constant.CHAR_HEIGHT));
////			} else {
////			  sprite.setTextureRect(new IntRect(Constant.CHAR_WIDTH * frame, Constant.CHAR_HEIGHT * dirIndex, Constant.CHAR_WIDTH, Constant.CHAR_HEIGHT));
////			}
//			
//			app.draw(sprite, render);
//		}
	}
	
	public int getCount() {
		return _count;
	}

	public void clear() {
		_foes.clear();
		JobManager.getInstance().clear();
	}

	public void checkSurroundings() {
		// TODO Auto-generated method stub
		
	}

}
