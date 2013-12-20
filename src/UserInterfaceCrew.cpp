#include "UserInterfaceCrew.h"

#define CREW_LINE_HEIGHT 70
#define CREW_LINE_WIDTH 350

UserInterfaceCrew::UserInterfaceCrew(sf::RenderWindow* app, CharacterManager* characterManager) {
  _app = app;
  _characterManager = characterManager;

  if (!_font.loadFromFile("../snap/xolonium/Xolonium-Regular.otf"))
	throw(std::string("failed to load: ").append("../snap/xolonium/Xolonium-Regular.otf").c_str());
}

UserInterfaceCrew::~UserInterfaceCrew() {

}

void  UserInterfaceCrew::addCharacter(int index, Character* character) {
  int x = index % 4;
  int y = index / 4;

  sf::Text text;
  text.setFont(_font);
  text.setCharacterSize(20);
  text.setStyle(sf::Text::Regular);

  // Name
  text.setString(character->getName());
  text.setPosition(100 + UI_PADDING + CHAR_WIDTH + UI_PADDING + (CREW_LINE_WIDTH * x),
				   100 + UI_PADDING + 3 + (CREW_LINE_HEIGHT * y));
  _app->draw(text);

  // Function
  Profession function = character->getProfession();
  text.setString(function.name);
  text.setPosition(100 + UI_PADDING + CHAR_WIDTH + UI_PADDING + (CREW_LINE_WIDTH * x),
				   100 + UI_PADDING + (CREW_LINE_HEIGHT * y) + 22);
  text.setColor(function.color);
  _app->draw(text);

  sf::Sprite sprite;
  _characterManager->getSprite(&sprite, function.id);
  sprite.setPosition(100 + UI_PADDING + (CREW_LINE_WIDTH * x),
					 100 + UI_PADDING + (CREW_LINE_HEIGHT * y));
  _app->draw(sprite);
}

void	UserInterfaceCrew::refresh(int frame) {

  // Background
  sf::RectangleShape shape;
  shape.setSize(sf::Vector2f(WINDOW_WIDTH - 200, WINDOW_HEIGHT - 200));
  shape.setFillColor(sf::Color(200, 200, 200, 200));
  shape.setPosition(sf::Vector2f(100, 100));
  _app->draw(shape);

  std::list<Character*>* characters = _characterManager->getList();
  std::list<Character*>::iterator it;
  int i = 0;
  for (it = characters->begin(); it != characters->end(); ++it) {
	addCharacter(i++, *it);
  }

}
