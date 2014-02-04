#include "UserInterfaceMenuCharacter.h"
#include "CharacterNeeds.h"

#define MENU_CHARACTER_FONT_SIZE 20
#define MENU_CHARACTER_MESSAGE_FONT_SIZE 16

#define MENU_PADDING_TOP 34
#define MENU_PADDING_LEFT 16

UserInterfaceMenuCharacter::UserInterfaceMenuCharacter(sf::RenderWindow* app) {
  _app = app;
  _character = NULL;
}

UserInterfaceMenuCharacter::~UserInterfaceMenuCharacter() {

}

void	UserInterfaceMenuCharacter::init() {
  _backgroundTexture.loadFromFile("../res/menu1.png");
  _background.setTexture(_backgroundTexture);
  _background.setTextureRect(sf::IntRect(0, 0, 380, 420));

  if (!_font.loadFromFile("../snap/xolonium/Xolonium-Regular.otf"))
	throw(std::string("failed to load: ").append("../snap/xolonium/Xolonium-Regular.otf").c_str());
}

void	UserInterfaceMenuCharacter::addMessage(int posX, int posY, int width, int height, int value, sf::RenderStates render) {
  const char* msg;

  switch (value) {
  case CharacterNeeds::MSG_HUNGRY:
	msg = "MSG_HUNGRY";
	break;
  case CharacterNeeds::MSG_STARVE:
	msg = "MSG_STARVE";
	break;
  case CharacterNeeds::MSG_NEED_OXYGEN:
	msg = "MSG_NEED_OXYGEN";
	break;
  case CharacterNeeds::MSG_SLEEP_ON_FLOOR:
	msg = "SLEEP_ON_FLOOR";
	break;
  case CharacterNeeds::MSG_SLEEP_ON_CHAIR:
	msg = "SLEEP_ON_CHAIR";
	break;
  case CharacterNeeds::MSG_NO_WINDOW:
	msg = "MSG_NO_WINDOW";
	break;
  case CharacterNeeds::MSG_BLOCKED:
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
  _app->draw(text, render);
}

void  UserInterfaceMenuCharacter::addGauge(int posX, int posY, int width, int height, int value, const char* label, sf::RenderStates render) {
    sf::Text text;
    text.setString(label);
    text.setFont(_font);
    text.setCharacterSize(MENU_CHARACTER_FONT_SIZE);
    text.setStyle(sf::Text::Regular);
    text.setPosition(posX, posY);
    _app->draw(text, render);

    sf::RectangleShape shapeBg;
    shapeBg.setSize(sf::Vector2f(width, height));
    shapeBg.setFillColor(sf::Color(100, 200, 0));
    shapeBg.setPosition(posX, posY + MENU_CHARACTER_FONT_SIZE + 8);
    _app->draw(shapeBg, render);

    sf::RectangleShape shape;
    shape.setSize(sf::Vector2f(width * value / 100, height));
    shape.setFillColor(sf::Color(200, 255, 0));
    shape.setPosition(posX, posY + MENU_CHARACTER_FONT_SIZE + 8);
    _app->draw(shape, render);
}

void	UserInterfaceMenuCharacter::refresh(int frame) {

  sf::Transform			transform;
  transform.translate(WINDOW_WIDTH - 380 - 64, 250);
  sf::RenderStates		render(transform);

  // Background
  _app->draw(_background, render);

  if (_character != NULL) {

    // Name
    sf::Text text;
    text.setString(_character->getName());
    text.setFont(_font);
    text.setCharacterSize(MENU_CHARACTER_FONT_SIZE);
    text.setStyle(sf::Text::Regular);
    text.setPosition(MENU_PADDING_LEFT + 0, MENU_PADDING_TOP);
    _app->draw(text, render);

    // Job
	Profession function = _character->getProfession();
    sf::Text job;
    job.setString(function.name);
    job.setFont(_font);
    job.setCharacterSize(24);
    job.setColor(function.color);
    job.setStyle(sf::Text::Regular);
    job.setPosition(MENU_PADDING_LEFT + 0, MENU_PADDING_TOP + MENU_CHARACTER_FONT_SIZE);
    _app->draw(job, render);

	const char* texts[11] = {"Food", "Oxygen", "Happiness", "Energy", "Relation", "Security", "Health", "Sickness", "Injuries", "Satiety", "Sleep"};

	CharacterNeeds* needs = _character->getNeeds();
    for (int i = 0; i < 11; i++) {
      int value;
      switch (i) {
      case 0: value = min(max(needs->getFood(), 0), 100); break;
      case 1: value = min(max(needs->getOxygen(), 0), 100); break;
      case 2: value = min(max(needs->getHappiness(), 0), 100); break;
      case 3: value = min(max(needs->getEnergy(), 0), 100); break;
	  case 4: value = min(max(needs->getRelation(), 0), 100); break;
	  case 5: value = min(max(needs->getSecurity(), 0), 100); break;
	  case 6: value = min(max(needs->getHealth(), 0), 100); break;
	  case 7: value = min(max(needs->getSickness(), 0), 100); break;
	  case 8: value = min(max(needs->getInjuries(), 0), 100); break;
	  case 9: value = min(max(needs->getSatiety(), 0), 100); break;
	  case 10: value = min(max(needs->getSleeping(), 0), 100); break;
      }

      addGauge(MENU_PADDING_LEFT + 180 * (i % 2),
               10 + 50 * (i / 2) + (UI_FONT_SIZE + 16) + MENU_PADDING_TOP,
               160,
               12,
               value,
			   texts[i],
			   render);
    }

	int* messages = _character->getMessages();
    for (int i = 0; i < CHARACTER_MAX_MESSAGE; i++) {
	  if (messages[i] == -1 || messages[i] > frame - 100) {
		addMessage(MENU_PADDING_LEFT,
				   280 + (i * UI_FONT_SIZE) + MENU_PADDING_TOP,
				   UI_WIDTH - MENU_PADDING_TOP * 2,
				   UI_FONT_SIZE,
				   i,
				   render);
	  }
    }

  }
}
