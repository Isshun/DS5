#include "UserInterfaceMenuCharacter.h"

UserInterfaceMenuCharacter::UserInterfaceMenuCharacter(sf::RenderWindow* app) {
  _app = app;
}

UserInterfaceMenuCharacter::~UserInterfaceMenuCharacter() {

}

void	UserInterfaceMenuCharacter::refresh() {
  sf::RectangleShape shape;

  shape.setSize(sf::Vector2f(UI_WIDTH, WINDOW_HEIGHT));
  shape.setFillColor(sf::Color(100, 0, 0));

  _app->draw(shape);
}
