#include <sstream>
#include "UserInterfaceCrew.h"
#include "ResourceManager.h"
#include "SpriteManager.h"

#define CREW_LINE_HEIGHT 70
#define CREW_LINE_WIDTH 350

#define	FONT_SIZE		16
#define LINE_HEIGHT		24
#define TITLE_SIZE		FONT_SIZE + 8

#define COLOR_TILE_ACTIVE sf::Color(50, 200, 0)

UserInterfaceCrew::UserInterfaceCrew(sf::RenderWindow* app, int tileIndex)
  : UserInterfaceBase(app, tileIndex) {
  _characterManager = CharacterManager::getInstance();

  _textureTile.loadFromFile("../res/bg_tile_crew.png");
  _texturePanel.loadFromFile("../res/bg_panel_crew.png");
}

UserInterfaceCrew::~UserInterfaceCrew() {

}

void  UserInterfaceCrew::addCharacter(int index, Character* character) {
  int x = index % 4;
  int y = index / 4;

  sf::Text text;
  text.setFont(SpriteManager::getInstance()->getFont());
  text.setCharacterSize(20);
  text.setStyle(sf::Text::Regular);

  // Name
  text.setString(character->getName());
  text.setPosition(_posX + UI_PADDING + CHAR_WIDTH + UI_PADDING + (CREW_LINE_WIDTH * x),
				   _posY + UI_PADDING + 3 + (CREW_LINE_HEIGHT * y));
  _app->draw(text);

  // Function
  Profession function = character->getProfession();
  text.setString(function.name);
  text.setPosition(_posX + UI_PADDING + CHAR_WIDTH + UI_PADDING + (CREW_LINE_WIDTH * x),
				   _posY + UI_PADDING + (CREW_LINE_HEIGHT * y) + 22);
  text.setColor(function.color);
  _app->draw(text);

  sf::Sprite sprite;
  _characterManager->getSprite(&sprite, function.id, 0);
  sprite.setPosition(_posX + UI_PADDING + (CREW_LINE_WIDTH * x),
					 _posY + UI_PADDING + (CREW_LINE_HEIGHT * y));
  _app->draw(sprite);
}

void	UserInterfaceCrew::draw(int frame) {
  if (_isOpen) {
	drawPanel(frame);
  }
  drawTile();
}

void	UserInterfaceCrew::drawPanel(int frame) {
  UserInterfaceBase::drawPanel();

  std::list<Character*>* characters = _characterManager->getList();
  std::list<Character*>::iterator it;
  int i = 0;
  for (it = characters->begin(); it != characters->end(); ++it) {
	addCharacter(i++, *it);
  }
}

void	UserInterfaceCrew::drawTile() {
  UserInterfaceBase::drawTile(COLOR_TILE_ACTIVE);

  std::ostringstream oss;

  sf::Text text;
  text.setFont(SpriteManager::getInstance()->getFont());
  text.setCharacterSize(FONT_SIZE);

  {
	int matter = ResourceManager::getInstance().getMatter();
    oss << "Total: " << CharacterManager::getInstance()->getCount();

	text.setString(oss.str());
    text.setPosition(_posTileX,
					 _posTileY + TITLE_SIZE + UI_PADDING);
    _app->draw(text);
  }

  const Profession* professions = CharacterManager::getInstance()->getProfessions();
  for (int i = 0; professions[i].id != Character::PROFESSION_NONE; i++) {
	sf::RectangleShape shape;
	shape.setSize(sf::Vector2f(24, 24));
	shape.setFillColor(professions[i].color);
	shape.setPosition(UI_PADDING + (i * 28),
					  _posTileY + TITLE_SIZE + UI_PADDING + 32);
	_app->draw(shape);

	oss.str("");
	int count = CharacterManager::getInstance()->getCount(professions[i].id);
	oss << count;
	text.setString(oss.str());
	text.setColor(professions[i].textColor);
	text.setCharacterSize(10);
	text.setPosition(UI_PADDING + (i * 28) + (count < 10 ? 6 : 2),
					 _posTileY + TITLE_SIZE + UI_PADDING + 32 + 5);
    _app->draw(text);
  }

  // {
  //   std::ostringstream oss;
  //   oss << "Power: " << ResourceManager::getInstance().getPower();

  //   text.setString(oss.str());
  //   text.setPosition(posX + UI_PADDING, UI_PADDING + LINE_HEIGHT);
  //   _app->draw(text);
  // }

  // {
  //   std::ostringstream oss;
  //   oss << "O2: " << ResourceManager::getInstance().getO2();

  //   text.setString(oss.str());
  //   text.setPosition(posX + UI_PADDING, UI_PADDING + LINE_HEIGHT * 2);
  //   _app->draw(text);
  // }

  text.setString("Crew");
  text.setCharacterSize(TITLE_SIZE);
  text.setPosition(_posTileX + UI_PADDING, _posTileY + UI_PADDING);
  _app->draw(text);
  text.setString("C");
  text.setStyle(sf::Text::Underlined);
  text.setColor(sf::Color(255, 255, 0));
  _app->draw(text);
}

bool	UserInterfaceCrew::checkKey(sf::Keyboard::Key key) {
  UserInterfaceBase::checkKey(key);

  if (key == sf::Keyboard::C) {
	toogle();
	return true;
  }

  return false;
}
