package alone.in.deepspace.UserInterface;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;

import alone.in.deepspace.Character;
import alone.in.deepspace.CharacterManager;
import alone.in.deepspace.Constant;
import alone.in.deepspace.Profession;
import alone.in.deepspace.ResourceManager;
import alone.in.deepspace.SpriteManager;
import alone.in.deepspace.UserSubInterface;


public class UserInterfaceCrew extends UserSubInterface {

	private static int CREW_LINE_HEIGHT = 70;
	private static int CREW_LINE_WIDTH  = 350;

	private static int FONT_SIZE		= 16;
	private static int LINE_HEIGHT		= 24;
	private static int TITLE_SIZE		= Constant.FONT_SIZE + 8;

	private static Color COLOR_TILE_ACTIVE = new Color(50, 200, 0);

	private CharacterManager     _characterManager;

	public UserInterfaceCrew(RenderWindow app, int tileIndex) throws IOException {
		super(app, tileIndex);
		_characterManager = CharacterManager.getInstance();

		_textureTile = new Texture();
		_textureTile.loadFromFile((new File("res/bg_tile_crew.png")).toPath());
		_texturePanel = new Texture();
		_texturePanel.loadFromFile((new File("res/bg_panel_crew.png")).toPath());
	}

	void  addCharacter(int index, Character character) {
	  int x = index % 4;
	  int y = index / 4;
	
	  Text text = new Text();
	  text.setFont(SpriteManager.getInstance().getFont());
	  text.setCharacterSize(20);
	  text.setStyle(Text.REGULAR);
	
	  // Name
	  text.setString(character.getName());
	  text.setPosition(_posX + Constant.UI_PADDING + Constant.CHAR_WIDTH + Constant.UI_PADDING + (CREW_LINE_WIDTH * x),
					   _posY + Constant.UI_PADDING + 3 + (CREW_LINE_HEIGHT * y));
	  _app.draw(text);
	
	  // Function
	  Profession function = character.getProfession();
	  text.setString(function.getName());
	  text.setPosition(_posX + Constant.UI_PADDING + Constant.CHAR_WIDTH + Constant.UI_PADDING + (CREW_LINE_WIDTH * x),
					   _posY + Constant.UI_PADDING + (CREW_LINE_HEIGHT * y) + 22);
	  text.setColor(function.getColor());
	  _app.draw(text);
	
	  Sprite sprite = new Sprite();
	  _characterManager.getSprite(sprite, function.getType(), 0);
	  sprite.setPosition(_posX + Constant.UI_PADDING + (CREW_LINE_WIDTH * x),
						 _posY + Constant.UI_PADDING + (CREW_LINE_HEIGHT * y));
	  _app.draw(sprite);
	}
	
	void	draw(int frame) {
	  if (_isOpen) {
		drawPanel(frame);
	  }
	  drawTile();
	}
	
	void	drawPanel(int frame) {
	  super.drawPanel();
	
	  List<Character> characters = _characterManager.getList();
	  int i = 0;
	  for (Character c: characters) {
		addCharacter(i++, c);
	  }
	}
	
	void	drawTile() {
	  super.drawTile(COLOR_TILE_ACTIVE);
	
	  Text text = new Text();
	  text.setFont(SpriteManager.getInstance().getFont());
	  text.setCharacterSize(FONT_SIZE);
	
	  {
		int matter = ResourceManager.getInstance().getMatter();
		text.setString("Total: " + CharacterManager.getInstance().getCount());
	    text.setPosition(_posTileX,
						 _posTileY + TITLE_SIZE + Constant.UI_PADDING);
	    _app.draw(text);
	  }
	
	  Profession[] professions = CharacterManager.getInstance().getProfessions();
	  for (int i = 0; i < professions.length; i++) {
		RectangleShape shape = new RectangleShape();
		shape.setSize(new Vector2f(24, 24));
		shape.setFillColor(professions[i].getColor());
		shape.setPosition(Constant.UI_PADDING + (i * 28),
						  _posTileY + TITLE_SIZE + Constant.UI_PADDING + 32);
		_app.draw(shape);
	
		int count = CharacterManager.getInstance().getCount(professions[i].getType());
		text.setString(String.valueOf(count));
		text.setColor(professions[i].getTextColor());
		text.setCharacterSize(10);
		text.setPosition(Constant.UI_PADDING + (i * 28) + (count < 10 ? 6 : 2),
						 _posTileY + TITLE_SIZE + Constant.UI_PADDING + 32 + 5);
	    _app.draw(text);
	  }
	
	  text.setString("Crew");
	  text.setCharacterSize(TITLE_SIZE);
	  text.setPosition(_posTileX + Constant.UI_PADDING, _posTileY + Constant.UI_PADDING);
	  _app.draw(text);
	  text.setString("C");
	  text.setStyle(Text.UNDERLINED);
	  text.setColor(Color.YELLOW);
	  _app.draw(text);
	}
	
	protected boolean	checkKey(Keyboard.Key key) {
	  super.checkKey(key);
	
	  if (key == Keyboard.Key.C) {
		toogle();
		return true;
	  }
	
	  return false;
	}
}
