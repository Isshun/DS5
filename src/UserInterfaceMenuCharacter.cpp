#include "UserInterfaceMenuCharacter.h"

#define MENU_CHARACTER_FONT_SIZE 20
#define MENU_CHARACTER_MESSAGE_FONT_SIZE 16

UserInterfaceMenuCharacter::UserInterfaceMenuCharacter(sf::RenderWindow* app) {
  _app = app;
  _character = NULL;

  if (!_font.loadFromFile("../snap/xolonium/Xolonium-Regular.otf"))
	throw(std::string("failed to load: ").append("../snap/xolonium/Xolonium-Regular.otf").c_str());
}

UserInterfaceMenuCharacter::~UserInterfaceMenuCharacter() {

}

void  UserInterfaceMenuCharacter::addMessage(int posX, int posY, int width, int height, int value) {
  const char* msg;

  switch (value) {
  case Character::MSG_HUNGRY:
	msg = "MSG_HUNGRY";
	break;
  case Character::MSG_STARVE:
	msg = "MSG_STARVE";
	break;
  case Character::MSG_NEED_OXYGEN:
	msg = "MSG_NEED_OXYGEN";
	break;
  case Character::MSG_SLEEP_ON_FLOOR:
	msg = "SLEEP_ON_FLOOR";
	break;
  case Character::MSG_SLEEP_ON_CHAIR:
	msg = "SLEEP_ON_CHAIR";
	break;
  case Character::MSG_NO_WINDOW:
	msg = "MSG_NO_WINDOW";
	break;
  case Character::MSG_BLOCKED:
	msg = "MSG_BLOCKED";
	break;
  default:
	return;
  }

  sf::Text text;
  text.setString(msg);
  text.setFont(_font);
  text.setCharacterSize(MENU_CHARACTER_MESSAGE_FONT_SIZE);
  text.setStyle(sf::Text::Regular);
  text.setPosition(posX, posY);
  _app->draw(text);
}

void  UserInterfaceMenuCharacter::addGauge(int posX, int posY, int width, int height, int value, const char* label) {
    sf::Text text;
    text.setString(label);
    text.setFont(_font);
    text.setCharacterSize(MENU_CHARACTER_FONT_SIZE);
    text.setStyle(sf::Text::Regular);
    text.setPosition(posX, posY);
    _app->draw(text);

    sf::RectangleShape shapeBg;
    shapeBg.setSize(sf::Vector2f(width, height));
    shapeBg.setFillColor(sf::Color(100, 200, 0));
    shapeBg.setPosition(posX, posY + MENU_CHARACTER_FONT_SIZE + UI_PADDING);
    _app->draw(shapeBg);

    sf::RectangleShape shape;
    shape.setSize(sf::Vector2f(width * value / 100, height));
    shape.setFillColor(sf::Color(200, 255, 0));
    shape.setPosition(posX, posY + MENU_CHARACTER_FONT_SIZE + UI_PADDING);
    _app->draw(shape);
}

void	UserInterfaceMenuCharacter::refresh(int frame) {

  // Background
  sf::RectangleShape shape;
  shape.setSize(sf::Vector2f(UI_WIDTH, WINDOW_HEIGHT));
  shape.setFillColor(sf::Color(100, 0, 0));
  _app->draw(shape);

  if (_character != NULL) {
    // Job
    sf::Text text;
    text.setString(_character->getName());
    text.setFont(_font);
    text.setCharacterSize(MENU_CHARACTER_FONT_SIZE);
    text.setStyle(sf::Text::Regular);
    text.setPosition(UI_PADDING + 0, UI_PADDING);
    _app->draw(text);

    // Name
	Profession function = _character->getProfession();
    sf::Text job;
    job.setString(function.name);
    job.setFont(_font);
    job.setCharacterSize(24);
    job.setColor(function.color);
    job.setStyle(sf::Text::Regular);
    job.setPosition(UI_PADDING + 0, UI_PADDING + MENU_CHARACTER_FONT_SIZE);
    _app->draw(job);

	const char* texts[5] = {"Food", "Oxygen", "Hapiness", "Energy", "Health"};

    for (int i = 0; i < 4; i++) {
      int value;
      switch (i) {
      case 0: value = max(_character->getFood(), 0); break;
      case 1: value = max(_character->getOxygen(), 0); break;
      case 2: value = max(_character->getHapiness(), 0); break;
      case 3: value = max(_character->getEnergy(), 0); break;
	  case 4: value = max(_character->getHealth(), 0); break;
      }

      addGauge(UI_PADDING,
               80 * i + (UI_FONT_SIZE * 2) + UI_PADDING,
               UI_WIDTH - UI_PADDING * 2,
               UI_FONT_SIZE,
               value,
			   texts[i]);
    }

	int* messages = _character->getMessages();
    for (int i = 0; i < CHARACTER_MAX_MESSAGE; i++) {
	  if (messages[i] == -1 || messages[i] > frame - 100) {
		addMessage(UI_PADDING,
				   400 + (i * UI_FONT_SIZE) + UI_PADDING,
				   UI_WIDTH - UI_PADDING * 2,
				   UI_FONT_SIZE,
				   i);
	  }
    }

  }
}
