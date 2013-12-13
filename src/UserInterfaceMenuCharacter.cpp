#include "UserInterfaceMenuCharacter.h"

#define MENU_CHARACTER_FONT_SIZE 24

UserInterfaceMenuCharacter::UserInterfaceMenuCharacter(sf::RenderWindow* app) {
  _app = app;
  _character = NULL;
}

UserInterfaceMenuCharacter::~UserInterfaceMenuCharacter() {

}

void  UserInterfaceMenuCharacter::addGauge(int posX, int posY, int width, int height, int value) {
    sf::RectangleShape shapeBg;
    shapeBg.setSize(sf::Vector2f(width, height));
    shapeBg.setFillColor(sf::Color(100, 200, 0));
    shapeBg.setPosition(posX, posY);
    _app->draw(shapeBg);

    sf::RectangleShape shape;
    shape.setSize(sf::Vector2f(width * value / 100, height));
    shape.setFillColor(sf::Color(200, 255, 0));
    shape.setPosition(posX, posY);
    _app->draw(shape);
}

void	UserInterfaceMenuCharacter::refresh() {

  // Background
  sf::RectangleShape shape;
  shape.setSize(sf::Vector2f(UI_WIDTH, WINDOW_HEIGHT));
  shape.setFillColor(sf::Color(100, 0, 0));
  _app->draw(shape);

  if (_character != NULL) {
    sf::Font font;
    if (!font.loadFromFile("../snap/xolonium/Xolonium-Regular.otf"))
      throw(std::string("failed to load: ").append("../snap/xolonium/Xolonium-Regular.otf").c_str());

    // Job
    sf::Text text;
    text.setString(_character->getName());
    text.setFont(font);
    text.setCharacterSize(MENU_CHARACTER_FONT_SIZE);
    text.setStyle(sf::Text::Regular);
    text.setPosition(UI_PADDING + 0, UI_PADDING);
    _app->draw(text);

    // Name
    sf::Text job;
    job.setString(_character->getJobName());
    job.setFont(font);
    job.setCharacterSize(24);
    job.setStyle(sf::Text::Regular);
    job.setPosition(UI_PADDING + 0, UI_PADDING + MENU_CHARACTER_FONT_SIZE);
    _app->draw(job);

    for (int i = 0; i < 3; i++) {
      int value;
      switch (i) {
      case 0: value = _character->getFood(); break;
      case 1: value = _character->getOxygen(); break;
      case 2: value = _character->getHapiness(); break;
      }

      addGauge(UI_PADDING,
               (MENU_CHARACTER_FONT_SIZE * 3) + (UI_FONT_SIZE + UI_PADDING) * i,
               UI_WIDTH - UI_PADDING * 2,
               UI_FONT_SIZE,
               value);
    }
  }
}
