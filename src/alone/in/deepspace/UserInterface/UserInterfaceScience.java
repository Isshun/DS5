package alone.in.deepspace.UserInterface;
import java.io.File;
import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;

import alone.in.deepspace.Managers.SpriteManager;
import alone.in.deepspace.Utils.Constant;


public class UserInterfaceScience extends UserSubInterface {
	
	private static final int UIRES_POSX		= Constant.UI_WIDTH;
	private static final int UIRES_POSY		= 0;

	private static final int FONT_SIZE		= 16;
	private static final int LINE_HEIGHT	= 24;
	private static final int TITLE_SIZE		= FONT_SIZE + 8;

	private static final Color TILE_ACTIVE_COLOR	= new Color(25, 50, 255);

	UserInterfaceScience(RenderWindow app, int tileIndex) throws IOException {
		  super(app, tileIndex, new Vector2f(0, 0), new Vector2f(200, 200));
  
		_textureTile = new Texture();
		_textureTile.loadFromFile((new File("res/bg_tile_science.png")).toPath());
		_texturePanel = new Texture();
		_texturePanel.loadFromFile((new File("res/bg_panel_science.png")).toPath());
	}

	@Override
	public void onRefresh(RenderWindow app) {

  // {
  // 	int matter = ResourceManager.getInstance().getMatter();
  //   std.ostringstream oss;
  //   oss + "Matter: " + matter;

  //   Text text;
  //   text.setString(oss.str());
  //   text.setFont(_font);
  //   text.setCharacterSize(24);
  //   // text.setStyle(Text.Underlined);

  // 	if (matter == 0)
  // 	  text.setColor(Color(255, 0, 0));
  // 	else if (matter < 20)
  // 	  text.setColor(Color(255, 255, 0));
  //   text.setPosition(UIRES_POSX + UI_PADDING + 0, UIRES_POSY + UI_PADDING + 0);
  //   _app.draw(text);
  // }

  // {
  //   std.ostringstream oss;
  //   oss + "Power: " + ResourceManager.getInstance().getPower();

  //   Text text;
  //   text.setString(oss.str());
  //   text.setFont(_font);
  //   text.setCharacterSize(24);
  //   // text.setCharacterSize(UI_FONT_SIZE);
  //   // text.setStyle(Text.Underlined);
  //   // text.setColor(Color(255, 255, 0));
  //   text.setPosition(UIRES_POSX + UI_PADDING + 280 + 0, UIRES_POSY + UI_PADDING + 0);
  //   _app.draw(text);
  // }

  // {
  //   std.ostringstream oss;
  //   oss + "O2: " + ResourceManager.getInstance().getO2();

  //   Text text;
  //   text.setString(oss.str());
  //   text.setFont(_font);
  //   text.setCharacterSize(24);
  //   text.setPosition(UIRES_POSX + UI_PADDING + 540 + 0, UIRES_POSY + UI_PADDING + 0);
  //   _app.draw(text);
  // }

}

void	draw(int frame) {
  // if (_isOpen) {
  // 	drawPanel(frame);
  // }
  drawTile();
}

void	drawTile() {
//  super.drawTile(TILE_ACTIVE_COLOR);
//
//  Text text = new Text();
//  text.setFont(SpriteManager.getInstance().getFont());
//  text.setCharacterSize(FONT_SIZE);
//
//  // {
//  // 	int matter = ResourceManager.getInstance().getMatter();
//  //   std.ostringstream oss;
//  //   oss + "Matter: " + matter;
//
//  // 	text.setString(oss.str());
//
//  // 	if (matter == 0)
//  // 	  text.setColor(Color(255, 0, 0));
//  // 	else if (matter < 20)
//  // 	  text.setColor(Color(255, 255, 0));
//  //   text.setPosition(_posTileX + UI_PADDING, TITLE_SIZE + UI_PADDING + UI_PADDING);
//  //   _app.draw(text);
//  // 	text.setColor(Color(255, 255, 255));
//  // }
//
//  // {
//  //   std.ostringstream oss;
//  //   oss + "Power: " + ResourceManager.getInstance().getPower();
//
//  //   text.setString(oss.str());
//  //   text.setPosition(_posTileX + UI_PADDING, TITLE_SIZE + UI_PADDING + UI_PADDING + LINE_HEIGHT);
//  //   _app.draw(text);
//  // }
//
//  // {
//  //   std.ostringstream oss;
//  //   oss + "O2: " + ResourceManager.getInstance().getO2();
//
//  //   text.setString(oss.str());
//  //   text.setPosition(_posTileX + UI_PADDING, TITLE_SIZE + UI_PADDING + UI_PADDING + LINE_HEIGHT * 2);
//  //   _app.draw(text);
//  // }
//
//  text.setString("Research");
//  text.setCharacterSize(TITLE_SIZE);
//  text.setPosition(_posTileX + Constant.UI_PADDING, _posTileY + Constant.UI_PADDING);
//  _app.draw(text);
//  text.setString("R");
//  text.setStyle(Text.UNDERLINED);
//  text.setColor(Color.YELLOW);
//  _app.draw(text);
}

protected boolean	checkKey(Keyboard.Key key) {
  super.checkKey(key);

  if (key == Keyboard.Key.R) {
	toogle();
	return true;
  }

  return false;
}

}
